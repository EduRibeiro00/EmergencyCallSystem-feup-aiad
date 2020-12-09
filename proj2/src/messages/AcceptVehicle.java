package messages;

import utils.Emergency;
import utils.Point;

public class AcceptVehicle implements java.io.Serializable {
    private Point coordinates;
    private int accidentDuration; // in ms
    private Emergency emergency;

    public AcceptVehicle(Emergency emergency) {
        this.coordinates = emergency.getCoordinates();
        this.accidentDuration = emergency.getDuration();
        this.emergency =emergency;
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

    public Emergency getEmergency() { return emergency; }

    public void setEmergency(Emergency emergency) { this.emergency = emergency; }
}