package behaviours;

import logs.LoggerHelper;
import messages.VehicleResponse;
import messages.AcceptVehicle;
import agents.ControlTowerAgent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import sajas.proto.ContractNetInitiator;
import messages.Messages;
import utils.Candidate;
import utils.Emergency;

import java.util.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class EmergencyDispatcherBehaviour extends ContractNetInitiator {

    private final Emergency emergency;
    private final int numberVehicles;
    private final List<ACLMessage> acceptMsgs;
    private final List<ACLMessage> rejectMsgs;
    private final ControlTowerAgent agent;
    private final PriorityQueue<Candidate> candidateQueue;
    private final int priority;
    private final int numTries;

    public EmergencyDispatcherBehaviour(ControlTowerAgent agent, ACLMessage cfp, Emergency emergency, int numberVehicles, int priority, int numTries) {
        super(agent, cfp);
        this.emergency = emergency;
        this.numberVehicles = numberVehicles;
        this.agent = agent;
        this.priority = priority;
        this.numTries = numTries;

        this.acceptMsgs = new ArrayList<>();
        this.rejectMsgs = new ArrayList<>();

        // vehicle with highest value will be at the front of the queue
        this.candidateQueue = new PriorityQueue<>(Comparator.comparingDouble(Candidate::getValue).reversed());
    }

    @Override
    protected void handleAllResponses(Vector responses, Vector acceptances) {
        int acceptedVehicles = 0;

        for (Object response : responses) {
            ACLMessage vehicleMsg = (ACLMessage) response;

            try {
                switch (vehicleMsg.getPerformative()){
                    case (ACLMessage.PROPOSE):
                        Object content = vehicleMsg.getContentObject();
                        if(content instanceof VehicleResponse) {
                            double value = ((VehicleResponse) content).getValue();
                            LoggerHelper.get().logReceiveVehiclePropose(
                                    vehicleMsg.getSender().getLocalName(),
                                    value
                            );
                            candidateQueue.add(new Candidate(value, vehicleMsg));
                        }
                        break;
                    case (ACLMessage.REFUSE):
                        LoggerHelper.get().logReceiveVehicleRefuse(vehicleMsg.getSender().getLocalName());
                }
            } catch (UnreadableException e) {
                e.printStackTrace();
            }
        }

        while(acceptedVehicles < numberVehicles) {
            Candidate currentCandidate = candidateQueue.poll();
            if (currentCandidate == null) break;

            LoggerHelper.get().logAcceptVehicle(
                    emergency.getId(),
                    currentCandidate.getMessage().getSender().getLocalName(),
                    currentCandidate.getValue()
            );

            acceptMsgs.add(currentCandidate.getMessage());
            acceptedVehicles++;
        }

        for (Candidate candidate : candidateQueue) {
            rejectMsgs.add(candidate.getMessage());
        }

        sendRejectMsgs(acceptances);
        sendAcceptMsgs(acceptances);

        if(acceptedVehicles < numberVehicles) {
            LoggerHelper.get().logInfo(
                    (LoggerHelper.get().simpleLog() ?
                            (LoggerHelper.get().getIDOut(emergency.getId())) : "")+
                            "Tower - will try to recruit vehicles from next type");
            agent.handleEmergency(emergency, numberVehicles - acceptedVehicles, this.priority + 1, numTries);
        }
    }

    @Override
    protected void handleInform(ACLMessage inform) {
        agent.getMainBehaviour().removeSubBehaviour(this);
    }

    @Override
    protected void handleAllResultNotifications(Vector resultNotifications) {
        agent.getMainBehaviour().removeSubBehaviour(this);
    }

    private void sendRejectMsgs(Vector acceptances) {
        for (ACLMessage vehicleMsg : rejectMsgs) {
            ACLMessage towerReply = vehicleMsg.createReply();
            towerReply.setPerformative(ACLMessage.REJECT_PROPOSAL);
            towerReply.setContent(Messages.REJECT_VEHICLE);
            acceptances.add(towerReply);
        }
    }

    private void sendAcceptMsgs(Vector acceptances) {
        for (ACLMessage bestVehicleMsg : acceptMsgs) {
            ACLMessage towerReply = bestVehicleMsg.createReply();
            towerReply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
            towerReply.setContent(Messages.ACCEPT_VEHICLE);
            try {
                towerReply.setContentObject(new AcceptVehicle(emergency.getCoordinates(), emergency.getDuration()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            acceptances.add(towerReply);
        }
    }
}
