package agents;

import behaviours.InemBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class InemAgent extends VehicleAgent {


    public InemAgent(String name) {
        super(name);
    }

    @Override
    protected void setup() {
        addBehaviour(new InemBehaviour(this, getMt()));
    }
}
