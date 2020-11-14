package messages;

public class VehicleResponse implements java.io.Serializable {
    private double value;

    public VehicleResponse(double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }
}