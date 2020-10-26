package behaviours;

import agents.ClientAgent;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import utils.Emergency;
import utils.EmergencyType;
import utils.Point;

import java.io.IOException;
import java.util.Random;

public class EmergencyCallBehaviour extends TickerBehaviour {

    private final int MIN_NUM_VEHICLES = 1;
    private final int MAX_NUM_VEHICLES = 3;
    private String targetAgentName;
    private ClientAgent agent;

    public EmergencyCallBehaviour(ClientAgent clientAgent, long period, String targetAgentName) {
        super(clientAgent, period);
        this.agent = clientAgent;
        this.targetAgentName = targetAgentName;
        System.out.println("TARGET: " + targetAgentName);
    }

    @Override
    protected void onTick() {
        ACLMessage request = new ACLMessage(ACLMessage.INFORM);
        request.addReceiver(new AID(targetAgentName, AID.ISLOCALNAME));

        EmergencyType randomEmergencyType = EmergencyType.values()[new Random().nextInt(EmergencyType.values().length)];
        Emergency emergency = new Emergency(randomEmergencyType, Point.genRandomPoint(), 1);
        try {
            request.setContentObject(emergency);
        } catch (IOException e) {
            e.printStackTrace();
        }

        agent.send(request);
        System.out.println("SENT MESSAGE!");
    }
}
