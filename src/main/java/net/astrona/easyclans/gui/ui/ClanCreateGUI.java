package net.astrona.easyclans.gui.ui;

import net.astrona.easyclans.ClansPlugin;
import net.astrona.easyclans.controller.ClansController;
import net.astrona.easyclans.controller.LanguageController;
import net.astrona.easyclans.controller.PlayerController;
import net.astrona.easyclans.gui.GUI;
import net.astrona.easyclans.gui.Icon;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static net.kyori.adventure.key.Key.key;
import static net.kyori.adventure.sound.Sound.sound;

public class ClanCreateGUI extends GUI {

    private ItemStack banner;
    private int kickTime;
    private double moneyPrice, payoutPercentage;
    private String name = "DEFAULT_NAME", displayName = "DEFAULT", tag = "DEFAULT";
    private Player player;
    private ClansPlugin plugin;
    private PlayerController playerController;
    private ClansController clansController;


    public ClanCreateGUI(Player player, ClansPlugin plugin, PlayerController playerController, ClansController clansController) {
        super(54, LanguageController.getLocalized("create.menu.title"));
        this.player = player;
        this.plugin = plugin;
        this.playerController = playerController;
        this.clansController = clansController;
        init();
        fancyBackground();
        open(player);
    }


    private void legalizeBanner(){
        var meta = banner.getItemMeta();
        meta.displayName(ClansPlugin.MM.deserialize(LanguageController.getLocalized("create.menu.banner.name").replace("{name}", name)));
        for(var enchant : meta.getEnchants().keySet()){
            meta.removeEnchant(enchant);
        }
        var loreText = LanguageController.getLocalizedList("create.menu.banner.lore");
        meta.lore(loreText.stream().map(it ->
                ClansPlugin.MM.deserialize(it
                        .replace("{name}", name)
                        .replace("{display_name}", displayName))
        ).toList());
        banner.setItemMeta(meta);

    }


    private Icon clanBanner(){
        Icon icon = new Icon(banner, (self, player) -> {
            legalizeBanner();
            self.itemStack = banner;
        });

        icon.addDragItemAction(((player, itemStack) -> {
            player.sendMessage(itemStack.getType().toString());
            if(itemStack.getType().toString().endsWith("BANNER")) {
                banner = itemStack.clone();
                this.update(player, 13);
                player.playSound(sound(key("block.note_block.cow_bell"), Sound.Source.MASTER, 1f, 1.19f));
            }else{
                player.playSound(sound(key("block.note_block.didgeridoo"), Sound.Source.MASTER, 1f, 1.19f));
            }

        }));

        icon.addClickAction((player) -> {

        });


        return icon;
    }

    /*private Icon priceSettings(){

    }


    private Icon kickSettings(){

    }

    private Icon payoutSettings(){

    }*/



    private void init(){
        banner = new ItemStack(Material.ORANGE_BANNER); // random generator maybe?

        addIcon(13, clanBanner());
        /*addIcon(20, priceSettings());
        addIcon(31, kickSettings());
        addIcon(24, payoutSettings());*/
    }
}
