package net.astrona.easyclans.commands;

import net.astrona.easyclans.ClansPlugin;
import net.astrona.easyclans.controller.*;
import net.astrona.easyclans.gui.ui.*;
import net.astrona.easyclans.models.CPlayer;
import net.astrona.easyclans.models.Clan;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static net.astrona.easyclans.ClansPlugin.MM;
import static net.kyori.adventure.key.Key.key;
import static net.kyori.adventure.sound.Sound.sound;

public class ClansCommand implements TabExecutor {
    private ClansPlugin plugin;
    private final List<String> oneArgumentSubCommands = List.of(
            "menu", "bank", "members", "list", "create",
            "chat", "leave", "delete", "leave");
    private final List<String> moreArgumentSubCommands = List.of("kick", "join", "invite");
    private final PlayerController playerController;
    private final ClansController clansController;
    private final RequestsController requestsController;
    private final LogController logController;

    public ClansCommand(PlayerController playerController, ClansController clansController,
                        RequestsController requestsController,
                        ClansPlugin plugin, LogController logController) {
        this.playerController = playerController;
        this.clansController = clansController;
        this.requestsController = requestsController;
        this.plugin = plugin;
        this.logController = logController;
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

        if(oneArgumentSubCommands.contains(args[0].toLowerCase())){
            switch(args[0].toLowerCase()){
                case "bank" -> {
                    this.executeBankSubCommand(player, cPlayer, clan);
                }
                case "members" -> {
                    this.executeMembersSubCommand(player, cPlayer, clan);
                }
                case "list" -> {
                    this.executeListSubCommand(player, cPlayer, clan);
                }
                case "create" -> {
                    this.executeCreateSubCommand(player, cPlayer, clan);
                }
                case "chat" -> {
                    var cplayer = playerController.getPlayer(player.getUniqueId());
                    if (cplayer.isInClubChat()) {
                        player.sendMessage(MM.deserialize(LanguageController.getLocalized("clan.chat.leave_chat")));
                    } else {
                        player.sendMessage(MM.deserialize(LanguageController.getLocalized("clan.chat.join_chat")));
                    }
                    cplayer.setInClubChat(!cplayer.isInClubChat());
                }
                case "delete" -> {
                    this.executeDeleteClanSubCommand(player, cPlayer, clan);
                }
                case "leave" -> {
                    this.executeLeaveClanSubCommand(player, cPlayer, clan);
                }
            }
        }

        if(moreArgumentSubCommands.contains(args[0].toLowerCase())){
            if(args.length < 2){
                player.sendMessage("Invalid arguments count for command....");
                return true;
            }

            switch(args[0].toLowerCase()){
                case "kick" -> {
                    //OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
                    this.executeKickSubCommand(player, player);
                }
                case "join" -> {
                    this.executeJoinSubCommand(player, args[1]);
                }
                case "invite" -> {
                    //OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
                    this.executeInviteSubCommand(player, player);
                }
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
            new ClanListGUI(sender, clansController, playerController, requestsController, null, logController);
            return;
        }
        if(clan.getOwner().equals(sender.getUniqueId())){
            new AdminClanGUI(sender, clan, clansController, playerController,
                    requestsController, plugin, logController);
        }else{
            new ClanGUI(sender, clan, clansController, playerController, requestsController, logController, plugin);
        }

    }

    private void executeBankSubCommand(Player sender, CPlayer cPlayer, Clan clan) {
        if (clan == null) {
            sender.sendMessage(MM.deserialize(LanguageController.getLocalized("not_in_clan")));
            return;
        }
        new BankGUI(sender, clan, null, plugin, clansController, logController);
    }

    private void executeMembersSubCommand(Player sender, CPlayer cPlayer, Clan clan) {
        if (clan == null) {
            sender.sendMessage(MM.deserialize(LanguageController.getLocalized("not_in_clan")));
            return;
        }
        new MembersGUI(sender, clan, clansController, playerController, null, logController, plugin);
    }

    private void executeListSubCommand(Player sender, CPlayer cPlayer, Clan clan) {
        new ClanListGUI(sender, clansController, playerController, requestsController, null, logController);
    }

    private void executeCreateSubCommand(Player sender, CPlayer cPlayer, Clan clan) {
        if(clan != null){
            sender.sendMessage(MM.deserialize(LanguageController.getLocalized("already_in_clan")));
            return;
        }
        new ClanCreateGUI( sender, plugin, playerController, clansController, requestsController, logController);
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

    private void executeKickSubCommand(Player sender, OfflinePlayer receiver) {

    }

    private void executeJoinSubCommand(Player sender, String clanName) {

    }

    private void executeInviteSubCommand(Player sender, OfflinePlayer receiver) {

    }
}
