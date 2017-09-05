package com.hro.hrogame.primitives;

public class Point {

    // region Instance fields
    public float x;
    public float y;
    // endregion

    // region C-tor
    public Point() {
    }
    public Point(float x, float y) {
        this.x = x;
        this.y = y;
    }
    // endregion

    // region Setter
    public void set(float x, float y) {
        this.x = x;
        this.y = y;
    }
    // endregion
}
