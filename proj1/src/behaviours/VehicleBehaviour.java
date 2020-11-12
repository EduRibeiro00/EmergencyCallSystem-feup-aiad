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
    protected Point coordinates;
    protected boolean occupied = false;
    protected int numberEmployees;
    protected final int MIN_NUM_EMPLOYEES= 1;
    protected final int MAX_NUM_EMPLOYEES= 6;
    protected int duration = 0;
    private long activatedAt = Long.MAX_VALUE;

    public VehicleBehaviour(Agent agent, MessageTemplate msgTemp) {
        super(agent, msgTemp);
        coordinates = Point.genRandomPoint();
        numberEmployees = getRandomNumberEmployees();

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
        if (occupied) {
            long activeFor = System.currentTimeMillis() - activatedAt;
            if(activeFor>=0 && activeFor>=duration){
                acceptCfp(vehicleReply, cfp);
                occupied = false;
                activatedAt = Long.MAX_VALUE;
            } else {
                vehicleReply.setPerformative(ACLMessage.REFUSE);
                vehicleReply.setContent(Messages.IS_OCCUPIED);
            }
        }else {
            acceptCfp(vehicleReply, cfp);
        }
        
        return vehicleReply;
    }


    @Override
    public void handleRejectProposal(ACLMessage cfp, ACLMessage propose, ACLMessage reject) {
        if(occupied)
            LoggerHelper.get().logRejectProposalOccupied(this.myAgent.getLocalName());
        else
            LoggerHelper.get().logRejectProposal(this.myAgent.getLocalName(), coordinates);
    }


    @Override
    public ACLMessage handleAcceptProposal(ACLMessage cfp, ACLMessage propose, ACLMessage accept) {

        if (accept.getPerformative() == ACLMessage.ACCEPT_PROPOSAL) {
            try {
                Object content = accept.getContentObject();
                if(content instanceof AcceptVehicle){
                    AcceptVehicle acceptVehicleMsg = (AcceptVehicle) content;
                    double distance = coordinates.getDistance(acceptVehicleMsg.getCoordinates());
                    duration = (acceptVehicleMsg.getAccidentDuration() + (int) distance / 10) * 1000;
                    coordinates = acceptVehicleMsg.getCoordinates();
                    System.out.println("Vehicle will be occupied for:" + duration/1000 + " seconds");
                }
            } catch (UnreadableException e) {
                e.printStackTrace();
            }

        }

        LoggerHelper.get().logAcceptProposal(this.myAgent.getLocalName(), coordinates);
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

    public abstract VehicleType getVehicleType();

    private int getRandomNumberEmployees() {
        return ThreadLocalRandom.current().nextInt(MIN_NUM_EMPLOYEES, MAX_NUM_EMPLOYEES + 1);
    }
}
