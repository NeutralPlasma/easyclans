package eu.virtusdevelops.easyclans.commands.clan;

import eu.virtusdevelops.easyclans.ClansPlugin;
import eu.virtusdevelops.easyclans.commands.AbstractFeature;
import eu.virtusdevelops.easyclans.controller.LanguageController;
import eu.virtusdevelops.easyclans.gui.ui.ConfirmGUI;
import net.kyori.adventure.sound.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.annotations.AnnotationParser;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;
import org.jetbrains.annotations.NotNull;

import static eu.virtusdevelops.easyclans.ClansPlugin.MM;
import static net.kyori.adventure.key.Key.key;
import static net.kyori.adventure.sound.Sound.sound;

public class ClanChatCommand implements AbstractFeature {

    private ClansPlugin plugin;

    @Override
    public void registerFeature(@NonNull ClansPlugin plugin, @NotNull AnnotationParser<CommandSender> annotationParser) {
        this.plugin = plugin;
        annotationParser.parse(this);
    }

    @Permission("easyclans.command.chat")
    @Command("clan chat")
    public void clanDeleteCommand(
            final CommandSender sender
    ){

        if(!(sender instanceof Player player)){
            sender.sendMessage(MM.deserialize(LanguageController.getLocalized("player_only")));
            return;
        }

        var cPlayer = plugin.getPlayerController().getPlayer(player.getUniqueId());
        var clan = plugin.getClansController().getClan(cPlayer.getClanID());

        if(clan == null){
            sender.sendMessage(MM.deserialize(LanguageController.getLocalized("not_in_clan")));
            return;
        }


        if (cPlayer.isInClubChat()) {
            player.sendMessage(MM.deserialize(LanguageController.getLocalized("clan.chat.leave_chat")));
        } else {
            player.sendMessage(MM.deserialize(LanguageController.getLocalized("clan.chat.join_chat")));
        }
        cPlayer.setInClubChat(!cPlayer.isInClubChat());

    }





}
