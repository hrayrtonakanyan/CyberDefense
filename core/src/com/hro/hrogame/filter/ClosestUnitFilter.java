package com.hro.hrogame.filter;

import com.hro.hrogame.gameobject.GameObject;
import com.hro.hrogame.sensor.UnitSensor;
import com.hro.hrogame.utils.Util;

import java.util.List;

/**
 * Created by Lion on 8/14/17.
 */
public class ClosestUnitFilter extends UnitFilter {

    public ClosestUnitFilter(UnitSensor owner) {
        super(owner);
    }

    @Override
    public List<GameObject> filterUnitList(List<GameObject> unitList, int unitLimit) {
        return filter(unitList, unitLimit);
    }
    @Override
    public List<GameObject> filterUnitList(List<GameObject> unitList) {
        return filter(unitList, unitList.size());
    }

    private List<GameObject> filter(List<GameObject> unitList, int unitLimit) {
        List<GameObject> filteredList = Util.closestGameObjectList(owner.getShape().getCenterPoint(), unitList);
        removeDeadUnits(unitList);
        if (unitLimit >= filteredList.size()) return filteredList;
        while (filteredList.size() > unitLimit) {
            filteredList.remove(unitLimit);
        }
        return filteredList;
    }

    private void removeDeadUnits(List<GameObject> unitList) {
        for (int i = 0; i < unitList.size(); i++) {
            GameObject unit = unitList.get(i);
            if (unit.isDead()) {
                unitList.remove(unit);
                i--;
            }
        }

    }
}
