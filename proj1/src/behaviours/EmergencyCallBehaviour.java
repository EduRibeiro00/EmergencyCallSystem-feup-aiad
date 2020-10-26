package behaviours;

import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;

public class EmergencyCallBehaviour extends TickerBehaviour {

    public EmergencyCallBehaviour(Agent a, long period, ACLMessage request) {
        super(a, period);
    }

    @Override
    protected void onTick() {
        request.se
        // send random accident
    }
}
