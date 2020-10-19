package agents;

import behaviours.ControlTowerBehaviour;
import jade.core.AID;
import jade.core.Agent;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;

public class ControlTowerAgent extends Agent {

    VehicleAgent[] vehicleAgents;

    public ControlTowerAgent(VehicleAgent[] vehicleAgents) {
        this.vehicleAgents = vehicleAgents;
    }

    private ACLMessage cfp = new ACLMessage(ACLMessage.CFP);

    @Override
    protected void setup() {
        addVehicles(vehicleAgents);
        cfp.setProtocol(FIPANames.InteractionProtocol.FIPA_CONTRACT_NET);
        addBehaviour(new ControlTowerBehaviour(this, cfp));
    }

    private void addVehicles(VehicleAgent[] args) {
        for (VehicleAgent vehicle : args) {
            cfp.addReceiver(new AID(vehicle.getVehicleName(), AID.ISLOCALNAME));
        }
    }
}
