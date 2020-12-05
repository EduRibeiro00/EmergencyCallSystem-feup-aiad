package agents;

import behaviours.FiremanBehaviour;
import behaviours.InemBehaviour;
import behaviours.VehicleBehaviour;
import utils.VehicleType;

public class FiremanAgent extends VehicleAgent {

    private FiremanBehaviour behaviour;

    public FiremanAgent(String name,
                        int MIN_NUM_EMPLOYEES, int MAX_NUM_EMPLOYEES, int REFUEL_DURATION, int EMPLOYEE_CHANGE_PROB,
                        double EMPLOYEE_MULTIPLIER, double DISTANCE_MULTIPLIER, double FUEL_MULTIPLIER,
                        double EMPLOYEE_FUEL_MULTIPLIER, int MAX_FUEL_FIRE, int SPARE_FUEL_LEVEL_FIRE,
                        double FUEL_RATE_FIRE) {
        super(name);
        this.behaviour = new FiremanBehaviour(this, getMt(), MIN_NUM_EMPLOYEES, MAX_NUM_EMPLOYEES, REFUEL_DURATION,
                EMPLOYEE_CHANGE_PROB, EMPLOYEE_MULTIPLIER, DISTANCE_MULTIPLIER, FUEL_MULTIPLIER,
                EMPLOYEE_FUEL_MULTIPLIER, MAX_FUEL_FIRE, SPARE_FUEL_LEVEL_FIRE, FUEL_RATE_FIRE);
    }

    @Override
    public VehicleType getType() {return VehicleType.FIREMAN;}

    @Override
    protected VehicleBehaviour getVehicleBehaviour() {
        return behaviour;
    }
}
