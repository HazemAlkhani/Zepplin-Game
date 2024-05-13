
package com.mygdx.game;

import com.badlogic.gdx.math.Vector2;
import java.util.Random;

public class EnvironmentalManager {
    private final Vector2 wind = new Vector2();
    private final Random random = new Random();
    private  float windMaxSpeed = 13; // Maximum wind speed
    private  float windChangeFrequency = 0.1f; // Probability of wind changing each update
    private  float maxWindChange = 5; // Maximum change in wind speed per update
    private Vector2 targetWind = new Vector2();
    private float lerpFactor = 0.05f; // Adjust for smoother transitions


    public EnvironmentalManager() {
        // Initialize with no wind
        this.wind.set(0, 0);
    }

    public void update(float delta, Player player) {
        adjustWindConditions(delta);
        player.applyWindEffect(wind);
    }

    private void adjustWindConditions(float delta) {
        if (random.nextFloat() < windChangeFrequency) {
            float randomChangeX = (random.nextFloat() - 0.5f) * maxWindChange;
            float randomChangeY = (random.nextFloat() - 0.5f) * maxWindChange;
            targetWind.set(randomChangeX, randomChangeY);
            targetWind.limit(windMaxSpeed);
        }

        // Smoothly adjust wind towards target wind
        wind.lerp(targetWind, lerpFactor);

        // Check for gusts
        if (random.nextFloat() < 0.05f) {
            targetWind.add((random.nextFloat() - 0.5f) * 2 * maxWindChange, (random.nextFloat() - 0.5f) * 2 * maxWindChange);
            targetWind.limit(windMaxSpeed);
        }
    }

    public Vector2 getWind() {
        return wind;
    }
}
