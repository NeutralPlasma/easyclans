package net.astrona.easyclans.controller;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.astrona.easyclans.ClansPlugin;
import net.astrona.easyclans.models.CPlayer;
import net.astrona.easyclans.models.Clan;
import net.astrona.easyclans.utils.Formatter;
import org.bukkit.OfflinePlayer;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PlaceholderController extends PlaceholderExpansion {
    private ClansPlugin plugin;
    private PlayerController playerController;
    private ClansController clansController;
    private RequestsController requestsController;
    private SimpleDateFormat dateFormat;


    public PlaceholderController(ClansPlugin plugin, PlayerController playerController, ClansController clansController, RequestsController requestsController){
        this.plugin = plugin;
        this.playerController = playerController;
        this.clansController = clansController;
        this.requestsController = requestsController;
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

        switch(ident.toLowerCase()){
            case "tag": {
                if(clan == null) return "";
                return clan.getTag();
            }
            case "clan": {
                if(clan == null) return "";
                return clan.getName();
            }
            case "bank": {
                if(clan == null) return "";
                return Formatter.formatMoney(clan.getBank());
            }
            case "bank_raw": {
                if(clan == null) return "";
                return String.valueOf(clan.getBank());
            }
            case "clan_interest": {
                if(clan == null) return "";
                return String.format("%.5f", clan.getInterestRate());
            }
            case "clan_interest_raw": {
                if(clan == null) return "";
                return String.valueOf(clan.getInterestRate());
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
