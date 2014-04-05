package com.techshroom.crystalix.api.power;

import java.io.Serializable;

/**
 * Represents a collection of crystalons, sort of like IC2s packets.
 * 
 * @author kenzietogami
 */
public class CrystalonGluke implements Comparable<CrystalonGluke>, Serializable {
    private static final long serialVersionUID = 9178043987510557792L;
    /**
     * Singleton for no crystalon. Use instead of null.
     */
    public static final CrystalonGluke NONE = new CrystalonGluke(0);

    /**
     * See {@link Crystalon#gluke(int)}.
     * 
     * @see Crystalon#gluke(int)
     */
    public static CrystalonGluke of(int value) {
        if (value == 0) {
            // singleton
            return NONE;
        }
        return new CrystalonGluke(value);
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
    public void addValue(int value) {
        stored += value;
    }

    /**
     * Sets the value as the given one.
     * 
     * @param value
     *            - the value to set this gluke to.
     */
    public void setValue(int value) {
        stored = value;
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
     * Splits this CG into two pieces, one of the amount given and this one with
     * the leftover amount. Like decrStackSize for inventories.
     * 
     * @param amount
     *            - the amount to store into the new CG
     * @return a new CG of the given amount, or less if there is not as much as
     *         the given amount.
     */
    public CrystalonGluke split(int amount) {
        if (stored < amount) {
            setValue(0);
            return CrystalonGluke.of(stored);
        } else {
            setValue(stored - amount);
            return CrystalonGluke.of(amount);
        }
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
