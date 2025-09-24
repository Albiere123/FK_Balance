package br.ufpb.dcx.lima.albiere.fK_Money.inventario;

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

public class SingleInventory {
    private final String title;
    private final int rows;
    private final Inventory inventory;
    private final String id;

    public SingleInventory(String id, String title, int rows) {
        this.title = title;
        this.rows = rows;
        this.id = id;
        inventory = Bukkit.createInventory(null, this.rows*9, this.title);
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

}
