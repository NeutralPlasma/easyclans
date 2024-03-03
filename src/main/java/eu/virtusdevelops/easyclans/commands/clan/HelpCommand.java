package eu.virtusdevelops.easyclans.commands.clan;

import eu.virtusdevelops.easyclans.ClansPlugin;
import eu.virtusdevelops.easyclans.commands.AbstractCommand;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.annotations.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HelpCommand implements AbstractCommand {
    private ClansPlugin plugin;

    @Override
    public void registerFeature(@NonNull ClansPlugin plugin, @NotNull AnnotationParser<CommandSender> annotationParser) {
        this.plugin = plugin;
        annotationParser.parse(this);
    }

    @Permission("easyclans.command.help")
    @Command("clan help [query]")
    public void helpCommand(
            final CommandSender sender,
            @Argument("query") @Nullable final String[] query
    ){

        plugin.getMinecraftHelp().queryCommands(query != null ? String.join(" ", query) : "", sender);

    }

    @Permission("easyclans.command.reload")
    @Command("clan reload")
    public void reloadCommand(
            final CommandSender sender
    ){
        long start = System.currentTimeMillis();
        plugin.reload();
        long ms = System.currentTimeMillis() - start;
        sender.sendMessage(ClansPlugin.MM.deserialize("<green>Sucessfully reloaded plugin took: <yellow>" + ms + "<green>ms!"));
    }



}
