package eu.virtusdevelops.easyclans.gui.actions;

import eu.virtusdevelops.easyclans.gui.Icon;
import org.bukkit.entity.Player;

public interface Condition {
    boolean checkCondition(Player player, Icon icon);
}
