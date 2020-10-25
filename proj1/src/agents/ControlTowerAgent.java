package agents;

import behaviours.ControlTowerBehaviour;
import jade.core.AID;
import jade.core.Agent;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import  utils.Emergencies.EmergencyType;

public class ControlTowerAgent extends Agent {

    VehicleAgent[] vehicleAgents;

    public ControlTowerAgent(VehicleAgent[] vehicleAgents) {
        this.vehicleAgents = vehicleAgents;
    }


    @Override
    protected void setup() {

    }

    private void addVehicles(VehicleAgent[] args,ACLMessage cfp) {
        for (VehicleAgent vehicle : args) {
            cfp.addReceiver(new AID(vehicle.getVehicleName(), AID.ISLOCALNAME));
        }
    }

    public void handleAccident(EmergencyType emergencyType,int numberVehicles){
        ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
        addVehicles(vehicleAgents,cfp);
        cfp.setProtocol(FIPANames.InteractionProtocol.FIPA_CONTRACT_NET);
        addBehaviour(new ControlTowerBehaviour(this, cfp, emergencyType,numberVehicles));
    }
}
