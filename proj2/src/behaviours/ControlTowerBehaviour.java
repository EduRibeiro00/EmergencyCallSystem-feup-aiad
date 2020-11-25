package behaviours;

import sajas.core.behaviours.ParallelBehaviour;
import agents.ControlTowerAgent;
import jade.lang.acl.ACLMessage;
import utils.Emergency;

public class ControlTowerBehaviour extends ParallelBehaviour {
    private final ControlTowerAgent agent;

    public ControlTowerBehaviour(ControlTowerAgent controlTowerAgent) {
        super(WHEN_ALL);
        this.agent = controlTowerAgent;
        addSubBehaviour(new EmergencyReceiverBehaviour(this.agent));
    }

    public void dispatch(ACLMessage cfp, Emergency emergency, int numberVehicles, int priority, int numTries) {
        addSubBehaviour(
            new EmergencyDispatcherBehaviour(
                this.agent,
                cfp, emergency,
                numberVehicles,
                priority,
                numTries
            )
        );
    }
}
