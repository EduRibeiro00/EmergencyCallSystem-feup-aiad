package agents;

import behaviours.ControlTowerBehaviour;
import jade.core.AID;
import jade.core.Agent;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import utils.AgentTypes;

import static utils.Emergencies.EmergencyType.*;
import  utils.Emergencies.EmergencyType;

public class ControlTowerAgent extends Agent {

    VehicleAgent[] vehicleAgents;
    EmergencyType emergencyType;

    public ControlTowerAgent(VehicleAgent[] vehicleAgents,EmergencyType emergencyType) {
        this.vehicleAgents = vehicleAgents;
        this.emergencyType = emergencyType;
    }

    private ACLMessage cfp = new ACLMessage(ACLMessage.CFP);

    @Override
    protected void setup() {
        addVehicles(vehicleAgents);
        cfp.setProtocol(FIPANames.InteractionProtocol.FIPA_CONTRACT_NET);
        addBehaviour(new ControlTowerBehaviour(this, cfp, emergencyType));
    }

    private void addVehicles(VehicleAgent[] args) {
        for (VehicleAgent vehicle : args) {
            cfp.addReceiver(new AID(vehicle.getVehicleName(), AID.ISLOCALNAME));
        }
    }
}
