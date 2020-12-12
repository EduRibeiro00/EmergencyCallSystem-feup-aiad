package GUI;

import agents.VehicleAgent;
import uchicago.src.sim.gui.OvalNetworkItem;
import uchicago.src.sim.network.DefaultDrawableEdge;
import uchicago.src.sim.network.DefaultDrawableNode;
import utils.Emergency;
import utils.VehicleType;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GUI {
    // TODO: mudar para hash map se der
    private static List<DefaultDrawableNode> nodes = new ArrayList<>();

    public static Color parseColor(VehicleAgent agent){
        return switch (agent.getType()) {
            case FIREMAN -> Color.red;
            case INEM -> Color.WHITE;
            case POLICE -> Color.blue;
        };
    }
    private static Color parseEmergencyColor(Emergency emergency){
        return switch (emergency.getEmergencyType()) {
            case FIRE -> Color.orange;
            case ACCIDENT -> Color.magenta;
            case ROBBERY -> Color.cyan;
        };
    }

    public static DefaultDrawableNode generateNode(String label, Color color, double x, double y, int size) {
        OvalNetworkItem oval = new OvalNetworkItem(x, y);
        oval.allowResizing(false);
        oval.setHeight(size);
        oval.setWidth(size);
        DefaultDrawableNode node = new DefaultDrawableNode(label, oval);
        node.setColor(color);
        GUI.addNode(node);
        return node;
    }

    public static DefaultDrawableNode getNode(String label) {
        for (DefaultDrawableNode node : nodes) {
            if (node.getNodeLabel().equals(label)) {
                return node;
            }
         }

        return null;
    }

    public static void generateVehicleNode(VehicleAgent vehicleAgent) {
        DefaultDrawableNode node =
                GUI.generateNode(vehicleAgent.getVehicleName(), GUI.parseColor(vehicleAgent),
                        vehicleAgent.getCoordinates().getX(), vehicleAgent.getCoordinates().getY(),2);

        vehicleAgent.setNode(node);
    }

    public static void generateEmergencyNode(Emergency emergency) {
        GUI.generateNode(getEmergencyLabel(emergency.getId()), GUI.parseEmergencyColor(emergency),
                emergency.getCoordinates().getX(), emergency.getCoordinates().getY(),1);
    }

    public static String getEmergencyLabel(int emergencyId) {
        return "Emergency" + emergencyId;
    }

    public static void removeNode(String label){
        nodes.removeIf(node -> node.getNodeLabel().equals(label));
    }

    public static void createEdge(DefaultDrawableNode node1, DefaultDrawableNode node2){
        //Sample edge
        DefaultDrawableEdge sampleEdge = new DefaultDrawableEdge(node1, node2);
        node1.addOutEdge(sampleEdge);
        node2.addInEdge(sampleEdge);
    }

    public static void removeEdge(DefaultDrawableNode node1, DefaultDrawableNode node2) {
        node1.removeEdgesTo(node2);
        node2.removeEdgesFrom(node1);
    }

    public static void createEdgeName(DefaultDrawableNode myNode, String name, Color color){
        DefaultDrawableNode to = GUI.getNode(name);
        Edge edge = new Edge(myNode, to);
        edge.setColor(color);
        myNode.addOutEdge(edge);
    }

    public static void addNode(DefaultDrawableNode node) { nodes.add(node);}

    public static List<DefaultDrawableNode> getNodes(){return nodes;}
}
