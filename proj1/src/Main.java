import agents.ControlTowerAgent;
import agents.VehicleAgent;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

/**
 * Main class for the program.
 */
public class Main {
    /**
     * Main function of the program.
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        Runtime rt = Runtime.instance();
        Profile p = new ProfileImpl();
        ContainerController mainContainer = rt.createMainContainer(p);

        try {
            AgentController ac1 = mainContainer.acceptNewAgent("buyer", new ControlTowerAgent());
            AgentController ac2 = mainContainer.acceptNewAgent("seller", new VehicleAgent());
            ac1.start();
            ac2.start();
        } catch (StaleProxyException e) {
            e.printStackTrace();
        }
    }
}
