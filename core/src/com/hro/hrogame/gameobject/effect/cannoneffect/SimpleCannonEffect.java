package com.hro.hrogame.gameobject.effect.cannoneffect;

import com.badlogic.gdx.utils.Align;
import com.hro.hrogame.constants.ParametersConstants;
import com.hro.hrogame.controller.EntityManager;
import com.hro.hrogame.data.bullet.BulletData;
import com.hro.hrogame.data.effect.cannoneffectdata.CannonEffectData;
import com.hro.hrogame.gameobject.GameObject;
import com.hro.hrogame.gameobject.bullet.BulletListener;
import com.hro.hrogame.gameobject.bullet.BulletType;
import com.hro.hrogame.gameobject.bullet.TargetBullet;
import com.hro.hrogame.stage.GameStage;
import com.hro.hrogame.stage.LayerType;

import java.util.List;

public class SimpleCannonEffect extends CannonEffect {

    // region Static fields
    public static final String BULLET_TEXTURE_PATH = "bullet.png";
    public static final int WEIGHT = 5;
    public static final float COOLDOWN = 5;
    public static final float MIN_COOLDOWN = 0.5f;
    public static final float DAMAGE = 25;
    public static final int TARGET_LIMIT = 1;
    public static final int SENSOR_RADIUS_FOR_BASE = 80;
    public static final int SENSOR_RADIUS_FOR_TANK = 80;
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
        bullet.initialize(new BulletData("bullet.png", 3, 100, 100));
        // TODO: 8/16/17 Add a shooting point in effect class for the bullets to be instantiated from that point.
        bullet.setPosition(owner.getX(Align.center), owner.getY(Align.center), Align.center);
        bullet.setPlayerRace(owner.getPlayerType());
        bullet.shoot(target);
        bullet.addBulletListener(new BulletListener() {
            @Override
            public void onHit(List<GameObject> hitUnitList) {
                for (final GameObject target : hitUnitList) {
                    target.takeDamage(owner, data.damage);
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
}
