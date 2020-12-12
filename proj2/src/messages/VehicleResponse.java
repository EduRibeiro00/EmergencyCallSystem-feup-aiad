package messages;

public class VehicleResponse implements java.io.Serializable {
    private double value;
    private String vehicleName;

    public VehicleResponse(double value, String vehicleName) {
        this.value = value;
        this.vehicleName = vehicleName;
    }

    public double getValue() {
        return value;
    }

    public String getVehicleName() {
        return vehicleName;
    }
}