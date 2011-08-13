package no.openshell.oddstr13.minecartmeter;

import java.util.HashMap;
import java.text.DecimalFormat;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.Location;

/**
 * MinecartMeter
 *
 * @author Oddstr13
 */
public class MinecartMeter extends JavaPlugin {
    private final MinecartMeterListener mmListener = new MinecartMeterListener(this);
//    private final SampleBlockListener blockListener = new SampleBlockListener(this);
    private final HashMap<String, Location> startlocations = new HashMap<String, Location>();
    private final HashMap<String, Integer> traveldistances = new HashMap<String, Integer>();
    private final HashMap<String, Long> travelIGtimes = new HashMap<String, Long>();
    private final HashMap<String, Long> travelRLtimes = new HashMap<String, Long>();

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
        pm.registerEvent(Event.Type.VEHICLE_DESTROY, mmListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.VEHICLE_MOVE, mmListener, Priority.Normal, this);
//        pm.registerEvent(Event.Type.PLAYER_QUIT, playerListener, Priority.Normal, this);
//        pm.registerEvent(Event.Type.PLAYER_MOVE, playerListener, Priority.Normal, this);
//        pm.registerEvent(Event.Type.BLOCK_PHYSICS, blockListener, Priority.Normal, this);
//        pm.registerEvent(Event.Type.BLOCK_CANBUILD, blockListener, Priority.Normal, this);

        // Register our commands
//        getCommand("minecartmeter").setExecutor(new MinecartMeterCommandhandler(this));
//        getCommand("debug").setExecutor(new SampleDebugCommand(this));

        // EXAMPLE: Custom code, here we just output some info so we can check all is well
        PluginDescriptionFile pdfFile = this.getDescription();
        // TODO: maybe reformat the plugin enabled message?
        System.out.println( pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!" );
    }

    public Location getStartLocation(final Player player) {
        if (startlocations.containsKey(player.getName())) {
            return startlocations.get(player.getName());
        } else {
            return null;
        }
    }

    public void setStartLocation(final Player player, final Location value) {
        startlocations.put(player.getName(), value);
    }

    public String doubleMetersToString(double meters) {
        DecimalFormat f = new DecimalFormat("#.##");
        return f.format(meters);
    }

    public void resetDistanceCounter(final Player player) {
        traveldistances.put(player.getName(), 0);
    }

    public void increaseDistanceCounter(final Player player) {
        traveldistances.put(player.getName(), traveldistances.get(player.getName()) + 1);
    }

    public int getDistanceCounter(final Player player) {
        return traveldistances.get(player.getName());
    }

    public String worldTimeToString(long world_time) {
        /* huh? +8? woot.. anyway thanks to CommandBook, now we know this */
        int world_hh = (int) ((world_time / 1000) + 8) % 24;
        int world_mm = (int) (world_time % 1000) * 60 / 1000;
        return String.format("%02d:%02d", world_hh, world_mm);
    }

    public String tripTimeToString(long trip_time) {
        int week = 1000 * 24 * 7;
        int day  = 1000 * 24;
        int hour = 1000;

        int weeks   = (int) trip_time / week;
        int days    = (int) (trip_time % week) / day;
        int hours   = (int) ((trip_time % week) % day) / hour;
        int minutes = (int) (((trip_time % week) % day) % hour) * 60 / 1000;

        // TODO: add config option for short or long format, this is short format
        if (weeks != 0) {
            return String.format("%dw %dd %dh %dm", weeks, days, hours, minutes);
        } else if (days != 0) {
            return String.format("%dd %dh %dm", days, hours, minutes);
        } else if (hours != 0) {
            return String.format("%dh %dm", hours, minutes);
        } else {
            return String.format("%dm", minutes);
        }
    }

    public void setStartIGTime(Player player, long time) {
        travelIGtimes.put(player.getName(), time);
    }

    public long getStartIGTime(Player player) {
        return travelIGtimes.get(player.getName());
    }

    public void setStartRLTime(Player player, long time) {
        travelRLtimes.put(player.getName(), time);
    }

    public long getStartRLTime(Player player) {
        return travelRLtimes.get(player.getName());
    }

}
