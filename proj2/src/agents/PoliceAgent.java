package agents;

import behaviours.PoliceBehaviour;
import behaviours.VehicleBehaviour;
import utils.VehicleType;

public class PoliceAgent extends VehicleAgent {

    private PoliceBehaviour behaviour;

    public PoliceAgent(String name,
                       int MIN_NUM_EMPLOYEES, int MAX_NUM_EMPLOYEES, int REFUEL_DURATION, int EMPLOYEE_CHANGE_PROB,
                       double EMPLOYEE_MULTIPLIER, double DISTANCE_MULTIPLIER, double FUEL_MULTIPLIER,
                       double EMPLOYEE_FUEL_MULTIPLIER, int MAX_FUEL_POLICE, int SPARE_FUEL_LEVEL_POLICE,
                       double FUEL_RATE_POLICE) {
        super(name);
        this.behaviour = new PoliceBehaviour(this, getMt(), MIN_NUM_EMPLOYEES, MAX_NUM_EMPLOYEES, REFUEL_DURATION,
                EMPLOYEE_CHANGE_PROB, EMPLOYEE_MULTIPLIER, DISTANCE_MULTIPLIER, FUEL_MULTIPLIER,
                EMPLOYEE_FUEL_MULTIPLIER, MAX_FUEL_POLICE, SPARE_FUEL_LEVEL_POLICE, FUEL_RATE_POLICE);
    }

    @Override
    public VehicleType getType() { return VehicleType.POLICE; }

    @Override
    protected VehicleBehaviour getVehicleBehaviour() {
        return behaviour;
    }
}
