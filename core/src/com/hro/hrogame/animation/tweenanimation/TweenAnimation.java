package com.hro.hrogame.animation.tweenanimation;

import aurelienribon.tweenengine.*;
import com.badlogic.gdx.scenes.scene2d.Actor;
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
                .push(Tween.to(actor, VANISHING, duration)
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

    public static Timeline bounce(Actor actor, float duration, TweenManager manager, final AnimationListener listener) {
        float height = actor.getHeight();
        float width = actor.getWidth();
        return Timeline.createSequence().beginSequence()
                .push(Timeline.createParallel().beginParallel()
                        .push(Tween.to(actor, CHANGE_HEIGHT, 0.5f)
                                .target(actor.getHeight() - height / 2))
                        .push(Tween.to(actor, CHANGE_WIDTH, 0.5f)
                                .target(actor.getWidth() + width / 2))
                        .end())
                .push(Timeline.createParallel().beginParallel()
                        .push(Tween.to(actor, CHANGE_HEIGHT, 0.4f)
                                .target(actor.getHeight() + height / 4))
                        .push(Tween.to(actor, CHANGE_WIDTH, 0.4f)
                                .target(actor.getWidth() - width / 4))
                        .end())
                .push(Timeline.createParallel().beginParallel()
                        .push(Tween.to(actor, CHANGE_HEIGHT, 0.3f)
                                .target(actor.getHeight()))
                        .push(Tween.to(actor, CHANGE_WIDTH, 0.3f)
                                .target(actor.getWidth()))
                        .end())
                .end()
                .setCallback(new TweenCallback() {
                    @Override
                    public void onEvent(int i, BaseTween<?> baseTween) {
                        if (listener != null) listener.onComplete();
                    }
                })
                .start(manager);
    }
}
