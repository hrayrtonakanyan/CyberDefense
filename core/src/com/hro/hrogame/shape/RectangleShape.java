package com.hro.hrogame.shape;

import com.hro.hrogame.primitives.Point;

public class RectangleShape extends Shape {


    private Point centerPoint = new Point();
    private float x, y, width, height;


    private void updateCenterPoint() {
        centerPoint.set(x + width / 2, y + height / 2);
    }
    public void set(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        updateCenterPoint();
    }
    public void setSize(float width, float height) {
        this.width = width;
        this.height = height;
        updateCenterPoint();
    }

    @Override
    public boolean isPointInShape(Point point) {
        return point.x > x &&
                point.x < x + width &&
                point.y > y &&
                point.y < y + height;
    }
    @Override
    public Point getCenterPoint() {
        return centerPoint;
    }
    @Override
    public void setPosition(float x, float y) {
        this.x = x - width / 2;
        this.y = y - height / 2;
        updateCenterPoint();
    }

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
}
