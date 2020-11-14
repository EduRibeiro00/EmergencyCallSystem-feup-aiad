package agents;

import behaviours.ControlTowerBehaviour;
import jade.core.Agent;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import logs.LoggerHelper;
import messages.TowerRequest;
import utils.*;

import java.io.IOException;
import java.util.ArrayList;

public class ControlTowerAgent extends Agent {
    private static final String DF_NAME = "control-tower";

    private ControlTowerBehaviour behaviour;

    public ControlTowerAgent() {
        this.behaviour = new ControlTowerBehaviour(this);
    }

    @Override
    protected void setup() {
        DFUtils.registerInDF(this, DF_NAME);
        addBehaviour(this.behaviour);
    }

    @Override
    protected void takeDown() {
        DFUtils.deregisterFromDF(this);
    }

    public void handleEmergency(Emergency emergency) {
        LoggerHelper.get().logReceivedEmergency(emergency);
        handleEmergency(emergency, emergency.getNumberVehicles() ,0);
    }

    public void handleEmergency(Emergency emergency, int numberVehicles, int priority) {
        ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
        if (!addCorrespondingVehicles(cfp, emergency, numberVehicles, priority)) return;
        cfp.setProtocol(FIPANames.InteractionProtocol.FIPA_CONTRACT_NET);
        try {
            cfp.setContentObject(new TowerRequest(emergency.getCoordinates()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.behaviour.dispatch(cfp, emergency, numberVehicles, priority);
    }

    private boolean addCorrespondingVehicles(ACLMessage cfp, Emergency emergency, int numberVehicles, int priority) {
        ArrayList<VehicleType> vehiclePriorities =
                EmergencyVehiclePriorities.vehiclePriorities.get(emergency.getEmergencyType());

        // no more vehicles available
        if (priority >= vehiclePriorities.size()) {
            LoggerHelper.get().logNotEnoughVehicles(emergency);
            return false;
        }
    
        VehicleType vehicleType = vehiclePriorities.get(priority);
        DFAgentDescription[] vehicles = DFUtils.fetchFromDF(this, vehicleType.getDFName());
        // no vehicles of a certain type
        if (vehicles == null || vehicles.length < 1) {
            LoggerHelper.get().logInfo("[" +emergency.getId() + "] Tower - no vehicles; will try to recruit vehicles from next type");
            this.handleEmergency(emergency, numberVehicles, ++priority);
            return false;
        }

        LoggerHelper.get().logSendingCfpTo(vehicleType.getDFName());
        for (DFAgentDescription vehicle : vehicles) {
            cfp.addReceiver(vehicle.getName());
        }
        return true;
    }

    public static String getDFName() {
        return DF_NAME;
    }
}
