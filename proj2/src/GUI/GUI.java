package GUI;

import agents.VehicleAgent;
import repast.RepastLauncher;
import uchicago.src.sim.gui.Network2DDisplay;
import uchicago.src.sim.gui.OvalNetworkItem;
import uchicago.src.sim.network.DefaultDrawableEdge;
import uchicago.src.sim.network.DefaultDrawableNode;
import utils.Emergency;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GUI {
    // TODO: mudar para hash map se der
    private static List<DefaultDrawableNode> nodes = new ArrayList<>();
    private static int displayId = 0;
    private static DefaultDrawableNode controlTowerNode;

    public static int CONTROL_TOWER_EDGE_DURATION = 500; // in ms


    public static Color parseColor(VehicleAgent agent){
        switch (agent.getType()) {
            case FIREMAN: return Color.red;
            case INEM : return Color.WHITE;
            case POLICE : return Color.blue;
            default: return null;
        }

    }
    private static Color parseEmergencyColor(Emergency emergency){
        switch (emergency.getEmergencyType()) {
            case FIRE : return Color.orange;
            case ACCIDENT : return Color.magenta;
            case ROBBERY : return Color.cyan;

        };
        return null;

    }

    public static DefaultDrawableNode generateNode(String label, Color color, double x, double y, int size,boolean update) {
        OvalNetworkItem oval = new OvalNetworkItem(x, y);
        oval.allowResizing(false);
        oval.setHeight(size);
        oval.setWidth(size);
        DefaultDrawableNode node = new DefaultDrawableNode(label, oval);
        // just so the labels don't appear on the black background
        node.setLabelColor(Color.BLACK);
        node.setColor(color);
        if(update)
            GUI.addNodeUpdate(node);
        else GUI.addNode(node);



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
                        vehicleAgent.getCoordinates().getX(), vehicleAgent.getCoordinates().getY(),2,false);

        vehicleAgent.setNode(node);
    }

    public static void generateEmergencyNode(Emergency emergency) {
        double x = emergency.getCoordinates().getX();
        double y = emergency.getCoordinates().getY();

        GUI.generateNode(getEmergencyLabel(emergency.getId()),
                GUI.parseEmergencyColor(emergency), x, y,3,true);
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
        if(to ==null){
            System.out.println("Node is null when creating edge");
            return;
        }
        Edge edge = new Edge(myNode, to);
        edge.setColor(color);
        myNode.addOutEdge(edge);
    }

    public static void addNode(DefaultDrawableNode node) {
        nodes.add(node);
    }

    public static void addNodeUpdate(DefaultDrawableNode node) {
        nodes.add(node);
        RepastLauncher.get().updateNetwork();
    }

    public static void updateDisplay() {
        Network2DDisplay display = new Network2DDisplay(GUI.getNodes(), RepastLauncher.getWIDTH(), RepastLauncher.getHEIGHT());
        RepastLauncher.getDsurf().addDisplayableProbeable(display, "Network Display" + displayId++);
        RepastLauncher.setDisplay(display);
        RepastLauncher.getDsurf().addZoomable(display);
        RepastLauncher.getDsurf().display();
    }






    public static List<DefaultDrawableNode> getNodes(){return nodes;}

    public static DefaultDrawableNode getControlTowerNode() {
        return controlTowerNode;
    }

    public static void setControlTowerNode(DefaultDrawableNode controlTowerNodeNew) {
        controlTowerNode = controlTowerNodeNew;
    }
}
