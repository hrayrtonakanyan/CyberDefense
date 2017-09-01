package com.hro.hrogame.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.Align;
import com.hro.hrogame.gameobject.GameObject;
import com.hro.hrogame.primitives.Point;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Util {

    public static void cleanScreen() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    public static List<GameObject> closestGameObjectList(final Point self, List<GameObject> gameObjectList) {
        Collections.sort(gameObjectList, new Comparator<GameObject>() {
            @Override
            public int compare(GameObject o1, GameObject o2) {
                float d1 = calculateDistance(self, o1);
                float d2 = calculateDistance(self, o2);
                if (d1 > d2) return 1;
                if (d1 < d2) return -1;
                return 0;
            }
        });
        return gameObjectList;
    }

    public static float calculateDistance(GameObject obj1, GameObject obj2) {
        return (float)Math.sqrt(Math.pow(obj2.getX(Align.center) - obj1.getX(Align.center), 2) + Math.pow(obj2.getY(Align.center) - obj1.getY(Align.center), 2));
    }
    public static float calculateDistance(Point obj1, GameObject obj2) {
        return (float)Math.sqrt(Math.pow(obj2.getX(Align.center) - obj1.x, 2) + Math.pow(obj2.getY(Align.center) - obj1.y, 2));
    }

}
