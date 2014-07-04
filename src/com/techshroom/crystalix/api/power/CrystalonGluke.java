package com.techshroom.crystalix.api.power;

import java.io.Serializable;

/**
 * Represents a collection of crystalons, sort of like IC2s packets.
 * 
 * @author kenzietogami
 */
public final class CrystalonGluke implements Comparable<CrystalonGluke>,
        Serializable {
    private static final long serialVersionUID = 9178043987510557792L;
    /**
     * Singleton for no crystalon. Use instead of null.
     */
    public static final CrystalonGluke NONE = new CrystalonGluke(0);

    /**
     * Creates a gluke of power.
     * 
     */
    public static CrystalonGluke of(int value) {
        if (value == 0) {
            // singleton
            return NONE;
        }
        return new CrystalonGluke(value);
    }

    /**
     * Clone into a new instance
     */
    public static CrystalonGluke clone(CrystalonGluke cg) {
        return of(cg.getValue());
    }

    /**
     * The stored crystalons
     */
    protected int stored = 0;

    /**
     * Creates a new CrystalonGluke from the list of crystalons.
     * 
     * @param crystalons
     *            - the list of crystalons to put in.
     */
    CrystalonGluke(int value) {
        stored = value;
    }

    /**
     * Adds on the given value.
     * 
     * @param value
     */
    public CrystalonGluke addValue(int value) {
        return of(stored + value);
    }

    /**
     * Sets the value as the given one.
     * 
     * @param value
     *            - the value to set this gluke to.
     */
    @SuppressWarnings("static-method")
    public CrystalonGluke setValue(int value) {
        return of(value);
    }

    /**
     * Gets the value of this gluke.
     * 
     * @return the calculated value of this gluke.
     */
    public int getValue() {
        return stored;
    }

    /**
     * Splits this CG into two pieces, one of the amount given and one with the
     * leftover amount. Like decrStackSize for inventories.
     * 
     * NB: this CG will have the wanted amount. If you use 0 as the amount;
     * nothing will happen to keep {@link #NONE} as the singularity.
     * 
     * @param amount
     *            - the amount to subtract from this gluke
     * @return a new CG of the leftovers
     */
    public CrystalonGluke split(int amount) {
        if (amount == 0) {
            return NONE;
        }
        if (stored < amount) {
            return CrystalonGluke.of(0);
        }

        CrystalonGluke ret = CrystalonGluke.of(stored - amount);
        stored = amount;
        return ret;
    }

    @Override
    public int compareTo(CrystalonGluke o) {
        if (this.equals(o)) {
            return 0;
        }
        if (o.stored > this.stored) {
            return -1;
        }
        return 1;
    }

    @Override
    public int hashCode() {
        // integer perfectly matches equals
        return stored;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof CrystalonGluke) {
            return ((CrystalonGluke) obj).stored == stored;
        }
        return false;
    }

    @Override
    public String toString() {
        return stored + " crystalons";
    }
}
