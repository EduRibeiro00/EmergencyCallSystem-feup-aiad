package experiment;

import utils.Emergency;

public class DeterministicEmergency {
    private Emergency emergency;
    private int delay; // in ms

    public DeterministicEmergency(Emergency emergency, int delay) {
        this.emergency = emergency;
        this.delay = delay;
    }

    public Emergency getEmergency() {
        return emergency;
    }

    public int getDelay() {
        return delay;
    }
}
