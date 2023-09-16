package net.astrona.easyclans.controller;

import net.astrona.easyclans.ClansPlugin;
import net.kyori.adventure.text.Component;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class LanguageController {
    private static YamlConfiguration locals;

    public static void loadLocals(Plugin plugin) {
        File file = new File(plugin.getDataFolder(), "language.yml");

        if (!file.exists()) {
            file.getParentFile().mkdirs();

            try {
                copy(plugin.getResource("language.yml"), Files.newOutputStream(file.toPath()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            locals = new YamlConfiguration();
            locals.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }


    public static String getLocalized(String key) {
        if (locals.contains(key)) {
            return locals.getString(key);
        }
        return key;
    }

    public static List<String> getLocalizedList(String key) {
        if (locals.contains(key)) {
            return locals.getStringList(key);
        }
        return List.of(key);
    }

    public static List<Component> getLocalizedDesiralizedList(String key) {
        var list = getLocalizedList(key);
        List<Component> serialized = new ArrayList<>();
        for (var line : list) {
            serialized.add(ClansPlugin.MM.deserialize(line));
        }
        return serialized;
    }

    private static void copy(InputStream input, OutputStream output) {
        if (input == null) return;
        int n;
        byte[] buffer = new byte[1024 * 4];

        try {
            while ((n = input.read(buffer)) != -1) {
                output.write(buffer, 0, n);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
