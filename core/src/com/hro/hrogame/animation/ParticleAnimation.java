package com.hro.hrogame.animation;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Align;

/**
 * Created by Lion on 10/13/14.
 */
public class ParticleAnimation extends Actor {

    // region Instance Fields
    private ParticleEffect particleEffect;
    private AnimationListener animationListener;
    private boolean isEnabled;
    private boolean isLooping = false;
    private boolean isInPool = false;
    private boolean isSet = false;
    // endregion

    // region C-tor
    public  ParticleAnimation(ParticleEffect particleEffect) {
        this.particleEffect = new ParticleEffect(particleEffect);
        for (ParticleEmitter emitter : particleEffect.getEmitters()) {
            emitter.setContinuous(true);
        }
    }
    public ParticleAnimation() {}
    // endregion

    // region Overrides
    @Override
    public void act(float delta) {
        if (isEnabled) {
            particleEffect.setPosition(getX(Align.center), getY(Align.center));
            particleEffect.update(delta);
            if (particleEffect.isComplete() && !isLooping) {
                isEnabled = false;
                if (animationListener != null) {
                    particleEffect.dispose();
                    animationListener.onComplete();
                }
            }
        }
    }
    @Override
    public void draw(Batch batch, float parentAlpha) {
        particleEffect.draw(batch);
    }
    // endregion

    // region Logic Functions
    public void start() {
        this.isEnabled = true;
        this.particleEffect.start();
    }
    public void stop() {
        this.isEnabled = false;
    }
    public void reset() {
        this.isEnabled = false;
        this.isSet = false;
        this.particleEffect.reset();
    }
    public void dispose() {
        this.particleEffect.dispose();
    }
    // endregion

    // region Setters
    public void setParticleEffect(ParticleEffect particleEffect) {
        this.particleEffect = new ParticleEffect(particleEffect);
        this.isInPool = false;
        this.isSet = true;
    }
    public void setCompleteListener(AnimationListener animationListener) {
        this.animationListener = animationListener;
    }
    public void setSet(boolean isSet) {
        this.isSet = isSet;
    }
    public void setLooping(boolean isLooping) {
        this.isLooping = isLooping;
    }
    public void setInPool(boolean isInPool) {
        this.isInPool = isInPool;
    }
    // endregion

    // region Getters
    public boolean isComplete() {
        return particleEffect.isComplete();
    }
    public boolean isInPool() {
        return isInPool;
    }
    public boolean isSet() {
        return isSet;
    }
    public boolean isLooping() {
        return isLooping;
    }

    public ParticleEffect getParticleEffect() {
        return particleEffect;
    }
    // endregion
}
