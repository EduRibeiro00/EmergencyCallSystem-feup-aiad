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
import java.util.concurrent.ThreadLocalRandom;

public abstract class VehicleBehaviour extends ContractNetResponder {
    protected static final int MIN_NUM_EMPLOYEES= 1;
    protected static final int MAX_NUM_EMPLOYEES= 6;
    protected static final int SPARE_FUEL_LEVEL = 10;
    protected static final int REFUEL_DURATION = 20000;
    protected static final double EMPLOYEE_MULTIPLIER = 0.1;

    protected Point coordinates;
    protected int numberEmployees;
    protected int fuel;
    protected boolean occupied = false;
    protected boolean refueling = false;
    protected int duration = 0;
    private long activatedAt = Long.MAX_VALUE;
    private long refueledAt = Long.MAX_VALUE;
    private int consecutiveRejections = 0;

    public VehicleBehaviour(Agent agent, MessageTemplate msgTemp) {
        super(agent, msgTemp);
        coordinates = Point.genRandomPoint();
        numberEmployees = getRandomNumberEmployees();
        fuel = getMaxFuel();

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

        updateOccupied();
        updateRefueling();

        if(occupied) {
            vehicleReply.setPerformative(ACLMessage.REFUSE);
            vehicleReply.setContent(Messages.IS_OCCUPIED);
        }
        else if(refueling) {
            vehicleReply.setPerformative(ACLMessage.REFUSE);
            vehicleReply.setContent(Messages.IS_REFUELING);
        } else {
            try {
                Point emergencyCoords = ((TowerRequest) cfp.getContentObject()).getCoordinates();
                double distance = coordinates.getDistance(emergencyCoords);

                if(calculateFuel(distance) > fuel) {
                    vehicleReply.setPerformative(ACLMessage.REFUSE);
                    vehicleReply.setContent(Messages.NOT_ENOUGH_FUEL);
                } else {
                    acceptCfp(vehicleReply, cfp);
                }
            } catch (UnreadableException e) {
                e.printStackTrace();
            }

        }

        return vehicleReply;
    }


    @Override
    public void handleRejectProposal(ACLMessage cfp, ACLMessage propose, ACLMessage reject) {
        if(occupied)
            LoggerHelper.get().logRejectProposalOccupied(this.myAgent.getLocalName());
        else if(refueling)
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
            duration = (acceptVehicleMsg.getAccidentDuration() + (int) distance / 10) * 1000;
            coordinates = acceptVehicleMsg.getCoordinates();
            System.out.println("Vehicle will be occupied for:" + duration/1000 + " seconds");

            fuel -= calculateFuel(distance);
            if(fuel < 0) fuel = 0;

            if(fuel < SPARE_FUEL_LEVEL) {
                refueledAt = System.currentTimeMillis() + duration;
            }
        }

        occupied = true;
        activatedAt = System.currentTimeMillis();
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

    protected void updateOccupied() {
        long currentTime = System.currentTimeMillis();
        long activeFor = currentTime - activatedAt;
        if(activeFor>=0 && activeFor>=duration) {
            occupied = false;
            activatedAt = Long.MAX_VALUE;
        }
    }

    protected void updateRefueling() {
        long currentTime = System.currentTimeMillis();
        long refueledFor = currentTime - refueledAt;
        if(refueledFor>=0 && refueledFor>=REFUEL_DURATION) {
            refueling = false;
            refueledAt = Long.MAX_VALUE;
        } else if(refueledAt < Long.MAX_VALUE && !occupied) {
            refueling = true;
        }
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
}
