package net.astrona.easyclans.gui;

import net.astrona.easyclans.gui.actions.Action;
import net.astrona.easyclans.gui.actions.ItemAction;
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

    public Icon(ItemStack itemStack) {
        this.itemStack = itemStack;
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


    public void addClickAction(Action action){
        this.clickActions.add(action);
    }
    public void addLeftClickAction(Action action){
        this.leftClickActions.add(action);
    }
    public void addRightClickAction(Action action){
        this.rightClickActions.add(action);
    }
    public void addShiftLeftClickAction(Action action){
        this.shiftLeftClickActions.add(action);
    }
    public void addShiftRightClickAction(Action action){
        this.shiftRightClickActions.add(action);
    }
    public void addDragItemAction(ItemAction action){
        this.dragItemActions.add(action);
    }



}
