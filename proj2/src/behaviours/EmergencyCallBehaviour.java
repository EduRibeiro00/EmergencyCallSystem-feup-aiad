package behaviours;

import agents.ClientAgent;
import jade.core.AID;
import sajas.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import logs.LoggerHelper;
import utils.Emergency;
import utils.EmergencyType;
import utils.Point;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class EmergencyCallBehaviour extends SimpleBehaviour {
    private final ScheduledThreadPoolExecutor executor;

    private AID controlTowerID;
    private int currentID = 0;
    private long period;

    private int MIN_NUM_VEHICLES;
    private int MAX_NUM_VEHICLES;
    private int MIN_DURATION;
    private int MAX_DURATION;

    public EmergencyCallBehaviour(ClientAgent clientAgent, long period, AID controlTowerID,
                                  int MIN_VEHICLES_EMERGENCY, int MAX_VEHICLES_EMERGENCY,
                                  int MIN_DURATION_MS, int MAX_DURATION_MS) {
        //super(clientAgent, period);
        this.executor = new ScheduledThreadPoolExecutor(3);
        this.controlTowerID = controlTowerID;
        this.period = period;
        this.MIN_NUM_VEHICLES = MIN_VEHICLES_EMERGENCY;
        this.MAX_NUM_VEHICLES = MAX_VEHICLES_EMERGENCY;
        this.MIN_DURATION = MIN_DURATION_MS;
        this.MAX_DURATION = MAX_DURATION_MS;
    }

    @Override
    public void action() {
        scheduleCall();
    }

    private void scheduleCall() {
        ACLMessage request = new ACLMessage(ACLMessage.INFORM);
        request.addReceiver(controlTowerID);
        Emergency.incrementID();
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

        executor.schedule(
                () -> this.scheduleCall(),
                period,
                TimeUnit.MILLISECONDS
        );
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

    @Override
    public boolean done() {
        return false;
    }
}
