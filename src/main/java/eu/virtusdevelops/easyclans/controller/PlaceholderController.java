package eu.virtusdevelops.easyclans.controller;

import eu.virtusdevelops.easyclans.models.CPlayer;
import eu.virtusdevelops.easyclans.models.Clan;
import eu.virtusdevelops.easyclans.utils.Formatter;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import eu.virtusdevelops.easyclans.ClansPlugin;
import org.bukkit.OfflinePlayer;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PlaceholderController extends PlaceholderExpansion {
    private ClansPlugin plugin;
    private PlayerController playerController;
    private ClansController clansController;
    private SimpleDateFormat dateFormat;


    public PlaceholderController(ClansPlugin plugin, PlayerController playerController, ClansController clansController){
        this.plugin = plugin;
        this.playerController = playerController;
        this.clansController = clansController;
        Locale loc = new Locale(plugin.getConfig().getString("language.language"), plugin.getConfig().getString("language.country"));
        dateFormat = new SimpleDateFormat(LanguageController.getLocalized("time_format"), loc);

    }

    @Override
    public String getIdentifier() {
        return "easyclans";
    }

    @Override
    public String getAuthor() {
        return "NeutralPlasma";
    }

    @Override
    public String getVersion() {
        return plugin.getVersion();
    }

    @Override
    public boolean persist() {
        return true; // This is required or else PlaceholderAPI will unregister the Expansion on reload
    }



    @Override
    public String onRequest(OfflinePlayer offlinePlayer, String ident){
        if(ident.isBlank() || ident.isEmpty()) return null;
        CPlayer cPlayer = playerController.getPlayer(offlinePlayer.getUniqueId());
        Clan clan = clansController.getClan(cPlayer.getClanID());
        String[] ident2 = ident.split(":");
        switch(ident2[0].toLowerCase()){
            case "tag": {
                if(clan == null) return "";
                return clan.getTag();
            }
            case "clan": {
                if(clan == null) return "";
                return clan.getName();
            }
            case "currency": {
                if(clan == null) return "";
                var currency = clan.getCurrency(ident2[1]);
                if(currency == null) return "";
                return Formatter.formatMoney(currency.getValue());
            }
            case "currency_raw": {
                if(clan == null) return "";
                var currency = clan.getCurrency(ident2[1]);
                if(currency == null) return "";
                return String.valueOf(currency.getValue());
            }
            case "clan_interest": {
                if(clan == null) return "";
                return String.format("%.5f", clan.getInterestRate() + clan.getActualInterestRate());
            }
            case "clan_interest_raw": {
                if(clan == null) return "";
                return String.valueOf(clan.getInterestRate() + clan.getActualInterestRate());
            }
            case "join_date": {
                if(clan == null) return "";
                var date = new Date(cPlayer.getJoinClanDate());
                return dateFormat.format(date);
            }
            case "join_date_raw": {
                if(clan == null) return "";
                return String.valueOf(cPlayer.getJoinClanDate());
            }

        }


        return null;
    }
}
