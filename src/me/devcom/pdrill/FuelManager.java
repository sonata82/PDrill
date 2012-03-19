package me.devcom.pdrill;

import java.util.HashMap;

import org.bukkit.block.Block;
import org.bukkit.block.Furnace;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class FuelManager {
    public IDrill drill;
    public HashMap<Integer, Fuel> fuels;

    public FuelManager(IDrill drill) {
        this.drill = drill;
        fuels = drill.getConfiguredFuels();
    }

    public Fuel fuel() {
        if (drill.isVirtual()) {
            return drill.getVirtualFuel();
        }
        Integer currentFuelId = furnaceFuelId();
        return getFuelById(currentFuelId);
    }

    public ItemStack furnaceFuel() {
        Furnace furnace = (Furnace) drill.getBlockState();
        Inventory furnaceInv = furnace.getInventory();
        return furnaceInv.getItem(1);
    }

    public Integer furnaceFuelId() {
        ItemStack furnaceFuel = furnaceFuel();
        if (furnaceFuel != null) {
            return furnaceFuel.getTypeId();
        }
        return null;
    }

    public boolean furnaceHasFuel() {
        return (getFuelLevel() > 0 && (fuel() != null));
    }

    public int getFuelLevel() {
        return furnaceFuel().getAmount();
    }

    public void consumeFuel(Integer fuelCount) {
        Furnace furnace = (Furnace) drill.getBlockState();
        Inventory furnaceInv = furnace.getInventory();

        Integer currentFuelId = furnaceFuelId();
        Fuel fuel = getFuelById(currentFuelId);

        if (fuel != null) {
            Integer fuelLeft = getFuelLevel() - fuelCount;

            if (fuelLeft <= 0) {

                furnaceInv.clear(1);
                return;
            }

            ItemStack fuelStack = new ItemStack(currentFuelId, fuelLeft);
            furnaceInv.clear(1);
            furnaceInv.setItem(1, fuelStack);
        }
    }

    private Fuel getFuelById(Integer currentFuelId) {
        if (fuels.containsKey(currentFuelId)) {
            return fuels.get(currentFuelId);
        }
        return null;
    }

    public void swapFuel(Block nextBlock) {

        Furnace furnace = (Furnace) drill.getBlockState();
        Inventory furnaceInv = furnace.getInventory();

        Integer currentFuelId = furnaceFuelId();

        Furnace nextBFurnace = (Furnace) nextBlock.getState();
        Inventory nextBFurnaceInv = nextBFurnace.getInventory();

        if (getFuelLevel() > 0) {
            ItemStack fuelStack = new ItemStack(currentFuelId, getFuelLevel());

            nextBFurnaceInv.remove(currentFuelId);
            nextBFurnaceInv.setItem(1, fuelStack);

            furnaceInv.remove(currentFuelId);
        }
        if (drill.getBlockPlaceManager() != null) {
            ItemStack placeStack = furnaceInv.getItem(0);
            Integer currentPlaceId = placeStack.getTypeId();
            furnaceInv.remove(currentPlaceId);

            ItemStack newPlaceStack = new ItemStack(currentPlaceId,
                    placeStack.getAmount());
            nextBFurnaceInv.setItem(0, newPlaceStack);
        }
    }

}
