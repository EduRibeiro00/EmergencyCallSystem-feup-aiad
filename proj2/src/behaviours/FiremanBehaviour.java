package behaviours;

import agents.VehicleAgent;
import sajas.core.Agent;
import jade.lang.acl.MessageTemplate;
import utils.VehicleType;

public class FiremanBehaviour extends VehicleBehaviour {

    public FiremanBehaviour(VehicleAgent agent, MessageTemplate msgTemp) {
        super(agent, msgTemp);
    }

    @Override
    public VehicleType getVehicleType() {
        return VehicleType.FIREMAN;
    }
}
