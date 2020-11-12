package messages;

public class VehicleResponse implements java.io.Serializable {
    private double value;
    private final int NUMBER_EMPLOYEES_FACTOR = 3;


    public VehicleResponse(double distanceValue,int numberVehicles) {
        this.value = distanceValue + NUMBER_EMPLOYEES_FACTOR *  numberVehicles; //TODO determinar coeficiente?
        System.out.println("Distance value " + String.valueOf(distanceValue) + " Number vehicles " + numberVehicles);
    }

    public double getValue() {
        return value;
    }
}