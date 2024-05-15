package com.mygdx.game;

import com.badlogic.gdx.math.Vector2;
import java.util.Random;

public class EnvironmentalManager {
    private static final float WIND_MAX_SPEED = 13f; // Maximum wind speed
    private static final float WIND_CHANGE_FREQUENCY = 0.1f; // Probability of wind changing each update
    private static final float MAX_WIND_CHANGE = 5f; // Maximum change in wind speed per update
    private static final float LERP_FACTOR = 0.05f; // Adjust for smoother transitions
    private static final float GUST_PROBABILITY = 0.05f; // Probability of gust occurrence

    private final Vector2 wind = new Vector2();
    private final Random random = new Random();
    private final Vector2 targetWind = new Vector2();

    public EnvironmentalManager() {
        // Initialize with no wind
        this.wind.set(0, 0);
    }

    public void update(Player player) {
        adjustWindConditions();
        player.applyWindEffect(wind);
    }

    private void adjustWindConditions() {
        // Determine if the wind should change
        if (random.nextFloat() < WIND_CHANGE_FREQUENCY) {
            float randomChangeX = (random.nextFloat() - 0.5f) * MAX_WIND_CHANGE;
            float randomChangeY = (random.nextFloat() - 0.5f) * MAX_WIND_CHANGE;
            targetWind.set(randomChangeX, randomChangeY);
            targetWind.limit(WIND_MAX_SPEED);
        }

        // Smoothly adjust wind towards target wind
        wind.lerp(targetWind, LERP_FACTOR);

        // Check for gusts
        if (random.nextFloat() < GUST_PROBABILITY) {
            targetWind.add((random.nextFloat() - 0.5f) * 2 * MAX_WIND_CHANGE,
                    (random.nextFloat() - 0.5f) * 2 * MAX_WIND_CHANGE);
            targetWind.limit(WIND_MAX_SPEED);
        }
    }

    public Vector2 getWind() {
        return wind;
    }
}
