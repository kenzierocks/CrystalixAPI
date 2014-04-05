package com.techshroom.crystalix.api.power;

/**
 * Represents an object that can provide power.
 * 
 * @author kenzietogami
 */
public interface IPowerProvider {
    /**
     * Called when there is a request for power from a receiver.
     * 
     * @return a {@link CrystalonGluke} that is the power given to the
     *         requester.
     */
    public CrystalonGluke requestPower(int max);

    /**
     * Tells the requester if this object can currently provide power.
     * 
     * @return true if this object can provide power, false otherwise.
     */
    public boolean hasPower();
}
