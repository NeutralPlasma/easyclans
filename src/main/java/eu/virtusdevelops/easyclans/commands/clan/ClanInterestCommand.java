package eu.virtusdevelops.easyclans.commands.clan;

import eu.virtusdevelops.easyclans.ClansPlugin;
import eu.virtusdevelops.easyclans.commands.AbstractFeature;
import eu.virtusdevelops.easyclans.controller.LanguageController;
import eu.virtusdevelops.easyclans.gui.ui.ConfirmGUI;
import eu.virtusdevelops.easyclans.models.Clan;
import net.kyori.adventure.sound.Sound;
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

public class ClanInterestCommand implements AbstractFeature {

    private ClansPlugin plugin;

    @Override
    public void registerFeature(@NonNull ClansPlugin plugin, @NotNull AnnotationParser<CommandSender> annotationParser) {
        this.plugin = plugin;
        annotationParser.parse(this);
    }


    @Permission("easyclans.command.setinterest")
    @Command("clan setinterest <clan_name> <interest>")
    public void setClanInterestCommand(
            final CommandSender sender,
            @Argument(value = "clan_name", suggestions = "clan_name") final @NonNull String name,
            @Argument(value = "interest") final double interest
    ){
        var clan = plugin.getClansController().getClan(name);
        if(clan == null){
            sender.sendMessage(MM.deserialize(LanguageController.getLocalized("invalid_clan")));
            return;
        }
        clan.setInterestRate(interest);
        sender.sendMessage(MM.deserialize(LanguageController.getLocalized("updated_interest")
                .replace("{interest}", interest + "")
        ));
        plugin.getClansController().updateClan(clan);
    }


    @Suggestions("clan_name")
    public List<String> getClanNames(CommandContext<CommandSender> sender, String input){
        return plugin.getClansController().getClans().stream().map(Clan::getName).filter(name -> name.contains(input)).collect(Collectors.toList());

    }
}
