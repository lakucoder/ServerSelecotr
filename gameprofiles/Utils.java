package ru.lakucoder.selector.gameprofiles;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

public class Utils
{
    private static String version;

    public static String replaceText(String text)
    {
        return text.replace('&', '§');
    }

    public static List<String> replaceList(List<String> lore)
    {
        List<String> tmpLore = new ArrayList<String>();
        for (String aLore : lore) {
            tmpLore.add(aLore.replace('&', '§'));
        }
        return tmpLore;
    }

    public static Location getPetLocation(Location location)
    {
        return location.add(1.0D, 1.3D, -1.0D);
    }

    public static ItemStack getHead(String value)
    {
        ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (short)3);
        ItemMeta meta = head.getItemMeta();
        head.setItemMeta(meta);
        setSkullProfile(getProfile(value), head);

        return head;
    }

    private static GameProfile getProfile(String encodeUrl)
    {
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        profile.getProperties().put("textures", new Property("textures", encodeUrl));
        return profile;
    }

    private static void setSkullProfile(GameProfile profile, ItemStack skull)
    {
        if (skull.getType() != Material.SKULL_ITEM) {
            throw new IllegalArgumentException("Block must be a skull.");
        }
        SkullMeta sm = (SkullMeta)skull.getItemMeta();
        Class<?> skullClass = getCBClass("inventory.CraftMetaSkull");
        if (!skullClass.isInstance(sm)) {
            throw new IllegalArgumentException("SkullItemMeta not an instance of CraftMetaSkull!");
        }
        try
        {
            Field f = skullClass.getDeclaredField("profile");
            f.setAccessible(true);
            f.set(sm, profile);
            skull.setItemMeta(sm);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private static Class<?> getCBClass(String ClassName)
    {
        String className = "org.bukkit.craftbukkit." + version + ClassName;
        Class<?> c = null;
        try
        {
            c = Class.forName(className);
        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }
        return c;
    }

    static
    {
        String name = Bukkit.getServer().getClass().getPackage().getName();
        String mcVersion = name.substring(name.lastIndexOf('.') + 1);
        version = mcVersion + ".";
    }
}
