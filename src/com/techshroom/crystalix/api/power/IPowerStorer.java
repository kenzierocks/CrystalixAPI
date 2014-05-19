package com.techshroom.crystalix.api.power;

/**
 * Represents an object that stores glukes.
 * 
 * @author kenzietogami
 */
public interface IPowerStorer {
    /**
     * The result of subtracting {@link #getStoredPower()} from
     * {@link #getMaxPower()}
     * 
     * @return the power that can be stored in this IPowerStorer.
     */
    public int getPowerSpace();

    /**
     * The maximum amount of power that can be stored in this storer.
     * 
     * @return the max power this can hold.
     */
    public int getMaxPower();

    /**
     * Gets the current amount of stored power in this IPowerStorer.
     * 
     * @return
     */
    public int getStoredPower();
}
