package com.hro.hrogame.gameobject.effect.cannoneffect;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Align;
import com.hro.hrogame.constants.ParametersConstants;
import com.hro.hrogame.controller.EntityManager;
import com.hro.hrogame.data.bullet.BulletData;
import com.hro.hrogame.data.effect.cannoneffectdata.CannonEffectData;
import com.hro.hrogame.gameobject.GameObject;
import com.hro.hrogame.gameobject.bullet.BulletListener;
import com.hro.hrogame.gameobject.bullet.BulletType;
import com.hro.hrogame.gameobject.bullet.TargetBullet;
import com.hro.hrogame.primitives.Point;
import com.hro.hrogame.primitives.ProgressiveAttribute;
import com.hro.hrogame.stage.GameStage;
import com.hro.hrogame.stage.LayerType;
import com.hro.hrogame.utils.Util;

import java.util.List;

public class SimpleCannonEffect extends CannonEffect {

    // region Static fields
    public static final String BULLET_TEXTURE_PATH = "bullet.png";
    public static final int BULLET_HIT_UNIT_LIMIT = 1;
    public static final float BULLET_SPEED = Gdx.graphics.getWidth() / 4;
    public static final int BULLET_SPLASH_AREA_RADIUS = Gdx.graphics.getWidth() / 10;

    public static final int INITIAL_WEIGHT = 5;
    public static final float COOLDOWN = 2;
    public static final float MIN_COOLDOWN = 0.5f;
    public static final float DAMAGE = 25;
    public static final float MAX_DAMAGE = 400;
    public static final int TARGET_LIMIT = 1;
    public static final int MAX_TARGET_LIMIT = 1;
    public static final int SENSOR_RADIUS_FOR_BASE = Gdx.graphics.getWidth() / 3;
    public static final int SENSOR_RADIUS_FOR_TANK = Gdx.graphics.getWidth() / 5;
    // endregion

    // region Static methods
    public static ProgressiveAttribute[] getDataProgressiveAttributes() {
        return new ProgressiveAttribute[] {new ProgressiveAttribute(COOLDOWN, MIN_COOLDOWN),
                                           new ProgressiveAttribute(DAMAGE, MAX_DAMAGE),
                                           new ProgressiveAttribute(TARGET_LIMIT, MAX_TARGET_LIMIT)};
    }
    // endregion

    // region C-tor
    public SimpleCannonEffect(GameObject owner, EntityManager entityManager, CannonEffectData data) {
        super(owner, entityManager, data);
    }
    // endregion

    // region Shoot
    @Override
    protected void shootABullet(GameObject target) {
        TargetBullet bullet = (TargetBullet) entityManager.createBullet(BulletType.TARGET_BULLET);
        BulletData bulletData = new BulletData(SimpleCannonEffect.BULLET_TEXTURE_PATH,
                                               SimpleCannonEffect.BULLET_HIT_UNIT_LIMIT,
                                               SimpleCannonEffect.BULLET_SPEED,
                                               SimpleCannonEffect.BULLET_SPLASH_AREA_RADIUS);
        bullet.initialize(bulletData);
        Point shootingPoint = defineShootingPoint();
        bullet.setPosition(shootingPoint.x, shootingPoint.y, Align.center);
        bullet.setPlayerRace(owner.getPlayerType());
        bullet.shoot(target);
        bullet.addBulletListener(new BulletListener() {
            @Override
            public void onHit(List<GameObject> hitUnitList) {
                for (final GameObject target : hitUnitList) {
                    target.takeDamage(owner, data.damage.current);
                }
            }
        });
        GameStage stage = (GameStage) getStage();
        if (getStage() == null) throw new RuntimeException("Effect must be added to the stage to function and create bullets.");
        stage.addActor(bullet, LayerType.FOREGROUND);
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
