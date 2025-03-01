package eu.virtusdevelops.easyclans.controller;

import eu.virtusdevelops.easyclans.ClansPlugin;
import eu.virtusdevelops.easyclans.dao.ClanDao;
import eu.virtusdevelops.easyclans.dao.CurrencyDao;
import eu.virtusdevelops.easyclans.dao.LogDao;
import eu.virtusdevelops.easyclans.models.*;
import eu.virtusdevelops.easyclans.models.Currency;

import java.util.*;

public class ClansController {
    private final PlayerController playerController;
    private final CurrenciesController currenciesController;
    private final RanksController ranksController;
    private final LogController logController;
    private final Map<UUID, Clan> clans;
    private final ClansPlugin plugin;
    private final ClanDao clanDao;

    public ClansController(ClansPlugin plugin, PlayerController playerController,
                           CurrenciesController currenciesController, RanksController ranksController,
                           ClanDao clanDao, LogController logController) {
        this.plugin = plugin;
        this.playerController = playerController;
        this.currenciesController = currenciesController;
        this.ranksController = ranksController;
        this.clanDao = clanDao;
        this.logController = logController;
        this.clans = new HashMap<>();

        loadClans();
    }

    private void loadClans() {

        // load all clans
        // verify they have all the currencies supported.

        for(var clan : clanDao.getAll()){
            clans.put(clan.getId(), clan);
            setupClanCurrencies(clan);
        }
    }


    private void setupClanCurrencies(Clan clan) {

        for(var currency : currenciesController.getCurrencyProviders().keySet()){
            boolean has = false;

            for(var clanCurrency : clan.getCurrencies())
                if(clanCurrency.getName().equals(currency)) { has = true; break; }

            if(!has){
                var newCurrency = new Currency(UUID.randomUUID(), 0.0, currency, clan.getId());
                clan.addCurrency(newCurrency);
                currenciesController.saveCurrency(newCurrency);
            }
        }
    }




    public Clan createClan(Clan clan){
        setupClanCurrencies(clan);



        ClansPlugin.getExecutor().submit(() -> {
            var saved = clanDao.save(clan);

            // update player
            if(saved != null) {
                var player = playerController.getPlayer(saved.getOwner());
                if(player != null) {
                    player.setClanID(saved.getId());
                    player.setJoinClanDate(System.currentTimeMillis());
                    playerController.updatePlayer(player);
                }
                addClan(saved);
            }
        });


        return clan;
    }


    private void addClan(Clan clan) {
        clans.put(clan.getId(), clan);
    }

    public void deleteClan(Clan clan) {
        clans.remove(clan.getId());
        ClansPlugin.getExecutor().submit(() -> {
            for(var uuid : clan.getMembers()){
                var player = playerController.getPlayer(uuid);
                player.setClanID(null);
                playerController.updatePlayer(player);
            }
            clanDao.delete(clan);
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
        ClansPlugin.getExecutor().submit(() -> {
            clanDao.save(clan);
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

                logController.addLog(new Log("Inactivity kick", player.getUuid(), clan.getId(), LogType.AUTO_KICK));
                playerController.updatePlayer(player);
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
                logController.addLog(new Log("interest:max:" + toAddInterest, null, clan.getId(), LogType.INTEREST_ADD));
            }else{
                clan.setInterestRate(clan.getInterestRate()+toAddInterest);
                logController.addLog(new Log("interest:add:" + toAddInterest, null, clan.getId(), LogType.INTEREST_ADD));
            }


            var cOwner = playerController.getPlayer(clan.getOwner());
            if(System.currentTimeMillis() - cOwner.getLastActive() > 7 * 24 * 60 * 60 * 1000){
                logController.addLog(new Log("interest:reset:" + cOwner.getLastActive(), null, clan.getId(), LogType.INTEREST_RESET));
                clan.setInterestRate(0);
                clan.resetTempInterestRate();
                clan.updateActualInterestRate();
            }


            double interestFull = clan.getActualInterestRate() + clan.getInterestRate();
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
                        logController.addLog(new Log("currency:" + currency.getName() + ":" + toAdd , null, clan.getId(), LogType.MONEY_ADD));
                    }
                }
            }
            updateClan(clan);
        }
    }
}
