package agents;

import behaviours.VehicleBehaviour;
import sajas.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import utils.DFUtils;
import utils.Point;
import utils.VehicleType;

import java.util.concurrent.ThreadLocalRandom;

public abstract class VehicleAgent extends Agent {

    protected static final int MIN_NUM_EMPLOYEES= 1;
    protected static final int MAX_NUM_EMPLOYEES= 6;

    private final String vehicleName;
    private static final MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.CFP);



    protected Point coordinates;
    protected int numberEmployees;

    public VehicleAgent(String name) {
        this.vehicleName = name;
        coordinates = Point.genRandomPoint();
        numberEmployees = getRandomNumberEmployees();
    }

    public static MessageTemplate getMt() {
        return mt;
    }

    @Override
    protected void setup() {
        DFUtils.registerInDF(this, getType().getDFName());
        addBehaviour(getVehicleBehaviour());
    }

    @Override
    protected void takeDown() {
        DFUtils.deregisterFromDF(this);
    }

    public int getRandomNumberEmployees() {
        return ThreadLocalRandom.current().nextInt(MIN_NUM_EMPLOYEES, MAX_NUM_EMPLOYEES + 1);
    }


    public String getVehicleName() {
        return vehicleName;
    }

    public abstract VehicleType getType();

    public abstract VehicleBehaviour getVehicleBehaviour();

    public Point getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Point coordinates) {
        this.coordinates = coordinates;
    }

    public int getNumberEmployees() {
        return numberEmployees;
    }

    public void setNumberEmployees(int numberEmployees) {
        this.numberEmployees = numberEmployees;
    }
}
