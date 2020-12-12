package utils;
import java.util.Iterator;
import java.util.Map.Entry;
import javax.swing.JMenuItem;
import uchicago.src.sim.engine.SimModel;
import uchicago.src.sim.gui.*;

public class MyDisplaySurface extends DisplaySurface {
    public MyDisplaySurface(SimModel var1, String var2) {
        super(var1, var2);
    }

    private void removeDisplay(Displayable var1) {
        Iterator var2 = this.displays.entrySet().iterator();

        while(true) {
            while(true) {
                Entry var3;
                DisplayInfo var4;
                do {
                    if (!var2.hasNext()) {
                        return;
                    }

                    var3 = (Entry)var2.next();
                    var4 = (DisplayInfo)var3.getValue();
                } while(!var4.getDisplayable().equals(var1));

                String var5 = (String)var3.getKey();
                int var6 = 0;

                for(int var7 = this.menu.getItemCount(); var6 < var7; ++var6) {
                    JMenuItem var8 = this.menu.getItem(var6);
                    if (var8 != null && var8.getText().equals(var5)) {
                        this.menu.remove(var6);
                        break;
                    }
                }
            }
        }
    }

    public void removeDisplayable(Displayable var1) {
        this.painter.removeDisplayable(var1);
        this.removeDisplay(var1);
    }

    public void removeProbeable(Probeable var1) {
        this.probeables.remove(var1);
    }

    public void removeProbeableDisplayable(Displayable var1) {
        this.removeDisplayable(var1);
        if (var1 instanceof Probeable) {
            this.removeProbeable((Probeable)var1);
        }

    }
}