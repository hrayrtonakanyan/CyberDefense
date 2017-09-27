package com.hro.hrogame.animation.tweenanimation;

import aurelienribon.tweenengine.*;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Align;
import com.hro.hrogame.animation.particleanimation.AnimationListener;

import static com.hro.hrogame.animation.tweenanimation.ActorAccessor.*;

public class TweenAnimation {

    // region Static fields
    public static final float POP_UP_DURATION = 3;
    public static final float POP_UP_MOVE_TARGET = 30;
    public static final float POP_UP_VANISH_TARGET = 0;
    // endregion

    public static Timeline pop_up(Actor actor, float duration, float moveTarget, float vanishTarget,
                                  TweenManager manager, final AnimationListener listener) {
        return Timeline.createParallel().beginParallel()
                .push(Tween.to(actor, MOVE_Y, duration)
                        .ease(TweenEquations.easeOutExpo)
                        .target(actor.getY() + moveTarget))
                .push(Tween.to(actor, VANISH, duration)
                        .target(vanishTarget))
                .end()
                .setCallback(new TweenCallback() {
                    @Override
                    public void onEvent(int i, BaseTween<?> baseTween) {
                        if (listener != null) listener.onComplete();
                    }
                })
                .start(manager);
    }

    public static Timeline animateWaveLabel(Actor actor, float duration, float moveTargetX, float moveTargetY, float scaleTarget,
                                            TweenManager manager, final AnimationListener listener) {
        return Timeline.createParallel().beginParallel()
                .push(Tween.to(actor, MOVE_XY, duration)
                        .target(moveTargetX, moveTargetY))
                .push(Tween.to(actor, SCALE, duration)
                        .target(scaleTarget))
                .end()
                .setCallback(new TweenCallback() {
                    @Override
                    public void onEvent(int i, BaseTween<?> baseTween) {
                        if (listener != null) listener.onComplete();
                    }
                })
                .start(manager);
    }

    public static Timeline bounce(Actor actor, TweenManager manager, final AnimationListener listener) {
        float height = actor.getHeight();
        float width = actor.getWidth();
        float centerX = actor.getX(Align.center);
        float centerY = actor.getY(Align.center);
        return Timeline.createParallel().beginParallel()
                .push(Timeline.createSequence().beginSequence()
                        .push(Timeline.createParallel().beginParallel()
                                .push(Tween.to(actor, CHANGE_HEIGHT, 0.2f)
                                        .target(actor.getHeight() - height / 2))
                                .push(Tween.to(actor, CHANGE_WIDTH, 0.2f)
                                        .target(actor.getWidth() + width / 2))
                                .end())
                        .push(Timeline.createParallel().beginParallel()
                                .push(Tween.to(actor, CHANGE_HEIGHT, 0.1f)
                                        .target(actor.getHeight() + height / 4))
                                .push(Tween.to(actor, CHANGE_WIDTH, 0.1f)
                                        .target(actor.getWidth() - width / 4))
                                .end())
                        .push(Timeline.createParallel().beginParallel()
                                .push(Tween.to(actor, CHANGE_HEIGHT, 0.05f)
                                        .target(actor.getHeight()))
                                .push(Tween.to(actor, CHANGE_WIDTH, 0.05f)
                                        .target(actor.getWidth()))
                                .end())
                        .end())
                .push(Timeline.createParallel().beginParallel()
                        .push(Tween.to(actor, ALIGN_CENTER_X, 0.35f)
                                .target(centerX))
                        .push(Tween.to(actor, ALIGN_CENTER_Y, 0.35f)
                                .target(centerY))
                        .end())
                .setCallback(new TweenCallback() {
                    @Override
                    public void onEvent(int i, BaseTween<?> baseTween) {
                        if (listener != null) listener.onComplete();
                    }
                })
                .start(manager);
    }
}
