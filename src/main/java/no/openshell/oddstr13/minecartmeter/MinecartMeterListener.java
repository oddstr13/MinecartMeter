package no.openshell.oddstr13.minecartmeter;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.vehicle.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Vehicle;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;

/**
 * Handle vehicle events
 * @author Oddstr13
 */
public class MinecartMeterListener extends VehicleListener {
    private final MinecartMeter plugin;

    public MinecartMeterListener(MinecartMeter instance) {
        plugin = instance;
    }

    @Override
    public void onVehicleEnter(VehicleEnterEvent event) {
        Entity entity = event.getEntered();
        Vehicle vehicle = event.getVehicle();
        if (vehicle instanceof Minecart) {
            Minecart cart = (Minecart) vehicle;
            /* Do we have a passenger? :D */
            if (entity instanceof Player) {
                Entity passenger = cart.getPassenger();
                /* already a passenger in the minecart when the player enters? something isn't right here */
                if (passenger instanceof Player) {
                    Player p = (Player) passenger;
                    if (plugin.config.getBoolean("debug", false)) {
                        System.out.println("[DEBUG]: Passenger of minecart " + cart.getEntityId() + " is " + p.getName());
                        System.out.println("[DEBUG]: This is most likly a ghost event.");
                    }
                } else {
                    Player player = (Player) entity;
                    Location l = cart.getLocation();
                    String msg = "entered minecart with entityId " + cart.getEntityId() + 
                      " at location " + l.getWorld().getName() + "," + l.getX() + "," + l.getY() + "," + l.getZ();
                    //player.sendMessage("[DEBUG]: You " + msg);
                    if (plugin.config.getBoolean("debug", false)) {
                        System.out.println("[DEBUG]: " + player.getDisplayName() + "(" + player.getName() + ") " + msg);
                    }

                    World world = l.getWorld();
                    long world_full_time = world.getFullTime();
                    long world_time = world.getTime();
                    long rl_time_msec = System.currentTimeMillis();

                    plugin.setStartLocation(player, l);
                    plugin.resetDistanceCounter(player);
                    plugin.setStartIGTime(player, world_full_time);
                    plugin.setStartRLTime(player, rl_time_msec);

                    String time_string = plugin.worldTimeToString(world_time);

                    if (plugin.config.getBoolean("debug", false)) {
                        System.out.println("[DEBUG]: full time of " + world.getName() + ": " + world.getFullTime());
                        System.out.println("[DEBUG]: time of " + world.getName() + ": " + world.getTime()+ " " + time_string);
                        System.out.println("[DEBUG]: rl_time_msec: " + rl_time_msec);
                    }

                    /* Send current ingame time to the player when he/she enters the minecart. */
                    if (plugin.config.getBoolean("option.clock.departure", true)) {
                        if (plugin.config.getBoolean("format.text.custom.clock.departure.enabled", false)) {
                            player.sendMessage(plugin.config.getString("format.text.custom.clock.departure.prefix") + time_string + plugin.config.getString("format.text.custom.clock.departure.sufix"));
                        } else {
                            player.sendMessage("Welcome to Minecart Railways, the clock is now " + time_string + ". Have a nice ride.");
                        }
                    }


                    //player.sendMessage("[DEBUG]: full time of " + world.getName() + ": " + world.getFullTime());
                    //player.sendMessage("[DEBUG]: time of " + world.getName() + ": " + world.getTime()+ " " + time_string);
                    //player.sendMessage("[DEBUG]: rl_time_msec: " + rl_time_msec);
                }
            }
        }
    }

    @Override
    public void onVehicleExit(VehicleExitEvent event) {
        Entity entity = event.getExited();
        Vehicle vehicle = event.getVehicle();
        handleExitVehicle(vehicle, entity);
    }

    /* not sure if this one is accualy needed... */
    @Override
    public void onVehicleDestroy(VehicleDestroyEvent event) {
        Vehicle vehicle = event.getVehicle();
        if (vehicle instanceof Minecart) {
            Minecart cart = (Minecart) vehicle;
            Location l = cart.getLocation();
            if (!cart.isEmpty()) {
                Entity entity = cart.getPassenger();
                if (entity instanceof Player) {
                    Player player = (Player) entity;
                    if (plugin.config.getBoolean("debug", false)) {
                        System.out.println("[DEBUG]: minecart " + cart.getEntityId() + " destroyed, passenger " + player.getName() + " ejected.");
                    }
                }
            }
        }
    }

    @Override
    public void onVehicleMove(VehicleMoveEvent event) {
        Vehicle vehicle = event.getVehicle();
        Location from = event.getFrom();
        Location to = event.getTo();

        /* from CraftBook's MinecartManager.java */
        boolean crossesBlockBoundary =
               from.getBlockX() == to.getBlockX()
            && from.getBlockY() == to.getBlockY()
            && from.getBlockZ() == to.getBlockZ();

        if (!(crossesBlockBoundary)) {
            if (vehicle instanceof Minecart) {
                Minecart cart = (Minecart)vehicle;
                if (!(cart.isEmpty())) {
                    Entity passenger = cart.getPassenger();
                    if (passenger instanceof Player) {
                        Player player = (Player)passenger;
                        plugin.increaseDistanceCounter(player);
                    }
                }
            }
        }
    }

    public void handleExitVehicle(Vehicle vehicle, Entity entity) {
        if (vehicle instanceof Minecart) {
            Minecart cart = (Minecart) vehicle;
            if (entity instanceof Player) {
                Player player = (Player) entity;
                Location l = cart.getLocation();
                String msg = "exited minecart with entityId " + cart.getEntityId() +
                  " at location " + l.getWorld().getName() + "," + l.getX() + "," + l.getY() + "," + l.getZ();
                //player.sendMessage("[DEBUG]: You " + msg);
                if (plugin.config.getBoolean("debug", false)) {
                    System.out.println("[DEBUG]: " + player.getDisplayName() + "(" + player.getName() + ") " + msg);
                }
                Entity passenger = cart.getPassenger();
                if (passenger instanceof Player) {
                    Player p = (Player) passenger;
                    if (plugin.config.getBoolean("debug", false)) {
                        System.out.println("[DEBUG]: Passenger of minecart " + cart.getEntityId() + " is " + p.getName());
                    }
                }
                Location startlocation = plugin.getStartLocation(player);
                Double distance = l.distance(startlocation);

                World world = l.getWorld();
                long world_full_time = world.getFullTime();
                long world_time = world.getTime();
                String time_string = plugin.worldTimeToString(world_time);
                long trip_start_time = plugin.getStartIGTime(player);
                long trip_time = world_full_time - trip_start_time;
                long rl_time_msec = System.currentTimeMillis();
                String trip_time_string = plugin.tripTimeToString(trip_time);
                long rl_start_time = plugin.getStartRLTime(player);
                long rl_trip_time = rl_time_msec - rl_start_time;

                String rl_trip_time_string = plugin.rlTripTimeToString(rl_trip_time);

                if (plugin.config.getBoolean("debug", false)) {
                    System.out.println("[DEBUG]: player " + player.getName() + " have traveled " + plugin.doubleMetersToString(distance) + " meters in direct line by railroad.");
                    System.out.println("[DEBUG]: player " + player.getName() + "have traveled " + plugin.getDistanceCounter(player) + " meters.");
                    System.out.println("[DEBUG]: player " + player.getName() + " The trip took " + trip_time_string + ".");
                    System.out.println("[DEBUG]: full time of " + world.getName() + ": " + world.getFullTime());
                    System.out.println("[DEBUG]: time of " + world.getName() + ": " + world.getTime()+ " " + time_string);
                    System.out.println("[DEBUG]: rl_time_msec: " + rl_time_msec);
                    System.out.println("[DEBUG]: rl_trip_time: " + rl_trip_time);
                    System.out.println("[DEBUG]: player " + player.getName() + "The trip took " + rl_trip_time_string + ".");
                }

                //player.sendMessage("[DEBUG]: full time of " + world.getName() + ": " + world.getFullTime());
                //player.sendMessage("[DEBUG]: time of " + world.getName() + ": " + world.getTime()+ " " + time_string);
                //player.sendMessage("[DEBUG]: rl_time_msec: " + rl_time_msec);
                //player.sendMessage("[DEBUG]: rl_trip_time: " + rl_trip_time);

                /* Send current ingame time to the player when he/she exits the minecart. */
                if (plugin.config.getBoolean("option.clock.arrival", true)) {
                    if (plugin.config.getBoolean("format.text.custom.clock.arrival.enabled", false)) {
                        player.sendMessage(plugin.config.getString("format.text.custom.clock.arrival.prefix") + time_string + plugin.config.getString("format.text.custom.clock.arrival.sufix"));
                    } else {
                        player.sendMessage("You arrived at " + time_string + ". Thank you for choosing Minecart Railways.");
                    }
                }

                /* Send the distanse between enter and exit of minecart to the player. */
                if (plugin.config.getBoolean("option.traveldistanse.air", true)) {
                    if (plugin.config.getBoolean("format.text.custom.traveldistanse.air.enabled", false)) {
                        player.sendMessage(plugin.config.getString("format.text.custom.traveldistanse.air.prefix") + plugin.doubleMetersToString(distance) + plugin.config.getString("format.text.custom.traveldistanse.air.sufix"));
                    } else {
                        player.sendMessage("You have traveled " + plugin.doubleMetersToString(distance) + " meters in direct line.");
                    }
                }

                /* Send the acctual number of blocks the minecart have traveled when the player exits the minecart */
                if (plugin.config.getBoolean("option.traveldistanse.real", true)) {
                    if (plugin.config.getBoolean("format.text.custom.traveldistanse.real.enabled", false)) {
                        player.sendMessage(plugin.config.getString("format.text.custom.traveldistanse.real.prefix") + plugin.getDistanceCounter(player) + plugin.config.getString("format.text.custom.traveldistanse.real.sufix"));
                    } else {
                        player.sendMessage("You have traveled " + plugin.getDistanceCounter(player) + " meters by railroad.");
                    }
                }

                /* Send the trip duration in ingame time to the player when he/she exits the minecart. */
                if (plugin.config.getBoolean("option.traveltime.ingame", true)) {
                    if (plugin.config.getBoolean("format.text.custom.traveltime.ingame.enabled", false)) {
                        player.sendMessage(plugin.config.getString("format.text.custom.traveltime.ingame.prefix") + trip_time_string + plugin.config.getString("format.text.custom.traveltime.ingame.sufix"));
                    } else {
                        player.sendMessage("The trip took " + trip_time_string + ".");
                    }
                }

                /* Send the trip duration in real life time to the player when he/she exits the minecart. */
                if (plugin.config.getBoolean("option.traveltime.real", true)) {
                    if (plugin.config.getBoolean("format.text.custom.traveltime.real.enabled", false)) {
                        player.sendMessage(plugin.config.getString("format.text.custom.traveltime.real.prefix") + rl_trip_time_string + plugin.config.getString( "format.text.custom.traveltime.real.sufix"));
                    } else {
                        player.sendMessage("The trip took " + rl_trip_time_string + ".");
                    }
                }
            }
        }
    }
}
