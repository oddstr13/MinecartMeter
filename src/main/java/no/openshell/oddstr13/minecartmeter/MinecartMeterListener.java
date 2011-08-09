package no.openshell.oddstr13.minecartmeter;

import org.bukkit.Location;
import org.bukkit.event.vehicle.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Vehicle;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;

/**
 * Handle events for all Player related events
 * @author Dinnerbone
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
            if (entity instanceof Player) {
                Player player = (Player) entity;
                Location l = cart.getLocation();
                String msg = "entered minecart with entityId " + cart.getEntityId() + 
                  " at location " + l.getWorld().getName() + "," + l.getX() + "," + l.getY() + "," + l.getZ();
                player.sendMessage("[DEBUG]: You " + msg);
                System.out.println("[DEBUG]: " + player.getDisplayName() + "(" + player.getName() + ") " + msg);
                Entity passenger = cart.getPassenger();
                if (passenger instanceof Player) {
                    Player p = (Player) passenger;
                    System.out.println("[DEBUG]: Passenger of minecart " + cart.getEntityId() + " is " + p.getName());
                }
            }
        }
    }

    @Override
    public void onVehicleExit(VehicleExitEvent event) {
        Entity entity = event.getExited();
        Vehicle vehicle = event.getVehicle();
        if (vehicle instanceof Minecart) {
            Minecart cart = (Minecart) vehicle;
            if (entity instanceof Player) {
                Player player = (Player) entity;
                Location l = cart.getLocation();
                String msg = "exited minecart with entityId " + cart.getEntityId() + 
                  " at location " + l.getWorld().getName() + "," + l.getX() + "," + l.getY() + "," + l.getZ();
                player.sendMessage("[DEBUG]: You " + msg);
                System.out.println("[DEBUG]: " + player.getDisplayName() + "(" + player.getName() + ") " + msg);
                Entity passenger = cart.getPassenger();
                if (passenger instanceof Player) {
                    Player p = (Player) passenger;
                    System.out.println("[DEBUG]: Passenger of minecart " + cart.getEntityId() + " is " + p.getName());
                }
            }
        }
    }

}
