package behaviours;

import jade.core.behaviours.Behaviour;
import jade.core.behaviours.ParallelBehaviour;
import messages.InformStatus;
import agents.ControlTowerAgent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import jade.proto.ContractNetInitiator;
import messages.Messages;
import utils.Emergency;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

// TODO: instead of adding a behaviour per emergency, make this behaviour cyclic and handle a queue of emergencies
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
