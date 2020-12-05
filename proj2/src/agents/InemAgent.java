package agents;

import behaviours.InemBehaviour;
import behaviours.VehicleBehaviour;
import utils.VehicleType;

public class InemAgent extends VehicleAgent {

    public InemAgent(String name) {
        super(name);
    }

    @Override
    public VehicleType getType() {return VehicleType.INEM; }

    @Override
    public VehicleBehaviour getVehicleBehaviour() {
        return new InemBehaviour(this, getMt());
    }
}
