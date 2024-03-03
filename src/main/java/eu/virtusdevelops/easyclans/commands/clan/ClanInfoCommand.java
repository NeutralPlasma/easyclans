package eu.virtusdevelops.easyclans.commands.clan;

import eu.virtusdevelops.easyclans.ClansPlugin;
import eu.virtusdevelops.easyclans.commands.AbstractCommand;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.annotations.AnnotationParser;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;
import org.jetbrains.annotations.NotNull;

import static eu.virtusdevelops.easyclans.ClansPlugin.MM;

public class ClanInfoCommand implements AbstractCommand {
    private ClansPlugin plugin;

    @Override
    public void registerFeature(@NonNull ClansPlugin plugin, @NotNull AnnotationParser<CommandSender> annotationParser) {
        this.plugin = plugin;
        annotationParser.parse(this);
    }

    @Permission("easyclans.command.info")
    @Command("clan info")
    public void debugCommand(
            final CommandSender sender
    ){

        sender.sendMessage(MM.deserialize("<yellow>EasyClans <gray>(<gold>" + plugin.getPluginMeta().getVersion() + "<gray>)"));
        sender.sendMessage(MM.deserialize("<gold>Enabled economy providers: "));
        for(var provider : plugin.getCurrenciesController().getCurrencyProviders().values()){
            sender.sendMessage(MM.deserialize("<gray>- <yellow>" + provider.getPluginName() + " <gray> -> <yellow>" + provider.getVersion()));
        }

    }


}
