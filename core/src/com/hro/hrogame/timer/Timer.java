package com.hro.hrogame.timer;

import java.util.ArrayList;

public class Timer {

    // region Instance fields
    private final ArrayList<TimerTask> taskList = new ArrayList<TimerTask>();
    private boolean isPaused;
    // endregion

    // region Create task
    public Task createTask(float delay, float interval, int repetitionCount, Runnable action) {
        return new TimerTask(delay, interval, repetitionCount, action);
    }
    public Task createTask(float delay, float interval, Runnable action) {
        return new TimerTask(delay, interval, action);
    }
    public Task createTask(float delay, Runnable action) {
        return new TimerTask(delay, action);
    }
    // endregion

    // region Update
    public void update(float delta) {
        if (isPaused) return;
        for (int i = 0; i < taskList.size(); i++) {
            TimerTask task = taskList.get(i);
            task.update(delta);
            if (task.isComplete()) {
                taskList.remove(i);
                i--;
            }
        }
    }
    // endregion

    // region Schedule task
    public void scheduleTask(Task task) {
        taskList.add(validateNotScheduledTask(task));
    }
    public void scheduleTask(Task task, Runnable onCompleteAction) {
        TimerTask timerTask = validateNotScheduledTask(task);
        timerTask.setCompleteListener(onCompleteAction);
        taskList.add(timerTask);
    }
    // endregion

    // region Validate scheduled task
    private TimerTask validateScheduledTask(Task task) {
        if (isScheduled(task)) return (TimerTask) task;
        throw new RuntimeException("Specified task is not scheduled");
    }
    private TimerTask validateNotScheduledTask(Task task) {
        if (!isScheduled(task)) return (TimerTask) task;
        throw new RuntimeException("Specified task is already scheduled.");
    }
    private TimerTask validateTheTask(Task task) {
        if (task == null) throw new RuntimeException("Specified task must not be null.");
        if (!(task instanceof TimerTask)) throw new RuntimeException("You may only create tasks using timer.CreateTask(...). Use of custom implementation of Task is not allowed.");
        return (TimerTask) task;
    }
    // endregion

    // region Remove
    public void remove(Task task) {
        taskList.remove(validateScheduledTask(task));
    }
    public void clear() {
        taskList.clear();
    }
    // endregion

    // region Setter
    public void pause() {
        isPaused = true;
    }
    public void resume() {
        isPaused = false;
    }
    // endregion

    // region Getter
    public boolean isScheduled(Task task) {
        TimerTask validatedTask = validateTheTask(task);
        for (TimerTask timerTask : taskList) {
            if (timerTask.equals(validatedTask)) return true;
        }
        return false;
    }
    public int getTaskCount() {
        return taskList.size();
    }
    // endregion

    // region Inner class
    private class TimerTask implements Task {

        // region Instance fields
        private final float delay;
        private Float interval;
        private Integer repetitionCount;
        private boolean isComplete;
        private float overallElapsedTime;
        private float elapsedTime;
        private boolean isPaused;
        private boolean delayExecutionComplete;
        private final Runnable action;
        private Runnable onComplete;
        // endregion

        // region C-tor
        public TimerTask(float delay, float interval, int repetitionCount, Runnable action) {
            validateParameters(action, delay, interval, repetitionCount);
            this.delay = delay;
            this.interval = interval;
            this.repetitionCount = repetitionCount;
            this.action = action;
        }
        public TimerTask(float delay, float interval, Runnable action) {
            validateParameters(action, delay, interval);
            this.delay = delay;
            this.interval = interval;
            this.action = action;
        }
        public TimerTask(float delay, Runnable action) {
            validateParameters(action, delay);
            this.delay = delay;
            this.action = action;
        }
        private void validateParameters(Runnable action, float... parameters) {
            if (action == null) throw new RuntimeException("The action passed to the Task constructor must not be null");
            for (float parameter : parameters) {
                if (parameter < 0) throw new RuntimeException("Task parameters must not have a negative value.");
            }
        }
        // endregion

        // region Update
        public void update(float delta) {
            if (isComplete) throw new RuntimeException("Completed task must not be updated.");
            if (isPaused) return;
            updateTimeWithDelta(delta);
            if (!updateDelayExecution(delta)) return;
            updateIntervalExecution();
        }
        private boolean updateDelayExecution(float delta) {
            if (delayExecutionComplete) return true;
            if (overallElapsedTime - delta >= delay) return true;
            if (overallElapsedTime + delta <= delay) return false;
            delayExecutionComplete = true;
            return executeTheAction();
        }
        private void updateIntervalExecution() {
            if (interval == null) {
                complete();
                return;
            }
            if (elapsedTime < interval) return;
            executeTheAction();
        }
        private void updateRepetitionCount() {
            if (repetitionCount == null) return;
            repetitionCount--;
            if (repetitionCount == 0) complete();
        }
        private void updateTimeWithDelta(float delta) {
            overallElapsedTime += delta;
            elapsedTime += delta;
        }
        // endregion

        // region Action
        private boolean executeTheAction() {
            action.run();
            elapsedTime = 0;
            updateRepetitionCount();
            return !isComplete();
        }
        private void complete() {
            isComplete = true;
            if (onComplete != null) onComplete.run();
        }
        // endregion

        // region Setter
        @Override
        public void pause() {
            isPaused = true;
        }
        @Override
        public void resume() {
            isPaused = false;
        }
        public void setCompleteListener(Runnable action) {
            onComplete = action;
        }
        // endregion

        // region Getters
        @Override
        public boolean isComplete() {
            return isComplete;
        }
        @Override
        public boolean isPaused() {
            return isPaused;
        }
        @Override
        public boolean isInfinite() {
            return interval != null && repetitionCount == null;
        }
        // endregion
    }
    // endregion
}