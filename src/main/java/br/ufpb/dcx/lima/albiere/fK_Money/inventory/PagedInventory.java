package br.ufpb.dcx.lima.albiere.fK_Money.inventory;

import br.ufpb.dcx.lima.albiere.fK_Money.FK_Balance;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

public class PagedInventory implements InventoryModuleInterface {
    private final String title;
    private final int rows;
    private final Inventory inventory;
    private final String id;
    private int index = 0;
    private final int max_index;

    public int getIndex() {
        return index;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public int getMax_index() {
        return max_index;
    }

    public List<String> getNextLore(){
        return FK_Balance.getOptions().getConfig().getStringList("essential.cashTop.item_nextAndPrevious.NextLore")
                .stream()
                .map(s -> s.replaceAll("&", "§"))
                .toList();
    }

    public List<String> getPreviousLore() {
        List<String> lore1 = FK_Balance.getOptions().getConfig().getStringList("essential.moneyTop.item_nextAndPrevious.PreviousLore");
        lore1.replaceAll(s -> s.replaceAll("&", "§"));
        return lore1;
    }
    public PagedInventory(String id, String title, int rows, int max_index) {
        this.title = title;
        this.rows = rows;
        this.id = id;
        this.max_index = max_index;
        inventory = Bukkit.createInventory(null, this.rows*9, this.title);
        addItem(26,
                Material.getMaterial(FK_Balance.getOptions().getConfig().getString("essential.moneyTop.item_nextAndPrevious.Type")),
                1,
                Objects.requireNonNull(FK_Balance.getOptions().getConfig().getString("essential.moneyTop.item_nextAndPrevious.NextName")).replaceAll("&", "§"),
                getNextLore());
    }


    @Override
    public String getType() {
        return "paged";
    }

    public void open(Player p) {
        p.openInventory(inventory);
    }

    public int getRows() {
        return this.rows;
    }

    public String getTitle() {
        return this.title;
    }

    public void addItem(int position, Material type, int amount, String name, List<String> lore) {
        ItemStack i = new ItemStack(type, amount);
        ItemMeta meta = i.getItemMeta();
        assert meta != null;
        meta.setDisplayName(name);
        meta.setLore(lore);
        i.setItemMeta(meta);
        inventory.setItem(position, i);
    }

    public void addPlayerHead(int position, int amount, String title, List<String> lore, UUID uuid) {
        ItemStack i = new ItemStack(Material.PLAYER_HEAD, amount);
        SkullMeta meta = (SkullMeta) i.getItemMeta();

        assert meta != null;
        List<String> newLore = new ArrayList<>();
        for(String s : lore) {
            newLore.add(s.replaceAll("&", "§"));
        }
        meta.setOwningPlayer(Bukkit.getOfflinePlayer(uuid));
        meta.setDisplayName(title.replaceAll("&", "§"));
        meta.setLore(newLore);
        i.setItemMeta(meta);
        inventory.setItem(position, i);
    }

    public void addItem(int position, Material type, int amount) {
        ItemStack i = new ItemStack(type, amount);
        inventory.setItem(position, i);
    }

    public String getId() {
        return id;
    }

    public void setItem(int index, ItemStack i) {
        inventory.setItem(index, i);
    }


    public void clear() {
        inventory.clear();
    }

    public void clear(int index) {
        inventory.clear(index);
    }


    public void nextPage() {
        this.index+=1;
        if(index > max_index) return;
        clear();

        if(index < max_index) {

            addItem(26,
                    Material.getMaterial(FK_Balance.getOptions().getConfig().getString("essential.cashTop.item_nextAndPrevious.Type")),
                    1,
                    Objects.requireNonNull(FK_Balance.getOptions().getConfig().getString("essential.cashTop.item_nextAndPrevious.NextName")).replaceAll("&", "§"),
                    getNextLore());
        }
        if(index > 0) {
            addItem(18, Material.getMaterial(FK_Balance.getOptions().getConfig().getString("essential.moneyTop.item_nextAndPrevious.Type")), 1, Objects.requireNonNull(FK_Balance.getOptions().getConfig().getString("essential.moneyTop.item_nextAndPrevious.PreviousName")).replaceAll("&", "§"), getPreviousLore());
        }
    }

}
