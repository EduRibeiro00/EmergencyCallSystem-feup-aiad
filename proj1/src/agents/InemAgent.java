package agents;

import behaviours.InemBehaviour;
import utils.AgentTypes;

public class InemAgent extends VehicleAgent {

    public InemAgent(String name) {
        super(name);
    }

    @Override
    protected void setup() {
        addBehaviour(new InemBehaviour(this, getMt()));
    }

    @Override
    public AgentTypes.AgentType getType() {return AgentTypes.AgentType.INEM; }
}
