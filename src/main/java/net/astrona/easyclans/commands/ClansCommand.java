package net.astrona.easyclans.commands;

import net.astrona.easyclans.ClansPlugin;
import net.astrona.easyclans.controller.ClansController;
import net.astrona.easyclans.controller.LanguageController;
import net.astrona.easyclans.controller.PlayerController;
import net.astrona.easyclans.controller.RequestsController;
import net.astrona.easyclans.gui.ui.*;
import net.astrona.easyclans.models.CPlayer;
import net.astrona.easyclans.models.Clan;
import net.astrona.easyclans.models.components.chat.impl.PlayerChatComponent;
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
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
    private final List<String> oneArgumentSubCommands = List.of("menu", "bank", "members", "list", "create", "test", "chat");
    private final List<String> moreArgumentSubCommands = List.of("kick", "join", "invite");
    private final PlayerController playerController;
    private final ClansController clansController;
    private final RequestsController requestsController;
    private final PlayerChatComponent playerChatComponent;

    public ClansCommand(PlayerController playerController, ClansController clansController,
                        RequestsController requestsController, PlayerChatComponent playerChatComponent,
                        ClansPlugin plugin) {
        this.playerController = playerController;
        this.clansController = clansController;
        this.requestsController = requestsController;
        this.playerChatComponent = playerChatComponent;
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
                return true;
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

                    CPlayer cplayer = playerController.getPlayer(playerSender.getUniqueId());

                    if(cplayer == null){
                        cplayer = new CPlayer(playerSender.getUniqueId(), -1, System.currentTimeMillis(), System.currentTimeMillis(), playerSender.getName());
                    }
                    UUID test = cplayer.getUuid();
                    Clan clan = new Clan(
                            10,
                            test,
                            "Testing clan",
                            "DISPLAY!",
                            0,
                            0,
                            0,
                            0,
                            10.0,
                            new ItemStack(Material.CYAN_BANNER),
                            10000000,
                            "DD",
                            List.of(test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test,
                                    test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test,
                                    test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test,
                                    test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test,
                                    test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test,
                                    test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test,
                                    test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test,
                                    test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test,
                                    test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test,
                                    test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test),
                            System.currentTimeMillis()
                    );
                    cplayer.setClanID(clan.getId());

                    new AdminClanGUI(playerSender, clan, clansController, playerController, requestsController);
                }
                case "chat" -> {
                    CPlayer cplayer = playerController.getPlayer(playerSender.getUniqueId());

                    if(cplayer == null){
                        cplayer = new CPlayer(playerSender.getUniqueId(), -1, System.currentTimeMillis(), System.currentTimeMillis(), playerSender.getName());
                    }

                    if (cplayer.isInClubChat()) {
                        playerSender.sendMessage(MM.deserialize(LanguageController.getLocalized("clan.chat.leave_chat")));
                    } else {
                        playerSender.sendMessage(MM.deserialize(LanguageController.getLocalized("clan.chat.join_chat")));
                    }
                    cplayer.setInClubChat(!cplayer.isInClubChat());
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

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player playerSender)) {
            sender.sendMessage(MM.deserialize("<dark_red>Only a player can execute this command!</dark_red>"));
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
                        sender.sendMessage(MM.deserialize("<red>You are not in a clan."));
                        return List.of("You are not in a clan!");
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

    private void executeMenuSubCommand(Player sender) {

        // first check if player has clan, if has open clan menu, else open clans list to join or create.
        CPlayer cPlayer = playerController.getPlayer(sender.getUniqueId());

        if (cPlayer.getClanID() == -1) {
            sender.sendMessage(MM.deserialize("<red>You are not in a clan."));
            return;
        }

        Clan clan = clansController.getClan(cPlayer.getClanID());
        new ClanGUI(sender, clan, clansController, playerController, requestsController);
    }

    private void executeBankSubCommand(Player sender) {

        // check if player is in clan
        CPlayer cPlayer = playerController.getPlayer(sender.getUniqueId());

        if (cPlayer.getClanID() == -1) {
            sender.sendMessage(MM.deserialize("<red>You are not in a clan."));
            return;
        }

        Clan clan = clansController.getClan(cPlayer.getClanID());
        new BankGUI(sender, clan, null);
    }

    private void executeMembersSubCommand(Player sender) {
        CPlayer cPlayer = playerController.getPlayer(sender.getUniqueId());

        if (cPlayer.getClanID() == -1) {
            sender.sendMessage(MM.deserialize("<red>You are not in a clan."));
            return;
        }

        Clan clan = clansController.getClan(cPlayer.getClanID());
        new MembersGUI(sender, clan, clansController, playerController, null);
    }

    private void executeListSubCommand(Player sender) {
        CPlayer cPlayer = playerController.getPlayer(sender.getUniqueId());
        Clan clan = clansController.getClan(cPlayer.getClanID());

        new ClanListGUI(sender, clan, clansController, playerController, null);
    }

    private void executeCreateSubCommand(Player sender) {
        new ClanCreateGUI("DEFAULT_NAME", "DEFAULT", null, sender, plugin, playerController, clansController, requestsController, playerChatComponent);
    }

    private void executeKickSubCommand(Player sender, OfflinePlayer receiver) {

    }

    private void executeJoinSubCommand(Player sender, String clanName) {

    }

    private void executeInviteSubCommand(Player sender, OfflinePlayer receiver) {

    }
}
