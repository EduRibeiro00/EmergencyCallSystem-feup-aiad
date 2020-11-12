package messages;

public class VehicleResponse implements java.io.Serializable {
    private double value;
    private final int NUMBER_EMPLOYEES_FACTOR = 3;


    public VehicleResponse(double distanceValue,int numberEmployees) {
        this.value = distanceValue + NUMBER_EMPLOYEES_FACTOR *  numberEmployees; //TODO determinar coeficiente?
        System.out.println("Distance value " + distanceValue + " Number employees " + numberEmployees);
    }

    public double getValue() {
        return value;
    }
}