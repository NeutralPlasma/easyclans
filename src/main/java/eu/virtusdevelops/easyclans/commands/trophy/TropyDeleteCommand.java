package eu.virtusdevelops.easyclans.commands.trophy;

import eu.virtusdevelops.easyclans.ClansPlugin;
import eu.virtusdevelops.easyclans.commands.AbstractCommand;
import eu.virtusdevelops.easyclans.controller.LanguageController;
import eu.virtusdevelops.easyclans.controller.TropyController;
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

public class TropyDeleteCommand implements AbstractCommand {

    private ClansPlugin plugin;
    private TropyController tropyController;

    @Override
    public void registerFeature(@NonNull ClansPlugin plugin, @NotNull AnnotationParser<CommandSender> annotationParser) {
        this.plugin = plugin;
        this.tropyController = plugin.getTropyController();
        annotationParser.parse(this);
    }


    @Permission("easyclans.command.trophy.delete")
    @Command("trophy delete <trophy_name>")
    public void trophyDeleteCommand(
            final CommandSender sender,
            @Argument(value = "trophy_name", suggestions = "trophy_name") final @NonNull String name
    ){
        var existingTrophy = tropyController.getTrophy(name);

        if(existingTrophy == null){
            sender.sendMessage(MM.deserialize(LanguageController.getLocalized("trophy_not_exists")));
            return;
        }

        tropyController.deleteTrophy(existingTrophy);

        //plugin.getSqlStorage().deleteTropyh(existingTrophy);
        sender.sendMessage(MM.deserialize(LanguageController.getLocalized("trophy_deleted")));
    }


    @Suggestions("trophy_name")
    public List<String> getTrophyNames(CommandContext<CommandSender> sender, String input){
        return plugin.getTropyController().getTrophyList().stream().map(Trophy::getName).filter(name -> name.contains(input)).collect(Collectors.toList());
    }

}

