package agents;

import behaviours.EmergencyCallBehaviour;
import jade.core.Agent;

public class ClientAgent extends Agent {

    private final int SECONDS_BETWEEN_CALLS = 5;
    private String clientName;
    private ControlTowerAgent controlTower;

    public ClientAgent(String clientName, ControlTowerAgent controlTower) {
        this.clientName = clientName;
        this.controlTower = controlTower;
    }

    @Override
    protected void setup() {
        addBehaviour(new EmergencyCallBehaviour(
        this,
            SECONDS_BETWEEN_CALLS * 1000,
                controlTower.getControlTowerName()
            )
        );
    }

    public String getClientName() {
        return clientName;
    }
}
