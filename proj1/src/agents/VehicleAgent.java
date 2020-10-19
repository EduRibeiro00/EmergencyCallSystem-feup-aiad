package agents;

import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public abstract class VehicleAgent extends Agent {

    private String vehicleName;

    public VehicleAgent(String name) {
        this.vehicleName = name;
    }
    private static MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.CFP);

    public static MessageTemplate getMt() {
        return mt;
    }

    @Override
    protected abstract void setup();

    public String getVehicleName() {
        return vehicleName;
    }
}
