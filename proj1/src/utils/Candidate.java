package utils;

import jade.lang.acl.ACLMessage;

public class Candidate {
    private double value;
    private ACLMessage message;

    public Candidate(double value, ACLMessage message) {
        this.value = value;
        this.message = message;
    }

    public double getValue() {
        return value;
    }

    public ACLMessage getMessage() {
        return message;
    }
}
