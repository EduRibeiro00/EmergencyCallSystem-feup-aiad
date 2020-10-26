package agents;

import behaviours.InemBehaviour;
import utils.VehicleType;

public class InemAgent extends VehicleAgent {

    public InemAgent(String name) {
        super(name);
    }

    @Override
    protected void setup() {
        addBehaviour(new InemBehaviour(this, getMt()));
    }

    @Override
    public VehicleType getType() {return VehicleType.INEM; }
}
