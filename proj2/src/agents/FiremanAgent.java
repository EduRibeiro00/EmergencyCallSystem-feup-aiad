package agents;

import behaviours.FiremanBehaviour;
import behaviours.VehicleBehaviour;
import utils.VehicleType;

public class FiremanAgent extends VehicleAgent {

    public FiremanAgent(String name) {
        super(name);
    }

    @Override
    public VehicleType getType() {return VehicleType.FIREMAN;}

    @Override
    public VehicleBehaviour getVehicleBehaviour() {
        return new FiremanBehaviour(this, getMt());
    }
}
