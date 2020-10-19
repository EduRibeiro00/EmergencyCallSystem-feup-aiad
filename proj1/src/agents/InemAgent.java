package agents;

import behaviours.InemBehaviour;

public class InemAgent extends VehicleAgent {

    public InemAgent(String name) {
        super(name);
    }

    @Override
    protected void setup() {
        addBehaviour(new InemBehaviour(this, getMt()));
    }
}
