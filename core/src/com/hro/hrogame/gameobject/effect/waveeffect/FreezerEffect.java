package com.hro.hrogame.gameobject.effect.waveeffect;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.utils.Align;
import com.hro.hrogame.controller.EntityManager;
import com.hro.hrogame.data.bullet.BulletData;
import com.hro.hrogame.data.effect.residualeffectdata.FreezeOverTimeEffectData;
import com.hro.hrogame.data.effect.waveeffectdata.FreezerEffectData;
import com.hro.hrogame.gameobject.GameObject;
import com.hro.hrogame.gameobject.bullet.BulletListener;
import com.hro.hrogame.gameobject.bullet.BulletType;
import com.hro.hrogame.gameobject.bullet.WaveBullet;
import com.hro.hrogame.gameobject.effect.Effect;
import com.hro.hrogame.gameobject.effect.residualeffect.BurnOverTimeEffect;
import com.hro.hrogame.gameobject.effect.residualeffect.FreezeOverTimeEffect;
import com.hro.hrogame.stage.GameStage;
import com.hro.hrogame.stage.LayerType;
import java.util.List;
import static com.hro.hrogame.constants.ParametersConstants.*;

public class FreezerEffect extends Effect {

    // region Instance fields
    private FreezerEffectData data;
    private ParticleEffect freezerBulletParticleEffect;
    // endregion

    // region C-tor
    public FreezerEffect(GameObject owner, EntityManager entityManager, FreezerEffectData data) {
        super(owner, entityManager);
        this.data = data;
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
        bullet.initialize(new BulletData(-1, 100, 0));
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

    // region Create
    private FreezeOverTimeEffect createFreezeOverTimeEffect(GameObject target) {
        FreezeOverTimeEffectData data = new FreezeOverTimeEffectData(FREEZE_OVER_TIME_EFFECT_DURATION,
                                                                     FREEZE_OVER_TIME_EFFECT_SPEED_RATIO);
        return new FreezeOverTimeEffect(target, entityManager, data);
    }
    private void acquireFreezeOverTimeEffect(GameObject target) {
        FreezeOverTimeEffect freezeOverTimeEffect = target.isEffectAcquired(FreezeOverTimeEffect.class);
        if (freezeOverTimeEffect == null) {
            BurnOverTimeEffect burnOverTimeEffect = target.isEffectAcquired(BurnOverTimeEffect.class);
            if (burnOverTimeEffect != null) target.removeEffect(burnOverTimeEffect);
            target.addEffect(createFreezeOverTimeEffect(target));
        } else freezeOverTimeEffect.reNew();
    }
    // endregion

    // region Getters
    @Override
    protected float getCoolDown() {
        return data.cooldown;
    }
    // endregion
}
