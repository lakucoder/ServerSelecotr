package ru.lakucoder.selector;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;


public class Main extends JavaPlugin implements Listener
{
    public static FileConfiguration config;
    public void onEnable()
    {
        registerCommands();
        registerListeners();
        config = this.getConfig();
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        this.saveDefaultConfig();
    }
    public void registerListeners() {
        PluginManager pm = Bukkit.getServer().getPluginManager();
        pm.registerEvents(new ListenersGUI(), this);
    }
    public void registerCommands()
    {
        getCommand("serverselector").setExecutor(new MainGui());
    }
}
