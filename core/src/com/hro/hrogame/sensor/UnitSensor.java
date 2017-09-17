package com.hro.hrogame.sensor;

import com.hro.hrogame.controller.ShapeEntityProvider;
import com.hro.hrogame.filter.UnitFilter;
import com.hro.hrogame.gameobject.Entity;
import com.hro.hrogame.gameobject.GameObject;
import com.hro.hrogame.shape.Shape;

import java.util.ArrayList;
import java.util.List;

public abstract class UnitSensor extends Entity {

    // region Instance fields
    protected GameObject owner;
    private UnitFilter unitFilter;
    private ShapeEntityProvider entityProvider;
    private ArrayList<GameObject> unitList = new ArrayList<>();
    // endregion

    // region C-tor
    public UnitSensor(GameObject owner, ShapeEntityProvider entityProvider) {
        this.owner = owner;
        this.entityProvider = entityProvider;
    }
    // endregion

    // region Obtain
    public List<GameObject> obtainEnemies(int unitLimit) {
        entityProvider.obtainEnemies(unitList, this);
        unitFilter.filterUnitList(unitList, unitLimit);
        return unitList;
    }
    public List<GameObject> obtainEnemies() {
        entityProvider.obtainEnemies(unitList, this);
        unitFilter.filterUnitList(unitList);
        return unitList;
    }
    public List<GameObject> obtainAllies(int unitLimit) {
        entityProvider.obtainAllies(unitList, this);
        unitFilter.filterUnitList(unitList, unitLimit);
        return unitList;
    }
    public List<GameObject> obtainAllies() {
        entityProvider.obtainAllies(unitList, this);
        unitFilter.filterUnitList(unitList);
        return unitList;
    }
    public List<GameObject> obtainAll() {
        entityProvider.obtainAll(unitList, this);
        unitFilter.filterUnitList(unitList);
        return unitList;
    }
    // endregion

    // region Shape methods
    public final List<GameObject> filterUnitsInShape(List<GameObject> list) {
        return getShape().filterUnitsInShape(list);
    }
    void updateShapePosition() {
        coordinateConvertVector.set(getWidth() / 2, getHeight() / 2);
        localToStageCoordinates(coordinateConvertVector);
        getShape().setPosition(coordinateConvertVector.x, coordinateConvertVector.y);
    }
    // endregion

    // region Setter
    public void setUnitFilter(UnitFilter unitFilter) {
        this.unitFilter = unitFilter;
    }
    public void setOwner(GameObject owner) {
        this.owner = owner;
    }
    @Override
    public void setPosition(float x, float y) {
        super.setPosition(x, y);
        updateShapePosition();
    }
    @Override
    public void setPosition(float x, float y, int alignment) {
        super.setPosition(x, y, alignment);
        updateShapePosition();
    }
    @Override
    public void setX(float x) {
        super.setX(x);
        updateShapePosition();
    }
    @Override
    public void setY(float y) {
        super.setY(y);
        updateShapePosition();
    }
    // endregion

    // region Getter
    public abstract Shape getShape();
    public GameObject getOwner() {
        return owner;
    }
    @Override
    public GameObject revealOwningUnit() {
        return owner.revealOwningUnit();
    }
    // endregion
}
