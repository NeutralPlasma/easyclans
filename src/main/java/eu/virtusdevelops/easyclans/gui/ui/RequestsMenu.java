package eu.virtusdevelops.easyclans.gui.ui;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import eu.virtusdevelops.easyclans.ClansPlugin;
import eu.virtusdevelops.easyclans.controller.*;
import eu.virtusdevelops.easyclans.gui.AsyncPaginator;
import eu.virtusdevelops.easyclans.gui.GUI;
import eu.virtusdevelops.easyclans.gui.Icon;
import eu.virtusdevelops.easyclans.gui.actions.AsyncReturnTask;
import eu.virtusdevelops.easyclans.models.*;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.format.TextDecoration;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import static net.kyori.adventure.key.Key.key;
import static net.kyori.adventure.sound.Sound.sound;

public class RequestsMenu extends AsyncPaginator {

    private final CPlayer cPlayer;
    private final Clan clan;
    private final ClansController clansController;
    private final PlayerController playerController;
    private final CurrenciesController currenciesController;
    private final RequestsController requestsController;
    private final InvitesController invitesController;
    private final LogController logController;
    private final ClansPlugin plugin;
    private final GUI previousUI;
    private final SimpleDateFormat sdf;

    public RequestsMenu(Player player, Clan clan, ClansController clansController, PlayerController playerController, CurrenciesController currenciesController,
                        RequestsController requestsController, InvitesController invitesController, LogController logController, ClansPlugin plugin, GUI previousUI){
        super(player, plugin,54, LanguageController.getLocalized("requests_menu.title"), List.of(
                10, 11, 12, 13, 14, 15, 16,
                19, 20, 21, 22, 23, 24, 25,
                28, 29, 30, 31, 32, 33, 34,
                37, 38, 39, 40, 41, 42, 43
        ));

        this.clan = clan;
        this.clansController = clansController;
        this.playerController = playerController;
        this.currenciesController = currenciesController;
        this.requestsController = requestsController;
        this.invitesController = invitesController;
        this.logController = logController;
        this.plugin = plugin;
        this.cPlayer = playerController.getPlayer(player.getUniqueId());
        this.previousUI = previousUI;
        Locale loc = new Locale(plugin.getConfig().getString("language.language"), plugin.getConfig().getString("language.country"));
        sdf = new SimpleDateFormat(LanguageController.getLocalized("time_format"), loc);

        setup();
        init();
    }


    private void setup(){
        setFetchPageTask(new AsyncReturnTask<>() {
            @Override
            public List<Icon> fetchPageData(int page, int perPage) {
                var requests = requestsController.getClanRequests(clan.getId());
                List<Icon> icons = new ArrayList<>();

                for(int i = 0; i < perPage; i++){
                    Bukkit.getConsoleSender().sendMessage("Refreshing!");
                    var index = i + (page * perPage);
                    if(index >= requests.size()) break;
                    var request = requests.get(index);
                    var cMember = playerController.getPlayer(request.getPlayerUuid());
                    icons.add(createRequestIcon(request, cMember));
                }

                return icons;
            }

            @Override
            public List<Icon> fetchData() {
                return null;
            }
        });


        setGetItemsCountTask(new AsyncReturnTask<Integer>() {
            @Override
            public Integer fetchPageData(int page, int perPage) {
                return requestsController.getClanRequests(clan.getId()).size();
            }

            @Override
            public Integer fetchData() {
                return requestsController.getClanRequests(clan.getId()).size();
            }
        });
        if(previousUI != null)
            addCloseAction((target) -> {
                previousUI.open();
            });
    }


    private ItemStack createRequestItem(CRequest request, CPlayer cTarget){
        var item = new ItemStack(Material.PLAYER_HEAD);
        var meta = (SkullMeta) item.getItemMeta();

        var offPlayer = Bukkit.getOfflinePlayerIfCached(cTarget.getName());
        if(offPlayer != null){
            meta.setOwningPlayer(offPlayer);
        }else{
            GameProfile profile = new GameProfile(UUID.fromString("b475559e-5c77-4954-8fa4-7d50f54aaab3"),
                    "Unknown");
            profile.getProperties().put("textures", new Property("textures", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGZkNWJkZTk5NGUwYTY0N2FmMTgyMzY4MWE2MTNjMmJmYzNkOTczNmY4ODlkYmY4YzNiYmJhNWExM2Y4ZWQifX19"));
            Field profileField;
            try {
                profileField = meta.getClass().getDeclaredField("profile");
                profileField.setAccessible(true);
                profileField.set(meta, profile);
            } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException ex) {
                ex.printStackTrace();
            }
        }

        // name
        meta.displayName(ClansPlugin.MM.deserialize(
                        LanguageController.getLocalized("requests_menu.request_item.title")
                                .replace("{player}", cTarget.getName())
                ).decoration(TextDecoration.ITALIC, false)
        );

        var expireDate = Math.abs((System.currentTimeMillis() - request.getExpireTime()));
        meta.lore(LanguageController.getLocalizedList("requests_menu.request_item.lore").stream().map(it ->
                ClansPlugin.MM.deserialize(it
                        .replace("{requested}", sdf.format(request.getCreatedTime()))
                        .replace("{expires}", DurationFormatUtils.formatDurationWords(expireDate, true,true))
                        .replace("{time}", DurationFormatUtils.formatDurationWords(expireDate, true,true))
                )
        ).toList());


        item.setItemMeta(meta);
        return item;
    }

    private Icon createRequestIcon(CRequest request, CPlayer cTarget){
        var icon = new Icon(createRequestItem(request, cTarget));

        icon.addLeftClickAction((target) -> {
            if(!target.getUniqueId().equals(clan.getOwner())
                    && !target.hasPermission("easyclans.admin.accept_request")
                    && !(cPlayer.hasPermission(UserPermissions.ACCEPT_REQUEST) && cPlayer.getClanID() == clan.getId())){

                target.playSound(sound(key("block.note_block.didgeridoo"), Sound.Source.MASTER, 1f, 1.19f));
                return;
            }
            var requester = Bukkit.getPlayer(cTarget.getUuid());
            new ConfirmGUI(player, (ignored) -> {
                var provider =  currenciesController.getProvider("Vault");
                // check if sender has money

                if(provider.getValue(Bukkit.getOfflinePlayer(cTarget.getUuid())) < clan.getJoinMoneyPrice()){
                    player.sendMessage(ClansPlugin.MM.deserialize(
                            LanguageController.getLocalized("requests.not_enough_money_accepter")
                                    .replace("{player}", cTarget.getName())));
                    player.playSound(sound(key("block.note_block.cow_bell"), Sound.Source.MASTER, 1f, 1.19f));

                    if(requester != null){
                        requester.playSound(sound(key("block.note_block.cow_bell"), Sound.Source.MASTER, 1f, 1.19f));
                        requester.sendMessage(ClansPlugin.MM.deserialize(LanguageController.getLocalized("requests.not_enough_money_sender")
                                .replace("{clan}", clan.getName())));
                    }
                    open();
                    return;
                }
                if(clan.getMembers().size() > plugin.getConfig().getInt("clan.max_members")){
                    player.sendMessage(ClansPlugin.MM.deserialize(
                            LanguageController.getLocalized("requests.too_many_members")
                                    .replace("{max}", String.valueOf(plugin.getConfig().getInt("clan.max_members")))
                                    .replace("{current}", String.valueOf(clan.getMembers().size()))
                    ));
                    player.playSound(sound(key("block.note_block.cow_bell"), Sound.Source.MASTER, 1f, 1.19f));
                    open();
                    return;
                }
                // remove money and delete request
                provider.removeValue(Bukkit.getOfflinePlayer(cTarget.getUuid()), clan.getJoinMoneyPrice());
                requestsController.deleteRequest(request);

                // update player
                cTarget.setClanID(clan.getId());
                cTarget.setJoinClanDate(System.currentTimeMillis());
                playerController.updatePlayer(cTarget);

                // edit clan add member blabla
                playerController.setDefaultPermissions(cTarget);
                clan.addMember(cTarget.getUuid());

                // send message
                player.sendMessage(ClansPlugin.MM.deserialize(LanguageController.getLocalized("requests.accepted")
                        .replace("{player}", cTarget.getName())));
                if(requester != null){
                    requester.playSound(sound(key("block.note_block.cow_bell"), Sound.Source.MASTER, 1f, 1.19f));
                    requester.sendMessage(ClansPlugin.MM.deserialize(LanguageController.getLocalized("requests.request_accepted")
                            .replace("{clan}", clan.getName())));
                }

                // add log
                logController.addLog(new Log( cTarget.getUuid().toString(), player.getUniqueId(), clan.getId(), LogType.REQUEST_ACCEPTED));
                open();
            }, (ignored) -> {
                // delete request
                requestsController.deleteRequest(request);
                player.sendMessage(ClansPlugin.MM.deserialize(LanguageController.getLocalized("requests.decline")
                        .replace("{player}", cTarget.getName())));

                if(requester != null){
                    requester.playSound(sound(key("block.note_block.didgeridoo"), Sound.Source.MASTER, 1f, 1.19f));
                    requester.sendMessage(ClansPlugin.MM.deserialize(LanguageController.getLocalized("requests.request_declined")
                            .replace("{clan}", clan.getName())));
                }
                logController.addLog(new Log( cTarget.getUuid().toString(), player.getUniqueId(), clan.getId(), LogType.REQUEST_DECLINED));
                open();
            }, LanguageController.getLocalized("requests.menu.invite.title").replace("{player}", cTarget.getName()));
        });



        return icon;
    }
}
