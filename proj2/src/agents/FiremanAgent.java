package agents;

import behaviours.FiremanBehaviour;
import behaviours.InemBehaviour;
import behaviours.VehicleBehaviour;
import utils.VehicleType;

public class FiremanAgent extends VehicleAgent {
    private final int MAX_FUEL;
    private final int SPARE_FUEL_LEVEL;
    private final double FUEL_RATE;

    public FiremanAgent(String name,
                        int MIN_NUM_EMPLOYEES, int MAX_NUM_EMPLOYEES, int REFUEL_DURATION, int EMPLOYEE_CHANGE_PROB,
                        double EMPLOYEE_MULTIPLIER, double DISTANCE_MULTIPLIER, double FUEL_MULTIPLIER,
                        double EMPLOYEE_FUEL_MULTIPLIER, int MAX_FUEL_FIRE, int SPARE_FUEL_LEVEL_FIRE,
                        double FUEL_RATE_FIRE) {
        super(name, MIN_NUM_EMPLOYEES, MAX_NUM_EMPLOYEES, REFUEL_DURATION,
                EMPLOYEE_CHANGE_PROB, EMPLOYEE_MULTIPLIER, DISTANCE_MULTIPLIER, FUEL_MULTIPLIER,
                EMPLOYEE_FUEL_MULTIPLIER);

        this.MAX_FUEL = MAX_FUEL_FIRE;
        this.SPARE_FUEL_LEVEL = SPARE_FUEL_LEVEL_FIRE;
        this.FUEL_RATE = FUEL_RATE_FIRE;
    }

    @Override
    public VehicleType getType() {return VehicleType.FIREMAN;}

    @Override
    protected VehicleBehaviour getVehicleBehaviour() {
        return new FiremanBehaviour(this, getMt());
    }

    @Override
    public int getMAX_FUEL() {
        return MAX_FUEL;
    }

    @Override
    public int getSPARE_FUEL_LEVEL() {
        return SPARE_FUEL_LEVEL;
    }

    @Override
    public double getFUEL_RATE() {
        return FUEL_RATE;
    }
}
