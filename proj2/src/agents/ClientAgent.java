package agents;

import behaviours.DeterministicCallBehaviour;
import behaviours.EmergencyCallBehaviour;
import jade.core.AID;
import sajas.core.Agent;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import logs.LoggerHelper;
import sajas.core.behaviours.Behaviour;
import utils.DFUtils;

public class ClientAgent extends Agent {

    private static final int MAX_NUMBER_TRIES = 3;

    private String clientName;
    private String towerDFName;
    private AID controlTowerID;

    private Behaviour behaviour;

    public ClientAgent(String clientName, String towerDFName, boolean DETERMINISTIC,
                       int TIME_BETWEEN_CALLS_MS, int MIN_VEHICLES_EMERGENCY, int MAX_VEHICLES_EMERGENCY,
                       int MIN_DURATION_MS, int MAX_DURATION_MS) {
        this.clientName = clientName;
        this.towerDFName = towerDFName;

        int numberOfTries = 0;
        while (numberOfTries < MAX_NUMBER_TRIES){
            DFAgentDescription[] tower = DFUtils.fetchFromDF(this, this.towerDFName);
            if (tower == null || tower.length < 1) {
                numberOfTries++;
                continue;
            }

            this.controlTowerID = tower[0].getName();

            if(DETERMINISTIC) {
                this.behaviour = new DeterministicCallBehaviour(
                        controlTowerID
                );
            } else {
                this.behaviour = new EmergencyCallBehaviour(
                        this,
                        TIME_BETWEEN_CALLS_MS,
                        controlTowerID,
                        MIN_VEHICLES_EMERGENCY,
                        MAX_VEHICLES_EMERGENCY,
                        MIN_DURATION_MS,
                        MAX_DURATION_MS
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


    @Override
    protected void setup() {
        addBehaviour(behaviour);
    }

    public String getClientName() {
        return clientName;
    }
}
