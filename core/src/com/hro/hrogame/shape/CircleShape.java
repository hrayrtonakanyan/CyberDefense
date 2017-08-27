package com.hro.hrogame.shape;

import com.hro.hrogame.primitives.Point;

/**
 * Created by Lion on 8/15/17.
 */
public class CircleShape extends Shape {

    private Point center = new Point();
    private float radius;

    @Override
    public boolean isPointInShape(Point point) {
        return isPointInCircle(point, center, radius);
    }
    @Override
    public Point getCenterPoint() {
        return center;
    }

    public void set(float centerX, float centerY, float radius) {
        center.set(centerX, centerY);
        this.radius = radius;
    }
    @Override
    public void setPosition(float x, float y) {
        center.set(x, y);
    }



    public float getRadius() {
        return radius;
    }
    public void setRadius(float radius) {
        this.radius = radius;
    }


    public static boolean isPointInCircle(Point point, Point centerPoint, float radius) {
        return Math.sqrt(Math.pow(point.x - centerPoint.x, 2) + Math.pow(point.y - centerPoint.y, 2)) <= radius;
    }
}
