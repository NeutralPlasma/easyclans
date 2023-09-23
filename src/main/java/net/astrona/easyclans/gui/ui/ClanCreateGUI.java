package net.astrona.easyclans.gui.ui;

import net.astrona.easyclans.ClansPlugin;
import net.astrona.easyclans.controller.ClansController;
import net.astrona.easyclans.controller.LanguageController;
import net.astrona.easyclans.controller.PlayerController;
import net.astrona.easyclans.controller.RequestsController;
import net.astrona.easyclans.gui.GUI;
import net.astrona.easyclans.gui.Icon;
import net.astrona.easyclans.models.Clan;
import net.astrona.easyclans.utils.AbstractChatUtil;
import net.astrona.easyclans.utils.Formatter;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

import static net.astrona.easyclans.controller.LanguageController.getLocalizedList;
import static net.kyori.adventure.key.Key.key;
import static net.kyori.adventure.sound.Sound.sound;

public class ClanCreateGUI extends GUI {
    private ItemStack banner;
    private int kickTime;
    private double moneyPrice, payoutPercentage;
    private String name, displayName, tag = "DEFAULT";
    private final ClansPlugin plugin;
    private final PlayerController playerController;
    private final ClansController clansController;
    private final RequestsController requestsController;


    public ClanCreateGUI(String name, String displayName, ItemStack banner, Player player, ClansPlugin plugin,
                         PlayerController playerController, ClansController clansController, RequestsController requestsController) {
        super(54, LanguageController.getLocalized("create.menu.title"));
        this.name = name;
        this.displayName = displayName;
        this.banner = banner;
        this.plugin = plugin;
        this.playerController = playerController;
        this.clansController = clansController;
        this.requestsController = requestsController;
        init();
        fancyBackground();
        open(player);
    }

    private void legalizeBanner() {
        var meta = banner.getItemMeta();
        meta.displayName(ClansPlugin.MM.deserialize(LanguageController.getLocalized("create.menu.banner.name")
                        .replace("{name}", name)
                    ).decoration(TextDecoration.ITALIC, false)
        );
        for (var enchant : meta.getEnchants().keySet()) {
            meta.removeEnchant(enchant);
        }
        var loreText = LanguageController.getLocalizedList("create.menu.banner.lore");
        meta.lore(loreText.stream().map(it ->
                ClansPlugin.MM.deserialize(it
                                .replace("{name}", name)
                                .replace("{display_name}", displayName)
                        ).decoration(TextDecoration.ITALIC, false)
        ).toList());
        banner.setItemMeta(meta);
    }

    private Icon clanBanner() {
        legalizeBanner();
        Icon icon = new Icon(banner, (self, player) -> {
            legalizeBanner();
            self.itemStack = banner;
        });

        icon.addDragItemAction(((player, itemStack) -> {
            player.sendMessage(itemStack.getType().toString());
            if (itemStack.getType().toString().endsWith("BANNER")) {
                banner = itemStack.clone();
                legalizeBanner();
                this.update(player, 13);
                player.playSound(sound(key("block.note_block.cow_bell"), Sound.Source.MASTER, 1f, 1.19f));
            } else {
                player.playSound(sound(key("block.note_block.didgeridoo"), Sound.Source.MASTER, 1f, 1.19f));
            }

        }));

        icon.addLeftClickAction((player) -> {
            new AbstractChatUtil(player, (meow) -> {
                name = meow.message();
            }, plugin).setOnClose(() -> {
                legalizeBanner();
                icon.itemStack = banner;
                open(player);
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    refresh(player);
                }, 5L);
            });
            player.closeInventory();
        });

        icon.addRightClickAction((player) -> {
            new AbstractChatUtil(player, (meow) -> {
                displayName = meow.message();
            }, plugin).setOnClose(() -> {
                legalizeBanner();
                icon.itemStack = banner;
                open(player);
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    refresh(player);
                }, 5L);

            });
            player.closeInventory();
        });

        return icon;
    }

    private ItemStack confirmButtonItem(){
        ItemStack itemStack = new ItemStack(Material.LIME_CONCRETE);
        var meta = itemStack.getItemMeta();
        meta.displayName(ClansPlugin.MM.deserialize(LanguageController.getLocalized("create.menu.create.name")));
        var loreText = getLocalizedList("create.menu.create.lore");

        meta.lore(loreText.stream().map(it ->
                ClansPlugin.MM.deserialize(it
                        .replace("{name}", name)
                        .replace("{display_name}", displayName)
                ).decoration(TextDecoration.ITALIC, false)
        ).toList());

        itemStack.setItemMeta(meta);

        return itemStack;
    }

    private Icon confirmButton() {
        Icon icon = new Icon(confirmButtonItem(), (self, player) -> {
            self.itemStack = confirmButtonItem();
        });
        icon.addClickAction(player -> {
            new ConfirmGUI(player, (confirmPlayer) -> {
                Clan clan = clansController.createClan(
                        confirmPlayer.getUniqueId(),
                        name,
                        displayName,
                        kickTime,
                        0,
                        moneyPrice,
                        banner,
                        0.0,
                        0.0,
                        "",
                        List.of(
                                confirmPlayer.getUniqueId()
                        )
                );

                new AdminClanGUI(player, clan, clansController, playerController, requestsController);
            }, (cancelPlayer) -> {
                cancelPlayer.openInventory(getInventory());
            }, LanguageController.getLocalized("create.menu.create.confirm-title"));
        });

        return icon;
    }


    private ItemStack priceSettingsItem() {
        var item = new ItemStack(Material.SUNFLOWER);
        var meta = item.getItemMeta();
        meta.displayName(ClansPlugin.MM.deserialize(LanguageController.getLocalized("create.menu.money_price.name"))
                //.decoration(TextDecoration.ITALIC, false)
        );
        var loreStrings = LanguageController.getLocalizedList("create.menu.money_price.lore");
        var moneyString = Formatter.formatMoney(moneyPrice);
        meta.lore(loreStrings.stream().map(it ->
                ClansPlugin.MM.deserialize(it
                                .replace("{price}", moneyString))
                        //.decoration(TextDecoration.ITALIC, false)
        ).toList());
        item.setItemMeta(meta);
        return item;
    }
    private Icon priceSettings(){
        Icon icon = new Icon(priceSettingsItem(), (self, player) -> {
            self.itemStack = priceSettingsItem();
        });

        icon.addLeftClickAction((player) -> {
            new AbstractChatUtil(player, (meow) -> {
                try{
                    double price = Double.parseDouble(meow.message());
                    if(price < 0){
                        player.playSound(sound(key("block.note_block.didgeridoo"), Sound.Source.MASTER, 1f, 1.19f));
                    }else{
                        moneyPrice = price;
                        player.playSound(sound(key("block.note_block.cow_bell"), Sound.Source.MASTER, 1f, 1.19f));
                    }
                }catch (NumberFormatException e){
                    player.playSound(sound(key("block.note_block.didgeridoo"), Sound.Source.MASTER, 1f, 1.19f));
                }
            }, plugin).setOnClose(() -> {
                icon.itemStack = priceSettingsItem();
                open(player);
            });
        });
        icon.addRightClickAction((player) -> {
            moneyPrice = 1000;
            player.playSound(sound(key("block.note_block.cow_bell"), Sound.Source.MASTER, 1f, 1.19f));
        });


        return icon;
    }


    private ItemStack kickSettingsItem(){
        var item = new ItemStack(Material.ANVIL);
        var meta = item.getItemMeta();
        meta.displayName(ClansPlugin.MM.deserialize(LanguageController.getLocalized("create.menu.kick_time.name"))
                //.decoration(TextDecoration.ITALIC, false)
        );
        var loreStrings = LanguageController.getLocalizedList("create.menu.kick_time.lore");
        var kickString = String.format("%s", kickTime);
        meta.lore(loreStrings.stream().map(it ->
                ClansPlugin.MM.deserialize(it
                                .replace("{time}", kickString))
                        //.decoration(TextDecoration.ITALIC, false)
        ).toList());
        item.setItemMeta(meta);
        return item;
    }

    private Icon kickSettings(){
        Icon icon = new Icon(kickSettingsItem(), (self, player) -> self.itemStack = kickSettingsItem());

        icon.addRightClickAction((player) -> {
            kickTime = -1;
            player.playSound(sound(key("block.note_block.cow_bell"), Sound.Source.MASTER, 1f, 1.19f));
        });

        icon.addLeftClickAction((player) -> {
            new AbstractChatUtil(player, (meow) -> {
                try{
                    int lkickTime = Integer.parseInt(meow.message());
                    if(lkickTime < -1){
                        player.playSound(sound(key("block.note_block.didgeridoo"), Sound.Source.MASTER, 1f, 1.19f));
                    }else{
                        player.sendMessage(" " +lkickTime);
                        kickTime = lkickTime;
                        player.playSound(sound(key("block.note_block.cow_bell"), Sound.Source.MASTER, 1f, 1.19f));
                    }
                }catch (NumberFormatException e){
                    player.playSound(sound(key("block.note_block.didgeridoo"), Sound.Source.MASTER, 1f, 1.19f));
                }
            }, plugin).setOnClose(() -> {
                icon.itemStack = kickSettingsItem();
                open(player);
            });
        });


        return icon;
    }



    /*private Icon payoutSettings(){
        moneyPrice = 1000;
    }*/


    private void init() {
        if (banner == null) {
            banner = new ItemStack(Material.ORANGE_BANNER); // TODO: generate a random colored banner maybe
        }

        addIcon(13, clanBanner());
        addIcon(40, confirmButton());
        addIcon(20, priceSettings());
        addIcon(24, kickSettings());
        /*addIcon(31, payoutSettings());*/
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
