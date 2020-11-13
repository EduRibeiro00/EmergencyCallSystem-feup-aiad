package behaviours;

import jade.core.Agent;
import jade.lang.acl.MessageTemplate;
import utils.VehicleType;

public class InemBehaviour extends VehicleBehaviour {
    private final int MAX_FUEL = 700;
    private final int FUEL_RATE = 3;

    public InemBehaviour(Agent agent, MessageTemplate msgTemp) {
        super(agent, msgTemp);
    }

    @Override
    public VehicleType getVehicleType() {
        return VehicleType.INEM;
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
