package net.astrona.easyclans.gui;

import net.astrona.easyclans.gui.actions.Action;
import net.astrona.easyclans.gui.actions.ItemAction;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class Icon {

    public final ItemStack itemStack;
    private final List<Action> clickActions = new ArrayList<>();
    private final List<Action> leftClickActions = new ArrayList<>();
    private final List<Action> rightClickActions = new ArrayList<>();
    private final List<Action> shiftLeftClickActions = new ArrayList<>();
    private final List<Action> shiftRightClickActions = new ArrayList<>();
    private final List<ItemAction> dragItemActions = new ArrayList<>();

    private final Action refreshAction;


    public Icon(ItemStack itemStack) {
        this.itemStack = itemStack;
        this.refreshAction = null;
    }
    public Icon(ItemStack itemStack, Action refreshAction) {
        this.itemStack = itemStack;
        this.refreshAction = refreshAction;
    }

    public List<Action> getClickActions() {
        return clickActions;
    }

    public List<Action> getLeftClickActions() {
        return leftClickActions;
    }

    public List<Action> getRightClickActions() {
        return rightClickActions;
    }

    public List<Action> getShiftLeftClickActions() {
        return shiftLeftClickActions;
    }

    public List<Action> getShiftRightClickActions() {
        return shiftRightClickActions;
    }

    public List<ItemAction> getDragItemActions() {
        return dragItemActions;
    }

    /**
     * Refreshes the icon
     * DOES NOT UPDATE THE UI!
     * Action that specifies what happens when refresh on icon is called :)
     * @param player which player so blabla
     */
    public void refresh(Player player){
        if(this.refreshAction != null)
            this.refreshAction.execute(player);
    }

    public void addClickAction(Action action) {
        this.clickActions.add(action);
    }

    public void addLeftClickAction(Action action) {
        this.leftClickActions.add(action);
    }

    public void addRightClickAction(Action action) {
        this.rightClickActions.add(action);
    }

    public void addShiftLeftClickAction(Action action) {
        this.shiftLeftClickActions.add(action);
    }

    public void addShiftRightClickAction(Action action) {
        this.shiftRightClickActions.add(action);
    }

    public void addDragItemAction(ItemAction action) {
        this.dragItemActions.add(action);
    }


}
