package com.miketheshadow.guiplugin.gui;

import com.miketheshadow.guiplugin.DBHandler.PageDBHandler;
import com.miketheshadow.guiplugin.DBHandler.RecipeDBHandler;
import com.miketheshadow.guiplugin.recipe.CustomRecipe;
import com.mongodb.lang.Nullable;
import de.tr7zw.nbtapi.NBTContainer;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Page extends Document {
    String name;

    @Nullable
    String icon;

    List<List<String>> categories = new ArrayList<>();
    List<List<String>> recipes = new ArrayList<>();

    @Nullable
    Page previousPage = null;
    public Page(String name,@Nullable String icon) {
        categories.add(new ArrayList<>());
        recipes.add(new ArrayList<>());
        this.icon = icon;
        this.name = name;
    }

    public Document toDocument() {
        Document document = new Document();
        document.append("name",name);
        document.append("icon",icon);
        for(int i = 0; i < categories.size();i++) {
            document.append("c_page_" + i,categories.get(i));
        }
        for(int i = 0; i < recipes.size();i++) {
            document.append("r_page_" + i,recipes.get(i));
        }
        return document;
    }

    public Page(Document document) {
        name = document.getString("name");
        icon = document.getString("icon");
        for(int i = 0; i < 100;i++) {
            List<String> categoryPage = document.getList("c_page_" + i,String.class);
            if(categoryPage == null)break;
            categories.add(categoryPage);
        }
        for(int i = 0; i < 100;i++) {
            List<String> recipePage = document.getList("r_page_" + i,String.class);
            if(recipePage == null)break;
            recipes.add(recipePage);
        }
    }

    public void addCategory(Player player,  String categoryName, NBTContainer category) {
        if(categories.size() == 0) {
            categories.add(new ArrayList<>());
        }
        int size = categories.size() - 1;
        List<String> lastPage = categories.get(size);
        if(lastPage.size() == 45) {
            categories.add(new ArrayList<>());
            categories.get(size + 1).add(category.toString());
        } else {
            categories.get(size).add(category.toString());
        }
        PageDBHandler.updatePage(this);
        PageDBHandler.addPage(player,new Page(categoryName,category.toString()));
    }

    public void addRecipe(Player player, CustomRecipe recipe) {
        if(recipes.size() == 0) {
            recipes.add(new ArrayList<>());
        }
        int size = recipes.size() - 1;
        List<String> lastPage = recipes.get(size);
        if(lastPage.size() == 45) {
            recipes.add(new ArrayList<>());
            recipes.get(size + 1).add(recipe.getIcon());
        } else {
            recipes.get(size).add(recipe.getIcon());
        }
        RecipeDBHandler.addRecipe(player,recipe);
        PageDBHandler.updatePage(this);
    }

    public void removeRecipe(CustomRecipe recipe) {
        for(List<String> stringList : recipes) {
            for(String single : stringList) {
                if(recipe.getIcon().equals(single)) {
                    stringList.remove(single);
                    PageDBHandler.updatePage(this);
                    return;
                }
            }
        }
    }

    public String getName() { return name; }
    @Nullable public String getIcon() { return icon; }
    public List<List<String>> getCategories() { return categories; }
    public List<List<String>> getRecipes() { return recipes; }

    public Page getPreviousPage() {return previousPage;}
    public void setPreviousPage(Page previousPage){this.previousPage = previousPage;}
}
