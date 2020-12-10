package GUI;

import agents.VehicleAgent;
import uchicago.src.sim.gui.OvalNetworkItem;
import uchicago.src.sim.network.DefaultDrawableEdge;
import uchicago.src.sim.network.DefaultDrawableNode;
import utils.Emergency;
import utils.VehicleType;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GUI {
    private static List<DefaultDrawableNode> nodes = new ArrayList<DefaultDrawableNode>();;


    public static Color parseColor(VehicleAgent agent){
        switch (agent.getType()){
            case FIREMAN:
                return Color.red;
            case INEM:
                return Color.WHITE;
            case POLICE: return Color.blue;
            default: return Color.BLACK;
        }
    }
    private static Color parseEmergencyColor(Emergency emergency){
        switch (emergency.getEmergencyType()){
            case FIRE:
                return Color.orange;
            case ACCIDENT:
                return Color.red;
            case ROBBERY: return Color.yellow;
            default: return Color.GRAY;
        }
    }

    public static DefaultDrawableNode generateNode(String label, Color color, double x, double y,int size) {
        OvalNetworkItem oval = new OvalNetworkItem(x,y);
        oval.allowResizing(false);
        oval.setHeight(size);
        oval.setWidth(size);
        DefaultDrawableNode node = new DefaultDrawableNode(label, oval);
        node.setColor(color);
        GUI.addNode(node);
        return node;
    }
    public static DefaultDrawableNode getNode(String label) {
        for(DefaultDrawableNode node : nodes) {
            if(node.getNodeLabel().equals(label)) {
                return node;
            }
        }
        return null;
    }

    public static void generateVehicleNode(VehicleAgent vehicleAgent){
        DefaultDrawableNode node =
                GUI.generateNode(vehicleAgent.getVehicleName(), GUI.parseColor(vehicleAgent),
                        vehicleAgent.getCoordinates().getX() ,vehicleAgent.getCoordinates().getY(),2);
        vehicleAgent.setNode(node);
    }

    public static void generateEmergencyNode(Emergency emergency){


        DefaultDrawableNode node =
                GUI.generateNode("Emergency" + emergency.getId(), GUI.parseEmergencyColor(emergency),
                        emergency.getCoordinates().getX() ,emergency.getCoordinates().getY(),5);
        emergency.setNode(node);

    }

    public static void createEdge(DefaultDrawableNode node1,DefaultDrawableNode node2){
        //Sample edge
        DefaultDrawableEdge sampleEdge = new DefaultDrawableEdge(node1, node2);
        node1.addOutEdge(sampleEdge);
        node2.addInEdge(sampleEdge);
    }

    public static void createEdgeName(DefaultDrawableNode myNode,String name,Color color){
        DefaultDrawableNode to = GUI.getNode(name);
        Edge edge = new Edge(myNode, to);
        edge.setColor(color);
        myNode.addOutEdge(edge);
    }

    public static void addNode(DefaultDrawableNode node) { nodes.add(node);}

    public static List<DefaultDrawableNode> getNodes(){return nodes;}

}
