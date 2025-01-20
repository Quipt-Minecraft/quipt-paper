/*
 * Copyright (c) 2024. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package me.quickscythe.quipt.utils;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.registries.VanillaRegistries;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.bukkit.inventory.meta.ItemMeta;
import org.json.JSONObject;

public class ItemUtils {

    public static ItemStack deserializeVanillaString(String string) {
        // String will look something like: minecraft:stone[display:{Name:"{\"text\":\"Stone\",\"color\":\"red\"}"}]
        String extractedMaterial = string.contains("\\[") ? string.split("\\[")[0] : string;
        String extractedData = string.replaceFirst(extractedMaterial, "");
        JSONObject components = serializeComponents(extractedData);
        NamespacedKey itemKey = NamespacedKey.fromString(extractedMaterial);
        ItemType itemType = RegistryAccess.registryAccess().getRegistry(RegistryKey.ITEM).get(itemKey);
        ItemStack itemStack = itemType.createItemStack();
        ItemMeta itemMeta = itemStack.getItemMeta();

        return itemStack;
    }

    public static JSONObject serializeComponents(String componentString){
        componentString = componentString.substring(1, componentString.length() - 1);

        String[] pairs = componentString.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

        JSONObject jsonObject = new JSONObject();

        for (String pair : pairs) {
            String[] keyValue = pair.split("=", 2);
            String key = keyValue[0].trim();
            String value = keyValue[1].trim();

            if (value.startsWith("{") && value.endsWith("}")) {
                jsonObject.put(key, new JSONObject(value));
            } else {
                jsonObject.put(key, Integer.parseInt(value));
            }
        }

        return jsonObject;
    }

    public static JSONObject serializeComponents(ItemStack itemStack) {
        String input = itemStack.getItemMeta().getAsComponentString();
        return serializeComponents(input);
    }
}
