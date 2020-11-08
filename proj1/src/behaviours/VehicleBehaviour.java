package behaviours;

import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import jade.proto.ContractNetResponder;
import logs.LoggerHelper;
import messages.TowerRequest;
import messages.VehicleResponse;
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
            double value = 0;
            try {
                Point emergencyCoords = ((TowerRequest) cfp.getContentObject()).getCoordinates();
                value = coordinates.getDistance(emergencyCoords);
                // the higher the value, the better the vehicle is to go to the emergency.
                // value = -distance; if distance = 0, value is max.
                if (value == 0)
                    value = Integer.MAX_VALUE;
                else
                    value = -value;

            } catch (UnreadableException e) {
                e.printStackTrace();
            }

            try {
                vehicleReply.setContentObject(new VehicleResponse(value));
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
