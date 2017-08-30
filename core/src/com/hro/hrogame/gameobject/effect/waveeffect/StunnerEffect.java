package com.hro.hrogame.gameobject.effect.waveeffect;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.utils.Align;
import com.hro.hrogame.constants.ParametersConstants;
import com.hro.hrogame.controller.EntityManager;
import com.hro.hrogame.data.bullet.BulletData;
import com.hro.hrogame.data.effect.residualeffectdata.StunOverTimeEffectData;
import com.hro.hrogame.data.effect.waveeffectdata.StunnerEffectData;
import com.hro.hrogame.gameobject.GameObject;
import com.hro.hrogame.gameobject.bullet.BulletListener;
import com.hro.hrogame.gameobject.bullet.BulletType;
import com.hro.hrogame.gameobject.bullet.WaveBullet;
import com.hro.hrogame.gameobject.effect.Effect;
import com.hro.hrogame.gameobject.effect.residualeffect.StunOverTimeEffect;
import com.hro.hrogame.stage.GameStage;
import com.hro.hrogame.stage.LayerType;
import java.util.List;

public class StunnerEffect extends Effect {

    // region Instance fields
    private StunnerEffectData data;
    private ParticleEffect stunnerBulletParticleEffect;
    // endregion

    // region C-tor
    public StunnerEffect(GameObject owner, EntityManager entityManager, StunnerEffectData data) {
        super(owner, entityManager);
        this.data = data;
        stunnerBulletParticleEffect = new ParticleEffect();
        stunnerBulletParticleEffect.load(Gdx.files.internal("stunner_particle"), Gdx.files.internal(""));
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
        bullet.initialize(new BulletData(-1, 100, 0));
        bullet.setFuzzPosition(owner.getX(Align.center), owner.getY(Align.center));
        bullet.setBulletAnimation(stunnerBulletParticleEffect);
        bullet.setPlayerRace(owner.getPlayerType());
        bullet.addBulletListener(new BulletListener() {
            @Override
            public void onHit(List<GameObject> hitUnitList) {
                for (final GameObject target : hitUnitList) {
                    acquireStunOverTimeEffect(target);
                }
            }
        });
        GameStage stage = (GameStage) getStage();
        if (getStage() == null) throw new RuntimeException("Effect must be added to the stage to function and create bullets.");
        stage.addActor(bullet, LayerType.FOREGROUND);
    }
    // endregion

    // region Create
    private StunOverTimeEffect createStunOverTimeEffect(GameObject target) {
        StunOverTimeEffectData data = new StunOverTimeEffectData(ParametersConstants.STUN_OVER_TIME_EFFECT_DURATION);
        return new StunOverTimeEffect(target, entityManager, data);
    }
    private void acquireStunOverTimeEffect(GameObject target) {
        StunOverTimeEffect effect = target.isEffectAcquired(StunOverTimeEffect.class);
        if (effect == null) target.addEffect(createStunOverTimeEffect(target));
        else effect.reNew();
    }
    // endregion

    // region Getters
    @Override
    protected float getCoolDown() {
        return data.cooldown;
    }
    // endregion
}
