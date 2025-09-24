package br.ufpb.dcx.lima.albiere.fK_Money.inventario;

import br.ufpb.dcx.lima.albiere.fK_Money.exceptions.InventoryExistsException;
import br.ufpb.dcx.lima.albiere.fK_Money.exceptions.InventoryNotExists;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class InventoryManager implements InventoryInterface {

    private final Map<String, SingleInventory> inventorys = new HashMap<>();


    @Override
    public void InventoryCreate(String id, String title, int rows) throws InventoryExistsException {
        SingleInventory inventory = new SingleInventory(id, title.replaceAll("&", "§"), rows);
        if(inventorys.containsKey(id)) throw new InventoryExistsException("O inventário já existe!");
        else inventorys.put(id, inventory);
    }

    @Override
    public SingleInventory getInventory(String id) throws InventoryNotExists {
        if(inventorys.containsKey(id)) return inventorys.get(id);
        else throw new InventoryNotExists("Inventário não encontrado!");
    }


    @Override
    public boolean inventoryExists(String title) {
        AtomicBoolean test = new AtomicBoolean(false);
        inventorys.forEach((id, inventory) -> {
            if(inventory.getTitle().equals(title)) test.set(true);
        });

        return test.get();
    }

    @Override
    public boolean inventoryExists(String title, String id) {
        return inventorys.containsKey(id);
    }

    @Override
    public void setInventory() {

    }
}
