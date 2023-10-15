package eu.virtusdevelops.easyclans.commands;

import eu.virtusdevelops.easyclans.controller.*;
import eu.virtusdevelops.easyclans.gui.ui.*;
import eu.virtusdevelops.easyclans.models.CPlayer;
import eu.virtusdevelops.easyclans.models.Clan;
import eu.virtusdevelops.easyclans.models.Log;
import eu.virtusdevelops.easyclans.ClansPlugin;
import eu.virtusdevelops.easyclans.models.LogType;
import eu.virtusdevelops.easyclans.storage.SQLStorage;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static eu.virtusdevelops.easyclans.ClansPlugin.MM;
import static net.kyori.adventure.key.Key.key;
import static net.kyori.adventure.sound.Sound.sound;

public class ClansCommand implements TabExecutor {
    private ClansPlugin plugin;
    private final List<String> oneArgumentSubCommands = List.of(
            "menu", "bank", "members", "list", "create",
            "chat", "leave", "delete", "help", "clogs");
    private final List<String> moreArgumentSubCommands = List.of("accept");
    private final PlayerController playerController;
    private final ClansController clansController;
    private final RequestsController requestsController;
    private final LogController logController;
    private final CurrenciesController currenciesController;
    private final SQLStorage sqlStorage;

    public ClansCommand(PlayerController playerController, ClansController clansController,
                        RequestsController requestsController,
                        ClansPlugin plugin, LogController logController,
                        CurrenciesController currenciesController,
                        SQLStorage sqlStorage) {
        this.playerController = playerController;
        this.clansController = clansController;
        this.requestsController = requestsController;
        this.currenciesController = currenciesController;
        this.plugin = plugin;
        this.logController = logController;
        this.sqlStorage = sqlStorage;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length > 0 && args[0].equalsIgnoreCase("reload")){
            if(sender.hasPermission("easyclans.command.reload")){
                plugin.reload();
                plugin.getLogger().info("Reloaded configuration files.");
                sender.sendMessage(MM.deserialize("<green>Reloaded..."));
            }else{
                sender.sendMessage(MM.deserialize(LanguageController.getLocalized("no_permission")));
            }
            return true;
        }


        if (!(sender instanceof Player player)) {
            sender.sendMessage(MM.deserialize("<dark_red>Only a player can execute this command!</dark_red>"));
            return false;
        }

        CPlayer cPlayer = playerController.getPlayer(player.getUniqueId());
        Clan clan = clansController.getClan(cPlayer.getClanID());
        if(args.length == 0){
            this.executeMenuSubCommand(player, cPlayer, clan);
            return true;
        }


        switch(args[0].toLowerCase()){
            case "bank" -> {
                if(sender.hasPermission("easyclans.command.bank"))
                    this.executeBankSubCommand(player, cPlayer, clan);
                else
                    sender.sendMessage(ClansPlugin.MM.deserialize(LanguageController.getLocalized("no_permission")));
            }
            case "members" -> {
                if(sender.hasPermission("easyclans.command.members"))
                    this.executeMembersSubCommand(player, cPlayer, clan);
                else
                    sender.sendMessage(ClansPlugin.MM.deserialize(LanguageController.getLocalized("no_permission")));
            }
            case "list" -> {
                if(sender.hasPermission("easyclans.command.list"))
                    this.executeListSubCommand(player, cPlayer, clan);
                else
                    sender.sendMessage(ClansPlugin.MM.deserialize(LanguageController.getLocalized("no_permission")));
            }
            case "create" -> {
                if(sender.hasPermission("easyclans.command.create"))
                    this.executeCreateSubCommand(player, cPlayer, clan);
                else
                    sender.sendMessage(ClansPlugin.MM.deserialize(LanguageController.getLocalized("no_permission")));
            }
            case "chat" -> {
                if(player.hasPermission("easyclans.command.chat")){
                    var cplayer = playerController.getPlayer(player.getUniqueId());
                    if (cplayer.isInClubChat()) {
                        player.sendMessage(MM.deserialize(LanguageController.getLocalized("clan.chat.leave_chat")));
                    } else {
                        player.sendMessage(MM.deserialize(LanguageController.getLocalized("clan.chat.join_chat")));
                    }
                    cplayer.setInClubChat(!cplayer.isInClubChat());
                }else{
                    sender.sendMessage(ClansPlugin.MM.deserialize(LanguageController.getLocalized("no_permission")));
                }

            }
            case "delete" -> {
                if(sender.hasPermission("easyclans.command.delete"))
                    this.executeDeleteClanSubCommand(player, cPlayer, clan);
                else
                    sender.sendMessage(ClansPlugin.MM.deserialize(LanguageController.getLocalized("no_permission")));
            }
            case "leave" -> {
                if(sender.hasPermission("easyclans.command.leave"))
                    this.executeLeaveClanSubCommand(player, cPlayer, clan);
                else
                    sender.sendMessage(ClansPlugin.MM.deserialize(LanguageController.getLocalized("no_permission")));
            }
            case "help" -> {
                if(sender.hasPermission("easyclans.command.help"))
                    this.executeHelpCommand(player);
                else
                    sender.sendMessage(ClansPlugin.MM.deserialize(LanguageController.getLocalized("no_permission")));
            }
            case "accept" -> {
                if(sender.hasPermission("easyclans.command.accept")){
                    if(args.length < 2){
                        sender.sendMessage(ClansPlugin.MM.deserialize(
                                LanguageController.getLocalized("invalid_player")
                        ));
                        break;
                    }
                    this.executeAcceptSubCommand(player, args[1]);
                }else{
                    sender.sendMessage(ClansPlugin.MM.deserialize(LanguageController.getLocalized("no_permission")));
                }

            }
            case "info" -> {
                if(sender.hasPermission("easyclans.command.info"))
                    this.executeInfoCommand(sender);
                else
                    sender.sendMessage(ClansPlugin.MM.deserialize(LanguageController.getLocalized("no_permission")));
            }
            case "logs", "log" -> {
                if(sender.hasPermission("easyclans.command.logs"))
                    this.executeLogsCommand(player);
                else
                    sender.sendMessage(ClansPlugin.MM.deserialize(LanguageController.getLocalized("no_permission")));
            }
            case "clogs", "clog" -> {
                if(sender.hasPermission("easyclans.command.clogs"))
                    this.executeClanLogsCommand(player);
                else
                    sender.sendMessage(ClansPlugin.MM.deserialize(LanguageController.getLocalized("no_permission")));
            }
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player playerSender)) {
            //sender.sendMessage(MM.deserialize("<dark_red>Only a player can execute this command!</dark_red>"));
            return List.of("");
        }

        if (args.length < 2) {
            List<String> arguments = new ArrayList<>();
            arguments.addAll(oneArgumentSubCommands);
            if(sender.hasPermission("easyclans.command.info")){
                arguments.add("info");
            }
            arguments.addAll(moreArgumentSubCommands);
            return arguments;
        } else if (args.length == 2) {
            switch (args[0]) {
                case "kick" -> {
                    CPlayer cPlayer = playerController.getPlayer(playerSender.getUniqueId());

                    if (cPlayer.getClanID() == -1) {
                        return Collections.emptyList();
                    }

                    Clan clan = clansController.getClan(cPlayer.getClanID());

                    return clan.getMembers().stream()
                            .map(uuid -> playerController.getPlayer(uuid).getName())
                            .collect(Collectors.toList());
                }
                case "join" -> {
                    return clansController.getClans().stream()
                            .map(Clan::getName)
                            .collect(Collectors.toList());
                }
                case "invite" -> {
                    return playerController.getPlayers().stream()
                            .filter(CPlayer::isActive)
                            .map(CPlayer::getName)
                            .collect(Collectors.toList());
                }
            }
        }
        return List.of("");
    }

    private void executeMenuSubCommand(Player sender, CPlayer cPlayer, Clan clan) {
        if (clan == null) {
            new ClanListGUI(sender, clansController, playerController, requestsController, null, logController, plugin);
            return;
        }
        if(clan.getOwner().equals(sender.getUniqueId())){
            new AdminClanGUI(sender, clan, clansController, playerController,
                    requestsController, plugin, logController, currenciesController);
        }else{
            new ClanGUI(sender, clan, clansController, playerController, requestsController, logController, plugin, currenciesController);
        }

    }

    private void executeBankSubCommand(Player sender, CPlayer cPlayer, Clan clan) {
        if (clan == null) {
            sender.sendMessage(MM.deserialize(LanguageController.getLocalized("not_in_clan")));
            return;
        }
        new CurrenciesGUI(sender, clan, clansController, playerController, null, logController, plugin, currenciesController);
    }

    private void executeMembersSubCommand(Player sender, CPlayer cPlayer, Clan clan) {
        if (clan == null) {
            sender.sendMessage(MM.deserialize(LanguageController.getLocalized("not_in_clan")));
            return;
        }
        new MembersGUI(sender, clan, clansController, playerController, null, logController, plugin);
    }

    private void executeListSubCommand(Player sender, CPlayer cPlayer, Clan clan) {
        new ClanListGUI(sender, clansController, playerController, requestsController, null, logController, plugin);
    }

    private void executeCreateSubCommand(Player sender, CPlayer cPlayer, Clan clan) {
        if(clan != null){
            sender.sendMessage(MM.deserialize(LanguageController.getLocalized("already_in_clan")));
            return;
        }
        new ClanCreateGUI( sender, plugin, playerController, clansController, requestsController, logController,currenciesController);
    }

    private void executeDeleteClanSubCommand(Player sender, CPlayer cPlayer, Clan clan){
        if(!clan.getOwner().equals(sender.getUniqueId())){
            sender.sendMessage(MM.deserialize(LanguageController.getLocalized("not_owner")));
            return;
        }
        new ConfirmGUI(sender, (player) -> {
            // confirm
            player.playSound(sound(key("block.note_block.cow_bell"), Sound.Source.MASTER, 1f, 1.19f));
            clansController.deleteClan(clan.getId());
            player.closeInventory();

        }, (player) -> {
            // decline
            player.playSound(sound(key("block.note_block.didgeridoo"), Sound.Source.MASTER, 1f, 1.19f));
            player.closeInventory();

        }, LanguageController.getLocalized("delete_clan.title"));
    }

    private void executeLeaveClanSubCommand(Player sender, CPlayer cPlayer, Clan clan){
        if(clan.getOwner().equals(sender.getUniqueId())){
            sender.sendMessage(MM.deserialize(LanguageController.getLocalized("cant_leave")));
            return;
        }
        new ConfirmGUI(sender, (player) -> {
            // confirm
            player.playSound(sound(key("block.note_block.cow_bell"), Sound.Source.MASTER, 1f, 1.19f));
            cPlayer.setClanID(-1);
            playerController.updatePlayer(cPlayer);
            player.closeInventory();

        }, (player) -> {
            // decline
            player.playSound(sound(key("block.note_block.didgeridoo"), Sound.Source.MASTER, 1f, 1.19f));
            player.closeInventory();

        }, LanguageController.getLocalized("leave_confirm.title"));
    }

    private void executeHelpCommand(Player sender){
        LanguageController.getLocalizedDesiralizedList("help").forEach(sender::sendMessage);
    }

    private void executeInfoCommand(CommandSender sender){
        if(!sender.hasPermission("easyclans.command.info")){
            sender.sendMessage(ClansPlugin.MM.deserialize(LanguageController.getLocalized("no_permission")));
            return;
        }

        sender.sendMessage(MM.deserialize("<gold>Enabled economy providers: "));
        for(var provider : currenciesController.getCurrencyProviders().values()){
            sender.sendMessage(MM.deserialize("<gray>- <yellow>" + provider.getPluginName()));
        }
    }

    public void executeLogsCommand(Player sender){
        if(!sender.hasPermission("easyclans.command.logs")){
            sender.sendMessage(ClansPlugin.MM.deserialize(LanguageController.getLocalized("no_permission")));
            return;
        }
        new LogsGUI(sender, plugin, sqlStorage, clansController, playerController);
    }


    public void executeClanLogsCommand(Player sender){
        if(!sender.hasPermission("easyclans.command.clan_logs")){
            sender.sendMessage(ClansPlugin.MM.deserialize(LanguageController.getLocalized("no_permission")));
            return;
        }
        var cPlayer = playerController.getPlayer(sender.getUniqueId());
        var clan = clansController.getClan(cPlayer.getClanID());
        if(clan.getOwner().equals(cPlayer.getUuid()) || sender.hasPermission("easyclans.command.logs"))
            new ClanLogsGUI(sender, clan, plugin, sqlStorage, clansController, playerController);
        else
            sender.sendMessage(ClansPlugin.MM.deserialize(LanguageController.getLocalized("no_permission")));
    }

    private void executeAcceptSubCommand(Player sender, String player){
        var cPlayer = playerController.getPlayer(sender.getUniqueId());
        if(cPlayer.getClanID() == -1){
            sender.sendMessage(ClansPlugin.MM.deserialize(
                    LanguageController.getLocalized("not_in_clan")
            ));
            return;
        }
        var clan = clansController.getClan(cPlayer.getClanID());
        if(!clan.getOwner().equals(cPlayer.getUuid())){
            sender.sendMessage(ClansPlugin.MM.deserialize(
                    LanguageController.getLocalized("not_owner")
            ));
            return;
        }
        var oRequester = Bukkit.getOfflinePlayerIfCached(player);
        if(oRequester == null){
            sender.sendMessage(ClansPlugin.MM.deserialize(
                    LanguageController.getLocalized("invalid_player")
            ));
            return;
        }
        var request = requestsController.getRequest(oRequester.getUniqueId(),clan.getId());
        if(request == null){
            sender.sendMessage(ClansPlugin.MM.deserialize(
                    LanguageController.getLocalized("invalid_player")
            ));
            return;
        }
        if(!request.isValid()){
            sender.sendMessage(ClansPlugin.MM.deserialize(
                    LanguageController.getLocalized("request_expired")
            ));
            requestsController.deleteRequest(request);
            return;
        }

        if(clan.getMembers().size()+1 > plugin.getConfig().getInt("clan.max_members")){
            sender.sendMessage(ClansPlugin.MM.deserialize(
                    LanguageController.getLocalized("requests.too_many_members")
                            .replace("{max}", String.valueOf(plugin.getConfig().getInt("clan.max_members")))
                            .replace("{current}", String.valueOf(clan.getMembers().size()))
            ));
            return;
        }

        var provider =  currenciesController.getProvider("Vault");
        var requester = Bukkit.getPlayer(oRequester.getUniqueId());
        if(provider.getValue(oRequester) < clan.getJoinMoneyPrice()){
            sender.sendMessage(ClansPlugin.MM.deserialize(
                    LanguageController.getLocalized("requests.not_enough_money_accepter")
                            .replace("{player}", cPlayer.getName())));
            sender.playSound(sound(key("block.note_block.cow_bell"), Sound.Source.MASTER, 1f, 1.19f));

            if(requester != null){
                requester.playSound(sound(key("block.note_block.cow_bell"), Sound.Source.MASTER, 1f, 1.19f));
                requester.sendMessage(ClansPlugin.MM.deserialize(LanguageController.getLocalized("requests.not_enough_money_sender")
                        .replace("{clan}", clan.getName())));
            }
            return;
        }


        var cRequester = playerController.getPlayer(oRequester.getUniqueId());
        provider.removeValue(Bukkit.getOfflinePlayer(cPlayer.getUuid()), clan.getJoinMoneyPrice());
        requestsController.deleteRequest(request);

        // update player
        cRequester.setClanID(clan.getId());
        cRequester.setJoinClanDate(System.currentTimeMillis());
        playerController.updatePlayer(cRequester);

        // edit clan add member blabla
        clan.addMember(cRequester.getUuid());

        // send message
        sender.sendMessage(ClansPlugin.MM.deserialize(LanguageController.getLocalized("requests.accepted")
                .replace("{player}", cPlayer.getName())));
        if(requester != null){
            requester.playSound(sound(key("block.note_block.cow_bell"), Sound.Source.MASTER, 1f, 1.19f));
            requester.sendMessage(ClansPlugin.MM.deserialize(LanguageController.getLocalized("requests.request_accepted")
                    .replace("{clan}", clan.getName())));
        }

        // add log
        logController.addLog(new Log( "request:" + cPlayer.getUuid().toString(), sender.getUniqueId(), clan.getId(), LogType.REQUEST_ACCEPTED));


    }

    private void executeKickSubCommand(Player sender, OfflinePlayer receiver) {

    }

    private void executeJoinSubCommand(Player sender, String clanName) {

    }

    private void executeInviteSubCommand(Player sender, OfflinePlayer receiver) {

    }
}
