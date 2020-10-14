package behaviours;

import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.ContractNetResponder;

import java.util.concurrent.ThreadLocalRandom;

public class VehicleBehaviour extends ContractNetResponder {

    private static int MIN_DISTANCE = 1;
    private static int MAX_DISTANCE = 100;
    private int distance;

    public VehicleBehaviour(Agent agent, MessageTemplate msgTemp) {
        super(agent, msgTemp);
        distance = ThreadLocalRandom.current().nextInt(MIN_DISTANCE, MAX_DISTANCE + 1);
        System.out.println("Vehicle created with distance = " + distance);
    }

    @Override
    protected ACLMessage handleCfp(ACLMessage cfp) {
        ACLMessage vehicleReply = cfp.createReply();
        vehicleReply.setPerformative(ACLMessage.PROPOSE);
        vehicleReply.setContent(String.valueOf(distance));
        return vehicleReply;
    }

    @Override
    protected void handleRejectProposal(ACLMessage cfp, ACLMessage propose, ACLMessage reject) {
        System.out.println("Tower did not accept my distance of " + distance);
    }

    @Override
    protected ACLMessage handleAcceptProposal(ACLMessage cfp, ACLMessage propose, ACLMessage accept) {
        System.out.println("Tower accepted my distance of " + distance + "!!");
        return null;
    }
}
