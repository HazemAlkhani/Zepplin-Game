package com.mygdx.game;

import com.badlogic.gdx.math.Vector2;

public class Player {
    private Vector2 position;
    private Vector2 target;
    private Vector2 velocity;
    private float speed = 100; // Default speed, can be adjustable

    public Player(float startX, float startY) {
        this.position = new Vector2(startX, startY);
        this.target = new Vector2(startX, startY);
        this.velocity = new Vector2(0, 0); // Initialize velocity to zero
    }

    /**
     * Sets the target location for the player to move towards.
     * @param x The x-coordinate of the target position.
     * @param y The y-coordinate of the target position.
     */
    public void setTarget(float x, float y) {
        this.target.set(x, y);
    }

    /**
     * Overloaded method to set the target using a Vector2 object.
     * @param target The target position as a Vector2.
     */
    public void setTarget(Vector2 target) {
        this.target.set(target);
    }

    /**
     * Sets the velocity for the player.
     * @param vx The horizontal component of the velocity.
     * @param vy The vertical component of the velocity.
     */
    public void setVelocity(float vx, float vy) {
        this.velocity.set(vx, vy);
    }

    /**
     * Applies wind effect to the player's velocity.
     * @param wind The wind vector to be added to the current velocity.
     */
    public void applyWindEffect(Vector2 wind) {
        this.velocity.add(wind);
    }

    /**
     * Updates the player's position based on the target, velocity, and elapsed time.
     * @param delta Time elapsed since the last frame in seconds.
     */
    public void update(float delta) {
        // Check if position is close to target
        if (!position.epsilonEquals(target, 1f)) {
            Vector2 direction = target.cpy().sub(position);
            float distance = direction.len();
            if (distance > speed * delta) {
                direction.nor().scl(speed * delta);
                position.add(direction);
            } else {
                position.set(target);
                velocity.setZero(); // Stop moving once the target is reached
            }
        }
        // Continue applying continuous velocity
        position.add(velocity.x * delta, velocity.y * delta);
    }
    public float getVelocityX() {
        return velocity.x;
    }

    public float getVelocityY() {
        return velocity.y;
    }


    /**
     * Returns a copy of the player's position to prevent external modifications.
     * @return A new Vector2 instance representing the player's position.
     */
    public Vector2 getPosition() {
        return position.cpy();
    }

    /**
     * Returns a copy of the player's target to prevent external modifications.
     * @return A new Vector2 instance representing the player's target.
     */
    public Vector2 getTarget() {
        return target.cpy();
    }

    /**
     * Returns a copy of the player's velocity to prevent external modifications.
     * @return A new Vector2 instance representing the player's velocity.
     */
    public Vector2 getVelocity() {
        return velocity.cpy();
    }

    /**
     * Returns the current speed of the player.
     * @return The speed value.
     */
    public float getSpeed() {
        return speed;
    }

    /**
     * Sets the player's speed.
     * @param speed The new speed value.
     */
    public void setSpeed(float speed) {
        this.speed = speed;
    }
}
