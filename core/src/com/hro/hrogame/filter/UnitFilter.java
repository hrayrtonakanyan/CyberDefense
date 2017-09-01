package com.hro.hrogame.filter;

import com.hro.hrogame.gameobject.GameObject;
import com.hro.hrogame.sensor.UnitSensor;

import java.util.List;

public abstract class UnitFilter {

    protected UnitSensor owner;

    public UnitFilter(UnitSensor owner) {
        this.owner = owner;
    }

    public abstract List<GameObject> filterUnitList(List<GameObject> unitList, int unitLimit);
    public abstract List<GameObject> filterUnitList(List<GameObject> unitList);

    public UnitSensor getOwner() {
        return owner;
    }
    public void setOwner(UnitSensor owner) {
        this.owner = owner;
    }
}
