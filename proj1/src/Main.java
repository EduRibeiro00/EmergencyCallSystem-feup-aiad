import agents.*;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import utils.Emergency;
import utils.EmergencyType;
import utils.Point;

/**
 * Main class for the program.
 */
public class Main {
    /**
     * Main function of the program.
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        Runtime rt = Runtime.instance();
        Profile p = new ProfileImpl();
        ContainerController container = rt.createAgentContainer(p);

        try {
            VehicleAgent[] vehicles = createVehicles(2,2,2);
            startVehicles(vehicles, container);

            ControlTowerAgent controlTowerAgent = new ControlTowerAgent(vehicles);
            AgentController controlTower = container.acceptNewAgent("tower", controlTowerAgent);

            createAndHandleEmergencies(controlTowerAgent);

            controlTower.start();
        } catch (StaleProxyException e) {
            e.printStackTrace();
        }
    }

    private static void createAndHandleEmergencies(ControlTowerAgent controlTowerAgent) {
        controlTowerAgent.handleEmergency(new Emergency(
                EmergencyType.FIRE,
                Point.genRandomPoint(),
                1
        ));

        controlTowerAgent.handleEmergency(new Emergency(
                EmergencyType.ACCIDENT,
                Point.genRandomPoint(),
                3
        ));
    }

    private static VehicleAgent[] createVehicles(int numberInem, int numberFire, int numberPolice){
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

    private static void startVehicles(VehicleAgent[] vehicleAgents, ContainerController container){
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
