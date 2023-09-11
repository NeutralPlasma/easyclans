package net.astrona.easyclans.commands;

import net.astrona.easyclans.ClansPlugin;
import net.astrona.easyclans.controller.ClansController;
import net.astrona.easyclans.controller.PlayerController;
import net.astrona.easyclans.gui.ui.ClanGUI;
import net.astrona.easyclans.models.CPlayer;
import net.astrona.easyclans.models.Clan;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

import static net.astrona.easyclans.ClansPlugin.MM;

/*
- Kick
- Join
- Invite
- Disband
- Bank
- Members
- List
- Create
- Menu / Settings

 */
public class ClansCommand implements TabExecutor {
    private ClansPlugin plugin;
    private final List<String> oneArgumentSubCommands = List.of("menu", "bank", "members", "list", "create", "test");
    private final PlayerController playerController;
    private final ClansController clansController;

    public ClansCommand(PlayerController playerController, ClansController clansController, ClansPlugin plugin) {
        this.playerController = playerController;
        this.clansController = clansController;
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player playerSender)) {
            sender.sendMessage(MM.deserialize("<dark_red>Only a player can execute this command!</dark_red>"));
            return false;
        }

        if (args.length < 2) {
            if (!oneArgumentSubCommands.contains(args[0])) {
                sender.sendMessage(MM.deserialize(
                        """
                                <hover:show_text:"<red>%s -> ... [HERE]"><dark_red>Too many arguments.</dark_red>
                                """.formatted(args[0])
                ));
                return false;
            }
            switch (args[0]) {
                case "menu" -> {
                    this.executeMenuSubCommand(playerSender);
                }
                case "bank" -> {
                    this.executeBankSubCommand(playerSender);
                }
                case "members" -> {
                    this.executeMembersSubCommand(playerSender);
                }
                case "list" -> {
                    this.executeListSubCommand(playerSender);
                }
                case "create" -> {
                    this.executeCreateSubCommand(playerSender);
                }
                case "test" -> {
                    new ClanGUI(playerSender, plugin);
                }
            }
        } else if (args.length == 2) {
            switch (args[0]) {
                case "kick" -> {
                    OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
                    this.executeKickSubCommand(playerSender, player);
                }
                case "join" -> {
                    this.executeJoinSubCommand(playerSender, args[1]);
                }
                case "invite" -> {
                    OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
                    this.executeInviteSubCommand(playerSender, player);
                }
            }
        }

        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return null;
    }

    private void executeMenuSubCommand(Player sender) {

        // first check if player has clan, if has open clan menu, else open clans list to join or create.

        // TODO: gasper implementaj menu
    }

    private void executeBankSubCommand(Player sender) {

        // check if player is in clan

        // TODO: gasper implementaj od banke menu
    }

    private void executeMembersSubCommand(Player sender) {

        // check if player is in clan

        // TODO: gasper implementaj od members menu
    }

    private void executeListSubCommand(Player sender) {

        // check if player is in clan

        // TODO: gasper implementaj od clans list menu
    }

    private void executeCreateSubCommand(Player sender) {

    }

    private void executeKickSubCommand(Player sender, OfflinePlayer receiver) {

    }

    private void executeJoinSubCommand(Player sender, String clanName) {

    }

    private void executeInviteSubCommand(Player sender, OfflinePlayer receiver) {

    }
}
