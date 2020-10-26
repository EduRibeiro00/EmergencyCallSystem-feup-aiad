package messages;

import utils.Point;

public class InformStatus implements java.io.Serializable {
    private Point coordinates;

    public InformStatus(Point coordinates) {
        this.coordinates = coordinates;
    }
    
    public Point getCoordinates() {
        return coordinates;
    }
}