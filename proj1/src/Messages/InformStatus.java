package Messages;


public class InformStatus implements java.io.Serializable {
    private int distance;


    public InformStatus(int distance) {
        this.distance = distance;
    }
    
    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }
}