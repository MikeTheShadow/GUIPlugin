package com.miketheshadow.guiplugin.gui.types;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class CategoryGUI extends BaseGUI {


    public static void openCategoryGUI(InventoryClickEvent event) {
        Player player = (Player)event.getWhoClicked();
        Inventory craftingInventory = player.getOpenInventory().getTopInventory();
        event.setCancelled(false);
        if(event.getClickedInventory() == null) return;
        if(hasPressedButton(player,craftingInventory.getItem(event.getSlot()),craftingInventory)) {
            event.setCancelled(true);
        }
    }
}
