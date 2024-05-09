package com.mygdx.game;

import com.badlogic.gdx.math.Vector2;

public class Player {
    private final Vector2 position;
    private final Vector2 velocity;
    private float speed = 1; // Default starting speed (min speed)
    private boolean movingLeft = false;

    public Player(float startX, float startY) {
        this.position = new Vector2(startX, startY);
        this.velocity = new Vector2(-1, 0); // Initialize to the left but not moving yet
    }

    /**
     * Adjusts the player's speed by a given increment while ensuring it doesn't drop below 1.
     * @param increment Positive to increase, negative to decrease speed.
     */
    public void adjustSpeed(float increment) {
        // Maximum speed limit
        float maxSpeed = 20;
        // Minimum speed limit after movement starts
        float minSpeed = 1;
        speed = Math.max(minSpeed, Math.min(maxSpeed, speed + increment));
        System.out.println("Adjusted Speed: " + speed);
    }

    /**
     * Starts continuous movement to the left if not already moving.
     */
    public void startMovingLeft() {
        if (!movingLeft) {
            velocity.set(-1, 0); // Move left continuously
            movingLeft = true;
        }
    }

    /**
     * Sets the player's velocity while maintaining current speed limits.
     * @param vx The horizontal component of the velocity.
     * @param vy The vertical component of the velocity.
     */
    public void setVelocity(float vx, float vy) {
        velocity.set(vx, vy);
    }

    /**
     * Updates the player's position based on the velocity and elapsed time.
     * @param delta Time elapsed since the last frame in seconds.
     */
    public void update(float delta) {
        System.out.println("Before Update - Position: " + position + ", Velocity: " + velocity);

        // Apply the velocity to the position based on speed
        position.add(velocity.x * speed * delta, velocity.y * speed * delta);

        System.out.println("After Update - Position: " + position);
    }

    // Getter methods for position, velocity, and speed
    public Vector2 getPosition() {
        return position.cpy();
    }

    public Vector2 getVelocity() {
        return velocity.cpy();
    }

    public float getVelocityX() {
        return velocity.x;
    }

    public void getVelocityY() {
    }

    public float getSpeed() {
        return speed;
    }

    public void applyWindEffect(Vector2 wind) {

    }
}
