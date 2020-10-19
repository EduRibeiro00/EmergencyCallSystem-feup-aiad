package agents;

import behaviours.VehicleBehaviour;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class VehicleAgent extends Agent {

    private String vehicleName;

    public VehicleAgent(String name) {
        this.vehicleName = name;
    }

    private static MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.CFP);

    @Override
    protected void setup() {
        addBehaviour(new VehicleBehaviour(this, mt));
    }

    public String getVehicleName() {
        return vehicleName;
    }
}
