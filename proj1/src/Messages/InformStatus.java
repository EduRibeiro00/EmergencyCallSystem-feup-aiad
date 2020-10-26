package Messages;


import utils.AgentTypes.AgentType;

public class InformStatus implements java.io.Serializable {
    private int distance;
    private boolean occupied = false;



    public InformStatus(int distance,boolean occupied) {
        this.distance = distance;
        this.occupied = occupied;
    }
    
    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public boolean isOccupied() {
        return occupied;
    }

    public void setOccupied(boolean occupied) {
        this.occupied = occupied;
    }
}