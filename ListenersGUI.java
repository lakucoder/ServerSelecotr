package ru.lakucoder.selector;
import de.howaner.BungeeCordLib.BungeeCord;
import de.howaner.BungeeCordLib.server.BungeeServer;
import de.howaner.BungeeCordLib.server.ServerData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import java.util.ArrayList;
import java.util.Random;

public class ListenersGUI implements Listener {
    private Integer PlayerOnline = 0;
    private Integer MaxPlayerOnline = 0;

    @EventHandler
    public void click(InventoryClickEvent e) {
        Inventory inv = e.getInventory();
        HumanEntity entity = e.getWhoClicked();
        if (entity instanceof Player) {
            Player player = (Player)entity;
            if (inv.getName().equalsIgnoreCase(Main.config.getString("Inventory.MainMenu"))) {
                Integer i = 0;
                e.setCancelled(true);
                if(e.getRawSlot()==Main.config.getInt("RandomServerSlot")){
                    Integer ist = 0;
                    ArrayList<String> ips = new ArrayList<String>();
                    ArrayList<String> ports = new ArrayList<String>();
                    ArrayList<String> names = new ArrayList<String>();
                    for(String key : Main.config.getConfigurationSection("Servers").getKeys(false)) {
                        BungeeServer svr = BungeeCord.getManager().addServer(Main.config.getString("Servers."+key+".name"), Main.config.getInt("Servers."+key+".ip")+":"+Main.config.getInt("Servers."+key+".port"));
                        ServerData das = svr.getData();
                        if(das != null) {
                            player.sendMessage(Main.config.getString("Messages.MapNotavailable").replaceAll("%map_name%","Servers."+key+"mapName"));
                        }else{
                            String status = das.getMotd();
                            if(status.indexOf(Main.config.getString("Settings.ingamemotd"))!=-1) {
                            }else{
                                ist++;
                                Integer port1 = Main.config.getInt("Servers."+key+".slot");
                                ips.add(Main.config.getString("Servers."+key+".ip"));
                                ports.add(port1.toString());
                                names.add(Main.config.getString("Servers."+key+".name"));
                            }
                        }
                    }
                    if(ist != 0) {
                        Random randomGenerator = new Random();
                        System.out.println(ist);
                        int randomInt = randomGenerator.nextInt(ist);
                        ConnectTOServer(player, names.get(randomInt), ips.get(randomInt) + ":" + ports.get(randomInt));
                        player.sendMessage(Main.config.getString("Messages.NoAvailableServer"));
                    }else{
                        player.sendMessage(Main.config.getString("Messages.NoAvailableServer"));
                    }
                }
                for(String key : Main.config.getConfigurationSection("Servers").getKeys(false)) {
                    BungeeServer server = BungeeCord.getManager().addServer(Main.config.getString("Servers."+key+".name"), Main.config.getInt("Servers."+key+".ip")+":"+Main.config.getInt("Servers."+key+".port"));
                    if(e.getRawSlot() == Main.config.getInt("Servers."+key+".slot")){

                        ServerData datas = server.getData();
                        if(datas == null) {
                            player.sendMessage(Main.config.getString("Messages.MapNotavailable").replaceAll("%map_name%",Main.config.getString("Servers."+key+".mapName")));
                            player.closeInventory();
                            player.openInventory(MainGui.inv);
                            return;
                        }
                        MaxPlayerOnline = datas.getSlots();
                        PlayerOnline = datas.getPlayers();
                        String status = datas.getMotd();
                        if(status.indexOf(Main.config.getString("Settings.ingamemotd"))!=-1){
                            player.sendMessage(Main.config.getString("Messages.ServerIngame").replaceAll("%map_name%",Main.config.getString("Servers."+key+".mapName")));
                            player.closeInventory();
                            player.openInventory(MainGui.inv);
                        }else if(PlayerOnline == MaxPlayerOnline){
                            player.sendMessage(Main.config.getString("Messages.ServerIsFull").replaceAll("%map_name%",Main.config.getString("Servers."+key+".mapName")));
                            player.closeInventory();
                            player.openInventory(MainGui.inv);
                        }else{
                            ConnectTOServer(player, Main.config.getString("Servers." + key + ".name"), Main.config.getInt("Servers." + key + ".ip") + ":" + Main.config.getInt("Servers." + key + ".port"));
                            player.closeInventory();
                            player.openInventory(MainGui.inv);
                        }
                    }
                }
                executeCommand(player,false,"skywarsgame");
            }
        }
    }

    private void executeCommand (Player p, boolean console, String cmd){
        Bukkit.dispatchCommand(p, ChatColor.translateAlternateColorCodes('&', cmd.replaceAll("%player%", p.getName())));
    }

    public void ConnectTOServer(Player player,String name,String ipport){
        BungeeServer server = BungeeCord.getManager().addServer(name, ipport);
        try{
            server.teleportPlayer(player);
        }catch (Exception e){
            player.sendMessage("Ошибка повторите попытку позже");
        }
    }
}
