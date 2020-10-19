package agents;

import jade.core.*;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

public class MainAgent extends Agent {

    @Override
    protected void setup() {
        Runtime rt = Runtime.instance();
        Profile p = new ProfileImpl();
        ContainerController container = rt.createAgentContainer(p);

        try {
            VehicleAgent[] vehicles = createVehicles(10,container);
            AgentController controlTower = container.acceptNewAgent("tower", new ControlTowerAgent(vehicles));
            controlTower.start();
        } catch (StaleProxyException e) {
            e.printStackTrace();
        }
    }


    private VehicleAgent[] createVehicles(int number,ContainerController container){
        VehicleAgent[] vehicles  = new VehicleAgent[number];

        for (int i = 0; i < number; i++) {
            try {
                String name = "vehicle" + i;
                VehicleAgent vehicleAgent = new VehicleAgent(name);
                AgentController vehicle = container.acceptNewAgent(name, vehicleAgent);
                vehicles[i] = vehicleAgent;
                vehicle.start();
            } catch (StaleProxyException e) {
                e.printStackTrace();
            }
        }

        return vehicles;

    }



}
