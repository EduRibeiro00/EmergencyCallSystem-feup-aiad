package behaviours;


import GUI.GUI;
import GUI.Edge;
import agents.ControlTowerAgent;
import agents.VehicleAgent;
import repast.RepastLauncher;
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
import uchicago.src.sim.network.DefaultDrawableEdge;
import uchicago.src.sim.network.DefaultDrawableNode;
import utils.Emergency;
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

    private final VehicleAgent vehicleAgent;
    protected int fuel;
    private int consecutiveRejectionsByFuel;
    protected AtomicBoolean refueling;
    private final ScheduledThreadPoolExecutor executor;


    public VehicleBehaviour(VehicleAgent agent, MessageTemplate msgTemp) {
        super(agent, msgTemp);
        vehicleAgent = agent;

        consecutiveRejectionsByFuel = 0;
        fuel = getMaxFuel();
        this.vehicleAgent.setOccupied(new AtomicBoolean(false));
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
        if(this.vehicleAgent.getOccupied().get()) {
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
                double distance = vehicleAgent.getCoordinates().getDistance(emergencyCoords);

                // vehicle cannot take emergency because it does not have enough fuel
                if(calcFuelForTrip(distance) > fuel) {
                    vehicleReply.setPerformative(ACLMessage.REFUSE);
                    vehicleReply.setContent(Messages.NOT_ENOUGH_FUEL);
                    consecutiveRejectionsByFuel++;
                    LoggerHelper.get().logFuelInsuf(this.myAgent.getLocalName());
                // vehicle is free and has enough fuel; is eligible for the emergency
                } else {
                    vehicleAgent.setCurrentEmergencyCoords(emergencyCoords);
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
        if(this.vehicleAgent.getOccupied().get())
            LoggerHelper.get().logRejectProposalOccupied(this.myAgent.getLocalName());
        else if(refueling.get())
            LoggerHelper.get().logRejectProposalRefueling(this.myAgent.getLocalName());
        else
            LoggerHelper.get().logRejectProposal(this.myAgent.getLocalName(), vehicleAgent.getCoordinates());
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
            LoggerHelper.get().logAcceptProposal(this.myAgent.getLocalName(), vehicleAgent.getCoordinates());

            this.vehicleAgent.setEmergencyId(acceptVehicleMsg.getEmergencyId());
            GUI.createEdgeName(vehicleAgent.getNode(), GUI.getEmergencyLabel(acceptVehicleMsg.getEmergencyId()),Color.green);
            System.out.println("ZAAAAAAAAAAAAAAAS");
            System.out.println(GUI.getControlTowerNode());
            GUI.removeEdge(vehicleAgent.getNode(),GUI.getControlTowerNode());


            double distance = vehicleAgent.getCoordinates().getDistance(acceptVehicleMsg.getCoordinates());
            int duration = (acceptVehicleMsg.getAccidentDuration() + (int) Math.round(distance) * 20);

            vehicleAgent.setCoordinates(acceptVehicleMsg.getCoordinates());
            fuel = (fuel -= calcFuelForTrip(distance)) < 0 ? 0 : fuel;
            startEmergency(duration);
        }

        ACLMessage informReply = accept.createReply();
        informReply.setPerformative(ACLMessage.INFORM);

        //TODO Como e que isto funciona?
        GUI.createEdgeName(vehicleAgent.getNode(),ControlTowerAgent.getDFName(),Color.red);

        return informReply;
    }

    protected void acceptCfp(ACLMessage vehicleReply, ACLMessage cfp){
        vehicleReply.setPerformative(ACLMessage.PROPOSE);
        try {
            Point emergencyCoords = ((TowerRequest) cfp.getContentObject()).getCoordinates();
            double distance = vehicleAgent.getCoordinates().getDistance(emergencyCoords);

            double value = this.calcVehicleValue(distance);

            vehicleReply.setContentObject(new VehicleResponse(value));
        } catch (UnreadableException | IOException e) {
            e.printStackTrace();
        }
    }

    protected double calcVehicleValue(double distance) {
        // value is influenced by distance to the emergency, number of employees in the vehicle and the fuel left in the car

        double value = (distance * this.vehicleAgent.getDISTANCE_MULTIPLIER()) +
                (vehicleAgent.getNumberEmployees()  * this.vehicleAgent.getEMPLOYEE_MULTIPLIER()) +
                (fuel * this.vehicleAgent.getFUEL_MULTIPLIER());
        return Math.round(value * 1000.0) / 1000.0;
    }

    protected int calcFuelForTrip(double distance) {

        return (int)(distance * getFuelRate() *
                ( 1 + vehicleAgent.getNumberEmployees() * this.vehicleAgent.getEMPLOYEE_FUEL_MULTIPLIER()));

    }

    protected void startEmergency(int duration) {
        this.vehicleAgent.getOccupied().set(true);
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
                this.vehicleAgent.getREFUEL_DURATION(),
                TimeUnit.MILLISECONDS
        );

        vehicleAgent.getNode().setColor(Color.gray);

        LoggerHelper.get().logNeedRefuel(this.myAgent.getLocalName(), fuel);
    }

    protected void finishOccupied() {
        vehicleAgent.getOccupied().set(false);
        Emergency emergency = RepastLauncher.getEmergencyMap().get(this.vehicleAgent.getEmergencyId());
        emergency.incrementLeftVehiclesEmerg(this.vehicleAgent);
        this.vehicleAgent.setEmergencyId(-1);

        boolean shouldChangeEmployees = ThreadLocalRandom.current().nextInt(this.vehicleAgent.getEMPLOYEE_CHANGE_PROB()) == 0;

        if (shouldChangeEmployees) {
            vehicleAgent.setNumberEmployees( vehicleAgent.getRandomNumberEmployees());
            LoggerHelper.get().logEmployeeChange(this.myAgent.getLocalName(), vehicleAgent.getNumberEmployees());
        }

        LoggerHelper.get().logUnoccupied(this.myAgent.getLocalName(), fuel, vehicleAgent.getNumberEmployees());

        if(fuel < getSpareFuelLevel()) {
            startRefueling();
        }
    }

    protected void finishRefueling() {
        refueling.set(false);
        fuel = getMaxFuel();

        vehicleAgent.getNode().setColor(GUI.parseColor(vehicleAgent));
        LoggerHelper.get().logDoneRefuel(this.myAgent.getLocalName(), fuel);
    }



    public abstract VehicleType getVehicleType();

    protected int getMaxFuel() {
        return vehicleAgent.getMAX_FUEL();
    }

    protected double getFuelRate() {
        return vehicleAgent.getFUEL_RATE();
    }

    protected int getSpareFuelLevel() {
        return vehicleAgent.getSPARE_FUEL_LEVEL();
    }

    @Override
    public String toString() {
        return "Vehicle{" +
                "coordinates=" + vehicleAgent.getCoordinates() +
                ", numberEmployees=" + vehicleAgent.getNumberEmployees() +
                ", fuel=" + fuel +
                '}';
    }

    @Override
    public void onStart() {
        //TODO Isto esta bem?
        super.onStart();

        // create edge
        /*if(vehicleAgent.getNode() != null) {
            DefaultDrawableNode to =  GUI.getNode(vehicleAgent.getAID().getLocalName());

            Edge edge = new Edge(vehicleAgent.getNode(), to);
            edge.setColor(Color.ORANGE);
            vehicleAgent.getNode().addOutEdge(edge);
        }*/
    }






}
