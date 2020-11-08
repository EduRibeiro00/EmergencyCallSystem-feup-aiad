package agents;

import behaviours.EmergencyCallBehaviour;
import jade.core.AID;
import jade.core.Agent;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import logs.LoggerHelper;
import utils.DFUtils;

public class ClientAgent extends Agent {

    private static final int SECONDS_BETWEEN_CALLS = 5;

    private String clientName;
    private String towerDFName;
    private AID controlTowerID;

    public ClientAgent(String clientName, String towerDFName) {
        this.clientName = clientName;
        this.towerDFName = towerDFName;
    }

    @Override
    protected void setup() {
        DFAgentDescription[] tower = DFUtils.fetchFromDF(this, this.towerDFName);
        if (tower == null || tower.length < 1) {
            LoggerHelper.get().logError(
                    "[DF ERROR] - Client could not fetch control tower from the DF"
            );
            System.exit(-1);
        }

        this.controlTowerID = tower[0].getName();

        addBehaviour(new EmergencyCallBehaviour(
        this,
            SECONDS_BETWEEN_CALLS * 1000,
                controlTowerID
            )
        );
    }

    public String getClientName() {
        return clientName;
    }
}
