package com.miketheshadow.guiplugin.recipe;

import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTContainer;
import de.tr7zw.nbtapi.NBTItem;
import org.bson.Document;
import org.bukkit.inventory.ItemStack;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CustomRecipe implements Serializable {

    private List<String> materials;
    private final List<String> results;
    private final String icon;
    private final int levelReq;
    private final int laborCost;
    private final int moneyCost;
    private final String profession;


    public CustomRecipe(List<ItemStack> materials, List<ItemStack> results, int levelReq, int laborCost,int moneyCost, String profession, ItemStack icon) {

        //convert itemList for materials into string list
        List<String> materialsList = new ArrayList<>();
        for (ItemStack m : materials) materialsList.add(NBTItem.convertItemtoNBT(m).toString());
        this.materials = materialsList;

        //convert itemList for results into material list
        List<String> resultList = new ArrayList<>();
        for (ItemStack r : results) resultList.add(NBTItem.convertItemtoNBT(r).toString());

        this.results = resultList;
        this.levelReq = levelReq;
        this.laborCost = laborCost;
        this.profession = profession;
        this.icon = NBTItem.convertItemtoNBT(icon).toString(); //convert icon to String
        this.moneyCost = moneyCost;
    }

    public CustomRecipe(Document document) {
        this.results = document.getList("item", String.class);
        this.materials = document.getList("ingredients", String.class);
        this.icon = document.getString("icon");
        this.levelReq = document.getInteger("levelReq");
        this.laborCost = document.getInteger("laborCost");
        this.moneyCost = document.getInteger("moneyCost");
        this.profession = document.getString("profession");
    }

    public void setMaterials(List<ItemStack> materials) {
        List<String> items = new ArrayList<>();
        for (ItemStack item : materials) {
            items.add(NBTItem.convertItemtoNBT(item).toString());
        }
        this.materials = items;
    }

    public NBTCompound getItemToBeCrafted() {
        return new NBTContainer(results);
    }

    public Document toDocument() {
        Document document = new Document();
        document.append("ingredients", materials);
        document.append("item", results);
        document.append("icon", icon);
        document.append("levelReq", levelReq);
        document.append("laborCost", laborCost);
        document.append("moneyCost", moneyCost);
        document.append("profession", profession);
        return document;
    }

    public List<ItemStack> getMaterials() {
        return stringListToItemList(materials);
    }

    public List<ItemStack> getResults() {
        return stringListToItemList(results);
    }

    private List<ItemStack> stringListToItemList(List<String> list) {
        List<ItemStack> returnStack = new ArrayList<>();
        for(String item : list) {
            returnStack.add(NBTItem.convertNBTtoItem(new NBTContainer(item)));
        }
        return returnStack;
    }

    public String getProfession() { return profession; }

    public int getLevelReq() {
        return levelReq;
    }

    public int getLaborCost() {
        return laborCost;
    }

    public int getMoneyCost() {return moneyCost;}

    public String getIcon() {
        return icon;
    }

}
