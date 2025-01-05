package eu.virtusdevelops.easyclans.utils;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BannerUtils {
    public static Material getRandomBanner(){
        Random random = new Random();
        var colours = List.of("BLACK", "BLUE", "BROWN", "ORANGE", "CYAN", "GRAY", "GREEN", "LIGHT_BLUE", "LIME", "MAGENTA");
        int randomC = random.nextInt(colours.size());
        return Material.getMaterial(colours.get(randomC) + "_BANNER");
    }

    public static DyeColor getRandomColour(){
        return new RandomEnumGenerator<>(DyeColor.class).randomEnum();
    }

    public static List<Pattern> getPatterns(int amount){
        var randomPattern = new RandompatternTypeGenerator();
        List<Pattern> patterns = new ArrayList<>();
        for(int i = 0; i < amount; i++){
            patterns.add(new Pattern(getRandomColour(), randomPattern.randomPatternType()));
        }
        return patterns;
    }

    public static ItemStack generateRandomBanner(){
        var item = new ItemStack(getRandomBanner());
        BannerMeta meta = (BannerMeta) item.getItemMeta();
        Random random = new Random();
        meta.setPatterns(getPatterns(random.nextInt(5) + 1 ));
        item.setItemMeta(meta);
        return item;
    }

    public static  ItemStack strip(ItemStack itemStack){
        var item = itemStack.clone();
        var meta = item.getItemMeta();
        meta.lore(null);
        meta.addItemFlags(ItemFlag.HIDE_ITEM_SPECIFICS);
        meta.displayName(null);
        item.setItemMeta(meta);
        return item;
    }


}
