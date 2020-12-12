package behaviours;

import GUI.Results;
import agents.ControlTowerAgent;
import sajas.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import utils.Emergency;

public class EmergencyReceiverBehaviour extends CyclicBehaviour {

    private final ControlTowerAgent agent;

    public EmergencyReceiverBehaviour(ControlTowerAgent controlTowerAgent) {
        this.agent = controlTowerAgent;
    }

    @Override
    public void action() {
        MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
        ACLMessage requestMsg = this.agent.receive(mt);

        if(requestMsg != null) {
            try {
                Object content = requestMsg.getContentObject();
                if(content instanceof Emergency) {
                    Emergency emergency = (Emergency) content;
                    Results.incrementEmergencies();
                    this.agent.handleEmergency(emergency);

                }
            } catch (UnreadableException ignore) {}
        } else {
            block();
        }
    }
}
