package utils;

public enum VehicleType {
    POLICE("police-vehicle"),
    INEM("inem-vehicle"),
    FIREMAN("fireman-vehicle");

    private final String dfName;
    private VehicleType(String dfName) { this.dfName = dfName; }
    public String getDFName() { return this.dfName; }
}
