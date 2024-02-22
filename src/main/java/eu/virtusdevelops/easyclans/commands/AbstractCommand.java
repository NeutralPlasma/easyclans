package eu.virtusdevelops.easyclans.commands;

import eu.virtusdevelops.easyclans.ClansPlugin;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.annotations.AnnotationParser;

public interface AbstractCommand {
    void registerFeature(
            @NonNull ClansPlugin examplePlugin,
            @NonNull AnnotationParser<CommandSender> annotationParser
    );
}
