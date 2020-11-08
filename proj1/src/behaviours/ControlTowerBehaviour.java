package behaviours;

import jade.core.behaviours.ParallelBehaviour;
import agents.ControlTowerAgent;
import jade.lang.acl.ACLMessage;
import utils.Emergency;

// TODO: distance calcs and other metrics should be on the side of the vehicle, not on the control tower
public class ControlTowerBehaviour extends ParallelBehaviour {

    private ControlTowerAgent agent;

    public ControlTowerBehaviour(ControlTowerAgent controlTowerAgent) {
        super(WHEN_ALL);
        this.agent = controlTowerAgent;
        addSubBehaviour(new EmergencyReceiverBehaviour(this.agent));
    }

    public void dispatch(ACLMessage cfp, Emergency emergency, int numberVehicles, int priority) {
        addSubBehaviour(
            new EmergencyDispatcherBehaviour(
                this.agent,
                cfp, emergency,
                numberVehicles,
                priority
            )
        );
    }
}
