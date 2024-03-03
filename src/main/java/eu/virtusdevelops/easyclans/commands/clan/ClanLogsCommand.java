package eu.virtusdevelops.easyclans.commands.clan;

import eu.virtusdevelops.easyclans.ClansPlugin;
import eu.virtusdevelops.easyclans.commands.AbstractCommand;
import eu.virtusdevelops.easyclans.controller.LanguageController;
import eu.virtusdevelops.easyclans.gui.ui.ClanLogsMenu;
import eu.virtusdevelops.easyclans.gui.ui.LogsMenu;
import eu.virtusdevelops.easyclans.models.Clan;
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

public class ClanLogsCommand implements AbstractCommand {
    private ClansPlugin plugin;

    @Override
    public void registerFeature(@NonNull ClansPlugin plugin, @NotNull AnnotationParser<CommandSender> annotationParser) {
        this.plugin = plugin;
        annotationParser.parse(this);
    }

    @Permission("easyclans.command.logs")
    @Command("clan logs")
    public void playerLogsCommand(
            final CommandSender sender
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

        new LogsMenu(player, plugin);

    }


    @Permission("easyclans.command.clogs")
    @Command("clan clogs")
    public void clanLogsCommand(
            final CommandSender sender
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

        new ClanLogsMenu(player, clan, plugin);

    }



    @Permission("easyclans.command.clogs.admin")
    @Command("clan clogs <clan_name>")
    public void adminClanLogsCommand(
            final CommandSender sender,
            @Argument(value = "clan_name", suggestions = "clan_name") final @NonNull String name
    ){
        if(!(sender instanceof Player player)){
            sender.sendMessage(MM.deserialize(LanguageController.getLocalized("player_only")));
            return;
        }

        var cplayer = plugin.getPlayerController().getPlayer(player.getUniqueId());
        var clan = plugin.getClansController().getClan(name);

        if(clan == null){
            sender.sendMessage(MM.deserialize(LanguageController.getLocalized("invalid_clan")));
            return;
        }

        new ClanLogsMenu(player, clan, plugin);

    }


    @Suggestions("clan_name")
    public List<String> getClanNames(CommandContext<CommandSender> sender, String input){
        return plugin.getClansController().getClans().stream().map(Clan::getName).filter(name -> name.contains(input)).collect(Collectors.toList());

    }

}
