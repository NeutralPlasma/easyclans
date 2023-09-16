package net.astrona.easyclans.gui.ui;

import net.astrona.easyclans.ClansPlugin;
import net.astrona.easyclans.controller.ClansController;
import net.astrona.easyclans.controller.LanguageController;
import net.astrona.easyclans.controller.PlayerController;
import net.astrona.easyclans.controller.RequestsController;
import net.astrona.easyclans.gui.GUI;
import net.astrona.easyclans.gui.Icon;
import net.astrona.easyclans.models.Clan;
import net.astrona.easyclans.models.components.chat.ChangeClanDisplayNamePrompt;
import net.astrona.easyclans.models.components.chat.ChangeClanNamePrompt;
import net.astrona.easyclans.models.components.chat.impl.PlayerChatComponent;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

import static net.astrona.easyclans.controller.LanguageController.getLocalizedDesiralizedList;
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
    private final PlayerChatComponent playerChatComponent;


    public ClanCreateGUI(String name, String displayName, ItemStack banner, Player player, ClansPlugin plugin,
                         PlayerController playerController, ClansController clansController, RequestsController requestsController,
                         PlayerChatComponent playerChatComponent) {
        super(54, LanguageController.getLocalized("create.menu.title"));
        this.name = name;
        this.displayName = displayName;
        this.banner = banner;
        this.plugin = plugin;
        this.playerController = playerController;
        this.clansController = clansController;
        this.requestsController = requestsController;
        this.playerChatComponent = playerChatComponent;
        init();
        fancyBackground();
        open(player);
    }


    private void legalizeBanner(){
        var meta = banner.getItemMeta();
        meta.displayName(ClansPlugin.MM.deserialize(LanguageController.getLocalized("create.menu.banner.name")
                .replace("{name}", name))
                .decoration(TextDecoration.ITALIC, false)
        );
        for(var enchant : meta.getEnchants().keySet()){
            meta.removeEnchant(enchant);
        }
        var loreText = LanguageController.getLocalizedList("create.menu.banner.lore");
        meta.lore(loreText.stream().map(it ->
                ClansPlugin.MM.deserialize(it
                        .replace("{name}", name)
                        .replace("{display_name}", displayName))
                        .decoration(TextDecoration.ITALIC, false)
        ).toList());
        banner.setItemMeta(meta);
    }


    private Icon clanBanner(){
        legalizeBanner();

        Icon icon = new Icon(banner, (self, player) -> {
            self.itemStack = banner;
        });

        icon.addDragItemAction(((player, itemStack) -> {
            player.sendMessage(itemStack.getType().toString());
            if(itemStack.getType().toString().endsWith("BANNER")) {
                banner = itemStack.clone();
                legalizeBanner();
                this.update(player, 13);
                player.playSound(sound(key("block.note_block.cow_bell"), Sound.Source.MASTER, 1f, 1.19f));
            }else{
                player.playSound(sound(key("block.note_block.didgeridoo"), Sound.Source.MASTER, 1f, 1.19f));
            }

        }));

        icon.addLeftClickAction((player) -> {
            playerChatComponent.startChatPrompt(player, new ChangeClanNamePrompt(
                    plugin,
                    displayName,
                    banner,
                    playerController,
                    clansController,
                    requestsController,
                    playerChatComponent
                    ));
            player.closeInventory();
        });

        icon.addRightClickAction((player) -> {
            playerChatComponent.startChatPrompt(player, new ChangeClanDisplayNamePrompt(
                    plugin,
                    name,
                    banner,
                    playerController,
                    clansController,
                    requestsController,
                    playerChatComponent
                    ));
            player.closeInventory();
        });


        return icon;
    }

    private Icon confirmButton() {
        ItemStack itemStack = new ItemStack(Material.LIME_CONCRETE);
        var meta = itemStack.getItemMeta();
        meta.displayName(ClansPlugin.MM.deserialize(LanguageController.getLocalized("create.menu.create.name")));
        meta.lore(getLocalizedDesiralizedList("create.menu.create.lore"));
        itemStack.setItemMeta(meta);

        Icon icon = new Icon(itemStack);
        icon.addClickAction(player -> {
            new ConfirmGUI(player, (confirmPlayer) -> {
                Clan clan = clansController.createClan(
                        confirmPlayer.getUniqueId(),
                        name,
                        displayName,
                        0,
                        0,
                        0,
                        0,
                        0,
                        banner,
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

    /*private Icon priceSettings(){

    }


    private Icon kickSettings(){

    }

    private Icon payoutSettings(){

    }*/



    private void init(){
        if (banner == null) {
            banner = new ItemStack(Material.ORANGE_BANNER); // random generator maybe?
        }

        addIcon(13, clanBanner());
        addIcon(40, confirmButton());
        /*addIcon(20, priceSettings());
        addIcon(31, kickSettings());
        addIcon(24, payoutSettings());*/
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
