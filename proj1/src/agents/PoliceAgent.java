package agents;

import behaviours.InemBehaviour;
import behaviours.PoliceBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class PoliceAgent extends VehicleAgent {

    public PoliceAgent(String name) {
        super(name);
    }

    @Override
    protected void setup() {
        addBehaviour(new PoliceBehaviour(this, getMt()));
    }
}
