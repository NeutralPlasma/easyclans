package eu.virtusdevelops.easyclans.controller;

import eu.virtusdevelops.easyclans.ClansPlugin;
import eu.virtusdevelops.easyclans.models.*;
import eu.virtusdevelops.easyclans.models.Currency;
import eu.virtusdevelops.easyclans.storage.SQLStorage;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class ClansController {
    private PlayerController playerController;
    private CurrenciesController currenciesController;
    private RanksController ranksController;
    private final Map<UUID, Clan> clans;
    private final ClansPlugin plugin;
    private final SQLStorage sqlStorage;

    public ClansController(ClansPlugin plugin, SQLStorage sqlStorage, PlayerController playerController,
                           CurrenciesController currenciesController, RanksController ranksController) {
        this.plugin = plugin;
        this.sqlStorage = sqlStorage;
        this.playerController = playerController;
        this.currenciesController = currenciesController;
        this.ranksController = ranksController;
        this.clans = new HashMap<>();

        loadClans();
    }

    private void loadClans() {
        for (var clan : sqlStorage.getAllClans()) {
            for(var currency : currenciesController.getCurrencyProviders().keySet()){
                boolean has = false;
                for(var clanCurrency : clan.getCurrencies()){
                    if(clanCurrency.getName().equals(currency))
                        has = true;
                }
                if(!has){
                    var newCurrency = new eu.virtusdevelops.easyclans.models.Currency(UUID.randomUUID(), 0.0, currency, clan.getId());
                    clan.addCurrency(newCurrency);
                    sqlStorage.insertSingleClanCurrency(newCurrency);


                }

            }


            this.clans.put(clan.getId(), clan);

        }
    }


    private void addClan(Clan clan) {
        clans.put(clan.getId(), clan);
    }


    public Clan createClan(UUID owner, String name, String displayName, int autoKickTime,
                           int joinPointsPrice, double joinMoneyPrice,
                           ItemStack banner, double interestRate, String tag, List<UUID> members) {

        Clan clan = new Clan(owner, name, displayName, autoKickTime, joinPointsPrice, joinMoneyPrice,
                banner, interestRate, tag, members, false, System.currentTimeMillis());

        for(var currency : currenciesController.getCurrencyProviders().keySet()){
            var newCurrency = new Currency(UUID.randomUUID(), 0.0, currency, clan.getId());
            clan.addCurrency(newCurrency);
        }

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            if(sqlStorage.saveClan(clan)){
                var player = playerController.getPlayer(clan.getOwner());
                player.setClanID(clan.getId());
                player.setJoinClanDate(System.currentTimeMillis());
                playerController.updatePlayer(player);
                addClan(clan);
            }else{
                // FAILED inserting new clan into database notify.
                var player = playerController.getPlayer(clan.getOwner());
                var oPlayer = player.tryGetPlayer();
                if(oPlayer!= null) {
                    oPlayer.closeInventory();
                }
            }
        });
        return clan;
    }

    public void deleteClan(UUID id){
        if(!clans.containsKey(id)) return;
        Clan clan = clans.get(id);
        clans.remove(id);
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            for(var uuid : clan.getMembers()){
                var player = playerController.getPlayer(uuid);
                player.setClanID(null);
                playerController.updatePlayer(player);
            }
            sqlStorage.deleteClan(clan);
        });

    }

    /**
     * Retrieves a clan object by its id.
     *
     * @param id the id of the clan you want to retrieve.
     * @return the clan object associated with the provided id, or null if
     *         no clan with the given id is found.
     */
    public Clan getClan(UUID id) {
        if(id == null) return null;
        if (clans.containsKey(id))
            return clans.get(id);
        return null;
    }

    public Clan getClan(String name){
        if(name.isEmpty()) return null;
        return clans.values().stream().filter(it -> it.getName().equals(name)).findFirst().orElse(null);
    }

    public void updateClan(Clan clan){
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            sqlStorage.updateClan(clan);
        });

    }

    public List<Clan> getClans() {
        return clans.values().stream().toList();
    }


    public void processClans(){


        for(var player : playerController.getPlayers()){
            if(player.getClanID() == null) continue;
            var clan = clans.get(player.getClanID());
            if(clan == null){
                player.setClanID(null);
                playerController.updatePlayer(player);
                return;
            }

            // add interest rate based on players rank.
            RankMultiplyer rankMultiplyer = ranksController.getRank(player);
            double interestToAdd = rankMultiplyer.getMultiplier();
            if(!Double.isNaN(interestToAdd) && interestToAdd > 0){
                clan.addTempInterestRate(interestToAdd);
            }


            if(clan.getOwner().equals(player.getUuid())) continue;
            if(System.currentTimeMillis() - player.getLastActive() > clan.getAutoKickTime()){
                player.setClanID(null);
                // TODO: send kick notification
                sqlStorage.addLog(new Log("Inactivity kick", player.getUuid(), clan.getId(), LogType.AUTO_KICK, System.currentTimeMillis()));
                sqlStorage.updatePlayer(player);
            }
        }


        // interest update blabla
        for(var clan : clans.values()){
            clan.updateActualInterestRate();
            clan.resetTempInterestRate();

            double toAddInterest = plugin.getConfig().getDouble("clan.interest_rate_increase");
            double max = plugin.getConfig().getDouble("clan.max_interest_rate");
            if(clan.getInterestRate() + toAddInterest >= max){
                clan.setInterestRate(max);
                sqlStorage.addLog(new Log("interest:max:" + toAddInterest, null, clan.getId(), LogType.INTEREST_ADD, System.currentTimeMillis()));
            }else{
                clan.setInterestRate(clan.getInterestRate()+toAddInterest);
                sqlStorage.addLog(new Log("interest:add:" + toAddInterest, null, clan.getId(), LogType.INTEREST_ADD, System.currentTimeMillis()));
            }


            boolean reset = false;
            var cOwner = playerController.getPlayer(clan.getOwner());
            if(System.currentTimeMillis() - cOwner.getLastActive() > 7 * 24 * 60 * 60 * 1000){
                sqlStorage.addLog(new Log(
                        "interest:reset:" + cOwner.getLastActive(),
                        null,
                        clan.getId(),
                        LogType.INTEREST_RESET,
                        System.currentTimeMillis())
                );

                clan.setInterestRate(0);
                clan.resetTempInterestRate();
                clan.updateActualInterestRate();
                reset = true;
            }


            double interestFull = reset ? 0.0 : clan.getActualInterestRate() + clan.getInterestRate();
            if(interestFull != 0){
                for(var currency: clan.getCurrencies()){
                    if(currency.getValue() == 0) continue;
                    double interestMultiplier = currency.getValue();
                    if(currency.getValue() > plugin.getConfig().getDouble("currencies_max_interest_adding." + currency.getName())){
                        interestMultiplier = plugin.getConfig().getDouble("currencies_max_interest_adding." + currency.getName());
                    }
                    var toAdd = interestMultiplier * (interestFull/100);
                    if(toAdd > 0){
                        currency.addValue(toAdd);
                        sqlStorage.addLog(new Log("currency:" + currency.getName() + ":" + toAdd , null, clan.getId(), LogType.MONEY_ADD, System.currentTimeMillis()));
                    }
                }
            }
            sqlStorage.updateClan(clan);
        }
    }
}
