package agents;

import behaviours.EmergencyCallBehaviour;
import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;

public class ClientAgent extends Agent {

    private ControlTowerAgent controlTower;
    private ACLMessage request = new ACLMessage(ACLMessage.REQUEST);

    public ClientAgent(ControlTowerAgent controlTower) {
        this.controlTower = controlTower;
        this.request.addReceiver(new AID(controlTower.getName(), AID.ISLOCALNAME)));
    }

    @Override
    protected void setup() {
        addBehaviour(new EmergencyCallBehaviour(this, 1000, request));
    }
}
