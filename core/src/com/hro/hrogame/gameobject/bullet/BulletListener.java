package com.hro.hrogame.gameobject.bullet;

import com.hro.hrogame.gameobject.GameObject;

import java.util.List;

public interface BulletListener {

    void onHit(List<GameObject> hitUnitList);

}
