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

public class ClanDeleteCommand implements AbstractFeature {

    private ClansPlugin plugin;

    @Override
    public void registerFeature(@NonNull ClansPlugin plugin, @NotNull AnnotationParser<CommandSender> annotationParser) {
        this.plugin = plugin;
        annotationParser.parse(this);
    }

    @Permission("easyclans.command.delete")
    @Command("clan delete")
    public void clanDeleteCommand(
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

        if(!clan.getOwner().equals(player.getUniqueId())){
            sender.sendMessage(MM.deserialize(LanguageController.getLocalized("not_owner")));
            return;
        }

        new ConfirmGUI(player, (t) -> {
            // confirm
            player.playSound(sound(key("block.note_block.cow_bell"), Sound.Source.MASTER, 1f, 1.19f));
            plugin.getClansController().deleteClan(clan.getId());
            player.closeInventory();

        }, (t) -> {
            // decline
            player.playSound(sound(key("block.note_block.didgeridoo"), Sound.Source.MASTER, 1f, 1.19f));
            player.closeInventory();

        }, LanguageController.getLocalized("delete_clan.title"));

    }

    @Permission("easyclans.command.delete.admin")
    @Command("clan delete <clan_name>")
    public void adminClanDeleteCommand(
            final CommandSender sender,
            @Argument(value = "clan_name", suggestions = "clan_name") final @NonNull String name
    ){
        var clan = plugin.getClansController().getClan(name);
        if(clan == null){
            sender.sendMessage(MM.deserialize(LanguageController.getLocalized("invalid_clan")));
            return;
        }


        if(sender instanceof Player player){

            new ConfirmGUI(player, (t) -> {
                // confirm
                player.playSound(sound(key("block.note_block.cow_bell"), Sound.Source.MASTER, 1f, 1.19f));
                plugin.getClansController().deleteClan(clan.getId());
                player.closeInventory();

            }, (t) -> {
                // decline
                player.playSound(sound(key("block.note_block.didgeridoo"), Sound.Source.MASTER, 1f, 1.19f));
                player.closeInventory();

            }, LanguageController.getLocalized("delete_clan.title"));

        }else{
            plugin.getClansController().deleteClan(clan.getId());
        }
    }


    @Suggestions("clan_name")
    public List<String> getClanNames(CommandContext<CommandSender> sender, String input){
        return plugin.getClansController().getClans().stream().map(Clan::getName).filter(name -> name.contains(input)).collect(Collectors.toList());

    }
}
