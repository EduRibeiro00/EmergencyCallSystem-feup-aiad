package agents;

import behaviours.ControlTowerBehaviour;
import jade.core.AID;
import jade.core.Agent;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import  utils.Emergencies.EmergencyType;
import utils.EmergencyAgent;

public class ControlTowerAgent extends Agent {

    VehicleAgent[] vehicleAgents;

    public ControlTowerAgent(VehicleAgent[] vehicleAgents) {
        this.vehicleAgents = vehicleAgents;
    }


    @Override
    protected void setup() {

    }

    private void addVehicles(ACLMessage cfp) {
        for (VehicleAgent vehicle : vehicleAgents) {
            cfp.addReceiver(new AID(vehicle.getVehicleName(), AID.ISLOCALNAME));
        }
    }

    public void handleAccident(EmergencyType emergencyType,int numberVehicles,int priority){
        ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
        addCorrespondingVehicles(cfp,emergencyType,priority);
        cfp.setProtocol(FIPANames.InteractionProtocol.FIPA_CONTRACT_NET);
        addBehaviour(new ControlTowerBehaviour(this, cfp, emergencyType,numberVehicles,priority));
    }

    private void addCorrespondingVehicles(ACLMessage cfp,EmergencyType emergencyType,int priority){
        for (VehicleAgent vehicle : vehicleAgents) {
            if (vehicle.getType() == EmergencyAgent.emergencyAgent.get(emergencyType).get(priority)){
                cfp.addReceiver(new AID(vehicle.getVehicleName(), AID.ISLOCALNAME));
            }
        }
    }
}
