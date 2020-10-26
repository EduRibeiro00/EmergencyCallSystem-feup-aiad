package utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EmergencyVehiclePriorities {
    private static final ArrayList<VehicleType> fire = new ArrayList<>(){{
        add(VehicleType.FIREMAN);
        add(VehicleType.INEM);
        add(VehicleType.POLICE);
    }};
    private static final ArrayList<VehicleType> accident = new ArrayList<>(){{
        add(VehicleType.INEM);
        add(VehicleType.FIREMAN);
        add(VehicleType.POLICE);
    }};
    private static final ArrayList<VehicleType> robbery = new ArrayList<>(){{
        add(VehicleType.POLICE);
        add(VehicleType.INEM);
        add(VehicleType.FIREMAN);
    }};

    public static Map<EmergencyType, ArrayList<VehicleType>> vehiclePriorities = new HashMap<>() {{
        put(EmergencyType.FIRE, fire);
        put(EmergencyType.ACCIDENT, accident);
        put(EmergencyType.ROBBERY, robbery);
    }};
}
