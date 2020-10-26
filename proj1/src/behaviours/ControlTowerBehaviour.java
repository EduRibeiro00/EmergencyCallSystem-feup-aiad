package behaviours;

import messages.InformStatus;
import agents.ControlTowerAgent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import jade.proto.ContractNetInitiator;
import messages.Messages;
import utils.Emergency;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

// TODO: instead of adding a behaviour per emergency, make this behaviour cyclic and handle a queue of emergencies
public class ControlTowerBehaviour extends ContractNetInitiator {

    private double bestDistance;
    private Emergency emergency;
    private int numberVehicles;
    private  ACLMessage bestVehicleMsg;
    private List<ACLMessage> otherVehicleMsgs = new ArrayList<>();
    private ControlTowerAgent agent;
    private int priority = 0;

    public ControlTowerBehaviour(ControlTowerAgent agent, ACLMessage cfp, Emergency emergency, int numberVehicles, int priority) {
        super(agent, cfp);
        resetControlTowerInfo();
        this.emergency = emergency;
        this.numberVehicles = numberVehicles;
        this.agent = agent;
        this.priority = priority;
    }

    private void resetControlTowerInfo() {
        this.bestDistance = -1.0;
        this.emergency = null;
        otherVehicleMsgs.clear();
    }

    @Override
    protected void handleAllResponses(Vector responses, Vector acceptances) {
        int acceptedVehicles = 0;
        for (Object response : responses) {
            ACLMessage vehicleMsg = (ACLMessage) response;
            double distance = 0;

            try {
                switch (vehicleMsg.getPerformative()){
                    case (ACLMessage.PROPOSE):
                        Object content = vehicleMsg.getContentObject();
                        if(content instanceof InformStatus) {
                            // calc distance between vehicle and emergency
                            distance = ((InformStatus) content).getCoordinates().getDistance(emergency.getCoordinates());
                            System.out.println(
                                "Received message from vehicle " +
                                        vehicleMsg.getSender().getLocalName() +
                                        ", distance = " +
                                        distance
                            );
                        }
                        break;
                    case (ACLMessage.REFUSE):
                        System.out.println(vehicleMsg.getSender().getLocalName() + " was occupied");
                        otherVehicleMsgs.add(vehicleMsg);
                        continue;
                }
            } catch (UnreadableException e) {
                e.printStackTrace();
            }

            // TODO: falta selecionar os x melhores veiculos para irem la; por agora estamos so a selecionar o melhor
            if ((bestDistance < 0 || bestDistance > distance)) {
                bestDistance = distance;
                if (bestVehicleMsg != null)
                    otherVehicleMsgs.add(bestVehicleMsg);

                bestVehicleMsg = vehicleMsg;
            }
            else otherVehicleMsgs.add(vehicleMsg);
        }

        if (bestDistance < 0 || bestVehicleMsg == null)  return;

        sendRejectMsgs(acceptances);
        sendAcceptMsg(acceptances);

        if(acceptedVehicles < numberVehicles) {
            System.out.println("Will try to recruit vehicles from next type");
            this.priority++;
            agent.handleEmergency(emergency, numberVehicles - acceptedVehicles, this.priority);
        }
    }

    private void sendRejectMsgs(Vector acceptances) {
        for (ACLMessage vehicleMsg : otherVehicleMsgs) {
            ACLMessage towerReply = vehicleMsg.createReply();
            towerReply.setPerformative(ACLMessage.REJECT_PROPOSAL);
            towerReply.setContent(Messages.REJECT_VEHICLE);
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
        towerReply.setContent(Messages.ACCEPT_VEHICLE);
        acceptances.add(towerReply);
    }
}
