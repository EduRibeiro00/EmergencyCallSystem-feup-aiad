package repast;

import GUI.GUI;
import utils.Emergency;
import utils.MyDisplaySurface;
import GUI.Results;
import agents.*;
import jade.core.Profile;
import jade.core.ProfileImpl;
import logs.LoggerHelper;
import sajas.core.Runtime;
import sajas.wrapper.AgentController;
import sajas.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

import sajas.sim.repast3.Repast3Launcher;
import uchicago.src.sim.analysis.OpenSequenceGraph;
import uchicago.src.sim.analysis.Sequence;
import uchicago.src.sim.engine.Schedule;
import uchicago.src.sim.engine.SimInit;



import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uchicago.src.sim.gui.Network2DDisplay;
import utils.Point;


public class RepastLauncher extends Repast3Launcher {
    // ******************************************************
    // singleton variable
    private static RepastLauncher singleton;

    public static RepastLauncher get() {
        if (singleton == null)
            singleton = new RepastLauncher(BATCH_MODE);
        return singleton;
    }

    public RepastLauncher(boolean runInBatchMode) {
        super();
        this.runInBatchMode = runInBatchMode;
    }
    // ******************************************************
    // Emergency map

    private static Map<Integer, Emergency> emergencyMap = new HashMap<>();


    public static Map<Integer, Emergency> getEmergencyMap() {
        return emergencyMap;
    }

    public static void setEmergencyMap(Map<Integer, Emergency> emergencyMap) {
        RepastLauncher.emergencyMap = emergencyMap;
    }


    // ******************************************************
    // Build and schedule display
    private static MyDisplaySurface dsurf;
    private OpenSequenceGraph plot;
    private OpenSequenceGraph plot2;
    private final List<VehicleAgent> vehicles = new ArrayList<>();
    private static Network2DDisplay display;


    public static Network2DDisplay getDisplay() {
        return display;
    }

    public static void setDisplay(Network2DDisplay display) {
        RepastLauncher.display = display;
    }


    public static MyDisplaySurface getDsurf() {
        return dsurf;
    }

    // ******************************************************
    // width and height variables for coordinates
    private int CITY_WIDTH = 200;
    private int CITY_HEIGHT = 100;

    public int getCITY_WIDTH() {
        return CITY_WIDTH;
    }

    public void setCITY_WIDTH(int CITY_WIDTH) {
        if (CITY_WIDTH < 1)
            CITY_WIDTH = 1;
        this.CITY_WIDTH = CITY_WIDTH;
    }

    public int getCITY_HEIGHT() {
        return CITY_HEIGHT;
    }

    public void setCITY_HEIGHT(int CITY_HEIGHT) {
        if (CITY_HEIGHT < 1)
            CITY_HEIGHT = 1;
        this.CITY_HEIGHT = CITY_HEIGHT;
    }

    // ******************************************************
    // repast related variables
    private static final boolean BATCH_MODE = false;
    private final boolean runInBatchMode;
    // each step will be run each TICKS_FOR_STEP ticks
    private static final int TICKS_FOR_STEP = 25;
    // each tick takes around 30ms. This variable is used to calculate the amount
    // of time each vehicle node should take to get to its emergency node
    private static final int STEP_DURATION = 8 * TICKS_FOR_STEP;

    public static int getStepDuration() {
        return STEP_DURATION;
    }

    // ******************************************************
    // width and height for GUI
    private static final int WIDTH = get().CITY_WIDTH + 10;
    private static final int HEIGHT = get().CITY_HEIGHT + 10;

    public static int getWIDTH() {
        return WIDTH;
    }

    public static int getHEIGHT() {
        return HEIGHT;
    }

    // ******************************************************
    // Common vehicle variables
    private int MIN_NUM_EMPLOYEES= 1;
    private int MAX_NUM_EMPLOYEES= 6;
    private int REFUEL_DURATION_MS = 20000;
    private int EMPLOYEE_CHANGE_PROB = 10; // 1 in 10 chance of changing number employees

    private double MULTIPLIER_EMPLOYEE = 2.5;
    private double MULTIPLIER_DISTANCE = -1.0;
    private double MULTIPLIER_FUEL = 0.3;
    private double MULTIPLIER_EMPLOYEE_FUEL = 0.1;

    public int getMIN_NUM_EMPLOYEES() {
        return MIN_NUM_EMPLOYEES;
    }

    public void setMIN_NUM_EMPLOYEES(int MIN_NUM_EMPLOYEES) {
        if (MIN_NUM_EMPLOYEES > MAX_NUM_EMPLOYEES)
            MIN_NUM_EMPLOYEES = MAX_NUM_EMPLOYEES;
        if (MIN_NUM_EMPLOYEES < 1)
            MIN_NUM_EMPLOYEES = 1;
        this.MIN_NUM_EMPLOYEES = MIN_NUM_EMPLOYEES;
    }

    public int getMAX_NUM_EMPLOYEES() {
        return MAX_NUM_EMPLOYEES;
    }

    public void setMAX_NUM_EMPLOYEES(int MAX_NUM_EMPLOYEES) {
        if (MIN_NUM_EMPLOYEES > MAX_NUM_EMPLOYEES)
            MAX_NUM_EMPLOYEES = MIN_NUM_EMPLOYEES;
        if (MAX_NUM_EMPLOYEES < 1)
            MAX_NUM_EMPLOYEES = 1;
        this.MAX_NUM_EMPLOYEES = MAX_NUM_EMPLOYEES;
    }

    public int getREFUEL_DURATION_MS() {
        return REFUEL_DURATION_MS;
    }

    public void setREFUEL_DURATION_MS(int REFUEL_DURATION_MS) {
        if (REFUEL_DURATION_MS < 0)
            REFUEL_DURATION_MS = 0;
        this.REFUEL_DURATION_MS = REFUEL_DURATION_MS;
    }

    public int getEMPLOYEE_CHANGE_PROB() {
        return EMPLOYEE_CHANGE_PROB;
    }

    public void setEMPLOYEE_CHANGE_PROB(int EMPLOYEE_CHANGE_PROB) {
        if (EMPLOYEE_CHANGE_PROB < 1)
            EMPLOYEE_CHANGE_PROB = 1;
        this.EMPLOYEE_CHANGE_PROB = EMPLOYEE_CHANGE_PROB;
    }

    public double getMULTIPLIER_EMPLOYEE() {
        return MULTIPLIER_EMPLOYEE;
    }

    public void setMULTIPLIER_EMPLOYEE(double MULTIPLIER_EMPLOYEE) {
        this.MULTIPLIER_EMPLOYEE = MULTIPLIER_EMPLOYEE;
    }

    public double getMULTIPLIER_DISTANCE() {
        return MULTIPLIER_DISTANCE;
    }

    public void setMULTIPLIER_DISTANCE(double MULTIPLIER_DISTANCE) {
        this.MULTIPLIER_DISTANCE = MULTIPLIER_DISTANCE;
    }

    public double getMULTIPLIER_FUEL() {
        return MULTIPLIER_FUEL;
    }

    public void setMULTIPLIER_FUEL(double MULTIPLIER_FUEL) {
        this.MULTIPLIER_FUEL = MULTIPLIER_FUEL;
    }

    public double getMULTIPLIER_EMPLOYEE_FUEL() {
        return MULTIPLIER_EMPLOYEE_FUEL;
    }

    public void setMULTIPLIER_EMPLOYEE_FUEL(double MULTIPLIER_EMPLOYEE_FUEL) {
        this.MULTIPLIER_EMPLOYEE_FUEL = MULTIPLIER_EMPLOYEE_FUEL;
    }

    // ******************************************************
    // Inem vehicle variables
    private int NUM_INEM = 10;
    private int MAX_FUEL_INEM = 700;
    private int SPARE_FUEL_LEVEL_INEM = 100;
    private double FUEL_RATE_INEM = 3.0;

    public int getNUM_INEM() {
        return NUM_INEM;
    }

    public void setNUM_INEM(int NUM_INEM) {
        if (NUM_INEM < 0)
            NUM_INEM = 0;
        this.NUM_INEM = NUM_INEM;
    }

    public int getMAX_FUEL_INEM() {
        return MAX_FUEL_INEM;
    }

    public void setMAX_FUEL_INEM(int MAX_FUEL_INEM) {
        if (MAX_FUEL_INEM < SPARE_FUEL_LEVEL_INEM)
            MAX_FUEL_INEM = SPARE_FUEL_LEVEL_INEM;
        if (MAX_FUEL_INEM < 1)
            MAX_FUEL_INEM = 1;
        this.MAX_FUEL_INEM = MAX_FUEL_INEM;
    }

    public int getSPARE_FUEL_LEVEL_INEM() {
        return SPARE_FUEL_LEVEL_INEM;
    }

    public void setSPARE_FUEL_LEVEL_INEM(int SPARE_FUEL_LEVEL_INEM) {
        if (MAX_FUEL_INEM < SPARE_FUEL_LEVEL_INEM)
            SPARE_FUEL_LEVEL_INEM = MAX_FUEL_INEM;
        if (SPARE_FUEL_LEVEL_INEM < 1)
            SPARE_FUEL_LEVEL_INEM = 1;
        this.SPARE_FUEL_LEVEL_INEM = SPARE_FUEL_LEVEL_INEM;
    }

    public double getFUEL_RATE_INEM() {
        return FUEL_RATE_INEM;
    }

    public void setFUEL_RATE_INEM(double FUEL_RATE_INEM) {
        if (FUEL_RATE_INEM < 0.0)
            FUEL_RATE_INEM = 0.0;
        this.FUEL_RATE_INEM = FUEL_RATE_INEM;
    }

    // ******************************************************
    // Fire vehicle variables
    private int NUM_FIRE = 10;
    private int MAX_FUEL_FIRE = 1500;
    private int SPARE_FUEL_LEVEL_FIRE = 200;
    private double FUEL_RATE_FIRE = 6.0;

    public int getNUM_FIRE() {
        return NUM_FIRE;
    }

    public void setNUM_FIRE(int NUM_FIRE) {
        if (NUM_FIRE < 0)
            NUM_FIRE = 0;
        this.NUM_FIRE = NUM_FIRE;
    }

    public int getMAX_FUEL_FIRE() {
        return MAX_FUEL_FIRE;
    }

    public void setMAX_FUEL_FIRE(int MAX_FUEL_FIRE) {
        if (MAX_FUEL_FIRE < SPARE_FUEL_LEVEL_FIRE)
            MAX_FUEL_FIRE = SPARE_FUEL_LEVEL_FIRE;
        if (MAX_FUEL_FIRE < 1)
            MAX_FUEL_FIRE = 1;
        this.MAX_FUEL_FIRE = MAX_FUEL_FIRE;
    }

    public int getSPARE_FUEL_LEVEL_FIRE() {
        return SPARE_FUEL_LEVEL_FIRE;
    }

    public void setSPARE_FUEL_LEVEL_FIRE(int SPARE_FUEL_LEVEL_FIRE) {
        if (MAX_FUEL_FIRE < SPARE_FUEL_LEVEL_FIRE)
            SPARE_FUEL_LEVEL_FIRE = MAX_FUEL_FIRE;
        if (SPARE_FUEL_LEVEL_FIRE < 1)
            SPARE_FUEL_LEVEL_FIRE = 1;
        this.SPARE_FUEL_LEVEL_FIRE = SPARE_FUEL_LEVEL_FIRE;
    }

    public double getFUEL_RATE_FIRE() {
        return FUEL_RATE_FIRE;
    }

    public void setFUEL_RATE_FIRE(double FUEL_RATE_FIRE) {
        if (FUEL_RATE_FIRE < 0.0)
            FUEL_RATE_FIRE = 0.0;
        this.FUEL_RATE_FIRE = FUEL_RATE_FIRE;
    }

    // ******************************************************
    // Police vehicle variables
    private int NUM_POLICE = 10;
    private int MAX_FUEL_POLICE = 350;
    private int SPARE_FUEL_LEVEL_POLICE = 60;
    private double FUEL_RATE_POLICE = 2.0;

    public int getNUM_POLICE() {
        return NUM_POLICE;
    }

    public void setNUM_POLICE(int NUM_POLICE) {
        if (NUM_POLICE < 0)
            NUM_POLICE = 0;
        this.NUM_POLICE = NUM_POLICE;
    }

    public int getMAX_FUEL_POLICE() {
        return MAX_FUEL_POLICE;
    }

    public void setMAX_FUEL_POLICE(int MAX_FUEL_POLICE) {
        if (MAX_FUEL_POLICE < SPARE_FUEL_LEVEL_POLICE)
            MAX_FUEL_POLICE = SPARE_FUEL_LEVEL_POLICE;
        if (MAX_FUEL_POLICE < 1)
            MAX_FUEL_POLICE = 1;
        this.MAX_FUEL_POLICE = MAX_FUEL_POLICE;
    }

    public int getSPARE_FUEL_LEVEL_POLICE() {
        return SPARE_FUEL_LEVEL_POLICE;
    }

    public void setSPARE_FUEL_LEVEL_POLICE(int SPARE_FUEL_LEVEL_POLICE) {
        if (MAX_FUEL_POLICE < SPARE_FUEL_LEVEL_POLICE)
            SPARE_FUEL_LEVEL_POLICE = MAX_FUEL_POLICE;
        if (SPARE_FUEL_LEVEL_POLICE < 1)
            SPARE_FUEL_LEVEL_POLICE = 1;
        this.SPARE_FUEL_LEVEL_POLICE = SPARE_FUEL_LEVEL_POLICE;
    }

    public double getFUEL_RATE_POLICE() {
        return FUEL_RATE_POLICE;
    }

    public void setFUEL_RATE_POLICE(double FUEL_RATE_POLICE) {
        if (FUEL_RATE_POLICE < 0.0)
            FUEL_RATE_POLICE = 0.0;
        this.FUEL_RATE_POLICE = FUEL_RATE_POLICE;
    }

    // ******************************************************
    // Emergency variables (only for random generation of emergencies, i.e. when deterministic is false)
    private int TIME_BETWEEN_CALLS_MS = 1000;
    private int MIN_VEHICLES_EMERGENCY = 1;
    private int MAX_VEHICLES_EMERGENCY = 3;
    private int MIN_DURATION_MS = 2000;
    private int MAX_DURATION_MS = 5000;

    public int getMIN_DURATION_MS() {
        return MIN_DURATION_MS;
    }

    public void setMIN_DURATION_MS(int MIN_DURATION_MS) {
        if (MIN_DURATION_MS > MAX_DURATION_MS)
            MIN_DURATION_MS = MAX_DURATION_MS;
        if (MIN_DURATION_MS < 0)
            MIN_DURATION_MS = 0;
        this.MIN_DURATION_MS = MIN_DURATION_MS;
    }

    public int getMAX_DURATION_MS() {
        return MAX_DURATION_MS;
    }

    public void setMAX_DURATION_MS(int MAX_DURATION_MS) {
        if (MAX_DURATION_MS < MIN_DURATION_MS)
            MAX_DURATION_MS = MIN_DURATION_MS;
        if (MAX_DURATION_MS < 0)
            MAX_DURATION_MS = 0;
        this.MAX_DURATION_MS = MAX_DURATION_MS;
    }

    public int getMIN_VEHICLES_EMERGENCY() {
        return MIN_VEHICLES_EMERGENCY;
    }

    public void setMIN_VEHICLES_EMERGENCY(int MIN_VEHICLES_EMERGENCY) {
        if (MIN_VEHICLES_EMERGENCY > MAX_VEHICLES_EMERGENCY)
            MIN_VEHICLES_EMERGENCY = MAX_VEHICLES_EMERGENCY;
        if (MIN_VEHICLES_EMERGENCY < 1)
            MIN_VEHICLES_EMERGENCY = 1;

        this.MIN_VEHICLES_EMERGENCY = MIN_VEHICLES_EMERGENCY;
    }

    public int getMAX_VEHICLES_EMERGENCY() {
        return MAX_VEHICLES_EMERGENCY;
    }

    public void setMAX_VEHICLES_EMERGENCY(int MAX_VEHICLES_EMERGENCY) {
        if (MAX_VEHICLES_EMERGENCY < MIN_VEHICLES_EMERGENCY)
            MAX_VEHICLES_EMERGENCY = MIN_VEHICLES_EMERGENCY;
        if (MAX_VEHICLES_EMERGENCY < 1)
            MAX_VEHICLES_EMERGENCY = 1;

        this.MAX_VEHICLES_EMERGENCY = MAX_VEHICLES_EMERGENCY;
    }

    public int getTIME_BETWEEN_CALLS_MS() {
        return TIME_BETWEEN_CALLS_MS;
    }

    public void setTIME_BETWEEN_CALLS_MS(int TIME_BETWEEN_CALLS_MS) {
        if (TIME_BETWEEN_CALLS_MS < 0)
            TIME_BETWEEN_CALLS_MS = 0;
        this.TIME_BETWEEN_CALLS_MS = TIME_BETWEEN_CALLS_MS;
    }

    // ******************************************************
    // launch arguments (simple logs and deterministic emergencies i.e. reading from file)
    private boolean SIMPLE = true;
    private boolean DETERMINISTIC = false;

    public boolean isSIMPLE() {
        return SIMPLE;
    }

    public void setSIMPLE(boolean SIMPLE) {
        this.SIMPLE = SIMPLE;
    }

    public boolean isDETERMINISTIC() {
        return DETERMINISTIC;
    }

    public void setDETERMINISTIC(boolean DETERMINISTIC) {
        this.DETERMINISTIC = DETERMINISTIC;
    }

    // ******************************************************

    @Override
    public String[] getInitParam() {
        return new String[] {
            "CITY_WIDTH",
            "CITY_HEIGHT",

            "MIN_NUM_EMPLOYEES",
            "MAX_NUM_EMPLOYEES",
            "REFUEL_DURATION_MS",
            "EMPLOYEE_CHANGE_PROB",

            "MULTIPLIER_EMPLOYEE",
            "MULTIPLIER_DISTANCE",
            "MULTIPLIER_FUEL",
            "MULTIPLIER_EMPLOYEE_FUEL",

            "NUM_INEM",
            "MAX_FUEL_INEM",
            "SPARE_FUEL_LEVEL_INEM",
            "FUEL_RATE_INEM",

            "NUM_FIRE",
            "MAX_FUEL_FIRE",
            "SPARE_FUEL_LEVEL_FIRE",
            "FUEL_RATE_FIRE",

            "NUM_POLICE",
            "MAX_FUEL_POLICE",
            "SPARE_FUEL_LEVEL_POLICE",
            "FUEL_RATE_POLICE",

            "TIME_BETWEEN_CALLS_MS",
            "MIN_VEHICLES_EMERGENCY",
            "MAX_VEHICLES_EMERGENCY",
            "MIN_DURATION_MS",
            "MAX_DURATION_MS",

            "SIMPLE",
            "DETERMINISTIC",
        };
    }

    @Override
	public void setup() {
		super.setup();
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
        // display surface
        if (dsurf != null) dsurf.dispose();
        dsurf = new MyDisplaySurface(this, "Service Consumer/Provider Display");
        registerDisplaySurface("Service Consumer/Provider Display", dsurf);
        updateNetwork();
        dsurf.display();
        //*******************************************************************
        // Plot 1
        if (plot != null) plot.dispose();
        plot = new OpenSequenceGraph("Percentages", this);
        plot.setAxisTitles("time", "% successful service executions");

        plot.addSequence("Occupied Vehicles Percentage", new Sequence() {
            public double getSValue() {
                // iterate through vehicles
                return Results.getOccupiedVehicles(vehicles);
            }
        });
        plot.addSequence("Emergencies Success Percentage", new Sequence() {
            public double getSValue() {
                return Results.getSuccessEmergenciesPerc();
            }
        });

        plot.addSequence("Emergencies Success Percentage", new Sequence() {
            public double getSValue() {
                return Results.getNumberEmergFirstPriority()/Results.getNumberEmergencies();
            }
        });

        //*********************************************************************
        //Plot 2

        // graph
        if (plot2 != null) plot2.dispose();
        plot2 = new OpenSequenceGraph("Service performance", this);
        plot2.setAxisTitles("time", "% successful service executions");

        /*//Num emergency com veiculo de primeira prioridade
        plot2.addSequence("Number of Emergencies first priority", new Sequence() {
            public double getSValue() {
                // iterate through vehicles
                return Results.getNumberEmergFirstPriority();
            }
        });*/

        //Number of times vehicles refuelled
        plot2.addSequence("Times Refueled", new Sequence() {
            public double getSValue() {
                // iterate through vehicles
                return Results.getNumberTimesRefuelled();
            }
        });

        plot2.addSequence("Average Trip Duration", new Sequence() {
            public double getSValue() {
                return Results.getAvgTripDuration();
            }
        });



        //Tempo medio de espera de emergencia;
        //Numero de * total que tiveram que abastecer




        plot.display();
        plot2.display();
        getSchedule().scheduleActionAtInterval(1, dsurf, "updateDisplay", Schedule.LAST);
        getSchedule().scheduleActionAtInterval(TICKS_FOR_STEP, plot, "step", Schedule.LAST);
        getSchedule().scheduleActionAtInterval(TICKS_FOR_STEP, plot2, "step", Schedule.LAST);
        getSchedule().scheduleActionAtInterval(TICKS_FOR_STEP, this, "step", Schedule.INTERVAL_UPDATER);
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

        Point.setWidth(CITY_WIDTH);
        Point.setHeight(CITY_HEIGHT);

        try {
            // ----------------------------------------------------
            // starting control tower agent
            ControlTowerAgent controlTowerAgent = new ControlTowerAgent();
            AgentController controlTower = container.acceptNewAgent(ControlTowerAgent.getDFName(), controlTowerAgent);
            LoggerHelper.get().logInfo("START - Started control tower");
            controlTower.start();
            GUI.setControlTowerNode( GUI.generateNode(ControlTowerAgent.getDFName(),Color.GREEN, WIDTH/2,HEIGHT/2,5,false));

            // ----------------------------------------------------
            // starting vehicle agents
            createVehicles(NUM_INEM, NUM_FIRE, NUM_POLICE);
            startVehicles(container);

            // ----------------------------------------------------
            // starting client agent
            ClientAgent clientAgent = new ClientAgent("johnny", ControlTowerAgent.getDFName(), DETERMINISTIC,
                    TIME_BETWEEN_CALLS_MS, MIN_VEHICLES_EMERGENCY, MAX_VEHICLES_EMERGENCY, MIN_DURATION_MS, MAX_DURATION_MS);
            AgentController client = container.acceptNewAgent(clientAgent.getClientName(), clientAgent);
            String deterministicInfo = DETERMINISTIC ? "deterministic" : "random";
            LoggerHelper.get().logInfo("CLIENT - Started " + deterministicInfo + " client " + clientAgent.getClientName());
            client.start();

        } catch (StaleProxyException e) {
            e.printStackTrace();
        }
    }

    private void createVehicles(int numberInem, int numberFire, int numberPolice){
        LoggerHelper.get().logCreateVehicles(numberInem, numberFire, numberPolice);

        for (int i = 0; i < numberInem; i++) {
            String name = "Inem" + i;
            VehicleAgent vehicleAgent = new InemAgent(name,
                    MIN_NUM_EMPLOYEES, MAX_NUM_EMPLOYEES, REFUEL_DURATION_MS,
                    EMPLOYEE_CHANGE_PROB, MULTIPLIER_EMPLOYEE, MULTIPLIER_DISTANCE, MULTIPLIER_FUEL,
                    MULTIPLIER_EMPLOYEE_FUEL, MAX_FUEL_INEM, SPARE_FUEL_LEVEL_INEM, FUEL_RATE_INEM);
            vehicles.add(vehicleAgent);
        }
        for (int i = 0; i < numberFire; i++) {
            String name = "Fireman" + i;
            VehicleAgent vehicleAgent = new FiremanAgent(name,
                    MIN_NUM_EMPLOYEES, MAX_NUM_EMPLOYEES, REFUEL_DURATION_MS,
                    EMPLOYEE_CHANGE_PROB, MULTIPLIER_EMPLOYEE, MULTIPLIER_DISTANCE, MULTIPLIER_FUEL,
                    MULTIPLIER_EMPLOYEE_FUEL, MAX_FUEL_FIRE, SPARE_FUEL_LEVEL_FIRE, FUEL_RATE_FIRE);
            vehicles.add(vehicleAgent);
        }
        for (int i = 0; i < numberPolice; i++) {
            String name = "Police" + i;
            VehicleAgent vehicleAgent = new PoliceAgent(name,
                    MIN_NUM_EMPLOYEES, MAX_NUM_EMPLOYEES, REFUEL_DURATION_MS,
                    EMPLOYEE_CHANGE_PROB, MULTIPLIER_EMPLOYEE, MULTIPLIER_DISTANCE, MULTIPLIER_FUEL,
                    MULTIPLIER_EMPLOYEE_FUEL, MAX_FUEL_POLICE, SPARE_FUEL_LEVEL_POLICE, FUEL_RATE_POLICE);
            vehicles.add(vehicleAgent);

        }
    }

    private void startVehicles(ContainerController container) {
        for (VehicleAgent vehicleAgent : vehicles){
            AgentController vehicle = null;
            try {
                vehicle = container.acceptNewAgent(vehicleAgent.getVehicleName(), vehicleAgent);
                vehicle.start();
                GUI.generateVehicleNode(vehicleAgent);
            } catch (StaleProxyException e) {
                e.printStackTrace();
            }
        }
    }


    public void step()
    {
        for(VehicleAgent vehicle : vehicles) {
            vehicle.updateVehicleCoordinates();
        }
    }

    public void updateNetwork() {
        if (display != null)
            dsurf.removeProbeableDisplayable(display);
        display = new Network2DDisplay(GUI.getNodes(), WIDTH, HEIGHT);
        dsurf.addDisplayableProbeable(display, "Network Display" + display.hashCode());
        dsurf.addZoomable(display);
        this.addSimEventListener(dsurf);

    }

    /**
     * Launching Repast3
     * @param args
     */
    public static void main(String[] args) {
        SimInit init = new SimInit();
        init.setNumRuns(1);   // works only in batch mode
        init.loadModel(RepastLauncher.get(), null, BATCH_MODE);
    }

}
