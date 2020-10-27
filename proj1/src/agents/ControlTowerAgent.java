package agents;

import behaviours.ControlTowerBehaviour;
import jade.core.AID;
import jade.core.Agent;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import logs.LoggerHelper;
import utils.Emergency;
import utils.EmergencyVehiclePriorities;
import utils.VehicleType;

import java.util.ArrayList;

// TODO: instead of the tower having the vehicle agents, the vehicles should be registered in the DF for the tower to access
public class ControlTowerAgent extends Agent {

    VehicleAgent[] vehicleAgents;

    public ControlTowerAgent(VehicleAgent[] vehicleAgents) {
        this.vehicleAgents = vehicleAgents;
    }

    @Override
    protected void setup() {

    }

    public void handleEmergency(Emergency emergency) {
        LoggerHelper.get().logReceivedEmergency(emergency);
        handleEmergency(emergency, emergency.getNumberVehicles() ,0);
    }

    public void handleEmergency(Emergency emergency, int numberVehicles, int priority) {
        ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
        addCorrespondingVehicles(cfp, emergency, priority);
        cfp.setProtocol(FIPANames.InteractionProtocol.FIPA_CONTRACT_NET);
        addBehaviour(new ControlTowerBehaviour(this, cfp, emergency, numberVehicles, priority));
    }

    private void addCorrespondingVehicles(ACLMessage cfp, Emergency emergency, int priority) {
        ArrayList<VehicleType> vehiclePriorities =
                EmergencyVehiclePriorities.vehiclePriorities.get(emergency.getEmergencyType());

        // no more vehicles available
        if (priority >= vehiclePriorities.size()) {
            LoggerHelper.get().logNotEnoughVehicles(emergency);
            return;
        }

        for (VehicleAgent vehicle : vehicleAgents) {
            if (vehicle.getType() == EmergencyVehiclePriorities.vehiclePriorities.get(emergency.getEmergencyType()).get(priority)){
                cfp.addReceiver(new AID(vehicle.getVehicleName(), AID.ISLOCALNAME));
            }
        }
    }
}
