package agents;

import behaviours.VehicleBehaviour;
import sajas.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import utils.DFUtils;
import utils.VehicleType;

public abstract class VehicleAgent extends Agent {

    private final String vehicleName;
    private static final MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.CFP);

    public VehicleAgent(String name) {
        this.vehicleName = name;
    }

    public static MessageTemplate getMt() {
        return mt;
    }

    @Override
    protected void setup() {
        DFUtils.registerInDF(this, getType().getDFName());
        addBehaviour(getVehicleBehaviour());
    }

    @Override
    protected void takeDown() {
        DFUtils.deregisterFromDF(this);
    }


    public String getVehicleName() {
        return vehicleName;
    }

    public abstract VehicleType getType();

    protected abstract VehicleBehaviour getVehicleBehaviour();
}
