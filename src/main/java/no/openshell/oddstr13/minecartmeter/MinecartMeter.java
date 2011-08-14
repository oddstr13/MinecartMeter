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
    private final PluginDescriptionFile pdfFile = this.getDescription();

    public void onDisable() {
        System.out.println(pdfFile.getName() + " version " + pdfFile.getVersion() + " is disabled.");
        /* might want to empty the hash maps here? to potentialy free up some RAM */
    }

    public void onEnable() {
        // Register events
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvent(Event.Type.VEHICLE_ENTER, mmListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.VEHICLE_EXIT, mmListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.VEHICLE_DESTROY, mmListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.VEHICLE_MOVE, mmListener, Priority.Normal, this);

        // Register commands
//        getCommand("minecartmeter").setExecutor(new MinecartMeterCommandhandler(this));
//        getCommand("debug").setExecutor(new SampleDebugCommand(this));

        System.out.println(pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled.");
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

   /*
    * format ingame time to string
    * takes World.getTime() and outputs String HH:MM
    */
    public String worldTimeToString(long world_time) {
        /* huh? +8? woot.. anyway thanks to CommandBook, now we know this */
        int world_hh = (int) ((world_time / 1000) + 8) % 24;
        int world_mm = (int) (world_time % 1000) * 60 / 1000;
        return String.format("%02d:%02d", world_hh, world_mm);
    }

   /*
    * takes World.getFullTime() - previous_world_full_time
    * (the difference between two points in time)
    * and formats it to a (hopefully) human readable string
    */
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

   /*
    * takes time in milliseconds between two points in time,
    * and returns a (hopefully) human readable String
    */
    public String rlTripTimeToString(long trip_time) {
        int sec  = 1000;
        int min  = sec  * 60;
        int hour = min  * 60;
        int day  = hour * 24;
        int week = day  * 7;

        int weeks    = (int) trip_time / week;
        int days     = (int) (trip_time % week) / day;
        int hours    = (int) ((trip_time % week) % day) / hour;
        int minutes  = (int) (((trip_time % week) % day) % hour) / min;
        int secounds = (int) ((((trip_time % week) % day) % hour) % min) / sec;
        int msecs    = (int) ((((trip_time % week) % day) % hour) % min) % sec;

        if (weeks != 0) {
            return String.format("%dw %dd %dh %dm %ds", weeks, days, hours, minutes, secounds);
        } else if (days != 0) {
            return String.format("%dd %dh %dm %ds", days, hours, minutes, secounds);
        } else if (hours != 0) {
            return String.format("%dh %dm %ds", hours, minutes, secounds);
        } else if (minutes != 0) {
            return String.format("%dm %ds", minutes, secounds);
        } else if (secounds != 0) {
            return String.format("%d.%ds", secounds, msecs);
        } else {
            return String.format("%dms", msecs);
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
