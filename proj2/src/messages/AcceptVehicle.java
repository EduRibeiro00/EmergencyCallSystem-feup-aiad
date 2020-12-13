package messages;

import utils.Emergency;
import utils.Point;

public class AcceptVehicle implements java.io.Serializable {
    private Point coordinates;
    private int accidentDuration; // in ms
    private int emergencyId;

    public AcceptVehicle(Emergency emergency) {
        this.coordinates = emergency.getCoordinates();
        this.accidentDuration = emergency.getDuration();
        this.emergencyId = emergency.getId();
    }

    public Point getCoordinates() {
        return coordinates;
    }

    public int getAccidentDuration() {
        return accidentDuration;
    }

    public void setAccidentDuration(int accidentDuration) {
        this.accidentDuration = accidentDuration;
    }

    public void setCoordinates(Point coordinates) {
        this.coordinates = coordinates;
    }

    public int getEmergencyId() {
        return emergencyId;
    }

    public void setEmergencyId(int emergencyId) {
        this.emergencyId = emergencyId;
    }
}