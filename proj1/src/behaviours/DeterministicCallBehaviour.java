package behaviours;

import jade.core.AID;
import experiment.DeterministicEmergency;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import logs.LoggerHelper;
import utils.Emergency;
import utils.EmergencyType;
import utils.Point;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DeterministicCallBehaviour extends SimpleBehaviour {
    private final String EMERGENCIES_FILE_PATH = System.getProperty("user.dir") + "/src/experiment/emergencies.gen";
    private final AID controlTowerID;
    private List<DeterministicEmergency> deterministicEmergencies;
    private boolean done;

    public DeterministicCallBehaviour(AID controlTowerID) {
        this.controlTowerID = controlTowerID;
        this.done = false;
        this.readEmergencies();
    }

    private void readEmergencies() {
        deterministicEmergencies = new ArrayList<>();

        File emergenciesToGenerate = new File(EMERGENCIES_FILE_PATH);
        try {
            Scanner scanner = new Scanner(emergenciesToGenerate);
            while(scanner.hasNextLine()) readEmergency(scanner.nextLine());
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void readEmergency(String emergencyData) {
        Pattern emergencyPattern = Pattern.compile("(FIRE|ACCIDENT|ROBBERY) : \\((\\d+.\\d+), (\\d+.\\d+)\\) : (\\d+) : (\\d+) : (\\d+)");
        Matcher matcher = emergencyPattern.matcher(emergencyData);

        if(!matcher.matches()) {
            System.err.println("No Emergency match was found for: ");
            System.err.println(emergencyData);
            System.err.println();
            return;
        }
        
        EmergencyType type = EmergencyType.valueOf(
                matcher.group(1)
        );
        Point point = new Point(
                Double.parseDouble(matcher.group(2)),
                Double.parseDouble(matcher.group(3))
        );
        int numVehicles = Integer.parseInt(
                matcher.group(4)
        );
        int duration = Integer.parseInt(
                matcher.group(5)
        );
        int delay = Integer.parseInt(
                matcher.group(6)
        );

        DeterministicEmergency demEmg = new DeterministicEmergency(
                new Emergency(type, point, numVehicles, duration),
                delay
        );
        deterministicEmergencies.add(demEmg);
    }

    @Override
    public void action() {
        for(DeterministicEmergency demEmg : deterministicEmergencies) {
            ACLMessage request = new ACLMessage(ACLMessage.INFORM);
            request.addReceiver(controlTowerID);

            try {
                request.setContentObject(demEmg.getEmergency());
            } catch (IOException e) {
                e.printStackTrace();
            }


            try {
                Thread.sleep(demEmg.getDelay());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            myAgent.send(request);
            LoggerHelper.get().logCreatedEmergency(demEmg.getEmergency());
        }

        done = true;
    }

    @Override
    public boolean done() {
        return done;
    }
}
