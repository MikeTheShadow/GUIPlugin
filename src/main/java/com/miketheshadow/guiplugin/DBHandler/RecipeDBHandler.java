package com.miketheshadow.guiplugin.DBHandler;

import com.miketheshadow.guiplugin.gui.Page;
import com.miketheshadow.guiplugin.recipe.CustomRecipe;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.lang.Nullable;
import de.tr7zw.nbtapi.NBTItem;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class RecipeDBHandler {

    private static  MongoCollection<Document> collection = init();


    public static void addRecipe(Player player, CustomRecipe recipe) {
        FindIterable<Document> cursor = collection.find(new BasicDBObject("name",recipe.toDocument()));
        if (cursor.first() == null) {
            player.sendMessage(ChatColor.GREEN + "Adding new page...");
            collection.insertOne(recipe.toDocument());
            player.sendMessage(ChatColor.GREEN + "Complete!");
        } else {
            player.sendMessage(ChatColor.RED + "This page already exists!");
        }

    }

    @Nullable
    public static CustomRecipe getRecipeByIcon(ItemStack itemStack) {
        FindIterable<Document> cursor = collection.find(new BasicDBObject("icon", NBTItem.convertItemtoNBT(itemStack).toString()));
        return new CustomRecipe(cursor.first());
    }

    public static void removeRecipeByIcon(String icon) {
        collection.deleteOne(new BasicDBObject("icon", icon));
    }

    public static MongoCollection<Document> init() {
        if(collection == null) {
            MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
            MongoDatabase database = mongoClient.getDatabase("ComplexProficiencies");
            return database.getCollection("Recipes");
        }
        return collection;
    }
}
