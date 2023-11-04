package eu.virtusdevelops.easyclans.gui.ui;

import eu.virtusdevelops.easyclans.ClansPlugin;
import eu.virtusdevelops.easyclans.controller.ClansController;
import eu.virtusdevelops.easyclans.controller.LanguageController;
import eu.virtusdevelops.easyclans.controller.PlayerController;
import eu.virtusdevelops.easyclans.controller.TropyController;
import eu.virtusdevelops.easyclans.gui.GUI;
import eu.virtusdevelops.easyclans.gui.Icon;
import eu.virtusdevelops.easyclans.utils.AbstractChatUtil;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.format.TextDecoration;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

import static net.kyori.adventure.key.Key.key;
import static net.kyori.adventure.sound.Sound.sound;

public class TrophyCreateMenu extends GUI {

    private ClansPlugin plugin;
    private TropyController tropyController;
    private PlayerController playerController;
    private ClansController clansController;
    private final SimpleDateFormat sdf;

    private String title = "", description = "";
    private long startDate = System.currentTimeMillis(), endDate = System.currentTimeMillis();


    public TrophyCreateMenu(Player player, ClansPlugin plugin, TropyController tropyController, PlayerController playerController, ClansController clansController) {
        super(player, 36, LanguageController.getLocalized("trophy_create_menu.title"));

        this.plugin = plugin;
        this.tropyController = tropyController;
        this.playerController = playerController;
        this.clansController = clansController;

        Locale loc = new Locale(plugin.getConfig().getString("language.language"), plugin.getConfig().getString("language.country"));
        sdf = new SimpleDateFormat(LanguageController.getLocalized("time_format"), loc);

        init();
        fancyBackground();
        open();
    }

    private void init(){


        addIcon(11, titleIcon());
        addIcon(13, descriptionIcon());
        addIcon(15, timeIcon());
        addIcon(23, confirmIcon());

    }


    private ItemStack createTitleItem(){
        var item = new ItemStack(Material.BOOK);
        var meta = item.getItemMeta();

        meta.displayName(ClansPlugin.MM.deserialize(
                        LanguageController.getLocalized("trophy_create_menu.title_item.title")
                ).decoration(TextDecoration.ITALIC, false)
        );
        meta.lore(LanguageController.getLocalizedList("trophy_create_menu.title_item.lore").stream().map(it -> {
            return ClansPlugin.MM.deserialize(it
                    .replace("{title}", title)
            );
        }).toList());


        item.setItemMeta(meta);
        return item;
    }

    private Icon titleIcon(){
        Icon icon = new Icon(createTitleItem(), (self, target) -> {
            self.itemStack = createTitleItem();
        });
        icon.addClickAction((target) -> {
            target.sendMessage(ClansPlugin.MM.deserialize(LanguageController.getLocalized("trophy_create_menu.title_item.message")));
            target.playSound(sound(key("ui.button.click"), Sound.Source.MASTER, 1f, 1.19f));
            setForceClose(true);
            new AbstractChatUtil(target, (event) -> {
                this.title = event.message();
            }, plugin).setOnClose(() -> {
                open();
                refresh();
                setForceClose(false);
            });

        });
        return icon;
    }


    private ItemStack createDescriptionItem(){
        var item = new ItemStack(Material.BOOK);
        var meta = item.getItemMeta();

        meta.displayName(ClansPlugin.MM.deserialize(
                        LanguageController.getLocalized("trophy_create_menu.description_item.title")
                ).decoration(TextDecoration.ITALIC, false)
        );
        meta.lore(LanguageController.getLocalizedList("trophy_create_menu.description_item.lore").stream().map(it -> {
            return ClansPlugin.MM.deserialize(it
                    .replace("{description}", description)
            );
        }).toList());


        item.setItemMeta(meta);
        return item;
    }

    private Icon descriptionIcon(){
        Icon icon = new Icon(createDescriptionItem(), (self, target) -> {
            self.itemStack = createDescriptionItem();
        });
        icon.addClickAction((target) -> {
            target.sendMessage(ClansPlugin.MM.deserialize(LanguageController.getLocalized("trophy_create_menu.description_item.message")));
            target.playSound(sound(key("ui.button.click"), Sound.Source.MASTER, 1f, 1.19f));
            setForceClose(true);
            new AbstractChatUtil(target, (event) -> {
                this.description = event.message();
            }, plugin).setOnClose(() -> {
                open();
                refresh();
                setForceClose(false);
            });

        });
        return icon;
    }


    private ItemStack createTimeItem(){
        var item = new ItemStack(Material.BOOK);
        var meta = item.getItemMeta();

        meta.displayName(ClansPlugin.MM.deserialize(
                        LanguageController.getLocalized("trophy_create_menu.time_item.title")
                ).decoration(TextDecoration.ITALIC, false)
        );

        var start = sdf.format(startDate);
        var end = sdf.format(endDate);
        var duration = DurationFormatUtils.formatDurationWords(endDate-startDate, true,true);


        meta.lore(LanguageController.getLocalizedList("trophy_create_menu.time_item.lore").stream().map(it -> {
            return ClansPlugin.MM.deserialize(it
                    .replace("{start}", start)
                    .replace("{end}", end)
                    .replace("{duration}", duration)
            );
        }).toList());


        item.setItemMeta(meta);
        return item;
    }

    private Icon timeIcon(){
        Icon icon = new Icon(createTimeItem(), (self, target) -> {
            self.itemStack = createTimeItem();
        });

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        icon.addLeftClickAction((target) -> {
            target.sendMessage(ClansPlugin.MM.deserialize(LanguageController.getLocalized("trophy_create_menu.time_item.message_start")));
            target.playSound(sound(key("ui.button.click"), Sound.Source.MASTER, 1f, 1.19f));
            setForceClose(true);
            new AbstractChatUtil(target, (event) -> {
                try{
                    var date = LocalDateTime.parse(event.message(), formatter);
                    startDate = date.toEpochSecond(ZoneOffset.MIN) * 1000;
                }catch (Exception e){ }
            }, plugin).setOnClose(() -> {
                open();
                refresh();
                setForceClose(false);
            });

        });

        icon.addRightClickAction((target) -> {
            target.sendMessage(ClansPlugin.MM.deserialize(LanguageController.getLocalized("trophy_create_menu.time_item.message_end")));
            target.playSound(sound(key("ui.button.click"), Sound.Source.MASTER, 1f, 1.19f));
            setForceClose(true);
            new AbstractChatUtil(target, (event) -> {
                try{
                    var date = LocalDateTime.parse(event.message(), formatter);
                    endDate = date.toEpochSecond(ZoneOffset.MIN) * 1000;
                }catch (Exception e){ }
            }, plugin).setOnClose(() -> {
                open();
                refresh();
                setForceClose(false);
            });

        });
        return icon;
    }


    private ItemStack createConfirmItem(){
        var item = new ItemStack(Material.ANVIL);
        var meta = item.getItemMeta();
        meta.displayName(ClansPlugin.MM.deserialize(
                        LanguageController.getLocalized("trophy_create_menu.confirm_item.title")
                ).decoration(TextDecoration.ITALIC, false)
        );

        var start = sdf.format(startDate);
        var end = sdf.format(endDate);
        var duration = DurationFormatUtils.formatDurationWords(endDate-startDate, true,true);


        meta.lore(LanguageController.getLocalizedList("trophy_create_menu.confirm_item.lore").stream().map(it -> {
            return ClansPlugin.MM.deserialize(it
                    .replace("{start}", start)
                    .replace("{end}", end)
                    .replace("{duration}", duration)
            );
        }).toList());


        item.setItemMeta(meta);
        return item;
    }

    private Icon confirmIcon(){
        Icon icon = new Icon(createConfirmItem(), (self, target) -> {
            self.itemStack = createConfirmItem();
        });

        icon.addClickAction((target) -> {
            target.playSound(sound(key("ui.button.click"), Sound.Source.MASTER, 1f, 1.19f));
            var trophy = tropyController.createTrophy(title, description, startDate, endDate);
            if(trophy != null){
                new TrophyDetailsMenu(target, trophy, plugin, tropyController, clansController, playerController);
            }
        });

        return icon;
    }


}
