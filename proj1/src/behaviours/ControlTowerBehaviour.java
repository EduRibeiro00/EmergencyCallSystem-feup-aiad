package behaviours;

import Messages.InformStatus;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import jade.proto.ContractNetInitiator;
import utils.AgentTypes;
import utils.Emergencies.EmergencyType;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import static utils.AgentTypes.AgentType.*;

public class ControlTowerBehaviour extends ContractNetInitiator {

    private static String REJECT_CONTENT = "Will not recruit this vehicle";
    private static String ACCEPT_CONTENT = "Recruiting this vehicle";

    private int bestDistance;
    private EmergencyType emergencyType;
    private ACLMessage bestVehicleMsg;
    private List<ACLMessage> otherVehicleMsgs = new ArrayList<>();
    private int numberVehicles;

    public ControlTowerBehaviour(Agent agent, ACLMessage cfp, EmergencyType emergencyType,int numberVehicles) {
        super(agent, cfp);
        resetControlTowerInfo();
        this.emergencyType = emergencyType;

    }

    private void resetControlTowerInfo() {
        this.bestDistance = -1;
        this.emergencyType = null;
        otherVehicleMsgs.clear();
    }

    @Override
    protected void handleAllResponses(Vector responses, Vector acceptances) {

        for (Object response : responses) {
            ACLMessage vehicleMsg = (ACLMessage) response;
            int distance = 0;
            AgentTypes.AgentType agentType = null;
            try {
                Object content = vehicleMsg.getContentObject();
                switch (vehicleMsg.getPerformative()){
                    case (ACLMessage.PROPOSE):
                        if(content instanceof InformStatus){
                            distance = ((InformStatus) content).getDistance();
                            agentType = ((InformStatus) content).getType();
                            System.out.println(
                                    "Received message from vehicle " +
                                            vehicleMsg.getSender().getLocalName() +
                                            ", distance = " +
                                            distance
                            );
                        }
                }
            } catch (UnreadableException e) {
                e.printStackTrace();
            }

            if ((bestDistance == -1 || bestDistance > distance) && isCompatible(emergencyType,agentType)) {
                bestDistance = distance;
                if (bestVehicleMsg != null) otherVehicleMsgs.add(bestVehicleMsg);

                bestVehicleMsg = vehicleMsg;
            }
            else otherVehicleMsgs.add(vehicleMsg);

        }

        if (bestDistance == -1 || bestVehicleMsg == null)  return;

        sendRejectMsgs(acceptances);
        sendAcceptMsg(acceptances);
    }

    private void sendRejectMsgs(Vector acceptances) {
        for (ACLMessage vehicleMsg : otherVehicleMsgs) {
            ACLMessage towerReply = vehicleMsg.createReply();
            towerReply.setPerformative(ACLMessage.REJECT_PROPOSAL);
            towerReply.setContent(REJECT_CONTENT);
            acceptances.add(towerReply);
        }
    }

    private void sendAcceptMsg(Vector acceptances) {
        System.out.println(
                "Going to accept vehicle " +
                bestVehicleMsg.getSender().getLocalName() +
                ", distance = " +
                        bestDistance
        );

        ACLMessage towerReply = bestVehicleMsg.createReply();
        towerReply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
        towerReply.setContent(ACCEPT_CONTENT);
        acceptances.add(towerReply);
    }

    public static boolean isCompatible(EmergencyType emergencyType, AgentTypes.AgentType agentType){

        if (emergencyType == null) return false;
        switch (emergencyType){
            case Fire:
                if(agentType == FIREMAN) return true;
                break;
            case Robbery:
                if(agentType == POLICE) return true;
                break;
            case Accident:
                if(agentType == INEM) return true;
                break;
            default: return false;

        }
        return false;
    }
}
