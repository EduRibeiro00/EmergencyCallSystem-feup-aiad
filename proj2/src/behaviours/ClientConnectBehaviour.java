package behaviours;

import agents.ClientAgent;
import jade.core.AID;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import logs.LoggerHelper;
import sajas.core.behaviours.TickerBehaviour;
import utils.DFUtils;

public class ClientConnectBehaviour extends TickerBehaviour {
    private static final int MAX_NUMBER_TRIES = 15;
    private ClientAgent clientAgent;
    private String towerDFName;
    private boolean connected = false;

    public ClientConnectBehaviour(ClientAgent clientAgent, long period, String towerDFName) {
        super(clientAgent, period);
        this.clientAgent = clientAgent;
        this.towerDFName = towerDFName;
    }

    @Override
    protected void onTick() {
        int numberOfTries = 0;
        while (numberOfTries < MAX_NUMBER_TRIES && !connected){
            DFAgentDescription[] tower = DFUtils.fetchFromDF(clientAgent, this.towerDFName);
            if (tower == null || tower.length < 1) {
                numberOfTries++;
                continue;
            }
            AID controlTowerID = tower[0].getName();

            connected = true;
            this.clientAgent.addCallBehaviour(controlTowerID);
            break;
        }
    }
}
