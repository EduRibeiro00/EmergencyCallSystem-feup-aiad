package utils;

import jade.lang.acl.ACLMessage;

public class Candidate {
    private final double value;
    private final ACLMessage message;

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
