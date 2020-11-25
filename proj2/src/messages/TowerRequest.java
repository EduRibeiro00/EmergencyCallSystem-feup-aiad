package messages;

import utils.Point;

public class TowerRequest implements java.io.Serializable {
    private Point coordinates;

    public TowerRequest(Point coordinates) {
        this.coordinates = coordinates;
    }

    public Point getCoordinates() {
        return coordinates;
    }
}
