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
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class PageDBHandler {

    private static  MongoCollection<Document> collection = init();


    public static void addPage(Player player, Page page) {
        FindIterable<Document> cursor = collection.find(new BasicDBObject("name",page.getName()));
        if (cursor.first() == null) {
            player.sendMessage(ChatColor.GREEN + "Adding new page...");
            collection.insertOne(page.toDocument());
            player.sendMessage(ChatColor.GREEN + "Complete!");
        } else {
            player.sendMessage(ChatColor.RED + "This page already exists!");
        }

    }

    @Nullable
    public static Page getPage(String name) {
        FindIterable<Document> cursor = collection.find(new BasicDBObject("name", name));
        if(cursor.first() == null) return null;
        return new Page(cursor.first());
    }


    public static Page getPageFromIcon(String icon) {
        FindIterable<Document> cursor = collection.find(new BasicDBObject("icon", icon));
        return new Page(cursor.first());
    }

    public static void updatePage(Page page) {
        collection.replaceOne(new BasicDBObject("name", page.getName()), page.toDocument());
    }

    public static void removePageByIcon(String icon) {
        collection.deleteOne(new BasicDBObject("icon", icon));
    }

    public static MongoCollection<Document> init() {
        if(collection == null)
        {
            MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
            MongoDatabase database = mongoClient.getDatabase("ComplexProficiencies");
            return database.getCollection("Pages");
        }
        return collection;
    }
}
