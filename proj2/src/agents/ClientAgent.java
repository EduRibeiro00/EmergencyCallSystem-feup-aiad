package agents;

import behaviours.DeterministicCallBehaviour;
import behaviours.EmergencyCallBehaviour;
import jade.core.AID;
import sajas.core.Agent;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import logs.LoggerHelper;
import utils.DFUtils;

public class ClientAgent extends Agent {
    private static final int MAX_NUMBER_TRIES = 3;

    private final boolean DETERMINISTIC;
    private final int TIME_BETWEEN_CALLS_MS;
    private final int MIN_VEHICLES_EMERGENCY;
    private final int MAX_VEHICLES_EMERGENCY;
    private final int MIN_DURATION_MS;
    private final int MAX_DURATION_MS;

    private final String clientName;
    private final String towerDFName;

    public ClientAgent(String clientName, String towerDFName, boolean DETERMINISTIC,
                       int TIME_BETWEEN_CALLS_MS, int MIN_VEHICLES_EMERGENCY, int MAX_VEHICLES_EMERGENCY,
                       int MIN_DURATION_MS, int MAX_DURATION_MS) {
        this.clientName = clientName;
        this.towerDFName = towerDFName;

        this.DETERMINISTIC = DETERMINISTIC;
        this.TIME_BETWEEN_CALLS_MS = TIME_BETWEEN_CALLS_MS;
        this.MIN_VEHICLES_EMERGENCY = MIN_VEHICLES_EMERGENCY;
        this.MAX_VEHICLES_EMERGENCY = MAX_VEHICLES_EMERGENCY;
        this.MIN_DURATION_MS = MIN_DURATION_MS;
        this.MAX_DURATION_MS = MAX_DURATION_MS;
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

            AID controlTowerID = tower[0].getName();

            if(DETERMINISTIC) {
                addBehaviour(
                    new DeterministicCallBehaviour(
                            controlTowerID
                    )
                );
            } else {
                addBehaviour(
                    new EmergencyCallBehaviour(
                        this,
                            TIME_BETWEEN_CALLS_MS,
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

    public int getMIN_VEHICLES_EMERGENCY() {
        return MIN_VEHICLES_EMERGENCY;
    }

    public int getMAX_VEHICLES_EMERGENCY() {
        return MAX_VEHICLES_EMERGENCY;
    }

    public int getMIN_DURATION_MS() {
        return MIN_DURATION_MS;
    }

    public int getMAX_DURATION_MS() {
        return MAX_DURATION_MS;
    }
}
