package behaviours;

import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import jade.proto.ContractNetResponder;
import logs.LoggerHelper;
import messages.AcceptVehicle;
import messages.InformStatus;
import messages.Messages;
import utils.Point;
import utils.VehicleType;
import java.io.IOException;

public abstract class VehicleBehaviour extends ContractNetResponder {
    protected Point coordinates;
    protected boolean occupied = false;
    protected int duration = 0;
    private long activatedAt = Long.MAX_VALUE;

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
            long activeFor = System.currentTimeMillis() - activatedAt;
            if(activeFor>=0 && activeFor>=duration){
                accpetCfp(vehicleReply);
                occupied = false;
                activatedAt = Long.MAX_VALUE;
                //So começar a contagem quando ele é aceite
                //activatedAt = System.currentTimeMillis();
            }else {
                vehicleReply.setPerformative(ACLMessage.REFUSE);
                vehicleReply.setContent(Messages.IS_OCCUPIED);
            }
        }else {
            accpetCfp(vehicleReply);
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


    @Override
    public ACLMessage handleAcceptProposal(ACLMessage cfp, ACLMessage propose, ACLMessage accept) {

        if (accept.getPerformative() == ACLMessage.ACCEPT_PROPOSAL) {
            try {
                Object content = accept.getContentObject();
                if(content instanceof AcceptVehicle){

                    AcceptVehicle acceptVehicleMsg = (AcceptVehicle) content;
                    duration = acceptVehicleMsg.getAccidentDuration() ; // + distance/10 * 1000 // TODO: Quando for o veiculo a calcular a distancia adicionar aqui
                    coordinates = acceptVehicleMsg.getCoordinates();
                    System.out.println("Vehicle will be occupied for:" + duration/1000 + " seconds" );


                }
            } catch (UnreadableException e) {
                e.printStackTrace();
            }

        }


        LoggerHelper.get().logAcceptProposal(this.myAgent.getLocalName(), coordinates);
        occupied = true;
        activatedAt = System.currentTimeMillis();
        return null;
    }

    public Point getCoordinates() {
        return coordinates;
    }

    protected void accpetCfp(ACLMessage vehicleReply){
        vehicleReply.setPerformative(ACLMessage.PROPOSE);
        try {
            vehicleReply.setContentObject(new InformStatus(coordinates));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public abstract VehicleType getVehicleType();
}
