package com.mygdx.game;

import com.badlogic.gdx.math.Vector2;

public class Player {
    private final Vector2 position;
    private final Vector2 velocity;
    public boolean canMove;
    private float speed = 0;
    private boolean gameStarted = false;
    private boolean paused = false;

    public Player(float startX, float startY) {
        this.position = new Vector2(startX, startY);
        this.velocity = new Vector2(0, 0);
    }

    public void startMoving() {
        if (!canMove && !paused) {
            canMove = true;
            gameStarted = true;
            speed = 1; // Ensure speed is never zero once started
            velocity.x = -speed;
        }
    }

    public boolean hasGameStarted() {
        return gameStarted;
    }

    public void moveUp() {
        if (canMove && !paused) {
            velocity.y = speed;
        }
    }

    public void moveDown() {
        if (canMove && !paused) {
            velocity.y = -speed;
        }
    }

    public void update(float delta) {
        if (canMove && !paused) {
            position.add(velocity.x * delta, velocity.y * delta);
            if (velocity.y != 0) {
                velocity.y *= 0.9f;
            }
        }
    }

    public void adjustSpeed(float increment) {
        if (canMove && !paused) {
            float maxSpeed = 20;
            speed = Math.max(1, Math.min(maxSpeed, speed + increment));
            velocity.x = -speed;
        }
    }

    public void reset(float startX, float startY) {
        position.set(startX, startY);
        velocity.set(0, 0);
        speed = 0;
        canMove = false;
        gameStarted = false;
        paused = false; // Reset paused state
    }

    public Vector2 getPosition() {
        return position.cpy();
    }

    public float getSpeed() {
        return speed;
    }

    public boolean isAtEndpoint(Vector2 endpoint) {
        return position.dst(endpoint) < 10;
    }

    public boolean isOutOfBounds(float leftBound) {
        return position.x < leftBound || position.x > 800 || position.y < 0 || position.y > 600;
    }

    public void applyWindEffect(Vector2 wind) {
        if (!paused) {
            // Add the wind vector to the current velocity
            velocity.add(wind);
        }
    }

    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }
}
