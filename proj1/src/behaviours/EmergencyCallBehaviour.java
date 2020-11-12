package behaviours;

import agents.ClientAgent;
import jade.core.AID;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import logs.LoggerHelper;
import utils.Emergency;
import utils.EmergencyType;
import utils.Point;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class EmergencyCallBehaviour extends TickerBehaviour {

    private final int MIN_NUM_VEHICLES = 1;
    private final int MAX_NUM_VEHICLES = 3;
    private AID controlTowerID;
    private final int MIN_DURATION= 15000;
    private final int MAX_DURATION = 20000;

    public EmergencyCallBehaviour(ClientAgent clientAgent, long period, AID controlTowerID) {
        super(clientAgent, period);
        this.controlTowerID = controlTowerID;
    }

    @Override
    protected void onTick() {
        ACLMessage request = new ACLMessage(ACLMessage.INFORM);
        request.addReceiver(controlTowerID);

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

        myAgent.send(request);
        LoggerHelper.get().logCreatedEmergency(emergency);
    }

    private EmergencyType getRandomEmergencyType() {
        int numEmergencyTypes = EmergencyType.values().length;
        int randomIndex = new Random().nextInt(numEmergencyTypes);
        return EmergencyType.values()[randomIndex];
    }

    private int getRandomNumberOfVehicles() {
        return ThreadLocalRandom.current().nextInt(MIN_NUM_VEHICLES, MAX_NUM_VEHICLES + 1);
    }
    
    private int getRandomAccidentDuration() {
        return ThreadLocalRandom.current().nextInt(MIN_DURATION, MAX_DURATION + 1);
    }
}
