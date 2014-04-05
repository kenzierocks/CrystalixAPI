package com.techshroom.crystalix.api.power;

/**
 * Represents an object that is a storer and receiver.
 * 
 * @author Kenzie Togami
 *
 */
public interface IPowerRS extends IPowerReceiver, IPowerStorer {

    /**
     * Gets the amount of power space in this {@link IPowerStorer} and
     * {@link IPowerReceiver}.
     * 
     * @return the max amount of crystalons this storage can have added to it
     */
    public int getPowerSpace();
}
