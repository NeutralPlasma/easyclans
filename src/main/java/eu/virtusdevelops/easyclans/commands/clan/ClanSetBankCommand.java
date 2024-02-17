package eu.virtusdevelops.easyclans.commands.clan;

import eu.virtusdevelops.easyclans.ClansPlugin;
import eu.virtusdevelops.easyclans.commands.AbstractFeature;
import eu.virtusdevelops.easyclans.controller.LanguageController;
import eu.virtusdevelops.easyclans.models.Clan;
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

public class ClanSetBankCommand implements AbstractFeature {

    private ClansPlugin plugin;

    @Override
    public void registerFeature(@NonNull ClansPlugin plugin, @NotNull AnnotationParser<CommandSender> annotationParser) {
        this.plugin = plugin;
        annotationParser.parse(this);
    }


    @Permission("easyclans.command.setbank")
    @Command("clan setbank <clan_name> <bank_name> <value>")
    public void setClanInterestCommand(
            final CommandSender sender,
            @Argument(value = "clan_name", suggestions = "clan_name") final @NonNull String name,
            @Argument(value = "bank_name", suggestions = "bank_name") final @NonNull String bankName,
            @Argument(value = "value") final double value
    ){
        var clan = plugin.getClansController().getClan(name);
        if(clan == null){
            sender.sendMessage(MM.deserialize(LanguageController.getLocalized("invalid_clan")));
            return;
        }

        var provider = plugin.getCurrenciesController().getProvider(bankName);

        if(provider == null){
            sender.sendMessage(MM.deserialize(LanguageController.getLocalized("invalid_bank")));
            return;
        }

        for(var currency : clan.getCurrencies()){
            if(currency.getName().equals(bankName)){
                currency.setValue(value);
            }
        }

        sender.sendMessage(MM.deserialize(LanguageController.getLocalized("updated_bank")
                .replace("{value}", value + "")
                .replace("{bank}", bankName)
        ));
        plugin.getClansController().updateClan(clan);
    }


    @Suggestions("clan_name")
    public List<String> getClanNames(CommandContext<CommandSender> sender, String input){
        return plugin.getClansController().getClans().stream().map(Clan::getName).filter(name -> name.contains(input)).collect(Collectors.toList());

    }

    @Suggestions("bank_name")
    public List<String> getBankNames(CommandContext<CommandSender> sender, String input){
        return plugin.getCurrenciesController().getCurrencyProviders().keySet().stream().filter(name -> name.contains(input)).collect(Collectors.toList());
    }
}
