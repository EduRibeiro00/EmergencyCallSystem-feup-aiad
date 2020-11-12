package behaviours;

import logs.LoggerHelper;
import messages.InformStatus;
import agents.ControlTowerAgent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import jade.proto.ContractNetInitiator;
import messages.Messages;
import utils.Candidate;
import utils.Emergency;
import utils.Point;

import java.util.*;

// TODO: instead of adding a behaviour per emergency, make this behaviour cyclic and handle a queue of emergencies
public class EmergencyDispatcherBehaviour extends ContractNetInitiator {

    private double bestDistance;
    private Emergency emergency;
    private int numberVehicles;
    private List<ACLMessage> bestVehicleMsgs;
    private List<ACLMessage> otherVehicleMsgs = new ArrayList<>();
    private ControlTowerAgent agent;
    private int priority = 0;
    private PriorityQueue<Candidate> candidateQueue;

    public EmergencyDispatcherBehaviour(ControlTowerAgent agent, ACLMessage cfp, Emergency emergency, int numberVehicles, int priority) {
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
        this.candidateQueue = new PriorityQueue<>(new Comparator<Candidate>() {
            @Override
            public int compare(Candidate c1, Candidate c2) {
                if (c1.getDistance() < c2.getDistance()) return -1;
                if (c1.getDistance() > c2.getDistance()) return 1;
                return 0;
            }
        });
        bestVehicleMsgs.clear();
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
                            Point vehicleCoords = ((InformStatus) content).getCoordinates();
                            distance = vehicleCoords.getDistance(emergency.getCoordinates());
                            LoggerHelper.get().logReceiveVehiclePropose(
                                    vehicleMsg.getSender().getLocalName(),
                                    vehicleCoords,
                                    distance
                            );
                            acceptedVehicles++;
                            candidateQueue.add(new Candidate(distance, vehicleMsg));
                        }
                        break;
                    case (ACLMessage.REFUSE):
                        LoggerHelper.get().logReceiveVehicleRefuse(vehicleMsg.getSender().getLocalName());
                        otherVehicleMsgs.add(vehicleMsg);
                        continue;
                }
            } catch (UnreadableException e) {
                e.printStackTrace();
            }

        }

        while(candidateQueue.peek() != null && bestVehicleMsgs.size() < numberVehicles) {
            Candidate currentCandidate = candidateQueue.peek();
            candidateQueue.remove(currentCandidate);
            
        }


        sendRejectMsgs(acceptances);
        sendAcceptMsg(acceptances);

        if(acceptedVehicles < numberVehicles) {
            LoggerHelper.get().logInfo("Tower - will try to recruit vehicles from next type");
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
        LoggerHelper.get().logAcceptVehicle(
                bestVehicleMsg.getSender().getLocalName(),
                bestDistance
        );

        ACLMessage towerReply = bestVehicleMsg.createReply();
        towerReply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
        towerReply.setContent(Messages.ACCEPT_VEHICLE);
        acceptances.add(towerReply);
    }
}
