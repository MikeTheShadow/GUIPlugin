package com.miketheshadow.guiplugin.gui.types;

import com.miketheshadow.guiplugin.DBHandler.PageDBHandler;
import com.miketheshadow.guiplugin.DBHandler.RecipeDBHandler;
import com.miketheshadow.guiplugin.GUIPlugin;
import com.miketheshadow.guiplugin.gui.GUIPlayer;
import com.miketheshadow.guiplugin.gui.GUIType;
import com.miketheshadow.guiplugin.gui.Page;
import com.miketheshadow.guiplugin.gui.listener.OpenGUI;
import com.miketheshadow.guiplugin.recipe.CustomRecipe;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ViewGUI extends BaseGUI
{
    public static void openViewGUI(InventoryClickEvent event) {
        Player player = (Player)event.getWhoClicked();
        Inventory craftingInventory = player.getOpenInventory().getTopInventory();
        GUIPlayer guiPlayer = OpenGUI.getPlayerInGUI(player.getUniqueId());
        Page page = guiPlayer.currentPage;
        if(event.getClickedInventory() == null) return;
        if(hasPressedButton(player,craftingInventory.getItem(event.getSlot()),craftingInventory)) return;
        if(event.getClick() != ClickType.LEFT) return;
        ItemStack itemClicked = event.getCurrentItem();
        if(!event.getClick().isLeftClick() || itemClicked == null || itemClicked.getType() == Material.AIR)return;

        String container = NBTItem.convertItemtoNBT(itemClicked).toString();
        if(guiPlayer.removeAdd && !hasPressedButton(player,itemClicked,craftingInventory)) {
            player.sendMessage("You want to remove item: " + itemClicked.getItemMeta().getDisplayName());
            removeAllSubItems(NBTItem.convertItemtoNBT(itemClicked).toString());
            return;
        }
        //check if item is a category

        if(BaseGUI.isPage(page.getCategories()) && !hasPressedButton(player,itemClicked,craftingInventory)) {
            Page pageToLoad = PageDBHandler.getPageFromIcon(NBTItem.convertItemtoNBT(itemClicked).toString());
            guiPlayer.currentPage = pageToLoad;
            pageToLoad.setPreviousPage(page);
            resetPage(player,pageToLoad,craftingInventory,guiPlayer);
        }
        else if(BaseGUI.isPage(page.getRecipes()) && !hasPressedButton(player,itemClicked,craftingInventory)){
            Page pageToLoad = new Page("crafting","crafting");
            pageToLoad.setPreviousPage(page);
            guiPlayer.currentPage = pageToLoad;
            guiPlayer.type = GUIType.CRAFT_GUI;
            resetPage(player,pageToLoad,craftingInventory,guiPlayer);
            CustomRecipe recipe = RecipeDBHandler.getRecipeByIcon(itemClicked);
            guiPlayer.recipe = recipe;
            GUIPlugin.addCraftingButton(craftingInventory,recipe);
        }

    }


    //TODO FIX ME
    public static void removeAllSubItems(String itemName) {
        Page page = PageDBHandler.getPageFromIcon(itemName);
        if(page.getRecipes().size() < 1 && page.getCategories().size() < 1) {
        } else {
            if(page.getRecipes().size() > 0) {
                //TODO REMOVE ALL RECIPES
            } else {
                for(List<String> pageList : page.getCategories()) {
                    for(String iPage : pageList) {
                        Page subPage = PageDBHandler.getPageFromIcon(iPage);
                        if(subPage.getCategories().size() < 1) {
                            PageDBHandler.removePageByIcon(subPage.getIcon());
                        } else {
                            removeAllSubItems(iPage);
                        }
                    }
                }
            }
        }
    }
}
