package no.openshell.oddstr13.minecartmeter;

import java.util.HashMap;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;

/**
 * Sample plugin for Bukkit
 *
 * @author Dinnerbone
 */
public class MinecartMeter extends JavaPlugin {
    private final MinecartMeterListener mmListener = new MinecartMeterListener(this);
//    private final SampleBlockListener blockListener = new SampleBlockListener(this);
//    private final HashMap<Player, Boolean> debugees = new HashMap<Player, Boolean>();

    // NOTE: There should be no need to define a constructor any more for more info on moving from
    // the old constructor see:
    // http://forums.bukkit.org/threads/too-long-constructor.5032/

    public void onDisable() {
        // TODO: Place any custom disable code here

        // NOTE: All registered events are automatically unregistered when a plugin is disabled

        // EXAMPLE: Custom code, here we just output some info so we can check all is well
        // TODO: use values from plugin.yml
        System.out.println("MinecartMeter version 0.0.1 disabled");
    }

    public void onEnable() {
        // TODO: Place any custom enable code here including the registration of any events

        // Register our events
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvent(Event.Type.VEHICLE_ENTER, mmListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.VEHICLE_EXIT, mmListener, Priority.Normal, this);
//        pm.registerEvent(Event.Type.PLAYER_QUIT, playerListener, Priority.Normal, this);
//        pm.registerEvent(Event.Type.PLAYER_MOVE, playerListener, Priority.Normal, this);
//        pm.registerEvent(Event.Type.BLOCK_PHYSICS, blockListener, Priority.Normal, this);
//        pm.registerEvent(Event.Type.BLOCK_CANBUILD, blockListener, Priority.Normal, this);
//        VEHICLE_ENTER 
//           Called when a vehicle is entered by a LivingEntity
//        VEHICLE_EXIT 
//           Called when a vehicle is exited by a LivingEntity
//        VEHICLE_MOVE 
//           Called when a vehicle moves position in the world

        // Register our commands
        getCommand("minecartmeter").setExecutor(new MinecartMeterCommandhandler(this));
//        getCommand("debug").setExecutor(new SampleDebugCommand(this));

        // EXAMPLE: Custom code, here we just output some info so we can check all is well
        PluginDescriptionFile pdfFile = this.getDescription();
        // TODO: maybe reformat the plugin enabled message?
        System.out.println( pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!" );
    }

/*
    public boolean isDebugging(final Player player) {
        if (debugees.containsKey(player)) {
            return debugees.get(player);
        } else {
            return false;
        }
    }
*/
/*
    public void setDebugging(final Player player, final boolean value) {
        debugees.put(player, value);
    }
*/
}
