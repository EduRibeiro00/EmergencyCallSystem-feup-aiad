package agents;

import behaviours.FiremanBehaviour;
import utils.VehicleType;

public class FiremanAgent extends VehicleAgent {

    public FiremanAgent(String name) {
        super(name);
    }

    @Override
    protected void setup() {
        addBehaviour(new FiremanBehaviour(this, getMt()));
    }

    @Override
    public VehicleType getType() {return VehicleType.FIREMAN;}
}
