package com.hro.hrogame.shape;

import com.badlogic.gdx.utils.Align;
import com.hro.hrogame.gameobject.GameObject;
import com.hro.hrogame.primitives.Point;

import java.util.List;

public abstract class Shape {

    // region Filter
    public final List<GameObject> filterUnitsInShape(List<GameObject> list) {
        for (int i = 0; i < list.size(); i++) {
            GameObject current = list.get(i);
            Point currentCenter = new Point(current.getX(Align.center), current.getY(Align.center));
            if (!isPointInShape(currentCenter)) {
                list.remove(current);
                i--;
            }
        }
        return list;
    }
    // endregion

    // region Abstract
    public abstract void setPosition(float x, float y);
    public abstract boolean isPointInShape(Point point);
    public abstract Point getCenterPoint();
    // endregion
}
