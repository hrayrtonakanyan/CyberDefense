package com.hro.hrogame.controller;

import com.hro.hrogame.gameobject.PlayerRace;
import com.hro.hrogame.gameobject.GameObject;

import java.util.List;

public interface EntityProvider extends ShapeEntityProvider {

    List<GameObject> obtainAll(List<GameObject> list);
    List<GameObject> obtainAllEnemies(PlayerRace requester, List<GameObject> list);
    List<GameObject> obtainAllAllies(GameObject requester, List<GameObject> list);

}
