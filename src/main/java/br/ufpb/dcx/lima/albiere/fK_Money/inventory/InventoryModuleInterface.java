package br.ufpb.dcx.lima.albiere.fK_Money.inventory;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

public interface InventoryModuleInterface {

    String getType();
    void open(Player p);
    int getRows();
    String getTitle();
    String getId();
    void addItem(int position, Material type, int amount, String name, List<String> lore);
    void addPlayerHead(int position, int amount, String title, List<String> lore, UUID uuid);
    void addItem(int position, Material type, int amount);
    void setItem(int index, ItemStack i);
    void clear(int index);
    void clear();

}
