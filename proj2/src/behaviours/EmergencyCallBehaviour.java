package behaviours;

import GUI.Results;
import GUI.GUI;
import agents.ClientAgent;
import jade.core.AID;
import sajas.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import logs.LoggerHelper;
import sajas.core.behaviours.TickerBehaviour;
import utils.Emergency;
import utils.EmergencyType;
import utils.Point;

import javax.xml.transform.Result;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;


public class EmergencyCallBehaviour extends TickerBehaviour {

    private final AID controlTowerID;
    private final ClientAgent clientAgent;

    public EmergencyCallBehaviour(ClientAgent clientAgent, long period, AID controlTowerID) {
        super(clientAgent, period);
        this.clientAgent = clientAgent;
        this.controlTowerID = controlTowerID;
    }

    private EmergencyType getRandomEmergencyType() {
        int numEmergencyTypes = EmergencyType.values().length;
        int randomIndex = new Random().nextInt(numEmergencyTypes);
        return EmergencyType.values()[randomIndex];
    }

    private int getRandomNumberOfVehicles() {
        return ThreadLocalRandom.current().nextInt(
                this.clientAgent.getMIN_VEHICLES_EMERGENCY(),
                this.clientAgent.getMAX_VEHICLES_EMERGENCY() + 1);
    }
    
    private int getRandomAccidentDuration() {
        return ThreadLocalRandom.current().nextInt(
                this.clientAgent.getMIN_DURATION_MS(),
                this.clientAgent.getMAX_DURATION_MS() + 1);
    }

    @Override
    protected void onTick() {
        ACLMessage request = new ACLMessage(ACLMessage.INFORM);
        request.addReceiver(controlTowerID);
        Emergency.incrementID();
        Emergency emergency = new Emergency(
                getRandomEmergencyType(),
                Point.genRandomPoint(),
                getRandomNumberOfVehicles(),
                getRandomAccidentDuration());
        //GUI.generateEmergencyNode(emergency); //TODO Problem
        Results.incrementEmergencies();




        try {
            request.setContentObject(emergency);
        } catch (IOException e) {
            e.printStackTrace();
        }

        myAgent.send(request);
        LoggerHelper.get().logCreatedEmergency(emergency);
    }
}
    /*
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

    @Override
    public boolean done() {
        return false;
    }
  */
