package com.miketheshadow.guiplugin.gui.types;

import com.miketheshadow.complexproficiencies.ComplexProficiencies;
import com.miketheshadow.complexproficiencies.api.UserAPI;
import com.miketheshadow.guiplugin.GUIPlugin;
import com.miketheshadow.guiplugin.gui.GUIPlayer;
import com.miketheshadow.guiplugin.gui.GUIType;
import com.miketheshadow.guiplugin.gui.Page;
import com.miketheshadow.guiplugin.gui.listener.OpenGUI;
import com.miketheshadow.guiplugin.recipe.CustomRecipe;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

import java.util.ArrayList;
import java.util.List;

import static com.miketheshadow.guiplugin.GUIPlugin.addOptionButtons;
import static com.miketheshadow.guiplugin.GUIPlugin.stringToItem;

public class BaseGUI {

    public static boolean hasPressedButton(Player player, ItemStack item, Inventory craftingInventory) {

        if(item == null) return false;
        if(!item.hasItemMeta())return false;
        GUIPlayer guiPlayer = OpenGUI.getPlayerInGUI(player.getUniqueId());
        Page currentPage = guiPlayer.currentPage;

        String itemName = item.getItemMeta().getDisplayName();
        /*

        NEXT PAGE

         */

        if(itemName.contains("NEXT PAGE")) {
            if(currentPage.getCategories().size() > 1) {
                if((guiPlayer.pageNum + 1) >= currentPage.getCategories().size()) {
                    craftingInventory.clear();
                    for (ItemStack i : GUIPlugin.stringListToItem(currentPage.getCategories().get(0))) {
                        if(i != null)craftingInventory.addItem(i);
                    }
                } else {
                    craftingInventory.clear();
                    for (ItemStack i : GUIPlugin.stringListToItem(currentPage.getCategories().get(guiPlayer.pageNum + 1))) {
                        if(i != null)craftingInventory.addItem(i);
                    }
                }
            } else if (currentPage.getRecipes().size() > 1){
                if((guiPlayer.pageNum + 1) >= currentPage.getRecipes().size()) {
                    craftingInventory.clear();
                    for (ItemStack i : GUIPlugin.stringListToItem(currentPage.getRecipes().get(0))) {
                        if(i != null)craftingInventory.addItem(i);
                    }
                } else {
                    craftingInventory.clear();
                    for (ItemStack i : GUIPlugin.stringListToItem(currentPage.getRecipes().get(guiPlayer.pageNum + 1))) {
                        if(i != null)craftingInventory.addItem(i);
                    }
                }
            }
            return true;
        }

        /*

        PREVIOUS PAGE

         */

        else if(itemName.contains("PREVIOUS PAGE")) {
            if(currentPage.getCategories().size() > 1) {
                if((guiPlayer.pageNum - 1) < 0) {
                    craftingInventory.clear();
                    for (ItemStack i : GUIPlugin.stringListToItem(currentPage.getCategories().get(currentPage.getCategories().size() - 1))) {
                        if(i != null)craftingInventory.addItem(i);
                    }
                } else {
                    craftingInventory.clear();
                    for (ItemStack i : GUIPlugin.stringListToItem(currentPage.getCategories().get(guiPlayer.pageNum - 1))) {
                        if(i != null)craftingInventory.addItem(i);
                    }
                }
            } else if (currentPage.getRecipes().size() > 1){
                if((guiPlayer.pageNum - 1) < 0) {
                    craftingInventory.clear();
                    for (ItemStack i : GUIPlugin.stringListToItem(currentPage.getRecipes().get(currentPage.getRecipes().size() - 1))) {
                        if(i != null)craftingInventory.addItem(i);
                    }
                } else {
                    craftingInventory.clear();
                    for (ItemStack i : GUIPlugin.stringListToItem(currentPage.getRecipes().get(guiPlayer.pageNum - 1))) {
                        if(i != null)craftingInventory.addItem(i);
                    }
                }
            }
            return true;
        }

        /*

        ADD CATEGORY

         */

        else if(itemName.contains("ADD CATEGORY")) {
            //if recipes exist warn user they cannot add recipes to a category page!
            if(hasItemInPage(currentPage.getRecipes())) {
                player.sendMessage("Cannot add a category to a recipe page");
            }
            else  {
                guiPlayer.type = GUIType.ADD_CATEGORY;
                GUIPlugin.addAddButton(player,craftingInventory);
            }
            return true;
        }

        /*

        ADD RECIPE

         */

        else if(itemName.contains("ADD RECIPE")) {

            //if categories exist warn user they cannot add categories to a recipe page!
            if(hasItemInPage(currentPage.getCategories())) {
                player.sendMessage(ChatColor.RED + "Cannot add a recipe to a category page");
            }
            else if(!guiPlayer.addingRecipe) {
                player.sendMessage(ChatColor.RED + "You didn't add the correct parameters!");
            } else {
                player.sendMessage(ChatColor.GREEN + "Adding recipe!");
                guiPlayer.type = GUIType.ADD_RECIPE;
                GUIPlugin.addRecipeAddButton(player,craftingInventory,guiPlayer);
            }

            return true;
        }

        /*

        CRAFT SAID RECIPE

         */
        else if(itemName.contains("REGISTER RECIPE")) {
            List<ItemStack> itemsToCraft = new ArrayList<>();
            List<ItemStack> itemRequirements = new ArrayList<>();
            for(int i = 0; i < 8;i++) {
                ItemStack addItem = craftingInventory.getItem(i);
                if(addItem == null)continue;
                if(addItem.getItemMeta() == null)continue;
                itemsToCraft.add(craftingInventory.getItem(i));
            }
            for(int i = 9; i < 44;i++) {
                ItemStack addItem = craftingInventory.getItem(i);
                if(addItem == null)continue;
                if(addItem.getItemMeta() == null)continue;
                itemRequirements.add(craftingInventory.getItem(i));
            }
            if(itemsToCraft.isEmpty()) {
                player.sendMessage(ChatColor.RED + "Missing item to craft!");
            } else if(itemRequirements.isEmpty()) {
                player.sendMessage(ChatColor.RED + "Missing materials for item!");
            } else {
                //public CustomRecipe(List<ItemStack> materials, List<ItemStack> results, int levelReq, int laborCost,int moneyCost, String profession, String icon)
                CustomRecipe recipe = new CustomRecipe(itemRequirements,itemsToCraft,guiPlayer.levelReq,guiPlayer.laborCost,guiPlayer.moneyCost,guiPlayer.profession,itemsToCraft.get(0));
                currentPage.addRecipe(player,recipe);
                player.sendMessage(ChatColor.GREEN + "Recipe added!");
                player.getOpenInventory().close();
            }
            return true;
        }

        /*

        SUBMIT NEW CATEGORY

         */

        else if(itemName.contains("ADD PAGE")) {
            for(int i = 0; i < 45;i++) {
                ItemStack stack = craftingInventory.getContents()[i];
                if(stack == null) continue;
                if(!stack.hasItemMeta())continue;
                if(stack.getType() == Material.AIR) continue;
                try {
                    String attempt = currentPage.getRecipes().get(0).get(0);
                    player.sendMessage(ChatColor.RED + "Cannot add category to recipe page!");
                    return true;
                } catch (Exception ignored) {}
                currentPage.addCategory(player,stack.getItemMeta().getDisplayName(), NBTItem.convertItemtoNBT(stack));
                player.sendMessage(ChatColor.GREEN + "Adding item: " + stack.getItemMeta().getDisplayName());
            }
            while(currentPage.getPreviousPage() != null) {
                currentPage = currentPage.getPreviousPage();
            }
            return true;
        }

        else if(itemName.contains("BACK")) {
            //TODO DO BACK BUTTON STUFF HERE
            if(currentPage.getPreviousPage() != null) {
                backPage(player,currentPage.getPreviousPage(),craftingInventory);
                guiPlayer.currentPage = currentPage.getPreviousPage();
            } else {
                player.sendMessage(ChatColor.RED + "You cannot go back any further!");
            }
            return true;
        }

        else if(itemName.contains("REMOVE ITEM")) {
            if(guiPlayer.removeAdd) {
                guiPlayer.removeAdd = false;
                GUIPlugin.addOptionButtons(craftingInventory,player,false);
            } else {
                guiPlayer.removeAdd = true;
                GUIPlugin.addOptionButtons(craftingInventory,player,true);
            }
            return true;
        }

        /*

        CRAFTING

         */
        else if(itemName.contains("CRAFT")) {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(player.getUniqueId());
            CustomRecipe recipe = guiPlayer.recipe;
            double playerBalance = GUIPlugin.econ.getBalance(offlinePlayer);
            int playerProfLevel = UserAPI.getProfLevel(player,recipe.getProfession());
            int playerLaborLevel = UserAPI.getUserLabor(player);
            if(playerProfLevel < recipe.getLevelReq()) {
                player.sendMessage(ChatColor.RED + "You are not high enough level to craft this!");
            } else if(!hasItemsForRecipe(player.getInventory(),recipe)) {
                player.sendMessage(ChatColor.RED + "You do not have the materials to craft this!");
            }
            else if(playerBalance < recipe.getMoneyCost()) {
                player.sendMessage(ChatColor.RED + "You don't have enough money to craft this!");
            } else if(playerLaborLevel < recipe.getLaborCost()) {
                player.sendMessage(ChatColor.RED + "You don't have enough labor to craft this!");
            } else if(getSize(player.getInventory()) < recipe.getResults().size()) {
                player.sendMessage(ChatColor.RED + "You don't have enough inventory space to craft this!");
            } else {
                GUIPlugin.econ.withdrawPlayer(offlinePlayer,recipe.getMoneyCost());
                for(ItemStack i : recipe.getMaterials()) {
                    player.getInventory().removeItem(i);
                }
                for(ItemStack i : recipe.getResults()) {
                    player.getInventory().addItem(i);
                }
                UserAPI.addExperienceToProf(player,recipe.getProfession(),recipe.getLaborCost());
            }
        }

        return false;
    }

    public static int getSize(Inventory inventory) {
        int size = -5;
        for(ItemStack i : inventory) {
            if(i == null)size++;
        }
        if(size < 0) return 0;
        return size;
    }


    public static boolean hasItemsForRecipe(Inventory inv, CustomRecipe recipe) {

        for (ItemStack i : recipe.getMaterials()) {
            if(!inv.containsAtLeast(i,i.getAmount())) return false;
        }

        return true;
    }

    public static boolean hasItemInPage(List<List<String>> list) {
        try {
            list.get(0).get(0);
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    public static void backPage(Player player,Page page,Inventory inventory) {
        inventory.clear();
        addOptionButtons(inventory,player,false);
        if(isPage(page.getCategories())) {
            List<String> items = page.getCategories().get(0);
            for(String item : items) {
                inventory.addItem(stringToItem(item));
            }
        } else if(isPage(page.getRecipes())) {
            List<String> items = page.getRecipes().get(0);
            for(String item : items) {
                inventory.addItem(stringToItem(item));
            }
        }

    }
    public static void resetPage(Player player,Page page,Inventory inventory,GUIPlayer guiPlayer) {
        inventory.clear();
        addOptionButtons(inventory,player,false);
        if(isPage(page.getCategories())) {
            List<String> items = page.getCategories().get(0);
            for(String item : items) {
                inventory.addItem(stringToItem(item));
            }
        } else if(isPage(page.getRecipes())) {
            List<String> items = page.getRecipes().get(0);
            for(String item : items) {
                inventory.addItem(stringToItem(item));
            }
        }
        guiPlayer.type = GUIType.VIEW_GUI;
    }

    public static boolean isPage(List<List<String>> list) {
        try {
            list.get(0).get(0);
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }
}
