package com.miketheshadow.guiplugin;

import com.miketheshadow.guiplugin.DBHandler.PageDBHandler;
import com.miketheshadow.guiplugin.gui.GUIPlayer;
import com.miketheshadow.guiplugin.gui.GUIType;
import com.miketheshadow.guiplugin.gui.Page;
import com.miketheshadow.guiplugin.gui.TagRegister;
import com.miketheshadow.guiplugin.gui.listener.CloseGUI;
import com.miketheshadow.guiplugin.gui.listener.OpenGUI;
import com.miketheshadow.guiplugin.recipe.CustomRecipe;
import de.tr7zw.nbtapi.NBTContainer;
import de.tr7zw.nbtapi.NBTItem;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

import static com.miketheshadow.guiplugin.gui.types.BaseGUI.isPage;

public final class GUIPlugin extends JavaPlugin {

    //economy
    public static Economy econ;

    //default
    public static GUIPlugin INSTANCE;

    @Override
    public void onEnable() {
        INSTANCE = this;
        if (!setupEconomy()) {
            this.getLogger().severe("Disabled due to no Vault dependency found!");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        // Plugin startup logic
        Bukkit.getPluginCommand("guiadd").setExecutor(this::onCommand);
        Bukkit.getPluginCommand("guiopen").setExecutor(this::onCommand);
        Bukkit.getPluginManager().registerEvents(new OpenGUI(),this);
        Bukkit.getPluginManager().registerEvents(new CloseGUI(),this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private boolean setupEconomy() {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            return false;
        }

        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if(command.getName().equalsIgnoreCase("guiadd")) {
            if(args.length != 1) return false;
            Player player = (Player)sender;
            Page page = new Page(args[0],null);
            PageDBHandler.addPage(player,page);
            return true;
        }
        if(command.getName().equalsIgnoreCase("guiopen")) {

            if(args.length != 2 && args.length != 6) return false;
            Player player = Bukkit.getPlayer(args[0]);
            //player exists
            if(player == null) {
                sender.sendMessage(ChatColor.RED + "Player isn't online!");
                return true;
            }
            //gui exists
            Page page = PageDBHandler.getPage(args[1]);

            if(page == null) {
                player.sendMessage(ChatColor.RED + "That GUI does not exist!");
                return true;
            }
            //player in gui
            if(OpenGUI.getPlayerInGUI(player.getUniqueId()) != null) {
                sender.sendMessage(ChatColor.RED + "Player is already in GUI!");
                return true;
            }
            //create inventory
            Inventory inventory = addOptionButtons(Bukkit.createInventory(player,54,page.getName()),player,false);
            //add categories/recipes
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
            //openInventory
            player.openInventory(inventory);
            GUIPlayer guiPlayer = new GUIPlayer(player.getUniqueId(),GUIType.VIEW_GUI,page);
            if(args.length == 6) {
                guiPlayer.addingRecipe = true;
                guiPlayer.levelReq = Integer.parseInt(args[2]);
                guiPlayer.laborCost = Integer.parseInt(args[3]);
                guiPlayer.moneyCost = Integer.parseInt(args[4]);
                guiPlayer.profession = args[5];
            }
            OpenGUI.OPENED_GUIS.add(guiPlayer);
            return true;
        }
        return false;
    }

    public static Inventory addOptionButtons(Inventory inventory, Player player,boolean remove) {
        for(int i = 45;i != 53;i++) {
            if(remove)inventory.setItem(i,new ItemStack(Material.REDSTONE_BLOCK));
            else inventory.setItem(i,new ItemStack(Material.AIR));
        }
        if(player.isOp()) {
            inventory.setItem(48, TagRegister.addCategory);
            inventory.setItem(49,TagRegister.addRecipe);
            inventory.setItem(50,TagRegister.removePage);
        }
        inventory.setItem(45, TagRegister.previousPage);
        inventory.setItem(52,TagRegister.backButton);
        inventory.setItem(53, TagRegister.nextPage);

        return inventory;
    }
    public static void addAddButton(Player player,Inventory inventory) {
        inventory.clear();
        if(player.isOp()) {
            inventory.setItem(49, TagRegister.addItemToPage);
        }
    }

    public static void addCraftingButton(Inventory inventory, CustomRecipe recipe) {
        inventory.clear();
        ItemStack stack = TagRegister.register(Material.BLUE_SHULKER_BOX,"CRAFT");
        ItemMeta meta = stack.getItemMeta();
        List<String> lore = meta.getLore();
        lore.add(ChatColor.RESET + "Level " + ChatColor.BLUE + recipe.getLevelReq() + ChatColor.RESET + " " + recipe.getProfession());
        lore.add(ChatColor.RESET + "Labor Cost: " + ChatColor.YELLOW  + recipe.getLaborCost());
        lore.add(ChatColor.RESET + "Money Cost: " + ChatColor.GREEN + recipe.getMoneyCost());
        meta.setLore(lore);
        stack.setItemMeta(meta);
        for(ItemStack item : recipe.getResults()) {
            inventory.addItem(item);
        }
        for(int i = 9; i < recipe.getMaterials().size() + 9;i++) {
            inventory.setItem(i,recipe.getMaterials().get(i - 9));
        }
        inventory.setItem(52,TagRegister.backButton);
        inventory.setItem(49, stack);
    }

    public static void addRecipeAddButton(Player player,Inventory inventory,GUIPlayer guiPlayer) {
        inventory.clear();
        if(player.isOp()) {
            ItemStack stack = TagRegister.register(Material.BLUE_SHULKER_BOX,"REGISTER RECIPE");
            ItemMeta meta = stack.getItemMeta();
            List<String> lore = meta.getLore();
            //GUIOPEN [player] [guiname] (use if adding recipes ->) [levelReq] [laborCost] [moneyCost] [profession]
            lore.add(ChatColor.RESET + "Level " + ChatColor.BLUE + guiPlayer.levelReq + ChatColor.RESET + " " + guiPlayer.profession);
            lore.add(ChatColor.RESET + "Labor Cost: " + ChatColor.YELLOW  + guiPlayer.laborCost);
            lore.add(ChatColor.RESET + "Money Cost: " + ChatColor.GREEN + guiPlayer.moneyCost);
            meta.setLore(lore);
            stack.setItemMeta(meta);
            inventory.setItem(49, stack);
        }
    }
    public static ItemStack stringToItem(String item) {
        return NBTItem.convertNBTtoItem(new NBTContainer(item));
    }

    public static ItemStack[] stringListToItem(List<String> item) {
        ItemStack[] stack = new ItemStack[item.size()];
        for(int i = 0; i < item.size() - 1;i++) {
            stack[i] = NBTItem.convertNBTtoItem(new NBTContainer(item.get(i)));
        }
        return stack;
    }
}
