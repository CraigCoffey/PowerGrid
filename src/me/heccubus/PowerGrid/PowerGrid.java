package me.heccubus.PowerGrid;

import java.util.logging.Logger;
import me.heccubus.PowerGrid.RedstoneListener;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

public class PowerGrid extends JavaPlugin{

	protected static Configuration CONFIG;	
	Logger log = Logger.getLogger("Minecraft");
	
	private final RedstoneListener redstonelistener = new RedstoneListener(this);

	public void onEnable() {
		log.info("PowerGrid has been enabled.");
		
		CONFIG = getConfiguration();
		CONFIG.load();
		int GridBlockID = CONFIG.getInt("GridBlockID", -1);
		if (GridBlockID == -1) {
			CONFIG.setProperty("GridBlockID", 22);
			CONFIG.save();
		}

        PluginManager pm = getServer().getPluginManager();
        pm.registerEvent(Event.Type.REDSTONE_CHANGE, redstonelistener, Event.Priority.Normal, this);
	}
	
	public void onDisable() {
		log.info("PowerGrid has been disabled.");			
	}
	
}