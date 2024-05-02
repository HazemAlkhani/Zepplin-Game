package com.mygdx.game;

import com.badlogic.gdx.math.Vector2;

public class EnvironmentalManager {
    private Vector2 wind = new Vector2();

    public EnvironmentalManager() {

        this.wind.set(0, 0);  // No wind initially
    }

    public void update(float delta, Player player) {
        // Here you can adjust the wind dynamically based on game logic
        // For example, increase wind speed or change direction
        adjustWindConditions(delta);

        // Apply the wind effect to the player
        player.applyWindEffect(wind);
    }

    private void adjustWindConditions(float delta) {
        // Simple placeholder for wind adjustment logic
        wind.add(0.1f * delta, 0.1f * delta); // Gradually increases wind
    }

    // If needed, methods to get the current wind conditions could also be added
}
