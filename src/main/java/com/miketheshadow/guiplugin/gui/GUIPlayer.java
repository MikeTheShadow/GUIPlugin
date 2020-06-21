package com.miketheshadow.guiplugin.gui;

import com.miketheshadow.guiplugin.recipe.CustomRecipe;
import com.mongodb.lang.Nullable;
import org.bukkit.inventory.Recipe;

import java.util.UUID;

public class GUIPlayer {

    //GUIOPEN [player] [guiname] (use if adding recipes ->) [levelReq] [laborCost] [moneyCost] [profession]

    public UUID UID;
    public GUIType type;
    public Page currentPage;
    public int pageNum = 0;
    //1 = remove mode //2 = add mode
    public boolean removeAdd = false;
    public boolean isCrafting = false;
    @Nullable
    public int levelReq;

    @Nullable
    public int laborCost;

    @Nullable
    public int moneyCost;

    @Nullable
    public String profession;

    @Nullable
    public CustomRecipe recipe;

    public boolean addingRecipe = false;

    public GUIPlayer(UUID UID, GUIType type,Page currentPage) {
        this.UID = UID;
        this.type = type;
        this.currentPage = currentPage;
    }
}
