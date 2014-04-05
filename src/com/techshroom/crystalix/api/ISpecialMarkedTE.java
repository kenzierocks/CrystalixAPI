package com.techshroom.crystalix.api;

import net.minecraft.world.World;

public interface ISpecialMarkedTE {
    public void markDirtyFromCallback();

    public World getWorld();

    public int getX();

    public int getY();

    public int getZ();
}
