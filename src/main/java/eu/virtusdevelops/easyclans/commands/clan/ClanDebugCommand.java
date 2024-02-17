package eu.virtusdevelops.easyclans.commands.clan;

import eu.virtusdevelops.easyclans.ClansPlugin;
import eu.virtusdevelops.easyclans.commands.AbstractFeature;
import eu.virtusdevelops.easyclans.controller.LanguageController;
import eu.virtusdevelops.easyclans.gui.ui.ClanLogsMenu;
import eu.virtusdevelops.easyclans.gui.ui.LogsMenu;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.annotations.AnnotationParser;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;
import org.jetbrains.annotations.NotNull;

import static eu.virtusdevelops.easyclans.ClansPlugin.MM;

public class ClanDebugCommand implements AbstractFeature {
    private ClansPlugin plugin;

    @Override
    public void registerFeature(@NonNull ClansPlugin plugin, @NotNull AnnotationParser<CommandSender> annotationParser) {
        this.plugin = plugin;
        annotationParser.parse(this);
    }

    @Permission("easyclans.command.debug")
    @Command("clan debug <player>")
    public void debugCommand(
            final CommandSender sender,
            @Argument(value = "player") final @NonNull String player
    ){
        OfflinePlayer target = Bukkit.getOfflinePlayerIfCached(player);
        if(target == null){
            sender.sendMessage(ClansPlugin.MM.deserialize("<red>Invalid player: " + player));
            return;
        }
        var cPlayer = plugin.getPlayerController().getPlayer(target.getUniqueId());
        sender.sendMessage(MM.deserialize(" <gray>- <gold>Rank: <yellow>" + cPlayer.getRank()));
        sender.sendMessage(MM.deserialize(" <gray>- <gold>Clan ID: <yellow>" + cPlayer.getClanID()));
        sender.sendMessage(MM.deserialize(" <gray>- <gold>Joined clan: <yellow>" + cPlayer.getJoinClanDate()));
        sender.sendMessage(MM.deserialize(" <gray>- <gold>Last active: <yellow>" + cPlayer.getLastActive()));
        sender.sendMessage(MM.deserialize(" <gray>- <gold>UUID: <yellow>" + cPlayer.getUuid()));
        sender.sendMessage(MM.deserialize(" <gray>- <gold>Is Active: <yellow>" + cPlayer.isActive()));
        sender.sendMessage(MM.deserialize(" <gray>- <gold>In clan chat: <yellow>" + cPlayer.isInClubChat()));
        sender.sendMessage(MM.deserialize(" <gray>- <gold>Permissions: <yellow>"));
        for(var permission : cPlayer.getUserPermissionsList()){
            sender.sendMessage(MM.deserialize(" <gray>- <yellow>" + permission.toString()));
        }

    }


}
