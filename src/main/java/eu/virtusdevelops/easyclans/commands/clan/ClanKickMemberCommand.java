package eu.virtusdevelops.easyclans.commands.clan;

import eu.virtusdevelops.easyclans.ClansPlugin;
import eu.virtusdevelops.easyclans.commands.AbstractCommand;
import eu.virtusdevelops.easyclans.controller.LanguageController;
import eu.virtusdevelops.easyclans.models.Log;
import eu.virtusdevelops.easyclans.models.LogType;
import eu.virtusdevelops.easyclans.models.UserPermissions;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.annotations.AnnotationParser;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;
import org.incendo.cloud.annotations.suggestion.Suggestions;
import org.incendo.cloud.context.CommandContext;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static eu.virtusdevelops.easyclans.ClansPlugin.MM;

public class ClanKickMemberCommand implements AbstractCommand {
    private ClansPlugin plugin;

    @Override
    public void registerFeature(@NonNull ClansPlugin plugin, @NotNull AnnotationParser<CommandSender> annotationParser) {
        this.plugin = plugin;
        annotationParser.parse(this);
    }


    @Permission("easyclans.command.kick")
    @Command("clan kick <member_name>")
    public void kickMemberCommand(
            final CommandSender sender,
            @Argument(value = "member_name", suggestions = "member_name") final @NonNull String memberName
    ){
        if(!(sender instanceof Player player)){
            sender.sendMessage(MM.deserialize(LanguageController.getLocalized("player_only")));
            return;
        }

        var cplayer = plugin.getPlayerController().getPlayer(player.getUniqueId());
        var clan = plugin.getClansController().getClan(cplayer.getClanID());

        if(clan == null){
            sender.sendMessage(MM.deserialize(LanguageController.getLocalized("not_in_clan")));
            return;
        }

        if(!clan.getOwner().equals(player.getUniqueId())
                && !cplayer.hasPermission(UserPermissions.KICK_MEMBER)){
            sender.sendMessage(MM.deserialize(LanguageController.getLocalized("no_permission")));
            return;
        }

        var pTarget = Bukkit.getOfflinePlayerIfCached(memberName);
        if(pTarget == null){
            sender.sendMessage(MM.deserialize(LanguageController.getLocalized("invalid_player")));
            return;
        }
        var cTarget = plugin.getPlayerController().getPlayer(pTarget.getUniqueId());

        if(!clan.getMembers().contains(cTarget.getUuid())){
            sender.sendMessage(MM.deserialize(LanguageController.getLocalized("invalid_player")));
            return;
        }

        if(clan.getOwner().equals(cTarget.getUuid())){
            sender.sendMessage(MM.deserialize(LanguageController.getLocalized("invalid_player")));
            return;
        }

        cTarget.removeFromClan();
        clan.getMembers().remove(cTarget.getUuid());
        plugin.getPlayerController().updatePlayer(cTarget);

        if(pTarget.isOnline()){
            var pOnline = Bukkit.getPlayer(pTarget.getUniqueId());
            pOnline.sendMessage(ClansPlugin.MM.deserialize(LanguageController.getLocalized("members.kick.kicked")));
        }

        player.sendMessage(ClansPlugin.MM.deserialize(
                LanguageController.getLocalized("members.kick.kick_success")
                        .replace("{player}", cTarget.getName())
        ));
        plugin.getLogController().addLog(new Log( cTarget.getUuid().toString(), player.getUniqueId(), clan.getId(), LogType.MEMBER_KICK));

    }


    @Suggestions("member_name")
    public List<String> getMemberNames(CommandContext<CommandSender> sender, String input){

        if(sender.sender() instanceof Player player){
            var pController = plugin.getPlayerController();
            var cplayer = pController.getPlayer(player.getUniqueId());
            var clan = plugin.getClansController().getClan(cplayer.getClanID());
            return clan.getMembers().stream().map(it -> pController.getPlayer(it).getName()).filter(name -> name.contains(input)).collect(Collectors.toList());
        }

        return Collections.emptyList();
    }
}
