package behaviours;

import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.ContractNetResponder;
import logs.LoggerHelper;
import messages.InformStatus;
import messages.Messages;
import utils.Point;
import utils.VehicleType;
import java.io.IOException;

public abstract class VehicleBehaviour extends ContractNetResponder {
    protected Point coordinates;
    protected boolean occupied = false;

    public VehicleBehaviour(Agent agent, MessageTemplate msgTemp) {
        super(agent, msgTemp);
        coordinates = Point.genRandomPoint();
        LoggerHelper.get().logStartVehicle(
                this.myAgent.getLocalName(),
                getVehicleType(),
                coordinates
        );
    }

    @Override
    public ACLMessage handleCfp(ACLMessage cfp) {
        LoggerHelper.get().logHandleCfp(this.myAgent.getLocalName());
        ACLMessage vehicleReply = cfp.createReply();
        if (occupied) {
            vehicleReply.setPerformative(ACLMessage.REFUSE);
            vehicleReply.setContent(Messages.IS_OCCUPIED);
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
        if(occupied)
            LoggerHelper.get().logRejectProposalOccupied(this.myAgent.getLocalName());
        else
            LoggerHelper.get().logRejectProposal(this.myAgent.getLocalName(), coordinates);
    }

    // TODO: ao ser alocado a uma emergencia, mudar coordenadas do veiculo para a emergencia e passado um bocado desocupar (tendo em conta a distancia)
    @Override
    public ACLMessage handleAcceptProposal(ACLMessage cfp, ACLMessage propose, ACLMessage accept) {
        LoggerHelper.get().logAcceptProposal(this.myAgent.getLocalName(), coordinates);
        occupied = true;
        return null;
    }

    public Point getCoordinates() {
        return coordinates;
    }

    public abstract VehicleType getVehicleType();
}
