package com.miketheshadow.guiplugin.gui.listener;


import com.miketheshadow.guiplugin.gui.GUIPlayer;
import com.miketheshadow.guiplugin.gui.GUIType;
import com.miketheshadow.guiplugin.gui.types.CategoryGUI;
import com.miketheshadow.guiplugin.gui.types.CraftingGUI;
import com.miketheshadow.guiplugin.gui.types.RecipeGUI;
import com.miketheshadow.guiplugin.gui.types.ViewGUI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class OpenGUI implements Listener {

    public static List<GUIPlayer> OPENED_GUIS = new ArrayList<>();


    @EventHandler
    public void inventoryClickEvent(InventoryClickEvent event) {
        Player player = (Player)event.getWhoClicked();
        Inventory playerInventory = player.getOpenInventory().getBottomInventory();
        GUIPlayer guiPlayer = null;
        for (GUIPlayer p : OPENED_GUIS) {
            if(p.UID == player.getUniqueId()) {
                guiPlayer = p;
                break;
            }
        }
        if(guiPlayer == null)return;
        event.setCancelled(true);
        if(guiPlayer.type == GUIType.VIEW_GUI) {
            if(event.getClickedInventory() == playerInventory)return;
            ViewGUI.openViewGUI(event);
        } else if(guiPlayer.type == GUIType.CRAFT_GUI) {
            if(event.getClickedInventory() == playerInventory)return;
            CraftingGUI.openCraftingGUI(event);
        } else if(guiPlayer.type == GUIType.ADD_CATEGORY){
            //TODO ADD EXCEPTION TO THIS RULE
            CategoryGUI.openCategoryGUI(event);
        } else if(guiPlayer.type == GUIType.ADD_RECIPE) {
            RecipeGUI.openRecipeGUI(event);
        }
    }

    public static GUIPlayer getPlayerInGUI(UUID uid) {
        for(GUIPlayer player : OPENED_GUIS) {
            if(player.UID == uid) return player;
        }
        return null;
    }
}
