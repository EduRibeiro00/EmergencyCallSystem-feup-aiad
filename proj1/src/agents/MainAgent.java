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
            String[] vehicles = createVehicles(10,container);
            AgentController controlTower = container.createNewAgent("tower", "agents.ControlTowerAgent",vehicles);
            controlTower.start();
        } catch (StaleProxyException e) {
            e.printStackTrace();
        }
    }


    private String[] createVehicles(int number,ContainerController container){
        String[] vehicles  = new String[number];

        for (int i = 0; i < number; i++) {
            try {
                String name = "vehicle" + i;
                AgentController vehicle = container.acceptNewAgent(name, new VehicleAgent());
                vehicles[i] = name;
                vehicle.start();
            } catch (StaleProxyException e) {
                e.printStackTrace();
            }
        }

        return vehicles;

    }



}
