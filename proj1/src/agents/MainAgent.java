package agents;

import jade.core.*;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import static utils.Emergencies.EmergencyType.*;

public class MainAgent extends Agent {

    @Override
    protected void setup() {
        Runtime rt = Runtime.instance();
        Profile p = new ProfileImpl();
        ContainerController container = rt.createAgentContainer(p);
        int priority = 0;

        try {
            VehicleAgent[] vehicles = createVehicles(2,2,2);
            startVehicles(vehicles,container);

            ControlTowerAgent controlTowerAgent = new ControlTowerAgent(vehicles);
            AgentController controlTower = container.acceptNewAgent("tower", controlTowerAgent);

            controlTowerAgent.handleAccident(Fire,1,priority);
            controlTowerAgent.handleAccident(Fire,2,priority);
            controlTower.start();
        } catch (StaleProxyException e) {
            e.printStackTrace();
        }
    }


    private VehicleAgent[] createVehicles(int numberInem,int numberFire,int numberPolice){
        int total = numberFire + numberInem + numberPolice;
        VehicleAgent[] vehicles  = new VehicleAgent[total];

        for (int i = 0; i < numberInem; i++) {
            String name = "Inem" + i;
            VehicleAgent vehicleAgent = new InemAgent(name);
            vehicles[i] = vehicleAgent;
        }
        for (int i = numberInem; i < numberInem+numberFire; i++) {
            String name = "Fireman" + i;
            VehicleAgent vehicleAgent = new FiremanAgent(name);
            vehicles[i] = vehicleAgent;
        }
        for (int i = numberInem+numberFire; i < total; i++) {
            String name = "Police" + i;
            VehicleAgent vehicleAgent = new PoliceAgent(name);
            vehicles[i] = vehicleAgent;
        }
        return vehicles;
    }

    private void startVehicles(VehicleAgent[] vehicleAgents,ContainerController container){
        for (VehicleAgent vehicleAgent : vehicleAgents){
            AgentController vehicle = null;
            try {
                vehicle = container.acceptNewAgent(vehicleAgent.getVehicleName(), vehicleAgent);
                vehicle.start();
            } catch (StaleProxyException e) {
                e.printStackTrace();
            }
        }
    }
}
