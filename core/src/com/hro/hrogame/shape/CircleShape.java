package com.hro.hrogame.shape;

import com.hro.hrogame.primitives.Point;

public class CircleShape extends Shape {

    // region Instance fields
    private Point center = new Point();
    private float radius;
    // endregion

    // region Setter
    @Override
    public void setPosition(float x, float y) {
        center.set(x, y);
    }
    public void setRadius(float radius) {
        this.radius = radius;
    }
    public void set(float centerX, float centerY, float radius) {
        center.set(centerX, centerY);
        this.radius = radius;
    }
    // endregion

    // region Getter
    @Override
    public Point getCenterPoint() {
        return center;
    }
    @Override
    public boolean isPointInShape(Point point) {
        return isPointInCircle(point, center, radius);
    }
    public static boolean isPointInCircle(Point point, Point centerPoint, float radius) {
        return Math.sqrt(Math.pow(point.x - centerPoint.x, 2) + Math.pow(point.y - centerPoint.y, 2)) <= radius;
    }
    public float getRadius() {
        return radius;
    }
    // endregion
}
