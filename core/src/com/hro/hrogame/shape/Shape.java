package com.hro.hrogame.shape;

import com.badlogic.gdx.utils.Align;
import com.hro.hrogame.gameobject.GameObject;
import com.hro.hrogame.primitives.Point;

import java.util.List;

/**
 * Created by Lion on 8/15/17.
 */
public abstract class Shape {

    public abstract boolean isPointInShape(Point point);
    public abstract Point getCenterPoint();
    public abstract void setPosition(float x, float y);

    public final List<GameObject> filterUnitsInShape(List<GameObject> list) {
        for (int i = 0; i < list.size(); i++) {
            GameObject current = list.get(i);
            Point currentCenter = new Point(current.getX(Align.center), current.getY(Align.center));
            if (!isPointInShape(currentCenter)) {
                list.remove(current);
                i--;
            }
        }

        vle(new CircleShape(), new Shape() {
            @Override
            public boolean isPointInShape(Point point) {
                return false;
            }

            @Override
            public Point getCenterPoint() {
                return null;
            }

            @Override
            public void setPosition(float x, float y) {

            }
        });

        return list;
    }


    public void vle(Shape... args) {

    }
}
