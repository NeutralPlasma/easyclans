package eu.virtusdevelops.easyclans.commands.clan;

import eu.virtusdevelops.easyclans.ClansPlugin;
import eu.virtusdevelops.easyclans.commands.AbstractFeature;
import eu.virtusdevelops.easyclans.controller.LanguageController;
import eu.virtusdevelops.easyclans.gui.ui.ClanCreateMenu;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.annotations.*;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

import static eu.virtusdevelops.easyclans.ClansPlugin.MM;

public class ClanCreateCommand implements AbstractFeature {
    private ClansPlugin plugin;


    @Override
    public void registerFeature(@NonNull ClansPlugin plugin, @NotNull AnnotationParser<CommandSender> annotationParser) {
        this.plugin = plugin;
        annotationParser.parse(this);
    }


    @Permission("easyclans.command.create")
    @Command("clan create [name]")

    public void clanCreateCommand(
            final CommandSender sender,
            @Argument("name") final @Nullable String name
            ){

        if(!(sender instanceof Player player)){
            sender.sendMessage(MM.deserialize(LanguageController.getLocalized("player_only")));
            return;
        }

        var cplayer = plugin.getPlayerController().getPlayer(player.getUniqueId());
        var clan = plugin.getClansController().getClan(cplayer.getClanID());
        if(clan != null){
            sender.sendMessage(MM.deserialize(LanguageController.getLocalized("already_in_clan")));
            return;
        }
        new ClanCreateMenu(player, plugin, name);
    }


}
