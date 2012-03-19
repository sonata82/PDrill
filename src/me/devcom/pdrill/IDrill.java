package me.devcom.pdrill;

import java.util.HashMap;

import org.bukkit.block.Block;
import org.bukkit.block.BlockState;

public interface IDrill {
    public boolean isVirtual();

    public Fuel getVirtualFuel();

    public Fuel getFuel();

    public abstract Block getNextBlock(String direction);

    public abstract boolean canMoveInDirection(String direction);

    public abstract boolean moveInDirection(String direction);

    public void sendMessage(String message);

    public void setEnabled(boolean enabled);

    public Integer getId();

    public HashMap<Integer, Fuel> getConfiguredFuels();

    public abstract BlockState getBlockState();

    public BlockPlaceManager getBlockPlaceManager();
}