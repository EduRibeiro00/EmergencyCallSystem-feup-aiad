package messages;

import utils.Point;

public class AcceptVehicle implements java.io.Serializable {
    private Point coordinates;
    private int accidentDuration;

    public AcceptVehicle(Point coordinates, int accidentDuration) {
        this.coordinates = coordinates;
        this.accidentDuration = accidentDuration;
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
}