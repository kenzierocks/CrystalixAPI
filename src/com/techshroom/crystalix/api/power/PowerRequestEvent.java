package com.techshroom.crystalix.api.power;

import cpw.mods.fml.common.eventhandler.Cancelable;
import net.minecraft.dispenser.IBlockSource;

@Cancelable
public class PowerRequestEvent extends PowerEvent {
    private final IBlockSource at;
    private CrystalonGluke returnedPower = CrystalonGluke.of(0);

    public PowerRequestEvent(IBlockSource requestSource) {
        at = requestSource;
    }

    public IBlockSource getSource() {
        return at;
    }

    public void setPower(CrystalonGluke cg) {
        if (cg == null) {
            setPower(CrystalonGluke.of(0));
            return;
        }
        returnedPower = cg;
    }

    public CrystalonGluke getPower() {
        return returnedPower;
    }
}
