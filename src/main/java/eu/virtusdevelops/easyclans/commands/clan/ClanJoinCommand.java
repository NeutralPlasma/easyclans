package eu.virtusdevelops.easyclans.commands.clan;

import eu.virtusdevelops.easyclans.ClansPlugin;
import eu.virtusdevelops.easyclans.commands.AbstractFeature;
import eu.virtusdevelops.easyclans.controller.LanguageController;
import eu.virtusdevelops.easyclans.models.Clan;
import eu.virtusdevelops.easyclans.models.Log;
import eu.virtusdevelops.easyclans.models.LogType;
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

import java.util.List;
import java.util.stream.Collectors;

import static eu.virtusdevelops.easyclans.ClansPlugin.MM;
import static net.kyori.adventure.key.Key.key;
import static net.kyori.adventure.sound.Sound.sound;

public class ClanJoinCommand implements AbstractFeature {

    private ClansPlugin plugin;

    @Override
    public void registerFeature(@NonNull ClansPlugin plugin, @NotNull AnnotationParser<CommandSender> annotationParser) {
        this.plugin = plugin;
        annotationParser.parse(this);
    }

    @Permission("easyclans.command.join")
    @Command("clan join <clan_name>")
    public void joinClanCommand(
            final CommandSender sender,
            @Argument(value = "clan_name", suggestions = "clan_name") final @NonNull String clanName
    ){
        if(!(sender instanceof Player player)){
            sender.sendMessage(MM.deserialize(LanguageController.getLocalized("player_only")));
            return;
        }

        var cplayer = plugin.getPlayerController().getPlayer(player.getUniqueId());
        var pClan = plugin.getClansController().getClan(cplayer.getClanID());
        var clan = plugin.getClansController().getClan(clanName);

        if(pClan != null){
            sender.sendMessage(MM.deserialize(LanguageController.getLocalized("already_in_clan")));
            return;
        }

        if(clan == null){
            sender.sendMessage(MM.deserialize(LanguageController.getLocalized("invalid_clan")));
            return;
        }

        var requestsController = plugin.getRequestsController();


        if(requestsController.getClanRequests(clan.getId()).stream().anyMatch((rq) -> rq.getPlayerUuid().equals(cplayer.getUuid()))){
            player.sendMessage(ClansPlugin.MM.deserialize(LanguageController.getLocalized("invite.already_sent")));
        }else{
            requestsController.createRequest(
                    clan.getId(),
                    cplayer.getUuid(),
                    System.currentTimeMillis() + (plugin.getConfig().getLong("clan.default_request_expire_duration") * 1000),
                    System.currentTimeMillis()
            );
            player.sendMessage(ClansPlugin.MM.deserialize(LanguageController.getLocalized("invite.sent")));
            plugin.getLogController().addLog(new Log(clan.getName() + ":" + player.getName(), player.getUniqueId(), clan.getId(), LogType.REQUEST_SENT));

            var owner = Bukkit.getPlayer(clan.getOwner());


            if(owner != null){
                owner.sendMessage(
                        ClansPlugin.MM.deserialize(LanguageController.getLocalized(
                                "invite.invite_received").replace("{player}", cplayer.getName())
                        ));
                owner.playSound(sound(key("block.note_block.cow_bell"), Sound.Source.MASTER, 1f, 1.19f));
            }
        }


    }


    @Suggestions("clan_name")
    public List<String> getClanNames(CommandContext<CommandSender> sender, String input){
        return plugin.getClansController().getClans().stream().map(Clan::getName).filter(name -> name.contains(input)).collect(Collectors.toList());

    }
}
