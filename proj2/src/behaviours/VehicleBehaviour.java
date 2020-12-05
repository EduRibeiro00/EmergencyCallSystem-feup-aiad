package behaviours;

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
import utils.Point;
import utils.VehicleType;
import java.io.IOException;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class VehicleBehaviour extends ContractNetResponder {
    protected int MIN_NUM_EMPLOYEES;
    protected int MAX_NUM_EMPLOYEES;
    protected int MAX_CONSECUTIVE_REJECTIONS;
    protected int REFUEL_DURATION;
    protected int EMPLOYEE_CHANGE_PROB;

    protected double EMPLOYEE_MULTIPLIER;
    protected double DISTANCE_MULTIPLIER;
    protected double FUEL_MULTIPLIER;
    protected double EMPLOYEE_FUEL_MULTIPLIER;

    protected Point coordinates;
    protected int numberEmployees;
    protected int fuel;
    private int consecutiveRejectionsByFuel;

    protected AtomicBoolean occupied;
    protected AtomicBoolean refueling;
    private final ScheduledThreadPoolExecutor executor;

    public VehicleBehaviour(Agent agent, MessageTemplate msgTemp,
                            int MIN_NUM_EMPLOYEES, int MAX_NUM_EMPLOYEES, int REFUEL_DURATION, int EMPLOYEE_CHANGE_PROB,
                            double EMPLOYEE_MULTIPLIER, double DISTANCE_MULTIPLIER, double FUEL_MULTIPLIER,
                            double EMPLOYEE_FUEL_MULTIPLIER) {

        super(agent, msgTemp);

        this.MIN_NUM_EMPLOYEES = MIN_NUM_EMPLOYEES;
        this.MAX_NUM_EMPLOYEES = MAX_NUM_EMPLOYEES;
        this.REFUEL_DURATION = REFUEL_DURATION;
        this.EMPLOYEE_CHANGE_PROB = EMPLOYEE_CHANGE_PROB;
        this.EMPLOYEE_MULTIPLIER = EMPLOYEE_MULTIPLIER;
        this.DISTANCE_MULTIPLIER = DISTANCE_MULTIPLIER;
        this.FUEL_MULTIPLIER = FUEL_MULTIPLIER;
        this.EMPLOYEE_FUEL_MULTIPLIER = EMPLOYEE_FUEL_MULTIPLIER;

        consecutiveRejectionsByFuel = 0;
        coordinates = Point.genRandomPoint();
        numberEmployees = getRandomNumberEmployees();
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
                double distance = coordinates.getDistance(emergencyCoords);

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
            LoggerHelper.get().logRejectProposal(this.myAgent.getLocalName(), coordinates);
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
            LoggerHelper.get().logAcceptProposal(this.myAgent.getLocalName(), coordinates);

            double distance = coordinates.getDistance(acceptVehicleMsg.getCoordinates());
            int duration = (acceptVehicleMsg.getAccidentDuration() + (int) Math.round(distance) * 20);

            coordinates = acceptVehicleMsg.getCoordinates();
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
            double distance = coordinates.getDistance(emergencyCoords);

            double value = this.calcVehicleValue(distance);

            vehicleReply.setContentObject(new VehicleResponse(value));
        } catch (UnreadableException | IOException e) {
            e.printStackTrace();
        }
    }

    protected double calcVehicleValue(double distance) {
        // value is influenced by distance to the emergency, number of employees in the vehicle and the fuel left in the car
        double value = (distance * DISTANCE_MULTIPLIER) + (numberEmployees * EMPLOYEE_MULTIPLIER) + (fuel * FUEL_MULTIPLIER);
        return Math.round(value * 1000.0) / 1000.0;
    }

    protected int calcFuelForTrip(double distance) {
        return (int)(distance * getFuelRate() * ( 1 + numberEmployees * EMPLOYEE_FUEL_MULTIPLIER));
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
            numberEmployees = getRandomNumberEmployees();
            LoggerHelper.get().logEmployeeChange(this.myAgent.getLocalName(), numberEmployees);
        }

        LoggerHelper.get().logUnoccupied(this.myAgent.getLocalName(), fuel, numberEmployees);

        if(fuel < getSpareFuelLevel()) {
            startRefueling();
        }
    }

    protected void finishRefueling() {
        refueling.set(false);
        fuel = getMaxFuel();

        LoggerHelper.get().logDoneRefuel(this.myAgent.getLocalName(), fuel);
    }

    private int getRandomNumberEmployees() {
        return ThreadLocalRandom.current().nextInt(MIN_NUM_EMPLOYEES, MAX_NUM_EMPLOYEES + 1);
    }

    public abstract VehicleType getVehicleType();

    protected abstract int getMaxFuel();

    protected abstract double getFuelRate();

    protected abstract int getSpareFuelLevel();

    @Override
    public String toString() {
        return "Vehicle{" +
                "coordinates=" + coordinates +
                ", numberEmployees=" + numberEmployees +
                ", fuel=" + fuel +
                '}';
    }
}
