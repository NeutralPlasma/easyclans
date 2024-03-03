package eu.virtusdevelops.easyclans.commands.trophy;

import eu.virtusdevelops.easyclans.ClansPlugin;
import eu.virtusdevelops.easyclans.commands.AbstractCommand;
import eu.virtusdevelops.easyclans.controller.LanguageController;
import eu.virtusdevelops.easyclans.controller.TropyController;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.annotations.AnnotationParser;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;
import org.jetbrains.annotations.NotNull;

import static eu.virtusdevelops.easyclans.ClansPlugin.MM;

public class TropyCreateCommand implements AbstractCommand {

    private ClansPlugin plugin;
    private TropyController tropyController;

    @Override
    public void registerFeature(@NonNull ClansPlugin plugin, @NotNull AnnotationParser<CommandSender> annotationParser) {
        this.plugin = plugin;
        this.tropyController = plugin.getTropyController();
        annotationParser.parse(this);
    }


    @Permission("easyclans.command.trophy.create")
    @Command("trophy create <trophy_name> <trophy_title> <description>")
    public void trophyCreateCommand(
            final CommandSender sender,
            @Argument(value = "trophy_name") final @NonNull String name,
            @Argument(value = "trophy_title") final @NonNull String title,
            @Argument(value = "description") final @NonNull String[] description
    ){
        var newTitle = title.replace("_", " ");
        var existingTrophy = tropyController.getTrophy(name);

        if(existingTrophy != null){
            sender.sendMessage(MM.deserialize(LanguageController.getLocalized("trophy_exists")));
            return;
        }
        var trophy = tropyController.createTrophy(name, newTitle, String.join(" " , description), System.currentTimeMillis(), System.currentTimeMillis());


        sender.sendMessage(MM.deserialize(LanguageController.getLocalized("trophy_created")
                .replace("{trophy}", trophy.getTitle())
        ));

    }

}

