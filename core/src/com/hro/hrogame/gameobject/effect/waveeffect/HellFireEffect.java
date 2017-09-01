package com.hro.hrogame.gameobject.effect.waveeffect;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.utils.Align;
import com.hro.hrogame.constants.ParametersConstants;
import com.hro.hrogame.controller.EntityManager;
import com.hro.hrogame.data.bullet.BulletData;
import com.hro.hrogame.data.effect.waveeffectdata.HellFireEffectData;
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

import java.util.List;

public class HellFireEffect extends Effect {

    // region Static field
    public static final int WEIGHT = 10;
    public static final float COOLDOWN = 10;
    public static final float MIN_COOLDOWN = 1;
    public static final float DAMAGE = 50;
    public static final int SENSOR_RADIUS_FOR_TANK = 200;
    // endregion

    // region Instance fields
    private HellFireEffectData data;
    private ParticleEffect hellFireBulletParticleEffect;
    // endregion

    // region C-tor
    public HellFireEffect(GameObject owner, EntityManager entityManager, HellFireEffectData data) {
        super(owner, entityManager);
        this.data = data;
        hellFireBulletParticleEffect = new ParticleEffect();
        hellFireBulletParticleEffect.load(Gdx.files.internal("hell_fire_particle"), Gdx.files.internal(""));
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
        bullet.initialize(new BulletData(null, -1, 100, 0));
        bullet.setFuzzPosition(owner.getX(Align.center), owner.getY(Align.center));
        bullet.setBulletAnimation(hellFireBulletParticleEffect);
        bullet.setPlayerRace(owner.getPlayerType());
        bullet.addBulletListener(new BulletListener() {
            @Override
            public void onHit(List<GameObject> hitUnitList) {
                for (final GameObject target : hitUnitList) {
                    target.takeDamage(owner, data.damage);
                    acquireBurnOverTimeEffect(target);
                }
            }
        });
        GameStage stage = (GameStage) getStage();
        if (getStage() == null) throw new RuntimeException("Effect must be added to the stage to function and create bullets.");
        stage.addActor(bullet, LayerType.FOREGROUND);
    }
    // endregion

    // region Acquire
    private void acquireBurnOverTimeEffect(GameObject target) {
        BurnOverTimeEffect burnOverTimeEffect = target.isEffectAcquired(BurnOverTimeEffect.class);
        if (burnOverTimeEffect == null) {
            FreezeOverTimeEffect freezeOverTimeEffect = target.isEffectAcquired(FreezeOverTimeEffect.class);
            if (freezeOverTimeEffect != null) target.removeEffect(freezeOverTimeEffect);
            burnOverTimeEffect = (BurnOverTimeEffect) entityManager.createEffect(target, EffectType.BURN_OVER_TIME);
            burnOverTimeEffect.setLevel(owner.getLevel());
            target.addEffect(burnOverTimeEffect);
        } else burnOverTimeEffect.reNew(owner.getLevel());
    }
    // endregion

    // region Level Up
    @Override
    public void levelUp(boolean showParticle) {
        if (isMaxLevel) return;
        data.cooldown -= data.cooldown * ParametersConstants.WEIGHT_PROGRESS;
        data.damage += data.damage * ParametersConstants.WEIGHT_PROGRESS;
        if (data.cooldown < MIN_COOLDOWN) {
            data.cooldown = MIN_COOLDOWN;
            isMaxLevel = true;
            return;
        }
        data.weight += data.weight * ParametersConstants.WEIGHT_PROGRESS;
    }
    // endregion

    // region Getters
    @Override
    protected float getCoolDown() {
        return data.cooldown;
    }
    @Override
    public int getWeight() {
        return data.weight;
    }
    // endregion
}
