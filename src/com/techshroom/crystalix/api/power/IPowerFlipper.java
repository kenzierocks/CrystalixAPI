package com.techshroom.crystalix.api.power;

/**
 * A power object that may provide or not depending or the environment.
 * 
 * @author Kenzie Togami
 *
 */
public interface IPowerFlipper {
    /**
     * Is this flipper providing right now?
     * 
     * @return <code>true</code> if this is providing
     */
    public boolean providing();
}
