package com.techshroom.crystalix.api.power;

public interface IPowerPS extends IPowerStorer, IPowerProvider {

    /**
     * Gives any extra power not received back to the provider to be stored.
     * 
     * @param extra
     *            - the extra power
     */
    public void returnExtra(CrystalonGluke extra);
}
