package com.hro.hrogame.gameobject.effect.cannoneffect;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Align;
import com.hro.hrogame.constants.ParametersConstants;
import com.hro.hrogame.controller.EntityManager;
import com.hro.hrogame.controller.SoundController;
import com.hro.hrogame.controller.SoundType;
import com.hro.hrogame.data.bullet.BulletData;
import com.hro.hrogame.data.effect.cannoneffectdata.CannonEffectData;
import com.hro.hrogame.gameobject.GameObject;
import com.hro.hrogame.gameobject.bullet.BulletListener;
import com.hro.hrogame.gameobject.bullet.BulletType;
import com.hro.hrogame.gameobject.bullet.TargetBullet;
import com.hro.hrogame.stage.GameStage;
import com.hro.hrogame.stage.LayerType;
import com.hro.hrogame.utils.Util;

import java.util.List;

public class HardCannonEffect extends CannonEffect {

    // region Static fields
    public static final String BULLET_TEXTURE_PATH = "hard_bullet.png";
    public static final float BULLET_HEIGHT = Gdx.graphics.getWidth() / 60;
    public static final float BULLET_WIDTH = BULLET_HEIGHT * 2;
    public static final int BULLET_HIT_UNIT_LIMIT = 3;
    public static final float BULLET_SPEED = Gdx.graphics.getWidth() / 5;

    public static final int INITIAL_WEIGHT = 10;
    public static final float COOLDOWN = 8;
    public static final float MIN_COOLDOWN = 3;
    public static final float DAMAGE = 15;
    public static final float MAX_DAMAGE = 100;
    public static final int TARGET_LIMIT = 1;
    public static final int MAX_TARGET_LIMIT = 1;
    public static final int BULLET_SPLASH_AREA_RADIUS = Gdx.graphics.getHeight() / 5;
    public static final int SENSOR_RADIUS_FOR_BASE = Gdx.graphics.getWidth() / 5;
    public static final int SENSOR_RADIUS_FOR_TANK = Gdx.graphics.getWidth() / 8;
    // endregion

    // region C-tor
    public HardCannonEffect(GameObject owner, EntityManager entityManager, SoundController soundController, CannonEffectData data) {
        super(owner, entityManager, soundController, data);
    }
    // endregion

    // region Shoot
    @Override
    protected void shootABullet(GameObject target) {
        TargetBullet bullet = (TargetBullet) entityManager.createBullet(BulletType.TARGET_BULLET);
        BulletData bulletData = new BulletData(HardCannonEffect.BULLET_TEXTURE_PATH,
                                               HardCannonEffect.BULLET_HIT_UNIT_LIMIT,
                                               HardCannonEffect.BULLET_SPEED,
                                               HardCannonEffect.BULLET_SPLASH_AREA_RADIUS);
        bullet.initialize(bulletData);
        bullet.setSize(BULLET_WIDTH, BULLET_HEIGHT);
        bullet.setPosition(getFiringPointX(), getFiringPointY(), Align.center);
        bullet.setPlayerRace(owner.getPlayerType());
        bullet.shoot(target);
        bullet.addBulletListener(new BulletListener() {
            @Override
            public void onHit(List<GameObject> hitUnitList) {
                soundController.play(SoundType.HARD_CANNONBALL);
                for (final GameObject target : hitUnitList) {
                    target.takeDamage(owner, data.damage.current);
                }
            }
        });
        GameStage stage = (GameStage) getStage();
        if (getStage() == null) throw new RuntimeException("Effect must be added to the stage to function and create bullets.");
        stage.addActor(bullet, LayerType.FOREGROUND);
        soundController.play(SoundType.HARD_CANNON);
    }
    // endregion

    // region Level up
    @Override
    public void levelUpEffect(int level)  {
        data.weight = Util.calcProgressAndDefineWeight(INITIAL_WEIGHT, level, ParametersConstants.PROGRESS_RATIO,
                true, data.cooldown, data.damage, data.targetLimit);
    }
    // endregion
}
