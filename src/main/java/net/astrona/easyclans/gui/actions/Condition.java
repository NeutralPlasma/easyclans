package net.astrona.easyclans.gui.actions;

import net.astrona.easyclans.gui.Icon;
import org.bukkit.entity.Player;

public interface Condition {
    boolean checkCondition(Player player, Icon icon);
}
