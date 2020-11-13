package behaviours;

import jade.core.Agent;
import jade.lang.acl.MessageTemplate;
import utils.VehicleType;

public class FiremanBehaviour extends VehicleBehaviour {
    private final int MAX_FUEL = 1500;
    private final int FUEL_RATE = 6;

    public FiremanBehaviour(Agent agent, MessageTemplate msgTemp) {
        super(agent, msgTemp);
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
    protected int getFuelRate() {
        return FUEL_RATE;
    }
}
