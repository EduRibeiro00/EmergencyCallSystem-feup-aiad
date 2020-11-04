package agents;

import behaviours.PoliceBehaviour;
import utils.VehicleType;

public class PoliceAgent extends VehicleAgent {

    public PoliceAgent(String name) {
        super(name);
    }

    @Override
    protected void setup() {
        addBehaviour(new PoliceBehaviour(this, getMt()));
    }

    @Override
    public VehicleType getType() { return VehicleType.POLICE; }
}
