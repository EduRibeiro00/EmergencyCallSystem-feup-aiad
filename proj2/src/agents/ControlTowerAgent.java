package agents;

import GUI.Results;
import GUI.GUI;
import GUI.EdgeEmerVehicle;
import behaviours.ControlTowerBehaviour;
import repast.RepastLauncher;
import sajas.core.Agent;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import logs.LoggerHelper;
import messages.TowerRequest;
import uchicago.src.sim.network.DefaultDrawableNode;
import utils.*;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ControlTowerAgent extends Agent {
    private static final String DF_NAME = "control-tower";
    private static final int EMERGENCY_MAX_TRIES = 3;
    private final int WAIT_BETWEEN_TRIES;

    private final ControlTowerBehaviour behaviour;
    private final ScheduledThreadPoolExecutor executor;

    public ControlTowerAgent(int WAIT_BETWEEN_TRIES) {
        this.WAIT_BETWEEN_TRIES = WAIT_BETWEEN_TRIES;
        this.behaviour = new ControlTowerBehaviour(this);
        this.executor = new ScheduledThreadPoolExecutor(3);
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
        RepastLauncher.getEmergencyMap().put(emergency.getId(), emergency);
        GUI.generateEmergencyNode(emergency);
        LoggerHelper.get().logReceivedEmergency(emergency);
        handleEmergency(emergency, emergency.getNumberVehicles() ,0, 1);
    }

    public void handleEmergency(Emergency emergency, int numberVehicles, int priority, int numTries) {
        ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
        if (!addCorrespondingVehicles(cfp, emergency, numberVehicles, priority, numTries)) return;
        cfp.setProtocol(FIPANames.InteractionProtocol.FIPA_CONTRACT_NET);
        try {
            cfp.setContentObject(new TowerRequest(emergency.getCoordinates()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.behaviour.dispatch(cfp, emergency, numberVehicles, priority, numTries);
    }

    private boolean addCorrespondingVehicles(ACLMessage cfp, Emergency emergency, int numberVehicles, int priority, int numTries) {
        ArrayList<VehicleType> vehiclePriorities =
                EmergencyVehiclePriorities.vehiclePriorities.get(emergency.getEmergencyType());

        // no more vehicles available
        if (priority >= vehiclePriorities.size()) {
            if (numTries >= EMERGENCY_MAX_TRIES) {
                String emergLabel = GUI.getEmergencyLabel(emergency.getId());
                Results.incrementFailedEmergencies();

                if(GUI.getNode(emergLabel).getNumInEdges() == 0) {
                    GUI.removeNode(emergLabel);
                }
                else{
                    List<EdgeEmerVehicle> edges = GUI.getEmergByID(emergency).getInEdges();
                    for (int i = 0; i < edges.size(); i++) {
                         edges.get(i).getVehicleAgent().setReachedMaxTries(true);
                    }
                }

                LoggerHelper.get().logMaxRetriesEmergency(emergency, EMERGENCY_MAX_TRIES);
            }
            else {
                LoggerHelper.get().logNotEnoughVehicles(emergency);
                executor.schedule(
                        () -> this.handleEmergency(emergency, numberVehicles, 0, numTries + 1),
                        WAIT_BETWEEN_TRIES,
                        TimeUnit.MILLISECONDS
                );
            }
            return false;
        }
    
        VehicleType vehicleType = vehiclePriorities.get(priority);
        DFAgentDescription[] vehicles = DFUtils.fetchFromDF(this, vehicleType.getDFName());
        // no vehicles of a certain type
        if (vehicles == null || vehicles.length < 1) {
            LoggerHelper.get().logInfo("[" +emergency.getId() + "] Tower - no vehicles; will try to recruit vehicles from next type");
            this.handleEmergency(emergency, numberVehicles, priority + 1, numTries);
            return false;
        }


        //Aqui é quando ele consegue à primeira?

        LoggerHelper.get().logSendingCfpTo(vehicleType.getDFName());
        for (DFAgentDescription vehicle : vehicles) {
            cfp.addReceiver(vehicle.getName());
        }
        return true;
    }

    public static String getDFName() {
        return DF_NAME;
    }

    public ControlTowerBehaviour getMainBehaviour() {
        return behaviour;
    }
}
