package com.hro.hrogame.gameobject.effect;

import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Timer;
import com.hro.hrogame.controller.EntityManager;
import com.hro.hrogame.gameobject.GameObject;
import com.hro.hrogame.sensor.UnitSensor;

import java.util.ArrayList;
import java.util.List;

public abstract class Effect extends GameObject {

    // region Instance fields
    private List<EffectListener> listeners = new ArrayList<>();
    protected EntityManager entityManager;
    private Timer cooldownTimer = new Timer();
    private Timer.Task cooldownTask = createCooldownTask();
    protected GameObject owner;
    protected UnitSensor sensor;
    private boolean isOverTimeEffect = false;
    private boolean isReady = true;
    private boolean isAutoExecutable;
    // endregion

    // region C-tor
    public Effect(GameObject owner, EntityManager entityManager) {
        this.owner = owner;
        this.entityManager = entityManager;
        setTouchable(Touchable.disabled);
    }
    // endregion

    // region Act
    @Override
    public void act(float delta) {
        if (sensor != null) sensor.setPosition(owner.getWidth() / 2, owner.getHeight() / 2, Align.center);
        super.act(delta);
        if (isReady) for (EffectListener listener : listeners) listener.onReady();
    }
    // endregion

    // region abstract
    protected abstract boolean isExecutable();
    protected abstract void execute();
    public abstract void levelUpEffect(int level);
    protected abstract float getCoolDown();
    public abstract float getEffectWeight();
    // endregion

    // region Execution
    private boolean tryExecute() {
        if (!isEnable) return false;
        if (!isReady) return false;
        if (!isExecutable()) return false;
        for (EffectListener listener : listeners) {
            isReady = false;
            execute();
            cooldownTimer.scheduleTask(cooldownTask, getCoolDown());
            listener.onExecute();
        }
        return true;
    }
    public final boolean executeOnCall() {
        if (isAutoExecutable) throw new RuntimeException("Auto executable effects must not be executed on call.");
        return tryExecute();
    }
    public final void makeAutoExecutable() {
        if (isAutoExecutable) throw new RuntimeException("The effect is already auto-executable.");
        isAutoExecutable = true;
        addEffectListener(new EffectAdapter() {
            @Override
            public void onReady() {
                tryExecute();
            }
        });
    }
    // endregion

    // region Timer functionality
    private Timer.Task createCooldownTask() {
        return new Timer.Task() {
            @Override
            public void run() {
                isReady = true;
            }
        };
    }
    public void clearTimer() {
        cooldownTimer.clear();
    }
    // endregion

    // region Effect listener
    public final void addEffectListener(EffectListener listener) {
        listeners.add(listener);
    }
    // endregion

    // region Setters
    protected void makeEffectOvertime() {
        isOverTimeEffect = true;
    }
    public void setSensor(UnitSensor sensor) {
        this.sensor = sensor;
        addActor(sensor);
    }
    // endregion

    // region Getters
    public boolean isPositionValidForEffect() {
        return isOverTimeEffect || isExecutable();
    }
    public boolean isOverTimeEffect() {
        return isOverTimeEffect;
    }
    public List<EffectListener> getEffectListeners() {
        return listeners;
    }
    @Override
    public GameObject revealOwningUnit() {
        return owner;
    }
    // endregion
}
