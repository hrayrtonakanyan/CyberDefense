package com.hro.hrogame.gameobject.effect.waveeffect;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.utils.Align;
import com.hro.hrogame.constants.ParametersConstants;
import com.hro.hrogame.controller.EntityManager;
import com.hro.hrogame.data.bullet.BulletData;
import com.hro.hrogame.data.effect.waveeffectdata.FreezerEffectData;
import com.hro.hrogame.gameobject.GameObject;
import com.hro.hrogame.gameobject.bullet.BulletListener;
import com.hro.hrogame.gameobject.bullet.BulletType;
import com.hro.hrogame.gameobject.bullet.WaveBullet;
import com.hro.hrogame.gameobject.effect.Effect;
import com.hro.hrogame.gameobject.effect.EffectType;
import com.hro.hrogame.gameobject.effect.residualeffect.BurnOverTimeEffect;
import com.hro.hrogame.gameobject.effect.residualeffect.FreezeOverTimeEffect;
import com.hro.hrogame.stage.GameStage;
import com.hro.hrogame.stage.LayerType;
import com.hro.hrogame.utils.Util;

import java.util.List;

public class FreezerEffect extends Effect {

    // region Static fields
    public static final int INITIAL_WEIGHT = 10;
    public static final float COOLDOWN = 25;
    public static final float MIN_COOLDOWN = 10;
    public static final int SENSOR_RADIUS_FOR_TANK = Gdx.graphics.getWidth() / 5;
    // endregion

    // region Instance fields
    private FreezerEffectData data;
    private ParticleEffect freezerBulletParticleEffect;
    // endregion

    // region C-tor
    public FreezerEffect(GameObject owner, EntityManager entityManager, FreezerEffectData data) {
        super(owner, entityManager);
        this.data = data;
        levelUpEffect(owner.getLevel());
        freezerBulletParticleEffect = new ParticleEffect();
        freezerBulletParticleEffect.load(Gdx.files.internal("freezer_particle"), Gdx.files.internal(""));
        makeAutoExecutable();
    }
    // endregion

    // region Execution
    @Override
    protected boolean isExecutable() {
        return sensor.obtainEnemies().size() > 0;
    }
    @Override
    protected void execute() {
        WaveBullet bullet = (WaveBullet) entityManager.createBullet(BulletType.WAVE_BULLET);
        bullet.initialize(new BulletData(null, -1, WaveBullet.SPEED, 0));
        bullet.setFuzzPosition(owner.getX(Align.center), owner.getY(Align.center));
        bullet.setBulletAnimation(freezerBulletParticleEffect);
        bullet.setPlayerRace(owner.getPlayerType());
        bullet.addBulletListener(new BulletListener() {
            @Override
            public void onHit(List<GameObject> hitUnitList) {
                for (final GameObject target : hitUnitList) {
                    acquireFreezeOverTimeEffect(target);
                }
            }
        });
        GameStage stage = (GameStage) getStage();
        if (getStage() == null) throw new RuntimeException("Effect must be added to the stage to function and create bullets.");
        stage.addActor(bullet, LayerType.FOREGROUND);
    }
    // endregion

    // region Acquire
    private void acquireFreezeOverTimeEffect(GameObject target) {
        FreezeOverTimeEffect freezeOverTimeEffect = target.isEffectAcquired(FreezeOverTimeEffect.class);
        if (freezeOverTimeEffect == null) {
            BurnOverTimeEffect burnOverTimeEffect = target.isEffectAcquired(BurnOverTimeEffect.class);
            if (burnOverTimeEffect != null) target.removeEffect(burnOverTimeEffect);
            freezeOverTimeEffect = (FreezeOverTimeEffect) entityManager.createEffect(target, EffectType.FREEZE_OVER_TIME);
            freezeOverTimeEffect.levelUpEffect(owner.getLevel());
            target.addEffect(freezeOverTimeEffect);
        } else freezeOverTimeEffect.reNew(owner.getLevel());
    }
    // endregion

    // region Level Up
    @Override
    public void levelUpEffect(int level)  {
        data.weight = Util.calcProgressAndDefineWeight(INITIAL_WEIGHT, level, ParametersConstants.PROGRESS_RATIO,
                true, data.cooldown);
    }
    // endregion

    // region Getters
    @Override
    protected float getCoolDown() {
        return data.cooldown.current;
    }
    @Override
    public float getEffectWeight() {
        return data.weight;
    }
    // endregion
}
