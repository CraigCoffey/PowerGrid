package me.heccubus.PowerGrid;

import java.util.logging.Logger;
import me.heccubus.PowerGrid.RedstoneListener;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

/**
 * PowerGrid
 * 
 * @author Craig Coffey (aka. Heccubus)
 * @version 0.5
 *
 */

public class PowerGrid extends JavaPlugin {

	protected static Configuration CONFIG;	
	Logger log = Logger.getLogger("Minecraft");
	
	private final RedstoneListener redstonelistener = new RedstoneListener(this);

	public void onEnable() {
		log.info("PowerGrid has been enabled.");
		
		CONFIG = getConfiguration();
		CONFIG.load();
		String GridOffColor = CONFIG.getString("GridOffColor", "");
		if (GridOffColor.equals("")) {
			CONFIG.setProperty("GridOffColor", "WHITE");
			CONFIG.setProperty("GridOnColor", "YELLOW");
			CONFIG.setProperty("MaxGridSize", 1000);
			CONFIG.save();
		}

        PluginManager pm = getServer().getPluginManager();
        pm.registerEvent(Event.Type.REDSTONE_CHANGE, redstonelistener, Event.Priority.Normal, this);
	}
	
	public void onDisable() {
		log.info("PowerGrid has been disabled.");			
	}
	
}