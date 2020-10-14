package agents;

import behaviours.ControlTowerBehaviour;
import jade.core.AID;
import jade.core.Agent;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;

public class ControlTowerAgent extends Agent {

    private ACLMessage cfp = new ACLMessage(ACLMessage.CFP);

    @Override
    protected void setup() {
        addVehicles(getArguments());
        cfp.setProtocol(FIPANames.InteractionProtocol.FIPA_CONTRACT_NET);
        addBehaviour(new ControlTowerBehaviour(this, cfp));
    }

    private void addVehicles(Object[] args) {
        for (Object vehicle : args) {
            cfp.addReceiver(new AID((String) vehicle, AID.ISLOCALNAME));
        }
    }
}
