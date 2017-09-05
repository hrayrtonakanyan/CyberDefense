package com.hro.hrogame.primitives;

public class ProgressiveAttribute {

    // region Instance fields
    public float current;
    public float max;
    // endregion

    // region C-tor
    public ProgressiveAttribute(float current, float max) {
        this.current = current;
        this.max = max;
    }
    // endregion
}
