package eu.virtusdevelops.easyclans.commands;

import eu.virtusdevelops.easyclans.ClansPlugin;
import eu.virtusdevelops.easyclans.commands.clan.*;
import eu.virtusdevelops.easyclans.commands.trophy.TropyAddClanCommand;
import eu.virtusdevelops.easyclans.commands.trophy.TropyCreateCommand;
import eu.virtusdevelops.easyclans.commands.trophy.TropyDeleteCommand;
import eu.virtusdevelops.easyclans.commands.trophy.TropyRemoveClanCommand;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.annotations.AnnotationParser;

import java.util.Arrays;
import java.util.List;

public class CommandsRegister {

    private static final List<AbstractCommand> COMMANDS = Arrays.asList(
            new HelpCommand(),
            new ClanCommand(),
            new ClanCreateCommand(),
            new ClanDeleteCommand(),
            new ClanKickMemberCommand(),
            new ClanJoinCommand(),
            new ClanLeaveCommand(),
            new ClanChatCommand(),
            new ClanAcceptMemberCommand(),
            new ClanLogsCommand(),
            new ClanDebugCommand(),
            new ClanInfoCommand(),
            new ClanInterestCommand(),
            new ClanSetBankCommand(),
            new ClanListCommand(),

            new TropyCreateCommand(),
            new TropyAddClanCommand(),
            new TropyDeleteCommand(),
            new TropyRemoveClanCommand()
    );

    private final ClansPlugin plugin;
    private final AnnotationParser<CommandSender> annotationParser;

    public CommandsRegister(
            final @NonNull ClansPlugin plugin,
            final @NonNull CommandManager<CommandSender> manager
    ){
        this.plugin = plugin;
        this.annotationParser = new AnnotationParser<>(manager, CommandSender.class);

        this.setupCommands();
    }

    private void setupCommands() {
        COMMANDS.forEach(feature -> feature.registerFeature(this.plugin, this.annotationParser));
    }

}
