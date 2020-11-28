package serviceConsumerProviderVis;

import java.util.List;

import agents.*;
import jade.core.Profile;
import jade.core.ProfileImpl;
import logs.LoggerHelper;
import sajas.core.Runtime;
import sajas.wrapper.AgentController;
import sajas.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

import sajas.sim.repast3.Repast3Launcher;
import uchicago.src.sim.engine.SimInit;
import uchicago.src.sim.network.DefaultDrawableNode;

public class RepastLauncher extends Repast3Launcher {

    private static final boolean BATCH_MODE = true;
    private boolean SIMPLE = true;
    private boolean DETERMINISTIC = true;

    private boolean runInBatchMode;

    public RepastLauncher(boolean runInBatchMode) {
        super();
        this.runInBatchMode = runInBatchMode;
    }


    @Override
    public String[] getInitParam() {
        return new String[] {"SIMPLE", "DETERMINISTIC"};
    }

    @Override
    public String getName() {
        return "Emergency Service";
    }

    @Override
    protected void launchJADE() {

        //boolean deterministic = Arguments.parseArguments(args);

        Runtime rt = Runtime.instance();
        Profile p = new ProfileImpl();
        ContainerController container = rt.createAgentContainer(p);

        try {
            VehicleAgent[] vehicles = createVehicles(10,10,10);
            startVehicles(vehicles, container);

            ControlTowerAgent controlTowerAgent = new ControlTowerAgent();
            AgentController controlTower = container.acceptNewAgent(ControlTowerAgent.getDFName(), controlTowerAgent);
            LoggerHelper.get().logInfo("START - Started control tower");
            controlTower.start();

            //ClientAgent clientAgent = new ClientAgent("johnny", ControlTowerAgent.getDFName(), deterministic);
            ClientAgent clientAgent = new ClientAgent("johnny", ControlTowerAgent.getDFName(), true);
            AgentController client = container.acceptNewAgent(clientAgent.getClientName(), clientAgent);
            //String deterministicInfo = deterministic ? "deterministic" : "random";
            String deterministicInfo =  "deterministic" ;
            LoggerHelper.get().logInfo("CLIENT - Started " + deterministicInfo + " client " + clientAgent.getClientName());
            client.start();
        } catch (StaleProxyException e) {
            e.printStackTrace();
        }
    }

    private static VehicleAgent[] createVehicles(int numberInem, int numberFire, int numberPolice){
        LoggerHelper.get().logCreateVehicles(numberInem, numberFire, numberPolice);

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

    private static void startVehicles(VehicleAgent[] vehicleAgents, ContainerController container) {
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




    /**
     * Launching Repast3
     * @param args
     */
    public static void main(String[] args) {
        boolean runMode = !BATCH_MODE;   // BATCH_MODE or !BATCH_MODE

        SimInit init = new SimInit();
        init.setNumRuns(1);   // works only in batch mode
        init.loadModel(new serviceConsumerProviderVis.RepastLauncher(runMode), null, runMode);
    }

}
