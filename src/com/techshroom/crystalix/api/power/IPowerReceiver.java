package com.techshroom.crystalix.api.power;

import net.minecraft.dispenser.IBlockSource;

/**
 * Represents an object that can receive power.
 * 
 * @author kenzietogami
 */
public interface IPowerReceiver {

    /**
     * Receives power after a {@link PowerRequestEvent}.
     * 
     * @param power
     *            - the power to receive
     * @return - the power leftover that couldn't be recived, and will be given
     *         back to the provider.
     */
    public CrystalonGluke receiveRequestedPower(CrystalonGluke power);

    /**
     * Snooty receivers can choose where they get their power from.
     * 
     * @param i
     *            - the power source
     * @return if it wants this power
     */
    public boolean wantsPowerFrom(IBlockSource i);
}
