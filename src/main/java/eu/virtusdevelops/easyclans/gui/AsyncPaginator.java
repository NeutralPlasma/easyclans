package eu.virtusdevelops.easyclans.gui;

import eu.virtusdevelops.easyclans.gui.actions.AsyncReturnTask;
import eu.virtusdevelops.easyclans.ClansPlugin;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

import static net.kyori.adventure.key.Key.key;
import static net.kyori.adventure.sound.Sound.sound;


public class AsyncPaginator extends GUI{
    private List<Integer> validSlots;
    private String titleTemplate;
    private ClansPlugin plugin;
    private AsyncReturnTask<List<Icon>> fetchPageTask;
    private AsyncReturnTask<Integer> getItemsCountTask;
    private int totalPages = 0;
    private int totalItems = 0;
    private int currentPage = 0;
    private int itemsPerPage;
    private Icon emptyIcon;

    public AsyncPaginator(Player player, ClansPlugin plugin, int size, String title, List<Integer> validSlots) {
        super(player, size, title, validSlots);
        titleTemplate = title;
        setTitle(titleTemplate.replace("{page}", "0")
                .replace("{pages}", "0"));
        this.plugin = plugin;
        this.validSlots = validSlots;
        this.itemsPerPage = validSlots.size();
        emptyIcon = new Icon(new ItemStack(Material.AIR));

        //init();
    }

    public void setFetchPageTask(AsyncReturnTask<List<Icon>> fetchPageTask) {
        this.fetchPageTask = fetchPageTask;
    }

    public void setGetItemsCountTask(AsyncReturnTask<Integer> getItemsCountTask) {
        this.getItemsCountTask = getItemsCountTask;
    }

    public void init(){
        setIcon(size-3, nextPage());
        setIcon(size-7, previousPage());
        setIcon(size-2, lastPage());
        setIcon(size-8, firstPage());



        openStock();
        setTitle("Loading....");
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            totalItems = getItemsCountTask.fetchData();
            totalPages = totalItems / itemsPerPage;
            updatePageFunction();
        });
        fancyBackground();
    }



    private Icon firstPage(){
        var item= new ItemStack(Material.BOOK);
        var meta = item.getItemMeta();
        meta.displayName(ClansPlugin.MM.deserialize("<gold>First page"));
        item.setItemMeta(meta);
        // Next page
        var icon = new Icon(
                item
        );
        icon.setVisibilityCondition((player, it) -> (currentPage != 0));
        icon.addClickAction((player) -> {
            player.playSound(sound(key("ui.button.click"), Sound.Source.MASTER, 1f, 1.19f));
            currentPage = 0;
            updatePage();
        });
        return icon;
    }

    private Icon lastPage(){
        var item= new ItemStack(Material.BOOK);
        var meta = item.getItemMeta();
        meta.displayName(ClansPlugin.MM.deserialize("<gold>Last page"));
        item.setItemMeta(meta);
        // Next page
        var icon = new Icon(
                item
        );
        icon.setVisibilityCondition((player, it) -> (currentPage != totalPages));
        icon.addClickAction((player) -> {
            player.playSound(sound(key("ui.button.click"), Sound.Source.MASTER, 1f, 1.19f));
            currentPage = totalPages;
            updatePage();
        });
        return icon;
    }

    private Icon nextPage(){
        var item= new ItemStack(Material.PAPER);
        var meta = item.getItemMeta();
        meta.displayName(ClansPlugin.MM.deserialize("<gold>Next page"));
        item.setItemMeta(meta);
        // Next page
        var icon = new Icon(
                item
        );
        icon.setVisibilityCondition((player, it) -> (currentPage*itemsPerPage <= totalItems - itemsPerPage));
        icon.addClickAction((player) -> {
            player.playSound(sound(key("ui.button.click"), Sound.Source.MASTER, 1f, 1.19f));
            currentPage++;
            updatePage();
        });
        return icon;
    }

    private Icon previousPage(){
        var item= new ItemStack(Material.PAPER);
        var meta = item.getItemMeta();
        meta.displayName(ClansPlugin.MM.deserialize("<gold>Previous page"));
        item.setItemMeta(meta);
        // Next page
        var icon = new Icon(
                item
        );
        icon.setVisibilityCondition((player, it) -> (currentPage > 0));
        icon.addClickAction((player) -> {
            player.playSound(sound(key("ui.button.click"), Sound.Source.MASTER, 1f, 1.19f));
            currentPage--;
            updatePage();
        });
        return icon;
    }

    private void updatePage(){
        Bukkit.getScheduler().runTaskAsynchronously(plugin, this::updatePageFunction);
    }

    private void updatePageFunction(){
        var data = fetchPageTask.fetchPageData(currentPage, itemsPerPage);
        for(int i = 0; i < itemsPerPage ; i++){
            int slot = validSlots.get(i);
            if(data.size() > i)
                setIcon(slot, data.get(i));
            else
                setIcon(slot, emptyIcon);
        }
        Bukkit.getScheduler().runTask(plugin, () -> {
            //setTitle("<Gray>Logs (<gold>" + currentPage + "<gray>/<gold>" + totalPages + "<gray>"); // todo
            setTitle(titleTemplate.replace("{page}", "" + currentPage)
                    .replace("{pages}", "" + totalPages)
                    .replace("{total}", "" + totalItems));
            refresh();
            openStock();
        });
    }



    public void openStock(){
        player.openInventory(getInventory());
    }
    @Override
    public void open() {
        updatePage();
        openStock();
    }


}
