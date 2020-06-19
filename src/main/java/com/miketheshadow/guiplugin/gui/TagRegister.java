package com.miketheshadow.guiplugin.gui;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class TagRegister {

    public static ItemStack previousPage = register(Material.GREEN_SHULKER_BOX, "PREVIOUS PAGE");
    public static ItemStack nextPage = register(Material.GREEN_SHULKER_BOX, "NEXT PAGE");
    public static ItemStack addRecipe = register(Material.BLUE_SHULKER_BOX,"ADD RECIPE");
    public static ItemStack addCategory = register(Material.BLUE_SHULKER_BOX,"ADD CATEGORY");
    public static ItemStack removePage = register(Material.BLUE_SHULKER_BOX,"REMOVE ITEM");
    public static ItemStack addItemToPage = register(Material.BLUE_SHULKER_BOX,"ADD PAGE");
    public static ItemStack backButton = register(Material.BLUE_SHULKER_BOX,"BACK");
    public static ItemStack craftItem = register(Material.BLUE_SHULKER_BOX,"CRAFT");
    public static ItemStack register(Material material, String name) {
        List<String> list = new ArrayList<>();
        list.add(name.toLowerCase());
        //create the item
        ItemStack item = new ItemStack(material);
        //add tags
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN + name);
        meta.setLore(list);
        item.setItemMeta(meta);

        return item;
    }
}
