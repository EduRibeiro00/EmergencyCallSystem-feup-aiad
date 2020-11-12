package behaviours;

import logs.LoggerHelper;
import messages.VehicleResponse;
import messages.AcceptVehicle;
import agents.ControlTowerAgent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import jade.proto.ContractNetInitiator;
import messages.Messages;
import utils.Candidate;
import utils.Emergency;

import java.util.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class EmergencyDispatcherBehaviour extends ContractNetInitiator {

    private double bestValue;
    private Emergency emergency;
    private int numberVehicles;
    private List<ACLMessage> bestVehicleMsgs = new ArrayList<>();
    private List<ACLMessage> otherVehicleMsgs = new ArrayList<>();
    private ControlTowerAgent agent;
    private PriorityQueue<Candidate> candidateQueue;
    private int priority;

    public EmergencyDispatcherBehaviour(ControlTowerAgent agent, ACLMessage cfp, Emergency emergency, int numberVehicles, int priority) {
        super(agent, cfp);
        resetControlTowerInfo();
        this.emergency = emergency;
        this.numberVehicles = numberVehicles;
        this.agent = agent;
        this.priority = priority;
    }

    private void resetControlTowerInfo() {
        this.bestValue = Integer.MIN_VALUE;
        this.emergency = null;
        this.candidateQueue = new PriorityQueue<>((c1, c2) -> {
            if (c1.getValue() < c2.getValue()) return -1;
            if (c1.getValue() > c2.getValue()) return 1;
            return 0;
        });
        this.bestVehicleMsgs.clear();
        this.otherVehicleMsgs.clear();
    }

    @Override
    protected void handleAllResponses(Vector responses, Vector acceptances) {
        int acceptedVehicles = 0;

        for (Object response : responses) {
            ACLMessage vehicleMsg = (ACLMessage) response;
            double value = 0;

            try {
                switch (vehicleMsg.getPerformative()){
                    case (ACLMessage.PROPOSE):
                        Object content = vehicleMsg.getContentObject();
                        if(content instanceof VehicleResponse) {
                            value = ((VehicleResponse) content).getValue();
                            LoggerHelper.get().logReceiveVehiclePropose(
                                    vehicleMsg.getSender().getLocalName(),
                                    value
                            );
                            acceptedVehicles++;
                            candidateQueue.add(new Candidate(value, vehicleMsg));
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
            bestVehicleMsgs.add(currentCandidate.getMessage());
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
        for (ACLMessage bestVehicleMsg : bestVehicleMsgs) {
            LoggerHelper.get().logAcceptVehicle(
                    bestVehicleMsg.getSender().getLocalName(),
                    bestValue
            );

            ACLMessage towerReply = bestVehicleMsg.createReply();
            towerReply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
            //towerReply.setContent(Messages.ACCEPT_VEHICLE);
            try {
                towerReply.setContentObject(new AcceptVehicle(emergency.getCoordinates(),emergency.getDuration()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            acceptances.add(towerReply);
        }
    }
}
