package ru.lakucoder.selector;
import de.howaner.BungeeCordLib.BungeeCord;
import de.howaner.BungeeCordLib.server.BungeeServer;
import de.howaner.BungeeCordLib.server.ServerData;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import ru.lakucoder.selector.gameprofiles.Utils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
public class MainGui implements CommandExecutor {

    private Integer ServerAvailable = 0;
    private Integer PlayerCount = 0;
    private Integer ServerCount = 0;
    public static Inventory inv = null;

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can do that!");
            return false;
        }
        Player player = (Player)sender;
        ItemStack ServerIcon= null;
        inv = Bukkit.createInventory(null, 54, Main.config.getString("Inventory.MainMenu"));
        List<String> LoreSW1 = new ArrayList<String>();
        for(String key : Main.config.getConfigurationSection("Servers").getKeys(false)) {
            ServerCount = ServerCount + 1;
            getStatus(Main.config.getString("Servers."+key+".ip"),Main.config.getInt("Servers."+key+".port"),LoreSW1,ServerIcon,Main.config.getInt("Servers."+key+".slot"),Main.config.getString("Servers."+key+".name"),inv,Main.config.getString("Servers."+key+".mode"),Main.config.getString("Servers."+key+".mapName"),player);
        }
        PlayerCount = 0;
        ServerAvailable = 0;
        ServerCount =0;
        player.openInventory(inv);
        return false;
    }

    private void getStatus(String ip, Integer port, List<String> lore,ItemStack item,Integer slot,String servername,Inventory inv,String mode,String mapName,Player plas){
        try{
            lore.clear();
            BungeeServer server = BungeeCord.getManager().addServer(servername, ip+":"+port.toString());
            ServerData data = server.getData();
            if (data == null) {
                lore.add("§8§l"+mode);
                lore.add("");
                lore.add("§7Игроков, в игре: §4NULL");
                lore.add("§fКарта: "+ "§e"+mapName);
                lore.add("§fСтатус игры: §2Перезагрузка");
                lore.add(" ");
                lore.add("§6Карта скоро станет доступна");
                item = new ItemStack(Material.INK_SACK, 0, (short) 1);
            }
            String status = data.getMotd();
            Integer maxOnlinePlayer = data.getSlots();
            Integer onlinePlayer = data.getPlayers();
            if(status.indexOf(Main.config.getString("Settings.ingamemotd"))!=-1){
                lore.add("§8§l"+mode);
                lore.add("");
                lore.add("§fИгроков, в игре: "+"§d"+ onlinePlayer +"§r/"+"§7"+ maxOnlinePlayer);
                lore.add("§fКарта: "+ "§e"+mapName);
                lore.add("§fСтатус игры: §2В игре");
                lore.add(" ");
                lore.add("§6Карта скоро станет доступна");
                item = new ItemStack(Material.INK_SACK, onlinePlayer, (short) 1);
            }else{
                lore.add("§8§l"+mode);
                lore.add("");
                lore.add("§fИгроков, ожидающих игру: "+"§d"+ onlinePlayer +"§r/"+"§7"+ maxOnlinePlayer);
                lore.add("§fКарта: "+ "§e"+mapName);
                lore.add("§fСтатус игры: §2Ожидание");
                lore.add(" ");
                lore.add("§6Кликните, что бы играть на этой карте!");
                item = new ItemStack(Material.INK_SACK, onlinePlayer, (short) 10);
                ServerAvailable = ServerAvailable + 1;
            }
            PlayerCount = PlayerCount + onlinePlayer;
        } catch (Exception e) {
        }

        ItemMeta metaskywars = item.getItemMeta();
        metaskywars.setDisplayName("§a§l"+servername);
        metaskywars.setLore(lore);
        item.setItemMeta(metaskywars);
        inv.setItem(slot,item);
        ItemStack SWRandom = null;
        SWRandom = new ItemStack(Material.EYE_OF_ENDER, ServerAvailable);
        ItemMeta MetaSWRandom= SWRandom.getItemMeta();
        MetaSWRandom.setDisplayName(Main.config.getString("Inventory.RandomServerName"));
        List<String> LoreRandom = new ArrayList<String>();
        LoreRandom.add(Main.config.getString("Inventory.RandomServerLore"));
        LoreRandom.add(Main.config.getString("Inventory.ServerToConnect").replace("{count}", ServerAvailable.toString()) );
        MetaSWRandom.setLore(LoreRandom);
        SWRandom.setItemMeta(MetaSWRandom);
        inv.setItem(Main.config.getInt("Inventory.RandomServerSlot"),SWRandom);

        List<String> LoreServer = new ArrayList<String>();
        ItemStack svr = new ItemStack(Utils.getHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM2Q1ZGYxODA5MjhiNWQyODY3MjVkYjU2YjQwZmIxODk3NjRkMmIxMjY2ZGM4ZmMxMmUzYTY1ZmNkZGUzIn19fQ=="));
        ItemMeta MetaServer = svr.getItemMeta();
        LoreServer.add("");
        LoreServer.add(Main.config.getString("Inventory.ServerCount").replaceAll("%count%", ServerCount.toString()));
        LoreServer.add(Main.config.getString("Inventory.AvailableServer").replaceAll("%count%", ServerAvailable.toString()));
        LoreServer.add(Main.config.getString("Inventory.Totalplayers").replaceAll("%count%", PlayerCount.toString()));
        MetaServer.setDisplayName(Main.config.getString("Inventory.ServerStatistics"));
        MetaServer.setLore(LoreServer);
        svr.setItemMeta(MetaServer);
        inv.setItem(Main.config.getInt("Inventory.ServerStatisticsSlot"), svr);

    }

}
