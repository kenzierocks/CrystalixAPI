package com.techshroom.crystalix.api.power;

import net.minecraft.dispenser.IBlockSource;

import com.techshroom.crystalix.power.PowerBus;
import com.techshroom.crystalix.power.PowerRequestHandler;

public final class PowerUtil {
    private PowerUtil() {
        throw new AssertionError("Nope.");
    }

    /**
     * Power ratios
     * 
     * @author kenzietogami
     */
    public static final class PowerRatios {
        private PowerRatios() {
            throw new AssertionError("Nope.");
        }

        /**
         * Crystalons -> other
         */
        public static final double C_TO_IC2 = 2.0, C_TO_BC = 5;
        /**
         * Other -> crystalons
         */
        public static final double IC2_TO_C = 0.5, BC_TO_C = 1 / 5;
    }

    public static final class PowerInterfaceHelper {
        private PowerInterfaceHelper() {
            throw new AssertionError("Nope.");
        }

        public static boolean isPowerProvider(Object o) {
            return o instanceof IPowerProvider;
        }

        public static boolean isPowerReceiver(Object o) {
            return o instanceof IPowerReceiver;
        }

        public static boolean isPowerStorer(Object o) {
            return o instanceof IPowerStorer;
        }

        public static boolean isPowerProviderAndStorer(Object o) {
            return o instanceof IPowerPS;
        }

        public static boolean isPowerProviderAndReceiver(Object o) {
            return o instanceof IPowerPR;
        }

        public static boolean isPowerReceiverAndStorer(Object o) {
            return o instanceof IPowerRS;
        }

        public static boolean isPowerProviderReceiverAndStorer(Object o) {
            return o instanceof IPowerPRS;
        }
    }

    public static final class PowerConversionHelper {
        private PowerConversionHelper() {
            throw new AssertionError("Nope.");
        }

        public static int convertToICPower(CrystalonGluke cg) {
            int value = cg.getValue();
            return (int) (value * PowerRatios.C_TO_IC2);
        }

        public static int convertToBCPower(CrystalonGluke cg) {
            int value = cg.getValue();
            return (int) (value * PowerRatios.C_TO_BC);
        }

        public static CrystalonGluke convertFromICPower(int ic2Power) {
            return CrystalonGluke.of((int) (ic2Power * PowerRatios.IC2_TO_C));
        }

        public static CrystalonGluke convertFromBCPower(int bcPower) {
            return CrystalonGluke.of((int) (bcPower * PowerRatios.BC_TO_C));
        }
    }

    public static final class PowerEventHelper {
        private PowerEventHelper() {
            throw new AssertionError("Nope.");
        }

        static {
            eventRegisters();
        }

        /**
         * Posts a {@link PowerRequestEvent}. The default handler will
         * automatically send the power to the receiver.
         * 
         * @param ibs
         *            - the block source of the receiver
         * @return the power given to the receiver block
         */
        public static CrystalonGluke requestPowerFor(IBlockSource ibs) {
            PowerRequestEvent req = new PowerRequestEvent(ibs);
            PowerBus.INSTANCE.post(req);
            return req.getPower();
        }

        private static void eventRegisters() {
            PowerBus.INSTANCE.register(new PowerRequestHandler());
        }
    }
}
