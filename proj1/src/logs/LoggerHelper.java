package logs;

import behaviours.VehicleBehaviour;
import utils.Emergency;
import utils.Point;
import utils.VehicleType;

import java.io.IOException;
import java.util.logging.*;

public class LoggerHelper {
    private final static String FILEPATH = System.getProperty("user.dir") + "/src/logs/proj.log";
    private final Logger logger;

    private static LoggerHelper instance;

    public static LoggerHelper get() {
        if (instance == null) {
            try {
                instance = new LoggerHelper();
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
        return instance;
    }

    private LoggerHelper() throws IOException {
        logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
        logger.setLevel(Level.INFO);
        FileHandler fileTxt = new FileHandler(FILEPATH);

        // create a TXT formatter
        SimpleFormatter formatterTxt = new SimpleFormatter();
        fileTxt.setFormatter(formatterTxt);
        logger.addHandler(fileTxt);

        logger.info("--------------- STARTED NEW EXECUTION ---------------");
    }

    public void logInfo(String text) {
        logger.info(text);
    }

    public void logError(String text) {
        logger.severe(text);
    }

    public void logWarning(String text) {
        logger.warning(text);
    }

    public void logCreateVehicles(int numberInem, int numberFire, int numberPolice) {
        int total = numberFire + numberInem + numberPolice;

        logger.info("START - Going to start a total of " + total + " vehicles (" +
                "Inem=" + numberInem + ", firemen=" + numberFire + ", police=" + numberPolice + ")");
    }

    public void logReceivedEmergency(Emergency emergency) {
        logger.info (
                "Tower received emergency: " + emergency
        );
    }

    public void logNotEnoughVehicles(Emergency emergency) {
        logger.info(
                "Not enough available vehicles for emergency " + emergency
        );
    }

    public void logStartVehicle(String vehicleName, VehicleType vehicleType, Point coordinates) {
        logger.info(
                "VEHICLE - Name: " + vehicleName + "; " +
                vehicleType + " created at coordinates " + coordinates
        );
    }

    public void logRejectProposalOccupied(String vehicleName) {
        logger.info(
                vehicleName + " - Tower did not accept because I was occupied"
        );
    }

    public void logRejectProposal(String vehicleName, Point coordinates) {
        logger.info(
                vehicleName + " - Tower refused my service; my location is " + coordinates
        );
    }

    public void logAcceptProposal(String vehicleName, Point coordinates) {
        logger.info(
                vehicleName + " - Tower selected me for the emergency! My location is " + coordinates
        );
    }

    public void logHandleCfp(String vehicleName) {
        /*logger.info(
                vehicleName + " - received CFP from tower"
        );*/
    }

    public void logReceiveVehiclePropose(String vehicleName, Point coordinates, double distance) {
        logger.info(
                "Tower - Received propose from vehicle " +
                vehicleName +
                ", at coordinates " + coordinates +
                "; distance = " + distance
        );
    }

    public void logReceiveVehicleRefuse(String vehicleName) {
        logger.info(
            "Tower - " + vehicleName + " was occupied"
        );
    }

    public void logAcceptVehicle(String vehicleName, double distance) {
        logger.info(
                "Tower - Going to accept vehicle " +
                vehicleName +
                ", distance = " +
                distance
        );
    }
}
