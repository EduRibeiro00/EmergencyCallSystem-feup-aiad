package agents;

import GUI.Results;
import behaviours.VehicleBehaviour;
import repast.RepastLauncher;
import sajas.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import uchicago.src.sim.network.DefaultDrawableNode;
import utils.DFUtils;
import utils.Emergency;
import utils.Point;
import utils.VehicleType;

import java.awt.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class VehicleAgent extends Agent {
    private final int MIN_NUM_EMPLOYEES;
    private final int MAX_NUM_EMPLOYEES;
    private final int REFUEL_DURATION;
    private final int EMPLOYEE_CHANGE_PROB;

    private final double EMPLOYEE_MULTIPLIER;
    private final double DISTANCE_MULTIPLIER;
    private final double FUEL_MULTIPLIER;
    private final double EMPLOYEE_FUEL_MULTIPLIER;

    protected Point coordinates;
    protected int numberEmployees;
    protected VehicleBehaviour vehicleBehaviour;
    private final String vehicleName;
    protected AtomicBoolean occupied;

    protected Point currentEmergencyCoords;
    protected int numStepsUntilEmergNode;
    protected double deltaXToEmergency;
    protected double deltaYToEmergency;

    protected int emergencyId = -1;
    private DefaultDrawableNode myNode;

    private static final MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.CFP);

    public VehicleAgent(String name, int MIN_NUM_EMPLOYEES, int MAX_NUM_EMPLOYEES, int REFUEL_DURATION,
                        int EMPLOYEE_CHANGE_PROB, double EMPLOYEE_MULTIPLIER, double DISTANCE_MULTIPLIER,
                        double FUEL_MULTIPLIER, double EMPLOYEE_FUEL_MULTIPLIER) {
        coordinates = Point.genRandomPoint();
        numberEmployees = getRandomNumberEmployees();
        this.vehicleName = name;

        this.MIN_NUM_EMPLOYEES = MIN_NUM_EMPLOYEES;
        this.MAX_NUM_EMPLOYEES = MAX_NUM_EMPLOYEES;
        this.REFUEL_DURATION = REFUEL_DURATION;
        this.EMPLOYEE_CHANGE_PROB = EMPLOYEE_CHANGE_PROB;
        this.EMPLOYEE_MULTIPLIER = EMPLOYEE_MULTIPLIER;
        this.DISTANCE_MULTIPLIER = DISTANCE_MULTIPLIER;
        this.FUEL_MULTIPLIER = FUEL_MULTIPLIER;
        this.EMPLOYEE_FUEL_MULTIPLIER = EMPLOYEE_FUEL_MULTIPLIER;

        resetCurrentEmergency();
    }

    public static MessageTemplate getMt() {
        return mt;
    }

    @Override
    protected void setup() {
        DFUtils.registerInDF(this, getType().getDFName());
        addBehaviour(createVehicleBehaviour());
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

    public  VehicleBehaviour getVehicleBehaviour(){ return this.vehicleBehaviour;}
    public abstract VehicleBehaviour createVehicleBehaviour();

    public Point getCoordinates() { return coordinates; }

    public int getNumberEmployees() {
        return numberEmployees;
    }

    public void setNumberEmployees(int numberEmployees) {
        this.numberEmployees = numberEmployees;
    }

    public void setNode(DefaultDrawableNode node){this.myNode = node;}

    public DefaultDrawableNode getNode(){return myNode;}

    public AtomicBoolean getOccupied() {return occupied;}

    public void setOccupied(AtomicBoolean occupied) {this.occupied = occupied;}

    public int getEmergencyId() {
        return emergencyId;
    }

    public void setEmergencyId(int emergencyId) {
        this.emergencyId = emergencyId;
    }

    public int getMIN_NUM_EMPLOYEES() {
        return MIN_NUM_EMPLOYEES;
    }

    public int getMAX_NUM_EMPLOYEES() {
        return MAX_NUM_EMPLOYEES;
    }

    public int getREFUEL_DURATION() {
        return REFUEL_DURATION;
    }

    public int getEMPLOYEE_CHANGE_PROB() {
        return EMPLOYEE_CHANGE_PROB;
    }

    public double getEMPLOYEE_MULTIPLIER() {
        return EMPLOYEE_MULTIPLIER;
    }

    public double getDISTANCE_MULTIPLIER() {
        return DISTANCE_MULTIPLIER;
    }

    public double getFUEL_MULTIPLIER() {
        return FUEL_MULTIPLIER;
    }

    public double getEMPLOYEE_FUEL_MULTIPLIER() {
        return EMPLOYEE_FUEL_MULTIPLIER;
    }

    public abstract int getMAX_FUEL();

    public abstract int getSPARE_FUEL_LEVEL();

    public abstract double getFUEL_RATE();

    public void resetCurrentEmergency() {
        this.currentEmergencyCoords = null;
        this.numStepsUntilEmergNode = 0;
        this.deltaXToEmergency = 0.0;
        this.deltaYToEmergency = 0.0;
    }

    // sets both the vehicle coordinates and the node coordinates, so they are always in sync
    public void setVehicleCoordinates(double x, double y) {
        this.myNode.setX(x);
        this.coordinates.setX(x);
        this.myNode.setY(y);
        this.coordinates.setY(y);
    }

    // called when vehicle is accepted for an emergency, to calc its path to the emergency location
    public void calcVehicleNodeMovement(Point emergencyCoords, double tripDuration) {
        Results.addTripTime(tripDuration);
        currentEmergencyCoords = emergencyCoords;
        double totalDistX = emergencyCoords.getX() - coordinates.getX();
        double totalDistY = emergencyCoords.getY() - coordinates.getY();

        numStepsUntilEmergNode = (int) Math.round(tripDuration / RepastLauncher.getStepDuration());
        System.out.println("------------");
        System.out.println("vehicleCoords: " + coordinates);
        System.out.println("emergCoords: " + emergencyCoords);
        System.out.println("tripDuration: " + tripDuration);
        System.out.println("numSteps: " + numStepsUntilEmergNode);
        System.out.println("------------");
        deltaXToEmergency = totalDistX / numStepsUntilEmergNode;
        deltaYToEmergency = totalDistY / numStepsUntilEmergNode;
    }

    // called every repast step to update vehicle coordinates
    public void updateVehicleCoordinates() {
        if(numStepsUntilEmergNode > 0) {
            // increment another step towards emergency
            double newX = this.myNode.getX() + deltaXToEmergency;
            double newY = this.myNode.getY() + deltaYToEmergency;
            this.setVehicleCoordinates(newX, newY);
            numStepsUntilEmergNode--;
        }
    }

    // called when vehicle arrives at emergency
    public void setCoordsToEmergency() {
        this.setVehicleCoordinates(
            this.currentEmergencyCoords.getX(),
            this.currentEmergencyCoords.getY()
        );
        this.resetCurrentEmergency();
    }
}
