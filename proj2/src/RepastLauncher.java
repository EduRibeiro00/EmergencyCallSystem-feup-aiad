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

public class RepastLauncher extends Repast3Launcher {
    private static final boolean BATCH_MODE = false;
    private static final boolean SIMPLE = true;
    private static final boolean DETERMINISTIC = true;

    private final boolean runInBatchMode;

    private static final int WIDTH = 200;
    private static final int HEIGHT = 200;

    private int NUM_INEM = 2;
    private int NUM_FIRE = 2;
    private int NUM_POLICE = 2;

    public int getNUM_INEM() {
        return NUM_INEM;
    }

    public void setNUM_INEM(int NUM_INEM) {
        this.NUM_INEM = NUM_INEM;
    }

    public int getNUM_FIRE() {
        return NUM_FIRE;
    }

    public void setNUM_FIRE(int NUM_FIRE) {
        this.NUM_FIRE = NUM_FIRE;
    }

    public int getNUM_POLICE() {
        return NUM_POLICE;
    }

    public void setNUM_POLICE(int NUM_POLICE) {
        this.NUM_POLICE = NUM_POLICE;
    }

    public RepastLauncher(boolean runInBatchMode) {
        super();
        this.runInBatchMode = runInBatchMode;
    }

    @Override
	public void setup() {
		super.setup();
        // property descriptors
        // ...
	}

    @Override
    public void begin() {
        super.begin();
        // display surfaces, spaces, displays, plots, ...
        if(!runInBatchMode) {
            buildAndScheduleDisplay();
        }
    }

    private void buildAndScheduleDisplay() {
        // TODO: fazer graficos e esquemas, para dar display
    }

    @Override
    public String[] getInitParam() {
        return new String[] {"NUM_INEM", "NUM_FIRE", "NUM_POLICE"};
    }

    @Override
    public String getName() {
        return "Emergency Service";
    }

    @Override
    protected void launchJADE() {
        Runtime rt = Runtime.instance();
        Profile p = new ProfileImpl();
        ContainerController container = rt.createMainContainer(p);

        if (SIMPLE) LoggerHelper.setSimpleLog();

        try {
            // ----------------------------------------------------
            // starting control tower agent
            ControlTowerAgent controlTowerAgent = new ControlTowerAgent();
            AgentController controlTower = container.acceptNewAgent(ControlTowerAgent.getDFName(), controlTowerAgent);
            LoggerHelper.get().logInfo("START - Started control tower");
            controlTower.start();

            // ----------------------------------------------------
            // starting vehicle agents
            VehicleAgent[] vehicles = createVehicles(NUM_INEM, NUM_FIRE, NUM_POLICE);
            startVehicles(vehicles, container);

            // ----------------------------------------------------
            // starting client agent
            ClientAgent clientAgent = new ClientAgent("johnny", ControlTowerAgent.getDFName(), DETERMINISTIC);
            AgentController client = container.acceptNewAgent(clientAgent.getClientName(), clientAgent);
            String deterministicInfo = DETERMINISTIC ? "deterministic" : "random";
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
        for (int i = numberInem; i < numberInem + numberFire; i++) {
            String name = "Fireman" + i;
            VehicleAgent vehicleAgent = new FiremanAgent(name);
            vehicles[i] = vehicleAgent;
        }
        for (int i = numberInem + numberFire; i < total; i++) {
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
        boolean runMode = BATCH_MODE;

        String filepath = System.getProperty("user.dir") + "/src/params.txt";

        SimInit init = new SimInit();
        init.setNumRuns(1);   // works only in batch mode
        init.loadModel(new RepastLauncher(runMode), filepath, runMode);
    }

}
