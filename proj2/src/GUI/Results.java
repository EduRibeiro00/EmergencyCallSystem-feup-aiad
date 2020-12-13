package GUI;

import agents.VehicleAgent;

import java.util.List;

public class Results {

    private static double numberFailedEmergencies = 0;
    private static double numberEmergencies = 0;
    private static double numberEmergFirstPriority = 0;
    private static double numberTimesRefuelled = 0;
    private static double totalTripDuration = 0;
    private static double numberTripDurations = 0;

    public static  void incrementFailedEmergencies(){
        numberFailedEmergencies++;
    }

    public static  void incrementEmergencies(){
        numberEmergencies++;
    }

    public static double getOccupiedVehicles(List<VehicleAgent> vehicles){
        double v = 0.0;
        for (VehicleAgent vehicle : vehicles) {
            v += vehicle.getOccupied().get() ? 1 : 0;
        }
        return v / vehicles.size();
    }

    public static double getNumberEmergencies() {
        return numberEmergencies;
    }

    public static double getSuccessEmergenciesPerc(){
        return (numberEmergencies - numberFailedEmergencies) / (numberEmergencies == 0 ? 1 : numberEmergencies);
    }

    public static double getNumberEmergFirstPriority() {
        return numberEmergFirstPriority;
    }

    public static void setNumberEmergFirstPriority(double numberEmergFirstPriority) {
        Results.numberEmergFirstPriority = numberEmergFirstPriority;
    }

    public static void incrementEmergFirstPriority() {
        Results.numberEmergFirstPriority++;
    }

    public static double getNumberTimesRefuelled() {
        return numberTimesRefuelled;
    }

    public static void setNumberTimesRefuelled(double numberTimesRefuelled) {
        Results.numberTimesRefuelled = numberTimesRefuelled;
    }

    public static void incrementTimesRefuelled() {
        Results.numberTimesRefuelled++;
    }

    public static double getAvgTripDuration() {
        return (totalTripDuration / numberTripDurations) / 1000;
    }

    public static void addTripTime(double tripTime) {
        Results.numberTripDurations++;
        totalTripDuration =  (totalTripDuration + tripTime);
    }
}
