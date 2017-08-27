package com.hro.hrogame.gameobject.bullet;

import com.hro.hrogame.gameobject.GameObject;

import java.util.List;

/**
 * Created by Lion on 8/16/17.
 */
public interface BulletListener {

    void onHit(List<GameObject> hitUnitList);

}
