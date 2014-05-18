package com.techshroom.crystalix.api;

import java.util.HashMap;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.*;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import com.techshroom.crystalix.Util;

public abstract class SpecialMarkedTE extends TileEntity {
    // there seems to be an id system?
    private static int tepacketids = 5;

    private static HashMap<Class<? extends SpecialMarkedTE>, Integer> idmap = new HashMap<Class<? extends SpecialMarkedTE>, Integer>();

    @Override
    public void markDirty() {
        Util.specialMarkAndSend(this);
    }

    public void markDirtyFromCallback() {
        super.markDirty();
    }

    public World getWorld() {
        return getWorldObj();
    }

    public int getX() {
        return xCoord;
    }

    public int getY() {
        return yCoord;
    }

    public int getZ() {
        return zCoord;
    }

    @Override
    public Packet getDescriptionPacket() {
        Class<? extends SpecialMarkedTE> c = getClass();
        if (!idmap.containsKey(c)) {
            idmap.put(c, tepacketids++);
        }
        int id = idmap.get(c);
        NBTTagCompound nbt = new NBTTagCompound();
        writeToNBT(nbt);
        S35PacketUpdateTileEntity p = new S35PacketUpdateTileEntity(xCoord,
                yCoord, zCoord, id, nbt);
        return p;
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
        readFromNBT(pkt.func_148857_g());
    }
}
