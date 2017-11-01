package org.loxf.jyadmin.biz.util;

import org.apache.commons.lang3.StringUtils;
import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;

import java.io.Serializable;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class IdGenerator {
    private static final String MACHINE_ID;
    private static final ThreadLocal<Integer> SEED = new ThreadLocal<Integer>() {
        protected Integer initialValue() {
            return Integer.valueOf(0);
        }
    };
    private static final int MAX = 9999;
    private static final int SIZE = 32;
    private static final int PREFIX_SIZE = 4;

    public IdGenerator() {
    }

    public static String generate(String prefix) {
        if (!StringUtils.isBlank(prefix) && prefix.length() <= 6) {
            int seed = ((Integer)SEED.get()).intValue();
            int random = ThreadLocalRandom.current().nextInt(0, 9999);
            String tmp = StringUtils.join(new Serializable[]{System.currentTimeMillis(), random, Thread.currentThread().getId(), MACHINE_ID, seed});
            int over = tmp.length() - (32 - prefix.length());
            if (over > 0) {
                tmp = StringUtils.substring(tmp, over);
            } else {
                tmp = StringUtils.leftPad(tmp, 32 - prefix.length(), '0');
            }

            ++seed;
            if (seed > 9999) {
                seed = 0;
            }

            SEED.set(seed);
            return StringUtils.join(new String[]{prefix, tmp});
        } else {
            throw new IllegalArgumentException("prefix illegal");
        }
    }

    public static void main(String[] args) {
        long d1 = System.currentTimeMillis();

        for(int i = 0; i < 1000000; ++i) {
            generate("YWUS");
        }

        long d2 = System.currentTimeMillis();
        System.out.println(d2 - d1);
        long d3 = System.currentTimeMillis();

        for(int i = 0; i < 1000000; ++i) {
            UUID.randomUUID().toString().replaceAll("-", "");
        }

        long d4 = System.currentTimeMillis();
        System.out.println(d4 - d3);
    }

    static {
        SystemInfo systemInfo = new SystemInfo();
        HardwareAbstractionLayer hardwareAbstractionLayer = systemInfo.getHardware();
        MACHINE_ID = hardwareAbstractionLayer.getNetworkIFs()[0].getMacaddr().replace(":", "").toUpperCase();
    }
}
