package com.miketheshadow.guiplugin.util;

import com.miketheshadow.complexproficiencies.api.UserAPI;
import com.miketheshadow.guiplugin.GUIPlugin;
import com.miketheshadow.guiplugin.gui.GUIPlayer;
import com.miketheshadow.guiplugin.gui.GUIType;
import com.miketheshadow.guiplugin.gui.listener.OpenGUI;
import com.miketheshadow.guiplugin.recipe.CustomRecipe;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class CraftingTimerTask implements Runnable {

    OfflinePlayer offlinePlayer;
    CustomRecipe recipe;
    Player player;
    int timer;
    Thread thread;
    int modifier = 4;
    public CraftingTimerTask(OfflinePlayer offlinePlayer,Player player, CustomRecipe recipe ,int timer) {
        this.offlinePlayer = offlinePlayer;
        this.recipe = recipe;
        this.player = player;
        this.timer = timer;
        if(recipe.getMaterials().size() > 1)this.modifier = 1;
    }

    @Override
    public void run() {
        GUIPlayer guiPlayer = OpenGUI.getPlayerInGUI(player.getUniqueId());
        if(guiPlayer == null) {
            player.sendMessage(ChatColor.RED + "You canceled the craft!");
            return;
        }
        if(!OpenGUI.OPENED_GUIS.contains(guiPlayer) || guiPlayer.type != GUIType.CRAFT_GUI) {
            player.sendMessage(ChatColor.RED + "You canceled the craft!");
            guiPlayer.isCrafting = false;
            return;
        }
        try { Thread.sleep(100/modifier); }catch (InterruptedException e) { e.printStackTrace(); }
        if(!OpenGUI.OPENED_GUIS.contains(guiPlayer) || guiPlayer.type != GUIType.CRAFT_GUI) {
            player.sendMessage(ChatColor.RED + "You canceled the craft!");
            guiPlayer.isCrafting = false;
            return;
        }
        if(timer > 100/modifier) {
            craftItem();
        } else {
            if(timer % (10/modifier) == 0) {
                int time = timer / (10/modifier);
                if(time <= 9)player.getOpenInventory().getTopInventory().setItem(35 + time,new ItemStack(Material.RED_SHULKER_BOX));
            }
            Sound sound = Sound.BLOCK_LEVER_CLICK;
            Location location = player.getLocation();
            World world = player.getWorld();
            world.playSound(location,sound,0.1f,1);
            CraftingTimerTask timerTask = new CraftingTimerTask(offlinePlayer ,player ,recipe ,timer + 1);
            timerTask.start();
        }

    }
    public void start() {

        if (this.thread == null) {
            this.thread = new Thread(this, "crafting thread");
            this.thread.start();
        }

    }

    public void craftItem() {
        GUIPlugin.econ.withdrawPlayer(offlinePlayer,recipe.getMoneyCost());
        for(ItemStack i : recipe.getMaterials()) {
            player.getInventory().removeItem(i);
        }
        for(ItemStack i : recipe.getResults()) {
            player.getInventory().addItem(i);
        }
        UserAPI.addExperienceToProf(player,recipe.getProfession(),recipe.getLaborCost());
        for(int i  = 0; i < 9;i++) {
            player.getOpenInventory().getTopInventory().setItem(36 + i,null);
        }
        GUIPlayer guiPlayer = OpenGUI.getPlayerInGUI(player.getUniqueId());
        guiPlayer.isCrafting = false;
    }
}
