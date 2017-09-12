package com.hro.hrogame.timer;

public interface Task {

    boolean  isComplete();
    boolean isPaused();
    boolean isInfinite();
    void pause();
    void resume();
}
