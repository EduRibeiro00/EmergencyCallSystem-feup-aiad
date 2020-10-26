package agents;

import behaviours.FiremanBehaviour;
import behaviours.InemBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import utils.AgentTypes;

public class FiremanAgent extends VehicleAgent {

    public FiremanAgent(String name) {
        super(name);
    }
    @Override
    protected void setup() {
        addBehaviour(new FiremanBehaviour(this, getMt()));
    }

    @Override
    public AgentTypes.AgentType getType() {return AgentTypes.AgentType.FIREMAN;}
}
