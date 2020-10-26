package behaviours;

import jade.core.Agent;
import jade.lang.acl.MessageTemplate;
import utils.VehicleType;

public class InemBehaviour extends VehicleBehaviour {

    public InemBehaviour(Agent agent, MessageTemplate msgTemp) {
        super(agent, msgTemp);
    }

    @Override
    public VehicleType getVehicleType() {
        return VehicleType.INEM;
    }


}
