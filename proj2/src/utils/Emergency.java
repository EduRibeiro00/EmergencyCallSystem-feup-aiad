package utils;

import agents.VehicleAgent;
import uchicago.src.sim.network.DefaultDrawableNode;

import GUI.GUI;

public class Emergency implements java.io.Serializable {
    private static int CURRENT_ID = 0;

    private int id;
    private EmergencyType emergencyType;
    private Point coordinates;
    private int numberVehicles;
    private int numberVehiclesLeftEmergency;
    private int duration; // in ms

    public Emergency(EmergencyType emergencyType, Point coordinates, int numberVehicles, int duration) {
        this.emergencyType = emergencyType;
        this.coordinates = coordinates;
        this.numberVehicles = numberVehicles;
        this.duration = duration;
        this.id = CURRENT_ID++;
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

    public static void incrementID(){
        CURRENT_ID++;
    }

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

    public void incrementLeftVehiclesEmerg(VehicleAgent vehicleAgent){
        numberVehiclesLeftEmergency++;
        GUI.removeEdge(vehicleAgent.getNode(), GUI.getNode(GUI.getEmergencyLabel(vehicleAgent.getEmergencyId())));
        if(numberVehiclesLeftEmergency==numberVehicles) {
            GUI.removeNode(GUI.getEmergencyLabel(id));
        }
    }

    public int getNumberVehiclesLeftEmergency() {
        return numberVehiclesLeftEmergency;
    }

    public void setNumberVehiclesLeftEmergency(int numberVehiclesLeftEmergency) {
        this.numberVehiclesLeftEmergency = numberVehiclesLeftEmergency;
    }
}
