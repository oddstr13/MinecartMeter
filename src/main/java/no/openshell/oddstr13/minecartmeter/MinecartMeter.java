package no.openshell.oddstr13.minecartmeter;

import java.util.HashMap;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.Location;
import org.bukkit.configuration.Configuration;

/**
 * MinecartMeter
 *
 * @author Oddstr13
 */
public class MinecartMeter extends JavaPlugin {
	private final MinecartMeterListener mmListener = new MinecartMeterListener(this);
	// private final SampleBlockListener blockListener = new
	// SampleBlockListener(this);
	private final HashMap<String, Location> startlocations = new HashMap<String, Location>();
	private final HashMap<String, Integer> traveldistances = new HashMap<String, Integer>();
	private final HashMap<String, Long> travelIGtimes = new HashMap<String, Long>();
	private final HashMap<String, Long> travelRLtimes = new HashMap<String, Long>();

	private PluginDescriptionFile pdfFile;
	public Configuration config;

	public void onDisable() {
		System.out.println(pdfFile.getName() + " version " + pdfFile.getVersion() + " is disabled.");
		/* might want to empty the hash maps here? to potentialy free up some RAM */
	}

	public void onEnable() {
		// Register events
		PluginManager pm = getServer().getPluginManager();
		pdfFile = getDescription();
		refreshConfigFile();
		pm.registerEvents(mmListener, this);

		// Register commands
		getCommand("minecartmeter").setExecutor(new MinecartMeterCommandhandler(this));

		System.out.println(pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled.");
	}

	public void refreshConfigFile() {
		saveDefaultConfig();

		config = getConfig();

		config.addDefault("option.traveltime.ingame", true); // show travel time in minecraft time?
		config.addDefault("option.traveltime.real", false); // show travel time in real time?
		config.addDefault("option.traveldistanse.real", true); // show distanse traveled by rail?
		config.addDefault("option.traveldistanse.air", true); // show distanse traveled, point-point air distanse
		config.addDefault("option.clock.departure", true); // show the clock on departure
		config.addDefault("option.clock.arrival", true); // show the clock on arrival
		config.addDefault("option.clock.ingame", true);
		config.addDefault("option.clock.real", false);

		config.addDefault("format.time.traveltime", 0); // 0=short, 1=long, 2=custom
		config.addDefault("format.time.24hour", true); // true=24hour clock, false=am/pm
		config.addDefault("format.time.upperAMPM", false); // true=AM/PM, false=am/pm

		config.addDefault("format.time.custom.prefix", "");
		config.addDefault("format.time.custom.sufix", "");
		config.addDefault("format.time.custom.seperator", ", ");
		config.addDefault("format.time.custom.lastseperator", " and ");

		config.addDefault("format.time.custom.week", " Week");
		config.addDefault("format.time.custom.weeks", " Weeks");
		config.addDefault("format.time.custom.day", " Day");
		config.addDefault("format.time.custom.days", " Days");
		config.addDefault("format.time.custom.hour", " Hour");
		config.addDefault("format.time.custom.hours", " Hours");
		config.addDefault("format.time.custom.minute", " Minute");
		config.addDefault("format.time.custom.minutes", " Minutes");
		config.addDefault("format.time.custom.second", " Second");
		config.addDefault("format.time.custom.seconds", " Seconds");
		config.addDefault("format.time.custom.millisecond", " Millisecond");
		config.addDefault("format.time.custom.milliseconds", " Milliseconds");

		config.addDefault("format.text.custom.traveltime.ingame.enabled", false);
		config.addDefault("format.text.custom.traveltime.ingame.prefix", "The trip took ");
		config.addDefault("format.text.custom.traveltime.ingame.sufix", ".");

		config.addDefault("format.text.custom.traveltime.real.enabled", false);
		config.addDefault("format.text.custom.traveltime.real.prefix", "The trip took ");
		config.addDefault("format.text.custom.traveltime.real.sufix", ".");

		config.addDefault("format.text.custom.traveldistanse.real.enabled", false);
		config.addDefault("format.text.custom.traveldistanse.real.prefix", "You have traveled ");
		config.addDefault("format.text.custom.traveldistanse.real.sufix", " meters by railroad.");

		config.addDefault("format.text.custom.traveldistanse.air.enabled", false);
		config.addDefault("format.text.custom.traveldistanse.air.prefix", "You have traveled ");
		config.addDefault("format.text.custom.traveldistanse.air.sufix", " meters in direct line.");

		config.addDefault("format.text.custom.clock.ingame.departure.enabled", false);
		config.addDefault("format.text.custom.clock.ingame.departure.prefix",
				"Welcome to Minecart Railways, the clock is now ");
		config.addDefault("format.text.custom.clock.ingame.departure.sufix", ". Have a nice ride.");

		config.addDefault("format.text.custom.clock.ingame.arrival.enabled", false);
		config.addDefault("format.text.custom.clock.ingame.arrival.prefix", "You arrived at ");
		config.addDefault("format.text.custom.clock.ingame.arrival.sufix",
				". Thank you for choosing Minecart Railways.");

		config.addDefault("format.text.custom.clock.real.departure.enabled", false);
		config.addDefault("format.text.custom.clock.real.departure.prefix",
				"Welcome to Minecart Railways, the clock is now ");
		config.addDefault("format.text.custom.clock.real.departure.sufix", ". Have a nice ride.");

		config.addDefault("format.text.custom.clock.real.arrival.enabled", false);
		config.addDefault("format.text.custom.clock.real.arrival.prefix", "You arrived at ");
		config.addDefault("format.text.custom.clock.real.arrival.sufix", ". Thank you for choosing Minecart Railways.");

		saveConfig();
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
		if (config.getBoolean("format.time.24hour", true)) {
			return String.format("%02d:%02d", world_hh, world_mm);
		} else {
			int world_h = world_hh % 12;
			if (world_h == 0) {
				world_h = 12;
			}
			String am_pm = "am";
			if ((world_hh / 12) != 0) {
				am_pm = "pm";
			}
			if (config.getBoolean("format.time.upperAMPM", false)) {
				am_pm = am_pm.toUpperCase();
			}
			return String.format("%d:%02d %s", world_h, world_mm, am_pm);
		}
	}

	public String realTimeToString() {
		/* http://www.rgagnon.com/javadetails/java-0106.html */
		String DATE_FORMAT_NOW;
		Calendar cal = Calendar.getInstance();
		if (config.getBoolean("format.time.24hour", true)) {
			DATE_FORMAT_NOW = "HH:mm:ss";
		} else {
			DATE_FORMAT_NOW = "h:mm:ss a";
		}

		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
		if (config.getBoolean("format.time.upperAMPM", false)) {
			return sdf.format(cal.getTime()).toUpperCase();
		} else {
			return sdf.format(cal.getTime()).toLowerCase();
		}
	}

	/*
	 * takes World.getFullTime() - previous_world_full_time
	 * (the difference between two points in time)
	 * and formats it to a (hopefully) human readable string
	 */
	public String tripTimeToString(long trip_time) {
		int week = 1000 * 24 * 7;
		int day = 1000 * 24;
		int hour = 1000;

		int weeks = (int) trip_time / week;
		int days = (int) (trip_time % week) / day;
		int hours = (int) ((trip_time % week) % day) / hour;
		int minutes = (int) (((trip_time % week) % day) % hour) * 60 / 1000;

		// 0=short, 1=long, 2=custom
		int format_type = config.getInt("format.time.traveltime", 0);

		if (format_type == 0) {
			if (weeks != 0) {
				return String.format("%dw %dd %dh %dm", weeks, days, hours, minutes);
			} else if (days != 0) {
				return String.format("%dd %dh %dm", days, hours, minutes);
			} else if (hours != 0) {
				return String.format("%dh %dm", hours, minutes);
			} else {
				return String.format("%dm", minutes);
			}
		} else if (format_type == 1) {
			String week_f = "Weeks";
			String day_f = "Days";
			String hour_f = "Hours";
			String min_f = "Minutes";
			if (weeks == 1) {
				week_f = "Week";
			}
			if (days == 1) {
				day_f = "Day";
			}
			if (hours == 1) {
				hour_f = "Hour";
			}
			if (minutes == 1) {
				min_f = "Minute";
			}

			if (weeks != 0) {
				return String.format("%d %s, %d %s, %d %s and %d %s", weeks, week_f, days, day_f, hours, hour_f,
						minutes, min_f);
			} else if (days != 0) {
				return String.format("%d %s, %d %s and %d %s", days, day_f, hours, hour_f, minutes, min_f);
			} else if (hours != 0) {
				return String.format("%d %s and %d %s", hours, hour_f, minutes, min_f);
			} else {
				return String.format("%d %s", minutes, min_f);
			}
		} else {
			String week_f = config.getString("format.time.custom.weeks");
			String day_f = config.getString("format.time.custom.days");
			String hour_f = config.getString("format.time.custom.hours");
			String min_f = config.getString("format.time.custom.minutes");

			String prefix_f = config.getString("format.time.custom.prefix");
			String sufix_f = config.getString("format.time.custom.sufix");
			String seperator_f = config.getString("format.time.custom.seperator");
			String lastseperator_f = config.getString("format.time.custom.lastseperator");

			if (weeks == 1) {
				week_f = config.getString("format.time.custom.week");
			}
			if (days == 1) {
				day_f = config.getString("format.time.custom.day");
			}
			if (hours == 1) {
				hour_f = config.getString("format.time.custom.hour");
			}
			if (minutes == 1) {
				min_f = config.getString("format.time.custom.minute");
			}

			if (weeks != 0) {
				return String.format("%s%d%s%s%d%s%s%d%s%s%d%s%s", prefix_f, weeks, week_f, seperator_f, days, day_f,
						seperator_f, hours, hour_f, lastseperator_f, minutes, min_f, sufix_f);
			} else if (days != 0) {
				return String.format("%s%d%s%s%d%s%s%d%s%s", prefix_f, days, day_f, seperator_f, hours, hour_f,
						lastseperator_f, minutes, min_f, sufix_f);
			} else if (hours != 0) {
				return String.format("%s%d%s%s%d%s%s", prefix_f, hours, hour_f, lastseperator_f, minutes, min_f,
						sufix_f);
			} else {
				return String.format("%s%d%s%s", prefix_f, minutes, min_f, sufix_f);
			}
		}
	}

	/*
	 * takes time in milliseconds between two points in time,
	 * and returns a (hopefully) human readable String
	 */
	public String rlTripTimeToString(long trip_time) {
		int sec = 1000;
		int min = sec * 60;
		int hour = min * 60;
		int day = hour * 24;
		int week = day * 7;

		int weeks = (int) trip_time / week;
		int days = (int) (trip_time % week) / day;
		int hours = (int) ((trip_time % week) % day) / hour;
		int minutes = (int) (((trip_time % week) % day) % hour) / min;
		int seconds = (int) ((((trip_time % week) % day) % hour) % min) / sec;
		int msecs = (int) ((((trip_time % week) % day) % hour) % min) % sec;

		int format_type = config.getInt("format.time.traveltime", 0);

		if (format_type == 0) {
			if (weeks != 0) {
				return String.format("%dw %dd %dh %dm %ds", weeks, days, hours, minutes, seconds);
			} else if (days != 0) {
				return String.format("%dd %dh %dm %ds", days, hours, minutes, seconds);
			} else if (hours != 0) {
				return String.format("%dh %dm %ds", hours, minutes, seconds);
			} else if (minutes != 0) {
				return String.format("%dm %ds", minutes, seconds);
			} else if (seconds != 0) {
				return String.format("%d.%ds", seconds, msecs);
			} else {
				return String.format("%dms", msecs);
			}
		} else if (format_type == 1) {
			String week_f = "Weeks";
			String day_f = "Days";
			String hour_f = "Hours";
			String min_f = "Minutes";
			String sec_f = "Seconds";
			String msec_f = "Milliseconds";

			if (weeks == 1) {
				week_f = "Week";
			}
			if (days == 1) {
				day_f = "Day";
			}
			if (hours == 1) {
				hour_f = "Hour";
			}
			if (minutes == 1) {
				min_f = "Minute";
			}
			if (seconds == 1) {
				sec_f = "Second";
			}
			if (msecs == 1) {
				msec_f = "Millisecond";
			}

			if (weeks != 0) {
				return String.format("%d %s, %d %s, %d %s, %d %s and %d %s", weeks, week_f, days, day_f, hours, hour_f,
						minutes, min_f, seconds, sec_f);
			} else if (days != 0) {
				return String.format("%d %s, %d %s, %d %s and %d %s", days, day_f, hours, hour_f, minutes, min_f,
						seconds, sec_f);
			} else if (hours != 0) {
				return String.format("%d %s, %d %s and %d %s", hours, hour_f, minutes, min_f, seconds, sec_f);
			} else if (minutes != 0) {
				return String.format("%d %s and %d", minutes, min_f, seconds, sec_f);
			} else if (seconds != 0) {
				return String.format("%d.%d %s", seconds, msecs, sec_f);
			} else {
				return String.format("%d %s", msecs, msec_f);
			}
		} else {
			String week_f = config.getString("format.time.custom.weeks");
			String day_f = config.getString("format.time.custom.days");
			String hour_f = config.getString("format.time.custom.hours");
			String min_f = config.getString("format.time.custom.minutes");
			String sec_f = config.getString("format.time.custom.seconds");
			String msec_f = config.getString("format.time.custom.milliseconds");

			String prefix_f = config.getString("format.time.custom.prefix");
			String sufix_f = config.getString("format.time.custom.sufix");
			String seperator_f = config.getString("format.time.custom.seperator");
			String lastseperator_f = config.getString("format.time.custom.lastseperator");

			if (weeks == 1) {
				week_f = config.getString("format.time.custom.week");
			}
			if (days == 1) {
				day_f = config.getString("format.time.custom.day");
			}
			if (hours == 1) {
				hour_f = config.getString("format.time.custom.hour");
			}
			if (minutes == 1) {
				min_f = config.getString("format.time.custom.minute");
			}
			if (seconds == 1) {
				sec_f = config.getString("format.time.custom.second");
			}
			if (msecs == 1) {
				sec_f = config.getString("format.time.custom.millisecond");
			}

			if (weeks != 0) {
				return String.format("%s%d%s%d%s%d%s%d%s%d%s%s", prefix_f, weeks, week_f, seperator_f, days, day_f,
						seperator_f, hours, hour_f, seperator_f, minutes, min_f, lastseperator_f, seconds, sec_f,
						sufix_f);
			} else if (days != 0) {
				return String.format("%s%d%s%d%s%d%s%d%s%s", prefix_f, days, day_f, seperator_f, hours, hour_f,
						seperator_f, minutes, min_f, lastseperator_f, seconds, sec_f, sufix_f);
			} else if (hours != 0) {
				return String.format("%s%d%s%d%s%d%s%s", prefix_f, hours, hour_f, seperator_f, minutes, min_f,
						lastseperator_f, seconds, sec_f, sufix_f);
			} else if (minutes != 0) {
				return String.format("%s%d%s%d%s%s", prefix_f, minutes, min_f, lastseperator_f, seconds, sec_f,
						sufix_f);
			} else if (seconds != 0) {
				return String.format("%s%d.%d%s%s", prefix_f, seconds, msecs, sec_f, sufix_f);
			} else {
				return String.format("%s%d%s%s", prefix_f, msecs, msec_f, sufix_f);
			}
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
