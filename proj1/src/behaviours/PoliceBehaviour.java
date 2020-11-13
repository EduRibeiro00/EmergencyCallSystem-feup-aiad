package behaviours;

import jade.core.Agent;
import jade.lang.acl.MessageTemplate;
import utils.VehicleType;

public class PoliceBehaviour extends VehicleBehaviour {
    private final int MAX_FUEL = 350;
    private final int FUEL_RATE = 2;

    public PoliceBehaviour(Agent agent, MessageTemplate msgTemp) {
        super(agent, msgTemp);
    }

    @Override
    public VehicleType getVehicleType() {
        return VehicleType.POLICE;
    }

    @Override
    protected int getMaxFuel() {
        return MAX_FUEL;
    }

    @Override
    protected int getFuelRate() {
        return FUEL_RATE;
    }
}
