package eu.virtusdevelops.easyclans.gui;

import eu.virtusdevelops.easyclans.gui.actions.Action;
import eu.virtusdevelops.easyclans.gui.actions.Condition;
import eu.virtusdevelops.easyclans.gui.actions.ItemAction;
import eu.virtusdevelops.easyclans.gui.actions.UpdateAction;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class Icon implements Comparable<Icon> {
    public ItemStack itemStack;
    private int priority;
    private final List<Action> clickActions = new ArrayList<>();
    private final List<Action> leftClickActions = new ArrayList<>();
    private final List<Action> rightClickActions = new ArrayList<>();
    private final List<Action> shiftLeftClickActions = new ArrayList<>();
    private final List<Action> shiftRightClickActions = new ArrayList<>();
    private final List<ItemAction> dragItemActions = new ArrayList<>();
    private final UpdateAction refreshAction;
    private Condition visibilityCondition;

    public Icon(ItemStack itemStack) {
        this.itemStack = itemStack;
        this.refreshAction = null;
        this.priority = 1;
    }

    public Icon(ItemStack itemStack, int priority) {
        this.itemStack = itemStack;
        this.refreshAction = null;
        this.priority = priority;
    }

    public Icon(ItemStack itemStack, UpdateAction refreshAction) {
        this.itemStack = itemStack;
        this.refreshAction = refreshAction;
        this.priority = 1;
    }

    public Icon(ItemStack itemStack, UpdateAction refreshAction, int priority) {
        this.itemStack = itemStack;
        this.refreshAction = refreshAction;
        this.priority = priority;
    }

    public void setVisibilityCondition(Condition visibilityCondition) {
        this.visibilityCondition = visibilityCondition;
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

    public Condition getVisibilityCondition() {
        return visibilityCondition;
    }

    /**
     * Refreshes the icon
     * DOES NOT UPDATE THE UI!
     * Action that specifies what happens when refresh on icon is called :)
     *
     * @param player the player whose inventory icon is going to get refreshed
     */
    public void refresh(Player player) {
        if (this.refreshAction != null)
            this.refreshAction.execute(this, player);
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

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    @Override
    public int compareTo(Icon u) {
        return u.getPriority() - getPriority();
    }

}
