package com.hro.hrogame.filter;

import com.hro.hrogame.gameobject.GameObject;
import com.hro.hrogame.sensor.UnitSensor;

import java.util.List;

public abstract class UnitFilter {

    // region Instance fields
    protected UnitSensor owner;
    // endregion

    // region C-tor
    public UnitFilter(UnitSensor owner) {
        this.owner = owner;
    }
    // endregion

    // region Abstract
    public abstract List<GameObject> filterUnitList(List<GameObject> unitList, int unitLimit);
    public abstract List<GameObject> filterUnitList(List<GameObject> unitList);
    // endregion

    // region Setter
    public void setOwner(UnitSensor owner) {
        this.owner = owner;
    }
    // endregion

    // region Getter
    public UnitSensor getOwner() {
        return owner;
    }
    // endregion
}
