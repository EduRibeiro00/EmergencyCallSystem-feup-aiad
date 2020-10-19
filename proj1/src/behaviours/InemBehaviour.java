package behaviours;

import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.ContractNetResponder;

import java.util.concurrent.ThreadLocalRandom;

public class InemBehaviour extends VehicleBehaviour {

    public InemBehaviour(Agent agent, MessageTemplate msgTemp) {
        super(agent, msgTemp);
        System.out.println("Inem created with distance = " + distance);
    }
    @Override
    public ACLMessage handleCfp(ACLMessage cfp) {
        ACLMessage vehicleReply = cfp.createReply();
        vehicleReply.setPerformative(ACLMessage.PROPOSE);
        vehicleReply.setContent(String.valueOf(distance));
        return vehicleReply;
    }

    @Override
    public void handleRejectProposal(ACLMessage cfp, ACLMessage propose, ACLMessage reject) {
        System.out.println("Tower did not accept my distance of " + distance);
    }

    @Override
    public ACLMessage handleAcceptProposal(ACLMessage cfp, ACLMessage propose, ACLMessage accept) {
        System.out.println("Tower accepted my distance of " + distance + "!!");
        return null;
    }


}
