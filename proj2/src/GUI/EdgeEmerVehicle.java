package GUI;

import agents.VehicleAgent;
import uchicago.src.sim.gui.DrawableEdge;
import uchicago.src.sim.gui.SimGraphics;
import uchicago.src.sim.network.DefaultEdge;
import uchicago.src.sim.network.Node;

import java.awt.*;

public class EdgeEmerVehicle extends DefaultEdge implements DrawableEdge {
    private Color color = Color.WHITE;
    private static final float DEFAULT_STRENGTH = 1;
    private VehicleAgent vehicleAgent;


    public EdgeEmerVehicle(Node from, Node to,VehicleAgent vehicleAgent) {
        super(from, to, "", DEFAULT_STRENGTH);this.vehicleAgent =vehicleAgent;
    }

    public EdgeEmerVehicle(Node from, Node to, float strength) {
        super(from, to, "", strength);
    }

    public void setColor(Color c) {
        color = c;
    }

    public Color getColor() {
        return color;
    }
    
    public void draw(SimGraphics g, int fromX, int toX, int fromY, int toY) {
        g.drawDirectedLink(color, fromX, toX, fromY, toY);
    }

    public VehicleAgent getVehicleAgent() {
        return vehicleAgent;
    }

    public void setVehicleAgent(VehicleAgent vehicleAgent) {
        this.vehicleAgent = vehicleAgent;
    }
}
