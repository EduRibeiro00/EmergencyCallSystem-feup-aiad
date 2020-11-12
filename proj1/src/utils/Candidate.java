package utils;

import jade.lang.acl.ACLMessage;

public class Candidate {
    private double distance;
    private ACLMessage message;

    public Candidate(double distance, ACLMessage message) {
        this.distance = distance;
        this.message = message;
    }

    public double getDistance() {
        return distance;
    }

    public ACLMessage getMessage() {
        return message;
    }
}
