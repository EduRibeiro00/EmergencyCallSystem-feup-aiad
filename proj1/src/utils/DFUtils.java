package utils;

import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import logs.LoggerHelper;

public class DFUtils {

    public static void registerInDF(Agent agent, String serviceName) {
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(agent.getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType(serviceName);
        sd.setName(agent.getLocalName());
        dfd.addServices(sd);

        try {
            DFService.register(agent, dfd);
        }
        catch (FIPAException fe) {
            fe.printStackTrace();
            LoggerHelper.get().logError(
                    "[DF ERROR] - Exception encountered when " + agent.getLocalName()
                            + " attempted to register to " + serviceName + " in the DF"
            );
        }
    }


    public static void deregisterFromDF(Agent agent) {
        try {
            DFService.deregister(agent);
        }
        catch (FIPAException fe) {
            fe.printStackTrace();
            LoggerHelper.get().logError(
                    "[DF ERROR] - Exception encountered when " + agent.getLocalName()
                            + " attempted to deregister from the DF"
            );
        }
    }

    public static DFAgentDescription[] fetchFromDF(Agent agent, String serviceName) {
        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType(serviceName);
        template.addServices(sd);
        try {
            return DFService.search(agent, template);
        }
        catch (FIPAException fe) {
            fe.printStackTrace();
            LoggerHelper.get().logError(
                    "[DF ERROR] - Exception encountered when " + agent.getLocalName()
                            + " attempted to fetch " + serviceName + " agents from the DF"
            );

            return null;
        }
    }
}
