package utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;




import static utils.AgentTypes.AgentType.*;
import static utils.Emergencies.EmergencyType.*;




public class EmergencyAgent {
    private static ArrayList<AgentTypes.AgentType> fire = new ArrayList<>(){{
        add(FIREMAN);add(INEM);add(POLICE);
    }};
    private static ArrayList<AgentTypes.AgentType> accident = new ArrayList<>(){{
        add(INEM);add(FIREMAN);add(POLICE);
    }};
    private static ArrayList<AgentTypes.AgentType> robbery = new ArrayList<>(){{
        add(POLICE);add(INEM);add(FIREMAN);
    }};

    public static Map<Emergencies.EmergencyType, ArrayList<AgentTypes.AgentType>> emergencyAgent = new HashMap<Emergencies.EmergencyType, ArrayList<AgentTypes.AgentType>>() {{
        put(Fire,fire);
        put(Accident,accident);
        put(Robbery,robbery);
    }};
}
