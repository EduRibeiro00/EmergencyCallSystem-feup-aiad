package behaviours;

import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.ContractNetResponder;
import messages.InformStatus;
import utils.Point;
import utils.VehicleType;
import java.io.IOException;
import static messages.Messages.IS_OCCUPIED;

public abstract class VehicleBehaviour extends ContractNetResponder {
    protected Point coordinates;
    protected boolean occupied = false;

    public VehicleBehaviour(Agent agent, MessageTemplate msgTemp) {
        super(agent, msgTemp);
        coordinates = Point.genRandomPoint();
        System.out.println(getVehicleType() + " created at coordinates " + coordinates);
    }

    @Override
    public ACLMessage handleCfp(ACLMessage cfp) {
        ACLMessage vehicleReply = cfp.createReply();
        if (occupied) {
            vehicleReply.setPerformative(ACLMessage.REFUSE);
            vehicleReply.setContent(IS_OCCUPIED);
        }else {
            vehicleReply.setPerformative(ACLMessage.PROPOSE);
            try {
                vehicleReply.setContentObject(new InformStatus(coordinates));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return vehicleReply;
    }

    @Override
    public void handleRejectProposal(ACLMessage cfp, ACLMessage propose, ACLMessage reject) {
        if(occupied) System.out.println("Tower did not accept because I was occupied");
        else System.out.println("Tower refused my service; my location is " + coordinates);
    }

    // TODO: ao ser alocado a uma emergencia, mudar coordenadas do veiculo para a emergencia e passado um bocado desocupar (tendo em conta a distancia)
    @Override
    public ACLMessage handleAcceptProposal(ACLMessage cfp, ACLMessage propose, ACLMessage accept) {
        System.out.println("Tower selected me for the emergency! My location is " + coordinates);
        occupied = true;
        return null;
    }

    public abstract VehicleType getVehicleType();
}
