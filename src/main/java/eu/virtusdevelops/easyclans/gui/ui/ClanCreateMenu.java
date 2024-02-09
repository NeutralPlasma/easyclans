package eu.virtusdevelops.easyclans.gui.ui;

import eu.virtusdevelops.easyclans.ClansPlugin;
import eu.virtusdevelops.easyclans.controller.*;
import eu.virtusdevelops.easyclans.gui.GUI;
import eu.virtusdevelops.easyclans.gui.Icon;
import eu.virtusdevelops.easyclans.models.*;
import eu.virtusdevelops.easyclans.utils.AbstractChatUtil;
import eu.virtusdevelops.easyclans.utils.BannerUtils;
import eu.virtusdevelops.easyclans.utils.ItemUtils;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.format.TextDecoration;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.UUID;

import static net.kyori.adventure.key.Key.key;
import static net.kyori.adventure.sound.Sound.sound;

public class ClanCreateMenu extends GUI {
    private final CPlayer cPlayer;
    private final ClansController clansController;
    private final PlayerController playerController;
    private final CurrenciesController currenciesController;
    private final RequestsController requestsController;
    private final InvitesController invitesController;
    private final LogController logController;
    private final RanksController ranksController;
    private final ClansPlugin plugin;

    // clan specific
    private ItemStack clanBanner = new ItemStack(Material.BLACK_BANNER);
    private String clanName;
    private String clanDisplayName;
    private String clanTag;
    double joinPrice;
    int kickTime;

    public ClanCreateMenu(Player player, ClansController clansController, PlayerController playerController, CurrenciesController currenciesController,
                    RequestsController requestsController, InvitesController invitesController, LogController logController, RanksController ranksController, ClansPlugin plugin){
        super(player, 54, LanguageController.getLocalized("clan_menu.title"));
        this.clansController = clansController;
        this.playerController = playerController;
        this.currenciesController = currenciesController;
        this.requestsController = requestsController;
        this.invitesController = invitesController;
        this.logController = logController;
        this.ranksController = ranksController;
        this.plugin = plugin;
        this.cPlayer = playerController.getPlayer(player.getUniqueId());

        this.clanName = player.getName();
        this.clanDisplayName = "DISPLAY_" + player.getName();
        clanTag = "" + clanName.charAt(0) + clanName.charAt(1);

        kickTime = plugin.getConfig().getInt("clan.default_kick_time") * 1000;
        joinPrice = plugin.getConfig().getDouble("clan.default_join_price");

        init();
        fancyBackground();
        open();
    }

    private void init(){
        randomizeBanner();

        addIcon(11, joinPriceIcon());
        addIcon(13, nameIcon());
        addIcon(15, bannerIcon());
        addIcon(29, kickTimeIcon());
        addIcon(33, createClanIcon());

        //addIcon(53, createConfirmIcon());
    }

    // <editor-fold desc="Banner icon">
    private void randomizeBanner(){
        clanBanner = BannerUtils.generateRandomBanner();
    }
    private ItemStack bannerItem(){
        var item = clanBanner.clone();
        var meta = item.getItemMeta();
        meta.displayName(ClansPlugin.MM.deserialize(
                        LanguageController.getLocalized("clan_create_menu.banner_item.title")
                ).decoration(TextDecoration.ITALIC, false)
        );
        meta.lore(LanguageController.getLocalizedList("clan_create_menu.banner_item.lore").stream()
                .map(it -> ClansPlugin.MM.deserialize(it).decoration(TextDecoration.ITALIC, false)).toList());
        item.setItemMeta(meta);


        return item;
    }
    private Icon bannerIcon(){
        var icon = new Icon(bannerItem(), (self, target) -> {
            self.itemStack = bannerItem();
        });
        icon.addShiftLeftClickAction((target) -> {
            // open settings menu
            target.playSound(sound(key("ui.button.click"), Sound.Source.MASTER, 1f, 1.19f));
            randomizeBanner();
            refresh();
        });
        icon.addShiftRightClickAction((target) -> {
            // open settings menu
            target.playSound(sound(key("ui.button.click"), Sound.Source.MASTER, 1f, 1.19f));
            randomizeBanner();
            refresh();
        });
        icon.addDragItemAction((target, item) -> {
            if (!item.getType().toString().endsWith("BANNER")) {
                target.playSound(sound(key("block.note_block.didgeridoo"), Sound.Source.MASTER, 1f, 1.19f));
                return;
            }
            var newBanner = item.clone();
            newBanner = ItemUtils.removeEnchants(newBanner);
            newBanner = ItemUtils.strip(newBanner);
            clanBanner = newBanner;
            target.playSound(sound(key("ui.button.click"), Sound.Source.MASTER, 1f, 1.19f));
            refresh();
        });

        return icon;
    }
    // </editor-fold>

    // <editor-fold desc="Join price icon">
    private ItemStack joinPriceItem(){
        var item = new ItemStack(Material.BOOK);
        var meta = item.getItemMeta();
        meta.displayName(ClansPlugin.MM.deserialize(
                        LanguageController.getLocalized("clan_create_menu.join_price_item.title")
                ).decoration(TextDecoration.ITALIC, false)
        );
        meta.lore(LanguageController.getLocalizedList("clan_create_menu.join_price_item.lore").stream().map(it -> ClansPlugin.MM.deserialize(it
                .replace("{price}", String.valueOf(joinPrice))
        )).toList());
        item.setItemMeta(meta);


        return item;
    }
    private Icon joinPriceIcon(){
        var icon = new Icon(joinPriceItem(), (self, target) -> {
            self.itemStack = joinPriceItem();
        });

        icon.setVisibilityCondition((target, self) ->
            target.hasPermission("easyclans.edit.join_price")
        );

        icon.addClickAction((target) -> {
            target.playSound(sound(key("ui.button.click"), Sound.Source.MASTER, 1f, 1.19f));
            target.sendMessage(ClansPlugin.MM.deserialize(LanguageController.getLocalized("clan_create_menu.join_price_item.message")));
            new AbstractChatUtil(target, (event) -> {
                try{
                    double price = Double.parseDouble(event.message());
                    if(price < 0) {
                        target.sendMessage(ClansPlugin.MM.deserialize(LanguageController.getLocalized("clan_create_menu.join_price_item.invalid_message")));
                        player.playSound(sound(key("block.note_block.didgeridoo"), Sound.Source.MASTER, 1f, 1.19f));
                        return;
                    }
                    joinPrice = price;
                    target.playSound(sound(key("ui.button.click"), Sound.Source.MASTER, 1f, 1.19f));
                }catch (NumberFormatException e){
                    target.sendMessage(ClansPlugin.MM.deserialize(LanguageController.getLocalized("clan_create_menu.join_price_item.invalid_message")));
                    player.playSound(sound(key("block.note_block.didgeridoo"), Sound.Source.MASTER, 1f, 1.19f));
                }
            }, plugin).setOnClose(() -> {
                open();
                refresh();
            });

        });

        return icon;
    }
    // </editor-fold>

    // <editor-fold desc="Kick icon">
    private ItemStack kickTimeItem(){
        var item = new ItemStack(Material.ANVIL);
        var meta = item.getItemMeta();
        meta.displayName(ClansPlugin.MM.deserialize(
                        LanguageController.getLocalized("clan_create_menu.kicktime_item.title")
                ).decoration(TextDecoration.ITALIC, false)
        );
        var kickText = kickTime == -1 ? LanguageController.getLocalized("disabled") : DurationFormatUtils.formatDurationWords(kickTime, true,true);

        meta.lore(LanguageController.getLocalizedList("clan_create_menu.kicktime_item.lore").stream().map(it -> ClansPlugin.MM.deserialize(it
                .replace("{time}", kickText)
        )).toList());
        item.setItemMeta(meta);


        return item;
    }
    private Icon kickTimeIcon(){
        var icon = new Icon(kickTimeItem(), (self, target) -> {
            self.itemStack = kickTimeItem();
        });

        icon.setVisibilityCondition((target, self) ->
                target.hasPermission("easyclans.edit.inactive_kick")
        );

        icon.addClickAction((target) -> {
            target.playSound(sound(key("ui.button.click"), Sound.Source.MASTER, 1f, 1.19f));
            target.sendMessage(ClansPlugin.MM.deserialize(LanguageController.getLocalized("clan_create_menu.kicktime_item.message")));
            new AbstractChatUtil(target, (event) -> {
                try{
                    int time = Integer.parseInt(event.message());
                    if(time < -1) {
                        target.sendMessage(ClansPlugin.MM.deserialize(LanguageController.getLocalized("clan_create_menu.kicktime_item.invalid_message")));
                        player.playSound(sound(key("block.note_block.didgeridoo"), Sound.Source.MASTER, 1f, 1.19f));
                        return;
                    }
                    kickTime = time == 0 ? -1 : time * 1000;
                    target.playSound(sound(key("ui.button.click"), Sound.Source.MASTER, 1f, 1.19f));
                }catch (NumberFormatException e){
                    target.sendMessage(ClansPlugin.MM.deserialize(LanguageController.getLocalized("clan_create_menu.kicktime_item.invalid_message")));
                    player.playSound(sound(key("block.note_block.didgeridoo"), Sound.Source.MASTER, 1f, 1.19f));
                }
            }, plugin).setOnClose(() -> {
                open();
                refresh();
            });

        });

        return icon;
    }
    // </editor-fold>

    // <editor-fold desc="Name icon">
    private ItemStack nameItem(){
        var item = new ItemStack(Material.WRITABLE_BOOK);
        var meta = item.getItemMeta();
        meta.displayName(ClansPlugin.MM.deserialize(
                        LanguageController.getLocalized("clan_create_menu.name_item.title")
                ).decoration(TextDecoration.ITALIC, false)
        );

        meta.lore(LanguageController.getLocalizedList("clan_create_menu.name_item.lore").stream().map(it -> ClansPlugin.MM.deserialize(it
                .replace("{name}", clanName)
                .replace("{display_name}", clanDisplayName)
                .replace("{tag}", clanTag)
        )).toList());
        item.setItemMeta(meta);


        return item;
    }
    private Icon nameIcon(){
        var icon = new Icon(nameItem(), (self, target) -> {
            self.itemStack = nameItem();
        });

        icon.setVisibilityCondition((target, self) ->
                target.hasPermission("easyclans.edit.clan_name")
        );

        icon.addLeftClickAction((target) -> {
            target.playSound(sound(key("ui.button.click"), Sound.Source.MASTER, 1f, 1.19f));
            target.sendMessage(ClansPlugin.MM.deserialize(LanguageController.getLocalized("clan_create_menu.name_item.message")));
            new AbstractChatUtil(target, (event) -> {
                var name = event.message();
                var stripped = name.replace(" ", "_").strip().trim();
                if(stripped.length() > plugin.getConfig().getInt("clan.max_name_length")
                        || stripped.length() < plugin.getConfig().getInt("clan.min_name_length")){
                    // not good
                    target.sendMessage(ClansPlugin.MM.deserialize(LanguageController.getLocalized("clan_create_menu.name_item.invalid_name")));
                    player.playSound(sound(key("block.note_block.didgeridoo"), Sound.Source.MASTER, 1f, 1.19f));
                    return;
                }
                if(clansController.getClan(stripped) != null){
                    target.sendMessage(ClansPlugin.MM.deserialize(LanguageController.getLocalized("clan_create_menu.name_item.invalid_name")));
                    player.playSound(sound(key("block.note_block.didgeridoo"), Sound.Source.MASTER, 1f, 1.19f));
                    return;
                }
                clanName = stripped;
                clanTag = "";
                for(int i = 0; i < plugin.getConfig().getInt("clan.tag_max_length"); i++){
                    if(clanName.length() > i)
                        clanTag += String.valueOf(clanName.charAt(i));
                    else
                        clanTag += String.valueOf(clanName.charAt(0));
                }
                target.playSound(sound(key("ui.button.click"), Sound.Source.MASTER, 1f, 1.19f));

            }, plugin).setOnClose(() -> {
                open();
                refresh();
            });
        });

        icon.addRightClickAction((target) -> {
            target.playSound(sound(key("ui.button.click"), Sound.Source.MASTER, 1f, 1.19f));
            target.sendMessage(ClansPlugin.MM.deserialize(LanguageController.getLocalized("clan_create_menu.name_item.display_name_message")));
            new AbstractChatUtil(target, (event) -> {
                var name = event.message();
                var stripped = name.replace(" ", "_").strip().trim();
                if(stripped.length() > plugin.getConfig().getInt("clan.display_name_max_length")
                        || stripped.length() < plugin.getConfig().getInt("clan.display_name_min_length")){
                    // not good
                    target.sendMessage(ClansPlugin.MM.deserialize(LanguageController.getLocalized("clan_create_menu.name_item.invalid_display_name")));
                    player.playSound(sound(key("block.note_block.didgeridoo"), Sound.Source.MASTER, 1f, 1.19f));
                    return;
                }

                clanDisplayName = stripped;
                target.playSound(sound(key("ui.button.click"), Sound.Source.MASTER, 1f, 1.19f));

            }, plugin).setOnClose(() -> {
                open();
                refresh();
            });
        });

        icon.addShiftLeftClickAction((target) -> {
            target.playSound(sound(key("ui.button.click"), Sound.Source.MASTER, 1f, 1.19f));
            target.sendMessage(ClansPlugin.MM.deserialize(LanguageController.getLocalized("clan_create_menu.name_item.tag_message")));
            new AbstractChatUtil(target, (event) -> {
                var name = event.message();
                var stripped = name.replace(" ", "_").strip().trim();
                if(stripped.length() > plugin.getConfig().getInt("clan.tag_max_length")
                        || stripped.length() < plugin.getConfig().getInt("clan.tag_min_length")){
                    // not good
                    target.sendMessage(ClansPlugin.MM.deserialize(LanguageController.getLocalized("clan_create_menu.name_item.invalid_tag")));
                    player.playSound(sound(key("block.note_block.didgeridoo"), Sound.Source.MASTER, 1f, 1.19f));
                    return;
                }

                clanTag = stripped;
                target.playSound(sound(key("ui.button.click"), Sound.Source.MASTER, 1f, 1.19f));

            }, plugin).setOnClose(() -> {
                open();
                refresh();
            });
        });

        return icon;
    }
    // </editor-fold>

    // <editor-fold desc="Clan Item">
    private ItemStack createClanItem(){
        var item = new ItemStack(Material.DARK_OAK_DOOR);
        var meta = item.getItemMeta();
        meta.displayName(ClansPlugin.MM.deserialize(
                        LanguageController.getLocalized("clan_create_menu.confirm_item.title")
                ).decoration(TextDecoration.ITALIC, false)
        );
        var kickText = kickTime == -1 ? LanguageController.getLocalized("disabled") : DurationFormatUtils.formatDurationWords(kickTime, true,true);
        meta.lore(LanguageController.getLocalizedList("clan_create_menu.confirm_item.lore").stream().map(it -> ClansPlugin.MM.deserialize(it
                .replace("{name}", clanName)
                .replace("{display_name}", clanDisplayName)
                .replace("{tag}", clanTag)
                .replace("{time}", kickText)
        )).toList());
        item.setItemMeta(meta);
        return item;
    }
    private Icon createClanIcon(){
        var icon = new Icon(createClanItem(), (self, target) -> {
            self.itemStack = createClanItem();
        });
        icon.setVisibilityCondition((target, self) ->
                target.hasPermission("easyclans.create")
        );
        icon.addClickAction((target) -> {

            new ConfirmGUI(target, (target2) -> {
                var provider = currenciesController.getProvider("Vault");
                if(provider.getValue(player) < plugin.getConfig().getDouble("clan.create.price.money") ){
                    target2.playSound(sound(key("block.note_block.didgeridoo"), Sound.Source.MASTER, 1f, 1.19f));
                    target2.sendMessage(ClansPlugin.MM.deserialize(
                            LanguageController.getLocalized("clan_create_menu.confirm.not_enough_money")
                                    .replace("{price}", eu.virtusdevelops.easyclans.utils.Formatter.formatMoney(plugin.getConfig().getDouble("clan.create.price.money")))
                    ));
                    open();
                    refresh();
                    return;
                }

                var members = new ArrayList<UUID>();
                members.add(player.getUniqueId());
                Clan clan = clansController.createClan(
                        target2.getUniqueId(),
                        clanName,
                        clanDisplayName,
                        kickTime,
                        0,
                        joinPrice,
                        BannerUtils.strip(clanBanner),
                        0.0,
                        clanTag,
                        members
                );
                logController.addLog(new Log(clanName, player.getUniqueId(), clan.getId(), LogType.CLAN_CREATE));
                new ClanMenu(target2, clan, clansController, playerController, currenciesController, requestsController, invitesController, logController, ranksController, plugin);

            }, (target2) -> {
                open();
                refresh();
            }, LanguageController.getLocalized("clan_create_menu.confirm.title"));
        });
        return icon;
    }
    // </editor-fold>


}
