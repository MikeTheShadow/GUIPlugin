package com.miketheshadow.guiplugin.gui.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class CloseGUI implements Listener {

    @EventHandler
    public void onGUICloseEvent(InventoryCloseEvent event) {
        OpenGUI.OPENED_GUIS.removeIf(p -> p.UID == event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPlayerLeaveEvent(PlayerQuitEvent event) {
        OpenGUI.OPENED_GUIS.removeIf(p -> p.UID == event.getPlayer().getUniqueId());
    }
}
