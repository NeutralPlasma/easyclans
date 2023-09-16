package net.astrona.easyclans.utils;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.Base64;

public class Serialization {
    public static String encodeItemBase64(ItemStack itemStack) {
        YamlConfiguration yamlConfiguration = new YamlConfiguration();
        yamlConfiguration.set("item", itemStack);
        return Base64.getEncoder().encodeToString(yamlConfiguration.saveToString().getBytes());
    }

    public static ItemStack decodeItemBase64(String encodedItem) {
        YamlConfiguration yamlConfiguration = new YamlConfiguration();

        try {
            yamlConfiguration.loadFromString(new String(Base64.getDecoder().decode(encodedItem)));
        } catch (InvalidConfigurationException e) {

            return null;
        }

        return yamlConfiguration.getItemStack("item");
    }

}
