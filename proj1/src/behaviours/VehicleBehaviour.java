package behaviours;

import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.ContractNetResponder;
import utils.AgentTypes.AgentType;
import static Messages.Messages.*;

import java.util.concurrent.ThreadLocalRandom;

public abstract class VehicleBehaviour extends ContractNetResponder {

    protected static int MIN_DISTANCE = 1;
    protected static int MAX_DISTANCE = 100;
    protected int distance;
    protected boolean occupied = false;

    public VehicleBehaviour(Agent agent, MessageTemplate msgTemp) {
        super(agent, msgTemp);
        distance = ThreadLocalRandom.current().nextInt(MIN_DISTANCE, MAX_DISTANCE + 1);


    }

    @Override
    public abstract ACLMessage handleCfp(ACLMessage cfp);

    @Override
    public abstract void handleRejectProposal(ACLMessage cfp, ACLMessage propose, ACLMessage reject);

    @Override
    public abstract ACLMessage handleAcceptProposal(ACLMessage cfp, ACLMessage propose, ACLMessage accept) ;

    public abstract AgentType getAgentType();
}
