package agents;

import behaviours.FiremanBehaviour;
import behaviours.InemBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class FiremanAgent extends VehicleAgent {

    public FiremanAgent(String name) {
        super(name);
    }
    @Override
    protected void setup() {
        addBehaviour(new FiremanBehaviour(this, getMt()));
    }
}
