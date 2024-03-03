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

import java.util.List;
import java.util.stream.Collectors;

import static eu.virtusdevelops.easyclans.ClansPlugin.MM;

public class TropyAddClanCommand implements AbstractCommand {

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


    @Permission("easyclans.command.trophy.addclan")
    @Command("trophy addclan <trophy_name> <clan_name> <position>")
    public void trophyAddClanCommand(
            final CommandSender sender,
            @Argument(value = "trophy_name", suggestions = "trophy_name") final @NonNull String name,
            @Argument(value = "clan_name", suggestions = "clan_name") final @NonNull String clanName,
            @Argument(value = "position") final int position
    ){
        if(position <= 0){
            sender.sendMessage(MM.deserialize(LanguageController.getLocalized("invalid_position")));
            return;
        }

        var existingTrophy = tropyController.getTrophy(name);
        if(existingTrophy == null){
            sender.sendMessage(MM.deserialize(LanguageController.getLocalized("trophy_not_exists")));
            return;
        }
        var clan = clansController.getClan(clanName);
        if(clan == null){
            sender.sendMessage(MM.deserialize(LanguageController.getLocalized("invalid_clan")));
            return;
        }

        if(tropyController.addClanToTrophy(existingTrophy,  clan, position, System.currentTimeMillis())){
            sender.sendMessage(MM.deserialize(LanguageController.getLocalized("trophy_clan_added")));
        }else{
            sender.sendMessage(MM.deserialize(LanguageController.getLocalized("trophy_clan_add_failed")));
        }
    }



    @Suggestions("trophy_name")
    public List<String> getTrophyNames(CommandContext<CommandSender> sender, String input){
        return plugin.getTropyController().getTrophyList().stream().map(Trophy::getName).filter(name -> name.contains(input)).collect(Collectors.toList());
    }

    @Suggestions("clan_name")
    public List<String> getClanNames(CommandContext<CommandSender> sender, String input){
        return plugin.getClansController().getClans().stream().map(Clan::getName).filter(name -> name.contains(input)).collect(Collectors.toList());
    }
}

