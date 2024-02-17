package eu.virtusdevelops.easyclans.commands.clan;

import eu.virtusdevelops.easyclans.ClansPlugin;
import eu.virtusdevelops.easyclans.commands.AbstractFeature;
import eu.virtusdevelops.easyclans.controller.LanguageController;
import eu.virtusdevelops.easyclans.models.Log;
import eu.virtusdevelops.easyclans.models.LogType;
import eu.virtusdevelops.easyclans.models.UserPermissions;
import net.kyori.adventure.sound.Sound;
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
import static net.kyori.adventure.key.Key.key;
import static net.kyori.adventure.sound.Sound.sound;

public class ClanAcceptMemberCommand implements AbstractFeature {
    private ClansPlugin plugin;

    @Override
    public void registerFeature(@NonNull ClansPlugin plugin, @NotNull AnnotationParser<CommandSender> annotationParser) {
        this.plugin = plugin;
        annotationParser.parse(this);
    }


    @Permission("easyclans.command.accept")
    @Command("clan accept <member_name>")
    public void kickMemberCommand(
            final CommandSender sender,
            @Argument(value = "member_name", suggestions = "request_name") final @NonNull String memberName
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
                && !cplayer.hasPermission(UserPermissions.ACCEPT_REQUEST)){
            sender.sendMessage(MM.deserialize(LanguageController.getLocalized("no_permission")));
            return;
        }

        var pTarget = Bukkit.getOfflinePlayerIfCached(memberName);
        var pOnline = Bukkit.getPlayer(pTarget.getUniqueId());

        if(pTarget == null){
            sender.sendMessage(MM.deserialize(LanguageController.getLocalized("invalid_player")));
            return;
        }
        var cTarget = plugin.getPlayerController().getPlayer(pTarget.getUniqueId());
        var tRequest = plugin.getRequestsController().getRequest(cTarget.getUuid(), clan.getId());

        if(tRequest == null){
            sender.sendMessage(MM.deserialize(LanguageController.getLocalized("invalid_player")));
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


        var provider =  plugin.getCurrenciesController().getProvider("Vault");
        if(provider.getValue(pTarget) < clan.getJoinMoneyPrice()){
            sender.sendMessage(ClansPlugin.MM.deserialize(
                    LanguageController.getLocalized("requests.not_enough_money_accepter")
                            .replace("{player}", cTarget.getName())));
            sender.playSound(sound(key("block.note_block.cow_bell"), Sound.Source.MASTER, 1f, 1.19f));

            if(pTarget.isOnline()){
                pOnline.playSound(sound(key("block.note_block.cow_bell"), Sound.Source.MASTER, 1f, 1.19f));
                pOnline.sendMessage(ClansPlugin.MM.deserialize(LanguageController.getLocalized("requests.not_enough_money_sender")
                        .replace("{clan}", clan.getName())));
            }
            return;
        }




        cTarget.setClanID(clan.getId());
        clan.getMembers().add(cTarget.getUuid());
        plugin.getPlayerController().updatePlayer(cTarget);

        if(pTarget.isOnline()){
            pOnline.playSound(sound(key("block.note_block.cow_bell"), Sound.Source.MASTER, 1f, 1.19f));
            pOnline.sendMessage(ClansPlugin.MM.deserialize(LanguageController.getLocalized("requests.request_accepted")
                    .replace("{clan}", clan.getName())));
        }

        sender.sendMessage(ClansPlugin.MM.deserialize(LanguageController.getLocalized("requests.accepted")
                .replace("{player}", cTarget.getName())));


        plugin.getLogController().addLog(new Log( cTarget.getUuid().toString(), player.getUniqueId(), clan.getId(), LogType.MEMBER_KICK));

    }

    @Suggestions("request_name")
    public List<String> getRequests(CommandContext<CommandSender> sender, String input){

        if(sender.sender() instanceof Player player){
            var pController = plugin.getPlayerController();
            var cplayer = pController.getPlayer(player.getUniqueId());
            var requests = plugin.getRequestsController().getClanRequests(cplayer.getClanID());
            return requests.stream().map(it -> pController.getPlayer(it.getPlayerUuid()).getName()).filter(name -> name.contains(input)).collect(Collectors.toList());
        }

        return Collections.emptyList();
    }
}
