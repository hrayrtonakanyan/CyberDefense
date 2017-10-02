package com.hro.hrogame.filter;

import com.hro.hrogame.gameobject.GameObject;
import com.hro.hrogame.sensor.UnitSensor;
import com.hro.hrogame.utils.Util;

import java.util.ArrayList;
import java.util.List;

public class ClosestUnitFilter extends UnitFilter {

    // region Instance fields
    private List<GameObject> oldTargetList;
    // endregion

    // region C-tor
    public ClosestUnitFilter(UnitSensor owner) {
        super(owner);
        oldTargetList = new ArrayList<>();
    }
    // endregion

    // region Filter
    @Override
    public List<GameObject> filterUnitList(List<GameObject> unitList, int unitLimit) {
        return filter(unitList, unitLimit);
    }
    @Override
    public List<GameObject> filterUnitList(List<GameObject> unitList) {
        return filter(unitList, unitList.size());
    }
    private List<GameObject> filter(List<GameObject> unitList, int unitLimit) {
        removeDeadUnits(unitList);
        List<GameObject> filteredList = Util.defineClosestGameObjectList(owner.getShape().getCenterPoint(), unitList);
        if (unitLimit >= filteredList.size()) return filteredList;
        if (oldTargetList.size() != 0) {
            removeDeadUnits(oldTargetList);
            for (GameObject unit : oldTargetList) {
                if (filteredList.contains(unit)) {
                    filteredList.remove(unit);
                    filteredList.set(0, unit);
                }
            }
        }
        while (filteredList.size() > unitLimit) filteredList.remove(unitLimit);
        oldTargetList.clear();
        oldTargetList.addAll(filteredList);
        return filteredList;
    }
    // endregion

    // region Remove
    private void removeDeadUnits(List<GameObject> unitList) {
        for (int i = 0; i < unitList.size(); i++) {
            GameObject unit = unitList.get(i);
            if (unit.isDead()) {
                unitList.remove(unit);
                i--;
            }
        }
    }
    // endregion
}