package com.mygdx.game;

import com.badlogic.gdx.math.Vector2;
import java.util.Random;

public class EnvironmentalManager {
    private final Vector2 wind = new Vector2();
    private final Random random = new Random();

    public EnvironmentalManager() {
        this.wind.set(0, 0); // No wind initially
    }

    public void update(float delta, Player player) {
        // Adjust the wind conditions dynamically
        adjustWindConditions(delta);

        // Apply the wind effect to the player's velocity
        player.applyWindEffect(wind);

        // Log the current wind conditions for debugging
        System.out.println("Current Wind: " + wind);
    }

    private void adjustWindConditions(float delta) {
        // Randomly change the wind direction or speed
        float randomChangeX = (random.nextFloat() - 0.5f) * 0.5f;
        float randomChangeY = (random.nextFloat() - 0.5f) * 0.5f;

        wind.add(randomChangeX * delta, randomChangeY * delta);
        // Cap the maximum wind speed
        float windMaxSpeed = 50;
        wind.limit(windMaxSpeed); // Cap the wind speed

        // Optionally, simulate gusts by adding temporary boosts
        if (random.nextFloat() < 0.1f) { // 10% chance of gust
            wind.add(random.nextFloat() * 10 - 5, random.nextFloat() * 10 - 5);
        }

        // Ensure the wind direction is capped
        wind.limit(windMaxSpeed);
    }

}
