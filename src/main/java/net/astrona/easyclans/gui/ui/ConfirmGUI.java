package net.astrona.easyclans.gui.ui;

import net.astrona.easyclans.ClansPlugin;
import net.astrona.easyclans.controller.LanguageController;
import net.astrona.easyclans.gui.GUI;
import net.astrona.easyclans.gui.Icon;
import net.astrona.easyclans.gui.actions.Action;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static net.kyori.adventure.key.Key.key;
import static net.kyori.adventure.sound.Sound.sound;

public class ConfirmGUI extends GUI {
    private Action confirmAction;
    private Action cancelAction;

    public ConfirmGUI(Player player, Action confirmAction, Action cancelAction, String title) {
        super(27, title);
        this.confirmAction = confirmAction;
        this.cancelAction = cancelAction;
        init();
        fancyBackground();
        this.open(player);
    }

    private void init() {
        ItemStack confirm = new ItemStack(Material.LIME_CONCRETE);
        var meta1 = confirm.getItemMeta();
        meta1.displayName(ClansPlugin.MM.deserialize(LanguageController.getLocalized("confirm")));
        confirm.setItemMeta(meta1);
        Icon confirmIcon = new Icon(confirm);
        confirmIcon.addClickAction(confirmAction);
        confirmIcon.addClickAction((player) -> {
            player.playSound(sound(key("block.note_block.cow_bell"), Sound.Source.MASTER, 1f, 1.19f));
        });
        setIcon(15, confirmIcon);

        ItemStack cancel = new ItemStack(Material.RED_CONCRETE);
        var meta2 = cancel.getItemMeta();
        meta2.displayName(ClansPlugin.MM.deserialize(LanguageController.getLocalized("cancel")));
        cancel.setItemMeta(meta2);
        Icon cancelIcon = new Icon(cancel);
        cancelIcon.addClickAction(cancelAction);
        cancelIcon.addClickAction((player) -> {
            player.playSound(sound(key("block.note_block.didgeridoo"), Sound.Source.MASTER, 1f, 1.19f));
        });
        setIcon(11, cancelIcon);
    }
}
