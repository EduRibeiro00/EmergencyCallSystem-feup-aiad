package utils;

import GUI.GUI;
import uchicago.src.sim.network.DefaultDrawableNode;

public class Emergency  implements java.io.Serializable {
    private EmergencyType emergencyType;
    private Point coordinates;
    private int numberVehicles;
    private int duration; // in ms
    private static int  currentID = 0;
    private DefaultDrawableNode node;



    private int id = 0;

    public Emergency(EmergencyType emergencyType, Point coordinates, int numberVehicles, int duration) {
        this.emergencyType = emergencyType;
        this.coordinates = coordinates;
        this.numberVehicles = numberVehicles;
        this.duration = duration;
        this.id = currentID;

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

    public int getId() {return id;}

    public void setId(int id) { this.id = id;}

    public static void incrementID(){currentID++;}

    @Override
    public String toString() {
        return "Emergency { " +
                "id=" + id +
                ", emergencyType=" + emergencyType +
                ", coordinates=" + coordinates +
                ", numberVehicles=" + numberVehicles +
                ", duration=" + duration + "ms" +
                '}';
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public DefaultDrawableNode getNode() { return node; }

    public void setNode(DefaultDrawableNode node) { this.node = node; }
}
