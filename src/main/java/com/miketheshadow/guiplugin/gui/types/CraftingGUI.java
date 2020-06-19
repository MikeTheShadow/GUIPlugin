package com.miketheshadow.guiplugin.gui.types;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class CraftingGUI extends BaseGUI {

    public static void openCraftingGUI(InventoryClickEvent event) {
        Player player = (Player)event.getWhoClicked();
        Inventory craftingInventory = player.getOpenInventory().getTopInventory();
        event.setCancelled(true);
        if(event.getClickedInventory() == null) return;
        if(hasPressedButton(player,craftingInventory.getItem(event.getSlot()),craftingInventory)) {
            event.setCancelled(true);
        }
    }

}
