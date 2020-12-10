package GUI;

import agents.VehicleAgent;

import java.util.List;

public class Results {

    private static int numberFailedEmergencies = 0;
    private static int numberEmergencies = 0;

    public static int getNumberFailedEmergencies() {
        return numberFailedEmergencies;
    }
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

    public static int getNumberEmergencies() {
        return numberEmergencies;
    }

    public static double getSuccessEmergenciesPerc(){
        return (numberEmergencies-numberFailedEmergencies)/(numberEmergencies==0 ? 1:numberEmergencies);
    }
}
