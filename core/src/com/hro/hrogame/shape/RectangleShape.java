package com.hro.hrogame.shape;

import com.hro.hrogame.primitives.Point;

public class RectangleShape extends Shape {

    // region Instance fields
    private Point centerPoint = new Point();
    private float x, y, width, height;
    // endregion

    // region Update
    private void updateCenterPoint() {
        centerPoint.set(x + width / 2, y + height / 2);
    }
    // endregion

    // region Setter
    public void setSize(float width, float height) {
        this.width = width;
        this.height = height;
        updateCenterPoint();
    }
    @Override
    public void setPosition(float x, float y) {
        this.x = x - width / 2;
        this.y = y - height / 2;
        updateCenterPoint();
    }
    // endregion

    // region Getter
    public float getX() {
        return x;
    }
    public float getY() {
        return y;
    }
    public float getWidth() {
        return width;
    }
    public float getHeight() {
        return height;
    }
    @Override
    public Point getCenterPoint() {
        return centerPoint;
    }
    @Override
    public boolean isPointInShape(Point point) {
        return point.x > x &&
                point.x < x + width &&
                point.y > y &&
                point.y < y + height;
    }
    // endregion
}
