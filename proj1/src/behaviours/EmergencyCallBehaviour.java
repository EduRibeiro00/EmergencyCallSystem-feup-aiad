package behaviours;

import agents.ClientAgent;
import jade.core.AID;
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
    private final int MIN_DURATION= 15000;
    private final int MAX_DURATION = 20000;
    private String targetAgentName;
    private ClientAgent agent;

    public EmergencyCallBehaviour(ClientAgent clientAgent, long period, String targetAgentName) {
        super(clientAgent, period);
        this.agent = clientAgent;
        this.targetAgentName = targetAgentName;
    }

    @Override
    protected void onTick() {
        ACLMessage request = new ACLMessage(ACLMessage.INFORM);
        request.addReceiver(new AID(targetAgentName, AID.ISLOCALNAME));

        Emergency emergency = new Emergency(
                getRandomEmergencyType(),
                Point.genRandomPoint(),
                getRandomNumberOfVehicles(),
                getRandomAccidentDuration());

        try {
            request.setContentObject(emergency);
        } catch (IOException e) {
            e.printStackTrace();
        }

        agent.send(request);
    }

    private EmergencyType getRandomEmergencyType() {
        int numEmergencyTypes = EmergencyType.values().length;
        int randomIndex = new Random().nextInt(numEmergencyTypes);
        return EmergencyType.values()[randomIndex];
    }

    private int getRandomNumberOfVehicles() {
        int randomNumber = new Random().nextInt(MAX_NUM_VEHICLES + MIN_NUM_VEHICLES) - MIN_NUM_VEHICLES;
        return randomNumber;
    }
    private int getRandomAccidentDuration() {
        int randomNumber = new Random().nextInt(MAX_DURATION - MIN_DURATION) + MIN_DURATION;
        return randomNumber;
    }
}