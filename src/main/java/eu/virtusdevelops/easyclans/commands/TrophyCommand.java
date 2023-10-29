package eu.virtusdevelops.easyclans.commands;

import eu.virtusdevelops.easyclans.controller.ClansController;
import eu.virtusdevelops.easyclans.controller.PlayerController;
import eu.virtusdevelops.easyclans.controller.TropyController;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TrophyCommand implements TabExecutor {
    private ClansController clansController;
    private PlayerController playerController;
    private TropyController tropyController;


    public TrophyCommand(ClansController clansController, PlayerController playerController, TropyController tropyController) {
        this.clansController = clansController;
        this.playerController = playerController;
        this.tropyController = tropyController;
    }





    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return null;
    }
}
