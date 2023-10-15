package eu.virtusdevelops.easyclans.gui.ui;

import eu.virtusdevelops.easyclans.controller.*;
import eu.virtusdevelops.easyclans.gui.GUI;
import eu.virtusdevelops.easyclans.gui.Icon;
import eu.virtusdevelops.easyclans.models.Clan;
import eu.virtusdevelops.easyclans.utils.Formatter;
import eu.virtusdevelops.easyclans.utils.PlayerUtils;
import eu.virtusdevelops.easyclans.ClansPlugin;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import static eu.virtusdevelops.easyclans.controller.LanguageController.getLocalizedDesiralizedList;
import static eu.virtusdevelops.easyclans.controller.LanguageController.getLocalizedList;
import static net.kyori.adventure.key.Key.key;
import static net.kyori.adventure.sound.Sound.sound;

public class ClanGUI extends GUI {
    private ClansPlugin plugin;
    private Clan clan;
    private ClansController clansController;
    private PlayerController playerController;
    private RequestsController requestsController;
    private LogController logController;
    private CurrenciesController currenciesController;
    private Player player;


    public ClanGUI(Player player, Clan clan, ClansController clansController, PlayerController playerController,
                   RequestsController requestsController, LogController logController, ClansPlugin plugin,
                   CurrenciesController currenciesController) {
        super(54, clan.getDisplayName());

        this.plugin = plugin;
        this.clan = clan;
        this.clansController = clansController;
        this.playerController = playerController;
        this.requestsController = requestsController;
        this.logController = logController;
        this.currenciesController = currenciesController;
        this.player = player;

        construct();
        fancyBackground();
        open(player);
    }

    private void construct() {
        setIcon(11, membersIcon());
        setIcon(13, clanInfoIcon());
        setIcon(15, bannerIcon());
        setIcon(31, bankIcon());
        //  33, 31, 29

    }

    ItemStack clanInfoIconItem() {
        ItemStack itemStack = new ItemStack(Material.DIAMOND);
        var meta = itemStack.getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_ITEM_SPECIFICS);
        meta.displayName(ClansPlugin.MM.deserialize(LanguageController.getLocalized("clan.menu.clan.name").replace("{clan}", clan.getName())));

        var loreStrings = LanguageController.getLocalizedList("clan.menu.clan.lore");

        meta.lore(loreStrings.stream().map(it ->
                        ClansPlugin.MM.deserialize(it
                                .replace("{clan}", String.valueOf(clan.getName()))
                                .replace("{clan_name}", String.valueOf(clan.getDisplayName()))
                                //.replace("{bank}", Formatter.formatMoney(clan.getBank()))
                                .replace("{interest_rate_members}", String.format("%.5f", clan.getActualInterestRate()))
                                .replace("{interest_rate_time}", String.format("%.5f", clan.getInterestRate()))
                                .replace("{interest_rate_full}", String.format("%.5f", clan.getInterestRate() + clan.getActualInterestRate()))

        )).toList());


        itemStack.setItemMeta(meta);
        return itemStack;
    }

    Icon clanInfoIcon() {
        Icon icon = new Icon(clanInfoIconItem(), (it, player) -> {
            it.itemStack = clanInfoIconItem();
        });
        icon.addClickAction((this::refresh));
        return icon;
    }

    Icon membersIcon() {
        ItemStack itemStack = new ItemStack(Material.PLAYER_HEAD);
        var meta = itemStack.getItemMeta();
        meta.displayName(ClansPlugin.MM.deserialize(LanguageController.getLocalized("clan.menu.members.name")));
        meta.lore(getLocalizedDesiralizedList("clan.menu.members.lore"));
        itemStack.setItemMeta(meta);
        Icon icon = new Icon(itemStack);
        icon.addClickAction((player -> {
            player.closeInventory();

            new MembersGUI(player, clan, clansController, playerController, this, logController, plugin);
        }));
        return icon;
    }

    ItemStack bankIconItem() {
        ItemStack itemStack = new ItemStack(Material.SUNFLOWER);
        var meta = itemStack.getItemMeta();
        meta.displayName(ClansPlugin.MM.deserialize(LanguageController.getLocalized("clan.menu.bank.name")));
        meta.lore(getLocalizedDesiralizedList("clan.menu.bank.lore"));
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    Icon bankIcon() {
        Icon icon = new Icon(bankIconItem(), ((it, player) -> {
            it.itemStack = bankIconItem();
        }));

        icon.addClickAction(player -> {
            player.closeInventory();
            new CurrenciesGUI(player, clan, clansController, playerController, this, logController, plugin, currenciesController);
            //new BankGUI(player, clan, this, plugin, clansController, logController, currenciesController);
        });

        return icon;
    }


    // banner
    private ItemStack bannerItem(){
        var item = clan.getBanner().clone();
        var meta = item.getItemMeta();

        meta.displayName(ClansPlugin.MM.deserialize(LanguageController.getLocalized("settings.menu.bannerIcon.title")));

        var loreText = getLocalizedList("clan.menu.bannerIcon.lore");
        meta.lore(loreText.stream().map(it ->
                ClansPlugin.MM.deserialize(it
                        .replace("{buy_price}", Formatter.formatMoney(plugin.getConfig().getDouble("clan.banner.buy_price.money")))
                )
        ).toList());
        item.setItemMeta(meta);

        return item;
    }



    private Icon bannerIcon(){
        var icon = new Icon(bannerItem(), (self, player) -> {
            self.itemStack = bannerItem();
        });

        icon.addClickAction((player1 -> {

            if(currenciesController.getProvider("Vault").getValue(player) >= plugin.getConfig().getDouble("clan.banner.buy_price.money")){
                currenciesController.getProvider("Vault").removeValue(player, plugin.getConfig().getDouble("clan.banner.buy_price.money"));
                //ClansPlugin.Economy.withdrawPlayer(player, plugin.getConfig().getDouble("clan.banner.buy_price.money"));
                PlayerUtils.giveItem(player, clan.getBanner().clone(), true);
            }else{
                player.sendMessage(ClansPlugin.MM.deserialize(
                        LanguageController.getLocalized("not_enough_money")
                                .replace("{price}", Formatter.formatMoney(plugin.getConfig().getDouble("clan.banner.buy_price.money")))
                ));
                player.playSound(sound(key("block.note_block.didgeridoo"), Sound.Source.MASTER, 1f, 1.19f));
            }

        }));





        return icon;
    }
}
