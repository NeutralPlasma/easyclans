package eu.virtusdevelops.easyclans.controller;

import eu.virtusdevelops.easyclans.ClansPlugin;
import eu.virtusdevelops.easyclans.models.Clan;
import eu.virtusdevelops.easyclans.models.Currency;
import eu.virtusdevelops.easyclans.models.Log;
import eu.virtusdevelops.easyclans.models.LogType;
import eu.virtusdevelops.easyclans.storage.SQLStorage;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class ClansController {
    private PlayerController playerController;
    private CurrenciesController currenciesController;
    private final Map<Integer, Clan> clans;
    private final ClansPlugin plugin;
    private final SQLStorage sqlStorage;

    public ClansController(ClansPlugin plugin, SQLStorage sqlStorage, PlayerController playerController,
                           CurrenciesController currenciesController) {
        this.plugin = plugin;
        this.sqlStorage = sqlStorage;
        this.playerController = playerController;
        this.currenciesController = currenciesController;
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
                    var newCurrency = new eu.virtusdevelops.easyclans.models.Currency(-1, 0.0, currency, clan.getId());
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

    /**
     * Adds a new clan to the clan list.
     *
     * @param owner                the UUID of the player who owns the clan.
     * @param name                 the name of the clan.
     * @param displayName          the display name of the clan.
     * @param autoKickTime         the auto-kick time for inactive members.
     * @param joinPointsPrice      the points price for joining the clan.
     * @param joinMoneyPrice       the money price for joining the clan.
     * @param banner               the banner item for the clan.
     * @param bank                 the initial bank balance of the clan.
     * @param interestRate         the interest rate
     * @param tag                  the tag associated with the clan.
     * @param members              the list of UUIDs of clan members.
     */
    public Clan createClan(UUID owner, String name, String displayName, int autoKickTime,
                           int joinPointsPrice, double joinMoneyPrice,
                           ItemStack banner, double bank, double interestRate, String tag, List<UUID> members) {

        Clan clan = new Clan(-1, owner, name, displayName, autoKickTime, joinPointsPrice, joinMoneyPrice,
                banner, bank, interestRate, tag, members, System.currentTimeMillis());

        for(var currency : currenciesController.getCurrencyProviders().keySet()){
            var newCurrency = new Currency(-1, 0.0, currency, clan.getId());
            clan.addCurrency(newCurrency);
        }

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            if(sqlStorage.saveClan(clan)){
                sqlStorage.saveClan(clan);
                // if failed again then send error blabla
            }
            var player = playerController.getPlayer(clan.getOwner());
            player.setClanID(clan.getId());
            player.setJoinClanDate(System.currentTimeMillis());
            playerController.updatePlayer(player);
            addClan(clan);
        });
        return clan;
    }

    public void deleteClan(int id){
        if(!clans.containsKey(id)) return;
        Clan clan = clans.get(id);
        clans.remove(id);
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            for(var uuid : clan.getMembers()){
                var player = playerController.getPlayer(uuid);
                player.setClanID(-1);
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
    public Clan getClan(int id) {
        if(id == -1) return null;
        if (clans.containsKey(id))
            return clans.get(id);
        return null;
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
            if(player.getClanID() == -1) continue;
            var clan = clans.get(player.getClanID());
            if(clan == null){
                player.setClanID(-1);
                playerController.updatePlayer(player);
                return;
            }

            // add interest rate based on players rank.
            double interestToAdd = plugin.getConfig().getDouble("rank_interest_value." + player.getRank());
            if(!Double.isNaN(interestToAdd) && interestToAdd > 0){
                clan.addTempInterestRate(interestToAdd);
            }


            if(clan.getOwner().equals(player.getUuid())) continue;
            if(System.currentTimeMillis() - player.getLastActive() > clan.getAutoKickTime()){
                player.setClanID(-1);
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


            var cOwner = playerController.getPlayer(clan.getOwner());
            if(System.currentTimeMillis() - cOwner.getLastActive() > 7 * 24 * 60 * 60 * 1000){
                sqlStorage.addLog(new Log("interest:reset:" + cOwner.getLastActive(), null, clan.getId(), LogType.INTEREST_RESET, System.currentTimeMillis()));
                clan.setInterestRate(0);
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
                        sqlStorage.addLog(new Log("currency:" + currency.getName() + ":" + toAdd , null, clan.getId(), LogType.MONEY_ADD, System.currentTimeMillis()));
                    }
                }
            }
            sqlStorage.updateClan(clan);
        }
    }
}
