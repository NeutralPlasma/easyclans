package eu.virtusdevelops.easyclans.commands.trophy;

import eu.virtusdevelops.easyclans.ClansPlugin;
import eu.virtusdevelops.easyclans.commands.AbstractCommand;
import eu.virtusdevelops.easyclans.controller.ClansController;
import eu.virtusdevelops.easyclans.controller.LanguageController;
import eu.virtusdevelops.easyclans.controller.TropyController;
import eu.virtusdevelops.easyclans.models.Clan;
import eu.virtusdevelops.easyclans.models.Trophy;
import org.bukkit.command.CommandSender;
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

public class TropyRemoveClanCommand implements AbstractCommand {

    private ClansPlugin plugin;
    private TropyController tropyController;
    private ClansController clansController;

    @Override
    public void registerFeature(@NonNull ClansPlugin plugin, @NotNull AnnotationParser<CommandSender> annotationParser) {
        this.plugin = plugin;
        this.tropyController = plugin.getTropyController();
        this.clansController = plugin.getClansController();
        annotationParser.parse(this);
    }


    @Permission("easyclans.command.trophy.removeclan")
    @Command("trophy removeclan <trophy_name> <clan_name>")
    public void trophyRemoveClanCommand(
            final CommandSender sender,
            @Argument(value = "trophy_name", suggestions = "trophy_name") final @NonNull String name,
            @Argument(value = "clan_name", suggestions = "clan_name") final @NonNull String clanName
    ){

        var clan = clansController.getClan(clanName);
        if(clan == null){
            sender.sendMessage(MM.deserialize(LanguageController.getLocalized("invalid_clan")));
            return;
        }

        var existingTrophy = tropyController.getTrophy(name);
        if(existingTrophy == null){
            sender.sendMessage(MM.deserialize(LanguageController.getLocalized("trophy_not_exists")));
            return;
        }

        if(tropyController.removeClanFromTrophy(existingTrophy, clan)){
            sender.sendMessage(MM.deserialize(LanguageController.getLocalized("trophy_clan_removed")));
        }else{
            sender.sendMessage(MM.deserialize(LanguageController.getLocalized("trophy_clan_remove_failed")));
        }
    }


    @Permission("easyclans.command.trophy.removeclan")
    @Command("trophy removeposition <trophy_name> <position>")
    public void trophyRemovePositionCommand(
            final CommandSender sender,
            @Argument(value = "trophy_name", suggestions = "trophy_name") final @NonNull String name,
            @Argument(value = "position", suggestions = "trophy_positions") final int position
    ){


        var existingTrophy = tropyController.getTrophy(name);
        if(existingTrophy == null){
            sender.sendMessage(MM.deserialize(LanguageController.getLocalized("trophy_not_exists")));
            return;
        }
        var cTrophy = existingTrophy.getTrophy(position);
        if(cTrophy == null){
            sender.sendMessage(MM.deserialize(LanguageController.getLocalized("trophy_position_not_exists")));
            return;
        }
        var clan = clansController.getClan(cTrophy.getClanID());


        if(tropyController.removeClanFromTrophy(existingTrophy, clan)){
            sender.sendMessage(MM.deserialize(LanguageController.getLocalized("trophy_clan_removed")));
        }else{
            sender.sendMessage(MM.deserialize(LanguageController.getLocalized("trophy_clan_remove_failed")));
        }
    }



    @Suggestions("trophy_name")
    public List<String> getTrophyNames(CommandContext<CommandSender> sender, String input){
        return plugin.getTropyController().getTrophyList().stream().map(Trophy::getName).filter(name -> name.contains(input)).collect(Collectors.toList());
    }

    @Suggestions("trophy_positions")
    public List<String> getTrophyPositions(CommandContext<CommandSender> sender, String input){
        var name = sender.getOrDefault("trophy_name", "");
        if(!name.isBlank()){
            var existingTrophy = tropyController.getTrophy(name);
            return existingTrophy.getOrganizedTrophies().keySet().stream().map(String::valueOf).filter(num -> num.contains(input)).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @Suggestions("clan_name")
    public List<String> getClanNames(CommandContext<CommandSender> sender, String input){
        return plugin.getClansController().getClans().stream().map(Clan::getName).filter(name -> name.contains(input)).collect(Collectors.toList());
    }
}

