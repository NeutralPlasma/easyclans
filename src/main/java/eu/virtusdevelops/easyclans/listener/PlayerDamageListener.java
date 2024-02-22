package eu.virtusdevelops.easyclans.listener;

import eu.virtusdevelops.easyclans.controller.ClansController;
import eu.virtusdevelops.easyclans.controller.PlayerController;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class PlayerDamageListener implements Listener {
    private PlayerController playerController;
    private ClansController clansController;


    public PlayerDamageListener(PlayerController playerController, ClansController clansController) {
        this.playerController = playerController;
        this.clansController = clansController;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityDamageEntity(EntityDamageByEntityEvent event){
        if(!(event.getEntity() instanceof Player victim)){
            return;
        }




        if(event.getDamager() instanceof Player damager){
            var cDamager = playerController.getPlayer(damager.getUniqueId());
            var cVictim = playerController.getPlayer(victim.getUniqueId());

            if(cVictim == null || cDamager == null) return;
            if(cVictim.getClanID() == null || cDamager.getClanID() == null) return;

            if(!cDamager.getClanID().equals(cVictim.getClanID())){
                return;
            }

            var clan = clansController.getClan(cDamager.getClanID());
            if(clan.isPvpEnabled()){
                return;
            }
            event.setCancelled(true);
        }

        if(event.getDamager() instanceof Projectile projectile){
            if(!(projectile.getShooter() instanceof Player damager)){
                return;
            }

            var cDamager = playerController.getPlayer(damager.getUniqueId());
            var cVictim = playerController.getPlayer(victim.getUniqueId());

            if(cVictim == null || cDamager == null) return;
            if(cVictim.getClanID() == null || cDamager.getClanID() == null) return;

            if(!cDamager.getClanID().equals(cVictim.getClanID())){
                return;
            }

            var clan = clansController.getClan(cDamager.getClanID());
            if(clan.isPvpEnabled()){
                return;
            }
            event.setCancelled(true);
            projectile.remove();


        }
    }

}
