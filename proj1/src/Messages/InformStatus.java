package Messages;


import utils.AgentTypes.AgentType;

public class InformStatus implements java.io.Serializable {
    private int distance;
    private AgentType type;



    public InformStatus(int distance, AgentType type ) {
        this.distance = distance;
        this.type = type;
    }
    
    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public AgentType getType() {
        return type;
    }

    public void setType(AgentType type) {
        this.type = type;
    }
}