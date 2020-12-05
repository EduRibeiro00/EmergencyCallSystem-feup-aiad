package agents;

import behaviours.InemBehaviour;
import behaviours.PoliceBehaviour;
import behaviours.VehicleBehaviour;
import utils.VehicleType;

public class InemAgent extends VehicleAgent {

    private InemBehaviour behaviour;

    public InemAgent(String name,
                     int MIN_NUM_EMPLOYEES, int MAX_NUM_EMPLOYEES, int REFUEL_DURATION, int EMPLOYEE_CHANGE_PROB,
                     double EMPLOYEE_MULTIPLIER, double DISTANCE_MULTIPLIER, double FUEL_MULTIPLIER,
                     double EMPLOYEE_FUEL_MULTIPLIER, int MAX_FUEL_INEM, int SPARE_FUEL_LEVEL_INEM,
                     double FUEL_RATE_INEM) {
        super(name);
        this.behaviour = new InemBehaviour(this, getMt(), MIN_NUM_EMPLOYEES, MAX_NUM_EMPLOYEES, REFUEL_DURATION,
                EMPLOYEE_CHANGE_PROB, EMPLOYEE_MULTIPLIER, DISTANCE_MULTIPLIER, FUEL_MULTIPLIER,
                EMPLOYEE_FUEL_MULTIPLIER, MAX_FUEL_INEM, SPARE_FUEL_LEVEL_INEM, FUEL_RATE_INEM);
    }

    @Override
    public VehicleType getType() {return VehicleType.INEM; }

    @Override
    protected VehicleBehaviour getVehicleBehaviour() {
        return behaviour;
    }
}
