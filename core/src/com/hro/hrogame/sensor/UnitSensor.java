package com.hro.hrogame.sensor;

import com.hro.hrogame.controller.ShapeEntityProvider;
import com.hro.hrogame.filter.UnitFilter;
import com.hro.hrogame.gameobject.Entity;
import com.hro.hrogame.gameobject.GameObject;
import com.hro.hrogame.shape.Shape;

import java.util.ArrayList;
import java.util.List;

public abstract class UnitSensor extends Entity {

    protected GameObject owner;
    protected UnitFilter unitFilter;
    protected ShapeEntityProvider entityProvider;
    protected ArrayList<GameObject> unitList = new ArrayList<>();


    public UnitSensor(GameObject owner, ShapeEntityProvider entityProvider) {
        this.owner = owner;
        this.entityProvider = entityProvider;
    }

    public abstract Shape getShape();

    @Override
    public GameObject revealOwningUnit() {
        return owner.revealOwningUnit();
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

    protected void updateShapePosition() {
        coordinateConvertVector.set(getWidth() / 2, getHeight() / 2);
        localToStageCoordinates(coordinateConvertVector);
        getShape().setPosition(coordinateConvertVector.x, coordinateConvertVector.y);
    }



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

    public final List<GameObject> filterUnitsInShape(List<GameObject> list) {
        return getShape().filterUnitsInShape(list);
    }

    public GameObject getOwner() {
        return owner;
    }
    public void setOwner(GameObject owner) {
        this.owner = owner;
    }
    public UnitFilter getUnitFilter() {
        return unitFilter;
    }
    public void setUnitFilter(UnitFilter unitFilter) {
        this.unitFilter = unitFilter;
    }
    public ShapeEntityProvider getEntityProvider() {
        return entityProvider;
    }
    public void setEntityProvider(ShapeEntityProvider entityProvider) {
        this.entityProvider = entityProvider;
    }
}
