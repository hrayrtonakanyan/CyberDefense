package com.hro.hrogame.controller;

import com.hro.hrogame.gameobject.GameObject;
import com.hro.hrogame.sensor.UnitSensor;

import java.util.List;

public interface ShapeEntityProvider {


    List<GameObject> obtainAll(List<GameObject> list, UnitSensor sensor);
    List<GameObject> obtainEnemies(List<GameObject> list, UnitSensor sensor);
    List<GameObject> obtainAllies(List<GameObject> list, UnitSensor sensor);
}
