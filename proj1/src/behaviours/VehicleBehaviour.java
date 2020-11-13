package behaviours;

import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import jade.proto.ContractNetResponder;
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
    protected static final int MIN_NUM_EMPLOYEES= 1;
    protected static final int MAX_NUM_EMPLOYEES= 6;
    protected static final int MAX_CONSECUTIVE_REJECTIONS = 3;
    protected static final int REFUEL_DURATION = 20000;
    protected static final double EMPLOYEE_MULTIPLIER = 0.1;

    protected Point coordinates;
    protected int numberEmployees;
    protected int fuel;
    private int consecutiveRejections;

    protected AtomicBoolean occupied;
    protected AtomicBoolean refueling;
    private ScheduledThreadPoolExecutor executor;

    public VehicleBehaviour(Agent agent, MessageTemplate msgTemp) {
        super(agent, msgTemp);

        consecutiveRejections = 0;
        coordinates = Point.genRandomPoint();
        numberEmployees = getRandomNumberEmployees();
        fuel = getMaxFuel();
        occupied = new AtomicBoolean(false);
        refueling = new AtomicBoolean(false);
        executor = new ScheduledThreadPoolExecutor(2);

        LoggerHelper.get().logStartVehicle(
                this.myAgent.getLocalName(),
                getVehicleType(),
                coordinates
        );
    }

    @Override
    public ACLMessage handleCfp(ACLMessage cfp) {
        LoggerHelper.get().logHandleCfp(this.myAgent.getLocalName());
        ACLMessage vehicleReply = cfp.createReply();

        if(occupied.get()) {
            vehicleReply.setPerformative(ACLMessage.REFUSE);
            vehicleReply.setContent(Messages.IS_OCCUPIED);
            consecutiveRejections++;
            LoggerHelper.get().logAlreadyOccupied(this.myAgent.getLocalName());
        }
        else if(refueling.get()) {
            vehicleReply.setPerformative(ACLMessage.REFUSE);
            vehicleReply.setContent(Messages.IS_REFUELING);
            consecutiveRejections++;
            LoggerHelper.get().logRefueling(this.myAgent.getLocalName());
        } else {
            try {
                Point emergencyCoords = ((TowerRequest) cfp.getContentObject()).getCoordinates();
                double distance = coordinates.getDistance(emergencyCoords);

                if(calculateFuel(distance) > fuel) {
                    vehicleReply.setPerformative(ACLMessage.REFUSE);
                    vehicleReply.setContent(Messages.NOT_ENOUGH_FUEL);
                    consecutiveRejections++;
                    LoggerHelper.get().logFuelInsuf(this.myAgent.getLocalName());
                } else {
                    acceptCfp(vehicleReply, cfp);
                }
            } catch (UnreadableException e) {
                e.printStackTrace();
            }
        }

        if(consecutiveRejections >= MAX_CONSECUTIVE_REJECTIONS) {
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

        if(content instanceof AcceptVehicle){
            AcceptVehicle acceptVehicleMsg = (AcceptVehicle) content;
            LoggerHelper.get().logAcceptProposal(this.myAgent.getLocalName(), coordinates);

            double distance = coordinates.getDistance(acceptVehicleMsg.getCoordinates());
            int duration = (acceptVehicleMsg.getAccidentDuration() + (int) Math.round(distance * 0.1));

            coordinates = acceptVehicleMsg.getCoordinates();
            fuel = (fuel-=calculateFuel(distance)) < 0 ? 0 : fuel;

            startEmergency(duration);
        }

        consecutiveRejections = 0;
        return null;
    }

    protected void acceptCfp(ACLMessage vehicleReply, ACLMessage cfp){
        vehicleReply.setPerformative(ACLMessage.PROPOSE);
            double value = 0;
            try {
                Point emergencyCoords = ((TowerRequest) cfp.getContentObject()).getCoordinates();
                value = coordinates.getDistance(emergencyCoords);
                // the higher the value, the better the vehicle is to go to the emergency.
                // value = -distance; if distance = 0, value is max.
                if (value == 0)
                    value = Integer.MAX_VALUE;
                else
                    value = -value;

            } catch (UnreadableException e) {
                e.printStackTrace();
            }

            try {
                vehicleReply.setContentObject(new VehicleResponse(value,numberEmployees));
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    protected void startEmergency(int duration) {
        occupied.set(true);
        executor.schedule(
                this::updateOccupied,
                duration,
                TimeUnit.MILLISECONDS
        );

        LoggerHelper.get().logOccupied(this.myAgent.getLocalName(), duration/1000);
    }

    protected void startRefueling() {
        refueling.set(true);
        executor.schedule(
                this::updateRefueling,
                REFUEL_DURATION,
                TimeUnit.MILLISECONDS
        );

        LoggerHelper.get().logNeedRefuel(this.myAgent.getLocalName(), fuel);
    }

    protected void updateOccupied() {
        occupied.set(false);
        LoggerHelper.get().logUnoccupied(this.myAgent.getLocalName(), fuel);

        if(fuel < getSpareFuelLevel()) {
            startRefueling();
        }
    }

    protected void updateRefueling() {
        refueling.set(false);
        fuel = getMaxFuel();

        LoggerHelper.get().logDoneRefuel(this.myAgent.getLocalName(), fuel);
    }

    protected int calculateFuel(double distance) {
        return (int)(distance * getFuelRate() * ( 1 + numberEmployees * EMPLOYEE_MULTIPLIER));
    }

    private int getRandomNumberEmployees() {
        return ThreadLocalRandom.current().nextInt(MIN_NUM_EMPLOYEES, MAX_NUM_EMPLOYEES + 1);
    }

    public abstract VehicleType getVehicleType();

    protected abstract int getMaxFuel();

    protected abstract int getFuelRate();

    protected abstract int getSpareFuelLevel();
}
