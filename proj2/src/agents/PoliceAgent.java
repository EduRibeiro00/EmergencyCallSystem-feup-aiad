package agents;

import behaviours.PoliceBehaviour;
import behaviours.VehicleBehaviour;
import utils.VehicleType;

public class PoliceAgent extends VehicleAgent {

    public PoliceAgent(String name) {
        super(name);
    }

    @Override
    public VehicleType getType() { return VehicleType.POLICE; }

    @Override
    public VehicleBehaviour getVehicleBehaviour() {
        return new PoliceBehaviour(this, getMt());
    }
}
