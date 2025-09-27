package br.ufpb.dcx.lima.albiere.fK_Money.inventory.manager;

import br.ufpb.dcx.lima.albiere.fK_Money.inventory.InventoryModuleInterface;

public interface InventoryInterface {

    InventoryModuleInterface createInventory(String id, String title, int rows, InventoryTypes type, int max_index);

}
