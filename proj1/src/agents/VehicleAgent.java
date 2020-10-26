package agents;

import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import utils.VehicleType;

public abstract class VehicleAgent extends Agent {

    private String vehicleName;
    private static MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.CFP);

    public VehicleAgent(String name) {
        this.vehicleName = name;
    }

    public static MessageTemplate getMt() {
        return mt;
    }

    @Override
    protected abstract void setup();

    public String getVehicleName() {
        return vehicleName;
    }

    public abstract VehicleType getType();


}
