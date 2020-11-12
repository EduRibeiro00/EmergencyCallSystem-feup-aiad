package messages;

public class VehicleResponse implements java.io.Serializable {
    private double value;


    public VehicleResponse(double distanceValue,int numberVehicles) {
        this.value = distanceValue + numberVehicles; //TODO determinar coeficiente?
        System.out.println("Distance value " + String.valueOf(distanceValue) + " Number vehicles " + numberVehicles);
    }

    public double getValue() {
        return value;
    }
}