package com.mygdx.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.Gdx;

public class Player {
    private final Vector2 position;
    private final Vector2 velocity;
    public boolean canMove;
    private float speed = 0; // Start with no movement


    public Player(float startX, float startY) {
        this.position = new Vector2(startX, startY);
        this.velocity = new Vector2(0, 0); // Initialize without movement
    }


    public void applyWindEffect(Vector2 wind) {
        // Add the wind vector to the current velocity
        Vector2 effectiveVelocity = new Vector2(velocity).add(wind);
        position.add(effectiveVelocity.scl(Gdx.graphics.getDeltaTime()));
    }



    public void startMoving() {
        if (!canMove) {
            canMove = true;
            speed = 0; // Initial speed when starting to move
            velocity.x = -speed; // Moves to the left
        }
    }


    public void moveUp() {
        if (canMove) {
            velocity.y = speed; // Moves up
        }
    }

    public void moveDown() {
        if (canMove) {
            velocity.y = -speed; // Moves down
        }
    }


    public void update(float delta) {
        if (canMove) {
            position.add(velocity.x * delta, 0); // Move horizontally only
        }
    }


    public void adjustSpeed(float increment) {
        if (canMove) {
            float maxSpeed = 20;
            speed = Math.max(0, Math.min(maxSpeed, speed + increment));
            velocity.x = -speed; // Maintain leftward movement
        }
    }


    public void reset(float startX, float startY) {
        position.set(startX, startY);
        velocity.set(0, 0);
        speed = 0;
        canMove = false;
    }



    // Getter methods for position, velocity, and speed
    public Vector2 getPosition() {
        return position.cpy();
    }


    public float getSpeed() {
        return speed;
    }


    public boolean isAtEndpoint(Vector2 endpoint) {
        return position.x <= endpoint.x;
    }

    public boolean isOutOfBounds(float leftBound) {
        return position.x < leftBound;
    }


}
