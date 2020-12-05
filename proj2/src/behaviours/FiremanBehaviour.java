package behaviours;

import sajas.core.Agent;
import jade.lang.acl.MessageTemplate;
import utils.VehicleType;

public class FiremanBehaviour extends VehicleBehaviour {
    private int MAX_FUEL;
    private int SPARE_FUEL_LEVEL;
    private double FUEL_RATE;

    public FiremanBehaviour(Agent agent, MessageTemplate msgTemp,
                            int MIN_NUM_EMPLOYEES, int MAX_NUM_EMPLOYEES, int REFUEL_DURATION, int EMPLOYEE_CHANGE_PROB,
                            double EMPLOYEE_MULTIPLIER, double DISTANCE_MULTIPLIER, double FUEL_MULTIPLIER,
                            double EMPLOYEE_FUEL_MULTIPLIER, int MAX_FUEL_FIRE, int SPARE_FUEL_LEVEL_FIRE,
                            double FUEL_RATE_FIRE) {
        super(agent, msgTemp, MIN_NUM_EMPLOYEES,  MAX_NUM_EMPLOYEES, REFUEL_DURATION, EMPLOYEE_CHANGE_PROB,
                EMPLOYEE_MULTIPLIER, DISTANCE_MULTIPLIER, FUEL_MULTIPLIER, EMPLOYEE_FUEL_MULTIPLIER);

        this.MAX_FUEL = MAX_FUEL_FIRE;
        this.SPARE_FUEL_LEVEL = SPARE_FUEL_LEVEL_FIRE;
        this.FUEL_RATE = FUEL_RATE_FIRE;
    }

    @Override
    public VehicleType getVehicleType() {
        return VehicleType.FIREMAN;
    }

    @Override
    protected int getMaxFuel() {
        return MAX_FUEL;
    }

    @Override
    protected double getFuelRate() {
        return FUEL_RATE;
    }

    @Override
    protected int getSpareFuelLevel() {
        return SPARE_FUEL_LEVEL;
    }
}
