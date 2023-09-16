package net.astrona.easyclans.gui;

import net.astrona.easyclans.ClansPlugin;
import net.astrona.easyclans.gui.actions.Action;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Paginator {
    private GUI gui;
    private final int size;
    private final String title;
    private int current_page;
    private final Player player;
    private final Map<Integer, Icon> fixedIcons = new HashMap<>();
    private final List<Integer> valid_slots;
    private final List<Icon> icons = new ArrayList<>();

    public Paginator(Player player, List<Integer> valid_slots, String title, int size) {
        this.player = player;
        this.size = size;
        this.title = title;
        this.valid_slots = valid_slots;

        gui = new GUI(size, title);

        init();
        gui.fancyBackground();
        //open(0);
    }

    private void init() {
        // Sets up the previous and next page
        var nextPageItem = new ItemStack(Material.PAPER);
        var meta = nextPageItem.getItemMeta();
        meta.displayName(ClansPlugin.MM.deserialize("<gold>Next page"));
        nextPageItem.setItemMeta(meta);

        // Next page
        var nextPage = new Icon(
                nextPageItem
        );
        nextPage.setVisibilityCondition((player, it) -> (current_page * valid_slots.size() + valid_slots.size() < icons.size()));
        nextPage.addClickAction((player) -> {
            current_page++;
            open(current_page);
        });

        gui.setIcon(size - 2, nextPage);

        // Previous page
        var prevPageItem = new ItemStack(Material.PAPER);
        var prevPageItemMeta = nextPageItem.getItemMeta();
        prevPageItemMeta.displayName(ClansPlugin.MM.deserialize("<gold>Next page"));
        prevPageItem.setItemMeta(prevPageItemMeta);

        var prevPage = new Icon(
                prevPageItem
        );
        prevPage.setVisibilityCondition((player, it) -> {
            if (current_page > 0) return true;
            return false;
        });
        prevPage.addClickAction((player) -> {
                    current_page--;
                    open(current_page);
                }
        );
        gui.setIcon(size - 8, prevPage);
    }

    public void addCloseAction(Action action) {
        gui.addCloseAction(action);
    }

    public void addIcon(Icon icon) {
        this.icons.add(icon);
    }

    public void removeIcon(Icon icon) {
        this.icons.remove(icon);
    }

    public void open() {
        open(current_page);
    }

    public void open(int page) {
        // Setup icons per page
        for (int i = 0; i < valid_slots.size(); i++) {
            gui.setIcon(valid_slots.get(i), null);
        }

        for (int i : fixedIcons.keySet()) {
            gui.setIcon(i, fixedIcons.get(i));
        }


        for (int i = 0; i < valid_slots.size(); i++) {
            int index = page * valid_slots.size() + i;
            if (icons.size() > index) {
                int slot = valid_slots.get(i);
                Icon icon = icons.get(index);
                gui.setIcon(slot, icon);
            } else {
                break;
            }
        }
        gui.setTitle(title.replace("{page}", String.valueOf(current_page)));
        gui.open(player);
    }
}
