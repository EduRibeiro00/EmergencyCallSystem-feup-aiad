package utils;

public class Emergency implements java.io.Serializable {
    private EmergencyType emergencyType;
    private Point coordinates;
    private int numberVehicles;

    public Emergency(EmergencyType emergencyType, Point coordinates, int numberVehicles) {
        this.emergencyType = emergencyType;
        this.coordinates = coordinates;
        this.numberVehicles = numberVehicles;
    }

    public EmergencyType getEmergencyType() {
        return emergencyType;
    }

    public void setEmergencyType(EmergencyType emergencyType) {
        this.emergencyType = emergencyType;
    }

    public Point getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Point coordinates) {
        this.coordinates = coordinates;
    }

    public int getNumberVehicles() {
        return numberVehicles;
    }

    public void setNumberVehicles(int numberVehicles) {
        this.numberVehicles = numberVehicles;
    }

    @Override
    public String toString() {
        return "Emergency{" +
                "emergencyType=" + emergencyType +
                ", coordinates=" + coordinates +
                ", numberVehicles=" + numberVehicles +
                '}';
    }
}
