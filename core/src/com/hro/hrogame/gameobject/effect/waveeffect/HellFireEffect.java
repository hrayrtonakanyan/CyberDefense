package com.hro.hrogame.gameobject.effect.waveeffect;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Timer;
import com.hro.hrogame.controller.EntityManager;
import com.hro.hrogame.data.bullet.BulletData;
import com.hro.hrogame.data.effect.residualeffectdata.BurnOverTimeEffectData;
import com.hro.hrogame.data.effect.waveeffectdata.HellFireEffectData;
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

public class HellFireEffect extends Effect {

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
        bullet.initialize(new BulletData(-1, 100, 0));
        bullet.setFuzzPosition(owner.getX(Align.center), owner.getY(Align.center));
        bullet.setBulletAnimation(hellFireBulletParticleEffect);
        bullet.setPlayerRace(owner.getPlayerType());
        bullet.addBulletListener(new BulletListener() {
            @Override
            public void onHit(List<GameObject> hitUnitList) {
                for (final GameObject target : hitUnitList) {
                    target.takeDamage(owner, data.damage);
                    acquireBurnOverTimeEffect(target);


                    target.setColor(Color.RED);
                    Timer.instance().scheduleTask(new Timer.Task() {
                        @Override
                        public void run() {
                            target.setColor(Color.WHITE);
                        }
                    }, 1);

                    System.out.println("Target Health: " + target.getCurrentHealth() + "/" + target.getMaxHealth());
                }
            }
        });
        GameStage stage = (GameStage) getStage();
        if (getStage() == null) throw new RuntimeException("Effect must be added to the stage to function and create bullets.");
        stage.addActor(bullet, LayerType.FOREGROUND);
    }
    // endregion

    // region Create
    private BurnOverTimeEffect createBurnOverTimeEffect(GameObject target) {
        BurnOverTimeEffectData data = new BurnOverTimeEffectData(BURN_OVER_TIME_EFFECT_COOLDOWN,
                                                                 BURN_OVER_TIME_EFFECT_DAMAGE,
                BURN_OVER_TIME_EFFECT_MAX_DAMAGE_AMOUNT);
        return new BurnOverTimeEffect(target, entityManager, data);
    }
    private void acquireBurnOverTimeEffect(GameObject target) {
        BurnOverTimeEffect burnOverTimeEffect = target.isEffectAcquired(BurnOverTimeEffect.class);
        if (burnOverTimeEffect == null) {
            FreezeOverTimeEffect freezeOverTimeEffect = target.isEffectAcquired(FreezeOverTimeEffect.class);
            if (freezeOverTimeEffect != null) target.removeEffect(freezeOverTimeEffect);
            target.addEffect(createBurnOverTimeEffect(target));
        } else burnOverTimeEffect.reNew();
    }
    // endregion

    // region Getters
    @Override
    protected float getCoolDown() {
        return data.cooldown;
    }
    // endregion
}
