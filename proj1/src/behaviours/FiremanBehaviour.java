package behaviours;

import Messages.InformStatus;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.ContractNetResponder;
import utils.AgentTypes;

import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

import static Messages.Messages.IS_OCCUPIED;


public class FiremanBehaviour extends VehicleBehaviour {

    public FiremanBehaviour(Agent agent, MessageTemplate msgTemp) {
        super(agent, msgTemp);
        System.out.println("Fireman created with distance = " + distance);
    }
    @Override
    public ACLMessage handleCfp(ACLMessage cfp) {
        ACLMessage vehicleReply = cfp.createReply();
        if (!occupied) {
            vehicleReply.setPerformative(ACLMessage.PROPOSE);
            try {
                vehicleReply.setContentObject(new InformStatus(distance,occupied));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            vehicleReply.setPerformative(ACLMessage.REFUSE);
            vehicleReply.setContent(IS_OCCUPIED);
        }
        return vehicleReply;

    }

    @Override
    public void handleRejectProposal(ACLMessage cfp, ACLMessage propose, ACLMessage reject) {
        if(occupied) System.out.println("Tower did not accept because I was occupied");
        else System.out.println("Tower did not accept my distance of " + distance);
    }

    @Override
    public ACLMessage handleAcceptProposal(ACLMessage cfp, ACLMessage propose, ACLMessage accept) {
        occupied = true;
        System.out.println("Tower accepted my distance of " + distance + "!!");
        return null;
    }

    @Override
    public AgentTypes.AgentType getAgentType() {
        return AgentTypes.AgentType.FIREMAN;
    }


}
