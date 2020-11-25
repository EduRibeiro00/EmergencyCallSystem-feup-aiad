package agents;

import behaviours.DeterministicCallBehaviour;
import behaviours.EmergencyCallBehaviour;
import jade.core.AID;
import sajas.core.Agent;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import logs.LoggerHelper;
import utils.DFUtils;

public class ClientAgent extends Agent {

    private static final int SECONDS_BETWEEN_CALLS = 1;
    private static final int MAX_NUMBER_TRIES = 3;

    private String clientName;
    private String towerDFName;
    private AID controlTowerID;
    private boolean deterministic;

    public ClientAgent(String clientName, String towerDFName, boolean deterministic) {
        this.clientName = clientName;
        this.towerDFName = towerDFName;
        this.deterministic = deterministic;
    }

    @Override
    protected void setup() {
        int numberOfTries = 0;
        while (numberOfTries < MAX_NUMBER_TRIES){
            DFAgentDescription[] tower = DFUtils.fetchFromDF(this, this.towerDFName);
            if (tower == null || tower.length < 1) {
                numberOfTries++;
                continue;
            }

            this.controlTowerID = tower[0].getName();

            if(deterministic) {
                addBehaviour(
                        new DeterministicCallBehaviour(
                                controlTowerID
                        )
                );
            } else {
                addBehaviour(
                        new EmergencyCallBehaviour(
                                this,
                                SECONDS_BETWEEN_CALLS * 1000,
                                controlTowerID
                        )
                );
            }
            break;
        }

        if(numberOfTries >= MAX_NUMBER_TRIES){
            LoggerHelper.get().logError(
                    "[DF ERROR] - Client could not fetch control tower from the DF"
            );
            System.exit(-1);
        }
    }

    public String getClientName() {
        return clientName;
    }
}
