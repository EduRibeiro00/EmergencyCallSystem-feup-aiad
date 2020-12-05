package behaviours;

import GUI.GUI;
import GUI.Edge;
import agents.VehicleAgent;
import sajas.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import sajas.proto.ContractNetResponder;
import logs.LoggerHelper;
import messages.TowerRequest;
import messages.VehicleResponse;
import messages.AcceptVehicle;
import messages.Messages;
import uchicago.src.sim.network.DefaultDrawableNode;
import utils.Point;
import utils.VehicleType;

import java.awt.*;
import java.io.IOException;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class VehicleBehaviour extends ContractNetResponder {

    protected static final int MAX_CONSECUTIVE_REJECTIONS = 3;
    protected static final int REFUEL_DURATION = 20000;
    protected static final int EMPLOYEE_CHANGE_PROB = 10; // 1 in 10 chance of changing number employees

    protected static final double EMPLOYEE_MULTIPLIER = 2.5;
    protected static final double DISTANCE_MULTIPLIER = -1.0;
    protected static final double FUEL_MULTIPLIER = 0.3;
    protected static final double EMPLOYEE_FUEL_MULTIPLIER = 0.1;


    protected int fuel;
    private int consecutiveRejectionsByFuel;
    final private VehicleAgent agent;

    protected AtomicBoolean occupied;
    protected AtomicBoolean refueling;
    private final ScheduledThreadPoolExecutor executor;



    DefaultDrawableNode myNode;

    public VehicleBehaviour(VehicleAgent agent, MessageTemplate msgTemp) {
        super(agent, msgTemp);
        this.agent =agent;

        consecutiveRejectionsByFuel = 0;
        fuel = getMaxFuel();
        occupied = new AtomicBoolean(false);
        refueling = new AtomicBoolean(false);
        executor = new ScheduledThreadPoolExecutor(2);

        LoggerHelper.get().logStartVehicle(
                this.myAgent.getLocalName(),
                getVehicleType(),
                this
        );
    }

    @Override
    public ACLMessage handleCfp(ACLMessage cfp) {
        LoggerHelper.get().logHandleCfp(this.myAgent.getLocalName());
        ACLMessage vehicleReply = cfp.createReply();

        // vehicle is occupied with another emergency
        if(occupied.get()) {
            vehicleReply.setPerformative(ACLMessage.REFUSE);
            vehicleReply.setContent(Messages.IS_OCCUPIED);
            LoggerHelper.get().logAlreadyOccupied(this.myAgent.getLocalName());
        }
        // vehicle is occupied refueling
        else if(refueling.get()) {
            vehicleReply.setPerformative(ACLMessage.REFUSE);
            vehicleReply.setContent(Messages.IS_REFUELING);
            LoggerHelper.get().logRefueling(this.myAgent.getLocalName());
        }
        // vehicle is not occupied
        else {
            try {
                Point emergencyCoords = ((TowerRequest) cfp.getContentObject()).getCoordinates();
                double distance = agent.getCoordinates().getDistance(emergencyCoords);

                // vehicle cannot take emergency because it does not have enough fuel
                if(calcFuelForTrip(distance) > fuel) {
                    vehicleReply.setPerformative(ACLMessage.REFUSE);
                    vehicleReply.setContent(Messages.NOT_ENOUGH_FUEL);
                    consecutiveRejectionsByFuel++;
                    LoggerHelper.get().logFuelInsuf(this.myAgent.getLocalName());
                // vehicle is free and has enough fuel; is eligible for the emergency
                } else {
                    consecutiveRejectionsByFuel = 0;
                    acceptCfp(vehicleReply, cfp);
                }
            } catch (UnreadableException e) {
                e.printStackTrace();
            }
        }

        // if it rejected X consecutive emergencies because it didn't have enough fuel, should refuel
        if(consecutiveRejectionsByFuel >= MAX_CONSECUTIVE_REJECTIONS) {
            LoggerHelper.get().logRejectedConsecutiveMax(this.myAgent.getLocalName(), MAX_CONSECUTIVE_REJECTIONS);
            startRefueling();
        }

        return vehicleReply;
    }


    @Override
    public void handleRejectProposal(ACLMessage cfp, ACLMessage propose, ACLMessage reject) {
        if(occupied.get())
            LoggerHelper.get().logRejectProposalOccupied(this.myAgent.getLocalName());
        else if(refueling.get())
            LoggerHelper.get().logRejectProposalRefueling(this.myAgent.getLocalName());
        else
            LoggerHelper.get().logRejectProposal(this.myAgent.getLocalName(), agent.getCoordinates());
    }


    @Override
    public ACLMessage handleAcceptProposal(ACLMessage cfp, ACLMessage propose, ACLMessage accept) {
        Object content = null;
        try {
            content = accept.getContentObject();
        } catch (UnreadableException e) {
            e.printStackTrace();
        }

        if(content instanceof AcceptVehicle) {
            AcceptVehicle acceptVehicleMsg = (AcceptVehicle) content;
            LoggerHelper.get().logAcceptProposal(this.myAgent.getLocalName(), agent.getCoordinates());

            double distance = agent.getCoordinates().getDistance(acceptVehicleMsg.getCoordinates());
            int duration = (acceptVehicleMsg.getAccidentDuration() + (int) Math.round(distance) * 20);

            agent.setCoordinates( acceptVehicleMsg.getCoordinates());
            fuel = (fuel -= calcFuelForTrip(distance)) < 0 ? 0 : fuel;

            startEmergency(duration);
        }

        ACLMessage informReply = accept.createReply();
        informReply.setPerformative(ACLMessage.INFORM);

        return informReply;
    }

    protected void acceptCfp(ACLMessage vehicleReply, ACLMessage cfp){
        vehicleReply.setPerformative(ACLMessage.PROPOSE);
        try {
            Point emergencyCoords = ((TowerRequest) cfp.getContentObject()).getCoordinates();
            double distance = agent.getCoordinates().getDistance(emergencyCoords);

            double value = this.calcVehicleValue(distance);

            vehicleReply.setContentObject(new VehicleResponse(value));
        } catch (UnreadableException | IOException e) {
            e.printStackTrace();
        }
    }

    protected double calcVehicleValue(double distance) {
        // value is influenced by distance to the emergency, number of employees in the vehicle and the fuel left in the car
        double value = (distance * DISTANCE_MULTIPLIER) + (agent.getNumberEmployees() * EMPLOYEE_MULTIPLIER) + (fuel * FUEL_MULTIPLIER);
        return Math.round(value * 1000.0) / 1000.0;
    }

    protected int calcFuelForTrip(double distance) {
        return (int)(distance * getFuelRate() * ( 1 + agent.getNumberEmployees() * EMPLOYEE_FUEL_MULTIPLIER));
    }

    protected void startEmergency(int duration) {
        occupied.set(true);
        executor.schedule(
                this::finishOccupied,
                duration,
                TimeUnit.MILLISECONDS
        );

        LoggerHelper.get().logOccupied(this.myAgent.getLocalName(), ((double) duration)/1000);
    }

    protected void startRefueling() {
        refueling.set(true);
        executor.schedule(
                this::finishRefueling,
                REFUEL_DURATION,
                TimeUnit.MILLISECONDS
        );

        LoggerHelper.get().logNeedRefuel(this.myAgent.getLocalName(), fuel);
    }

    protected void finishOccupied() {
        occupied.set(false);
        boolean shouldChangeEmployees = ThreadLocalRandom.current().nextInt(EMPLOYEE_CHANGE_PROB) == 0;
        if (shouldChangeEmployees) {
            agent.setNumberEmployees( agent.getRandomNumberEmployees());
            LoggerHelper.get().logEmployeeChange(this.myAgent.getLocalName(), agent.getNumberEmployees());
        }

        LoggerHelper.get().logUnoccupied(this.myAgent.getLocalName(), fuel, agent.getNumberEmployees());

        if(fuel < getSpareFuelLevel()) {
            startRefueling();
        }
    }

    protected void finishRefueling() {
        refueling.set(false);
        fuel = getMaxFuel();

        LoggerHelper.get().logDoneRefuel(this.myAgent.getLocalName(), fuel);
    }



    public abstract VehicleType getVehicleType();

    protected abstract int getMaxFuel();

    protected abstract int getFuelRate();

    protected abstract int getSpareFuelLevel();

    @Override
    public String toString() {
        return "Vehicle{" +
                "coordinates=" + agent.getCoordinates() +
                ", numberEmployees=" + agent.getNumberEmployees() +
                ", fuel=" + fuel +
                '}';
    }
    @Override
    public void onStart() {
        //TODO Isto esta bem?
        super.onStart();

        // create edge
        if(myNode != null) {
            DefaultDrawableNode to =  GUI.getNode( agent.getAID().getLocalName());

            Edge edge = new Edge(myNode, to);
            edge.setColor(Color.ORANGE);
            myNode.addOutEdge(edge);
        }
    }

    public DefaultDrawableNode getNode() {
        return myNode;
    }

    public void setNode(DefaultDrawableNode myNode) {
        this.myNode = myNode;
    }




}
