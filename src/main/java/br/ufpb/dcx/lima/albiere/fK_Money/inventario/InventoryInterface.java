package br.ufpb.dcx.lima.albiere.fK_Money.inventario;

public interface InventoryInterface {

    void InventoryCreate(String id, String title, int rows);
    SingleInventory getInventory(String id);

    boolean inventoryExists(String title, String id);

    void setInventory();
    boolean inventoryExists(String id);
}
