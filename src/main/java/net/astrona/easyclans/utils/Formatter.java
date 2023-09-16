package net.astrona.easyclans.utils;

import java.text.DecimalFormat;

public class Formatter {
    public static String formatMoney(double value) {
        if (Math.abs(value) >= 1_000_000) {
            return new DecimalFormat("#.#M").format(value / 1_000_000);
        } else if (Math.abs(value) >= 1_000) {
            return new DecimalFormat("#.#k").format(value / 1_000);
        } else {
            return new DecimalFormat("#.##").format(value);
        }
    }
}
