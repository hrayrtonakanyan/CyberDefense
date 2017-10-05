package com.hro.hrogame.gameobject.effect.waveeffect;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;
import com.hro.hrogame.constants.ParametersConstants;
import com.hro.hrogame.controller.EntityManager;
import com.hro.hrogame.controller.SoundController;
import com.hro.hrogame.controller.SoundType;
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
import com.hro.hrogame.utils.Util;

import java.util.List;

public class HellFireEffect extends Effect {

    // region Static field
    public static final int INITIAL_WEIGHT = 10;
    public static final float COOLDOWN = 50;
    public static final float MIN_COOLDOWN = 20;
    public static final float DAMAGE = 15;
    public static final float MAX_DAMAGE = 120;
    public static final int SENSOR_RADIUS_FOR_TANK = Gdx.graphics.getWidth() / 5;
    // endregion

    // region Instance fields
    private HellFireEffectData data;
    private ParticleEffect hellFireBulletParticleEffect;
    // endregion

    // region C-tor
    public HellFireEffect(Skin skin, GameObject owner, EntityManager entityManager, SoundController soundController, HellFireEffectData data) {
        super(skin, owner, entityManager, soundController);
        this.data = data;
        levelUpEffect(owner.getLevel());
        hellFireBulletParticleEffect = new ParticleEffect();
        hellFireBulletParticleEffect.load(Gdx.files.internal("hell_fire_particle"), skin.getAtlas());
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
        bullet.initialize(new BulletData(WaveBullet.SPEED));
        bullet.setFuzzPosition(owner.getX(Align.center), owner.getY(Align.center));
        bullet.setBulletAnimation(hellFireBulletParticleEffect);
        bullet.setPlayerRace(owner.getPlayerType());
        bullet.addBulletListener(new BulletListener() {
            @Override
            public void onHit(List<GameObject> hitUnitList) {
                for (final GameObject target : hitUnitList) {
                    target.takeDamage(owner, data.damage.current);
                    acquireBurnOverTimeEffect(target);
                }
            }
        });
        GameStage stage = (GameStage) getStage();
        if (getStage() == null) throw new RuntimeException("Effect must be added to the stage to function and create bullets.");
        stage.addActor(bullet, LayerType.FOREGROUND);
        soundController.play(SoundType.HELL_FIRE);
    }
    // endregion

    // region Acquire
    private void acquireBurnOverTimeEffect(GameObject target) {
        BurnOverTimeEffect burnOverTimeEffect = target.isEffectAcquired(BurnOverTimeEffect.class);
        if (burnOverTimeEffect == null) {
            FreezeOverTimeEffect freezeOverTimeEffect = target.isEffectAcquired(FreezeOverTimeEffect.class);
            if (freezeOverTimeEffect != null) target.removeEffect(freezeOverTimeEffect);
            burnOverTimeEffect = (BurnOverTimeEffect) entityManager.createEffect(target, EffectType.BURN_OVER_TIME);
            burnOverTimeEffect.levelUpEffect(owner.getLevel());
            target.addEffect(burnOverTimeEffect);
        } else burnOverTimeEffect.reNew(owner.getLevel());
    }
    // endregion

    // region Level Up
    @Override
    public void levelUpEffect(int level)  {
        data.weight = Util.calcProgressAndDefineWeight(INITIAL_WEIGHT, level, ParametersConstants.PROGRESS_RATIO,
                true, data.cooldown, data.damage);
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
