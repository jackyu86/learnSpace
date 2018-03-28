package com.github.jackyu86.lambda;

import java.util.function.Supplier;

/**
 * @Author: jack-yu
 * @Description:
 */
public enum FleetEnum{
    CAPTAIN(Captain::new),
    SAILOR(Sailor::new),
    COOK(Cook::new);

    private final Supplier<Fleet> constructor;

    FleetEnum(Supplier<Fleet> constructor) {
        this.constructor = constructor;
    }

    public Supplier<Fleet> getConstructor() {
        return this.constructor;
    }
}
