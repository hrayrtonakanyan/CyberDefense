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
import com.hro.hrogame.data.effect.waveeffectdata.StunnerEffectData;
import com.hro.hrogame.gameobject.GameObject;
import com.hro.hrogame.gameobject.bullet.BulletListener;
import com.hro.hrogame.gameobject.bullet.BulletType;
import com.hro.hrogame.gameobject.bullet.WaveBullet;
import com.hro.hrogame.gameobject.effect.Effect;
import com.hro.hrogame.gameobject.effect.EffectType;
import com.hro.hrogame.gameobject.effect.residualeffect.StunOverTimeEffect;
import com.hro.hrogame.stage.GameStage;
import com.hro.hrogame.stage.LayerType;
import com.hro.hrogame.utils.Util;

import java.util.List;

public class StunnerEffect extends Effect {

    // region Static fields
    public static final int INITIAL_WEIGHT = 10;
    public static final float COOLDOWN = 30;
    public static final float MIN_COOLDOWN = 15;
    public static final int SENSOR_RADIUS_FOR_TANK = Gdx.graphics.getWidth() / 5;
    // endregion

    // region Instance fields
    private StunnerEffectData data;
    private ParticleEffect stunnerBulletParticleEffect;
    // endregion

    // region C-tor
    public StunnerEffect(Skin skin, GameObject owner, EntityManager entityManager, SoundController soundController, StunnerEffectData data) {
        super(skin, owner, entityManager, soundController);
        this.data = data;
        levelUpEffect(owner.getLevel());
        stunnerBulletParticleEffect = new ParticleEffect();
        stunnerBulletParticleEffect.load(Gdx.files.internal("stunner_particle"), skin.getAtlas());
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
        soundController.play(SoundType.STUNNER);
    }
    // endregion

    // region Acquire
    private void acquireStunOverTimeEffect(GameObject target) {
        StunOverTimeEffect effect = target.isEffectAcquired(StunOverTimeEffect.class);
        if (effect == null) {
            effect = (StunOverTimeEffect) entityManager.createEffect(target, EffectType.STUN_OVER_TIME);
            effect.levelUpEffect(owner.getLevel());
            target.addEffect(effect);
        }
        else effect.reNew(owner.getLevel());
    }
    // endregion

    // region Level Up
    @Override
    public void levelUpEffect(int level) {
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
