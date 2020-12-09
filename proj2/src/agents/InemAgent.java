package agents;

import behaviours.InemBehaviour;
import behaviours.PoliceBehaviour;
import behaviours.VehicleBehaviour;
import utils.VehicleType;

public class InemAgent extends VehicleAgent {
    private final int MAX_FUEL;
    private final int SPARE_FUEL_LEVEL;
    private final double FUEL_RATE;

    public InemAgent(String name,
                     int MIN_NUM_EMPLOYEES, int MAX_NUM_EMPLOYEES, int REFUEL_DURATION, int EMPLOYEE_CHANGE_PROB,
                     double EMPLOYEE_MULTIPLIER, double DISTANCE_MULTIPLIER, double FUEL_MULTIPLIER,
                     double EMPLOYEE_FUEL_MULTIPLIER, int MAX_FUEL_INEM, int SPARE_FUEL_LEVEL_INEM,
                     double FUEL_RATE_INEM) {
        super(name, MIN_NUM_EMPLOYEES, MAX_NUM_EMPLOYEES, REFUEL_DURATION,
                EMPLOYEE_CHANGE_PROB, EMPLOYEE_MULTIPLIER, DISTANCE_MULTIPLIER, FUEL_MULTIPLIER,
                EMPLOYEE_FUEL_MULTIPLIER);

        this.MAX_FUEL = MAX_FUEL_INEM;
        this.SPARE_FUEL_LEVEL = SPARE_FUEL_LEVEL_INEM;
        this.FUEL_RATE = FUEL_RATE_INEM;
    }

    @Override
    public VehicleType getType() {return VehicleType.INEM; }

    @Override
    public VehicleBehaviour getVehicleBehaviour() {
        return new InemBehaviour(this, getMt());
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
