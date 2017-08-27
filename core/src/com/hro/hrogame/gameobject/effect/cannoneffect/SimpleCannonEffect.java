package com.hro.hrogame.gameobject.effect.cannoneffect;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Timer;
import com.hro.hrogame.controller.EntityManager;
import com.hro.hrogame.data.bullet.BulletData;
import com.hro.hrogame.data.effect.cannoneffectdata.SimpleCannonEffectData;
import com.hro.hrogame.gameobject.GameObject;
import com.hro.hrogame.gameobject.bullet.BulletListener;
import com.hro.hrogame.gameobject.bullet.BulletType;
import com.hro.hrogame.gameobject.bullet.TargetBullet;
import com.hro.hrogame.gameobject.effect.Effect;
import com.hro.hrogame.stage.GameStage;
import com.hro.hrogame.stage.LayerType;

import java.util.List;

/**
 * Created by Lion on 8/15/17.
 */
public class SimpleCannonEffect extends Effect {

    // region Instance field
    private SimpleCannonEffectData data;
    // endregion

    // region C-tor
    public SimpleCannonEffect(GameObject owner, EntityManager entityManager, SimpleCannonEffectData data) {
        super(owner, entityManager);
        this.data = data;
        makeAutoExecutable();
    }
    // endregion

    // region Act
    @Override
    public void act(float delta) {
        sensor.setPosition(owner.getWidth() / 2, owner.getHeight() / 2, Align.center);
        super.act(delta);
    }
    // endregion

    // region Execution
    @Override
    protected boolean isExecutable() {
        return sensor.obtainEnemies().size() > 0;
    }
    @Override
    protected void execute() {
        List<GameObject> targetList = sensor.obtainEnemies(data.targetLimit);
        for (GameObject target : targetList) {
            shootABullet(target);
        }
    }
    // endregion

    // region Shoot
    private void shootABullet(GameObject target) {
        TargetBullet bullet = (TargetBullet) entityManager.createBullet(BulletType.TARGET_BULLET);
        bullet.initialize(new BulletData(2, 80, 100));
        // TODO: 8/16/17 Add a shooting point in effect class for the bullets to be instantiated from that point.
        bullet.setPosition(owner.getX(Align.center), owner.getY(Align.center), Align.center);
        bullet.setPlayerRace(owner.getPlayerType());
        bullet.shoot(target);
        bullet.addBulletListener(new BulletListener() {
            @Override
            public void onHit(List<GameObject> hitUnitList) {
                for (final GameObject target : hitUnitList) {
                    target.takeDamage(owner, data.damage);

                    target.setColor(Color.RED);
                    Timer.instance().scheduleTask(new Timer.Task() {
                        @Override
                        public void run() {
                            target.setColor(Color.WHITE);
                        }
                    }, 0.5f);

                    System.out.println("Target Health: " + target.getCurrentHealth() + "/" + target.getMaxHealth());
                }
            }
        });
        GameStage stage = (GameStage) getStage();
        if (getStage() == null) throw new RuntimeException("Effect must be added to the stage to function and create bullets.");
        stage.addActor(bullet, LayerType.FOREGROUND);
    }
    // endregion

    // region Getter
    @Override
    protected float getCoolDown() {
        return data.cooldown;
    }
    // endregion
}
