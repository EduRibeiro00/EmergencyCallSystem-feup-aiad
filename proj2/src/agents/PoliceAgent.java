package agents;

import behaviours.InemBehaviour;
import behaviours.PoliceBehaviour;
import behaviours.VehicleBehaviour;
import utils.VehicleType;

public class PoliceAgent extends VehicleAgent {
    private final int MAX_FUEL;
    private final int SPARE_FUEL_LEVEL;
    private final double FUEL_RATE;

    public PoliceAgent(String name,
                       int MIN_NUM_EMPLOYEES, int MAX_NUM_EMPLOYEES, int REFUEL_DURATION, int EMPLOYEE_CHANGE_PROB,
                       double EMPLOYEE_MULTIPLIER, double DISTANCE_MULTIPLIER, double FUEL_MULTIPLIER,
                       double EMPLOYEE_FUEL_MULTIPLIER, int MAX_FUEL_POLICE, int SPARE_FUEL_LEVEL_POLICE,
                       double FUEL_RATE_POLICE) {
        super(name, MIN_NUM_EMPLOYEES, MAX_NUM_EMPLOYEES, REFUEL_DURATION,
                EMPLOYEE_CHANGE_PROB, EMPLOYEE_MULTIPLIER, DISTANCE_MULTIPLIER, FUEL_MULTIPLIER,
                EMPLOYEE_FUEL_MULTIPLIER);

        this.MAX_FUEL = MAX_FUEL_POLICE;
        this.SPARE_FUEL_LEVEL = SPARE_FUEL_LEVEL_POLICE;
        this.FUEL_RATE = FUEL_RATE_POLICE;
    }

    @Override
    public VehicleType getType() { return VehicleType.POLICE; }

    @Override
    public VehicleBehaviour getVehicleBehaviour() {
        return new PoliceBehaviour(this, getMt());
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
