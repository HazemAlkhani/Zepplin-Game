package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class EnvironmentalManager {
    private static final float WIND_MAX_SPEED = 8f;
    private static final float WIND_CHANGE_FREQUENCY = 0.005f;
    private static final float MAX_WIND_CHANGE = 0.5f;
    private static final float LERP_FACTOR = 0.002f;
    private static final float GUST_PROBABILITY = 0.002f;
    private static final float CLOUD_SPEED_MULTIPLIER = 20f;

    private final Vector2 wind = new Vector2();
    private final Random random = new Random();
    private final Vector2 targetWind = new Vector2();
    private final Sound windSound;
    private final float windVolume;

    private final List<Cloud> clouds;

    public EnvironmentalManager(Sound windSound, float windVolume, Texture cloudTexture, int level) {
        this.windSound = windSound;
        this.windVolume = windVolume;
        this.wind.set(0, 0);
        this.targetWind.set((random.nextFloat() - 0.5f) * 2 * MAX_WIND_CHANGE, (random.nextFloat() - 0.5f) * 2 * MAX_WIND_CHANGE);
        this.targetWind.limit(WIND_MAX_SPEED);
        this.clouds = new ArrayList<>();

        int numClouds = 75 * level;

        for (int i = 0; i < numClouds; i++) {
            float startX = random.nextInt(Gdx.graphics.getWidth());
            float startY = random.nextInt(Gdx.graphics.getHeight());
            float cloudWidth = 100;
            float cloudHeight = 45;
            clouds.add(new Cloud(cloudTexture, startX, startY, cloudWidth, cloudHeight));
        }
    }

    public void update(Player player, float delta) {
        adjustWindConditions();
        player.applyWindEffect(wind);
        for (Cloud cloud : clouds) {
            cloud.update(wind, delta * CLOUD_SPEED_MULTIPLIER);
        }

    }

    private void adjustWindConditions() {
        if (random.nextFloat() < WIND_CHANGE_FREQUENCY) {
            float randomChangeX = (random.nextFloat() - 0.5f) * MAX_WIND_CHANGE;
            float randomChangeY = (random.nextFloat() - 0.5f) * MAX_WIND_CHANGE;
            targetWind.set(randomChangeX, randomChangeY);
            targetWind.limit(WIND_MAX_SPEED);
            windSound.play(windVolume);
        }

        wind.lerp(targetWind, LERP_FACTOR);

        if (random.nextFloat() < GUST_PROBABILITY) {
            targetWind.add((random.nextFloat() - 0.5f) * 2 * MAX_WIND_CHANGE,
                    (random.nextFloat() - 0.5f) * 2 * MAX_WIND_CHANGE);
            targetWind.limit(WIND_MAX_SPEED);
            windSound.play(windVolume);
        }
    }

    public void draw(SpriteBatch batch) {
        for (Cloud cloud : clouds) {
            cloud.draw(batch);
        }
    }

    public void drawCompass(ShapeRenderer shapeRenderer, float x, float y) {
        float radius = 40;
        float angle = wind.angleRad();
        float needleLength = 40;

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.BLACK);
        shapeRenderer.circle(x, y, radius);
        shapeRenderer.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.rectLine(
                x,
                y,
                x + (float) Math.cos(angle) * needleLength,
                y + (float) Math.sin(angle) * needleLength,
                3
        );
        shapeRenderer.end();
    }

    public Vector2 getWind() {
        return wind;
    }

    public int getNumClouds() {
        return clouds.size();
    }

    private static class Cloud {
        private final Texture texture;
        private final Vector2 position;
        private final Vector2 velocity;
        private final float width;
        private final float height;

        public Cloud(Texture texture, float startX, float startY, float width, float height) {
            this.texture = texture;
            this.position = new Vector2(startX, startY);
            this.velocity = new Vector2(0, 0);
            this.width = width;
            this.height = height;
        }

        public void update(Vector2 wind, float delta) {
            velocity.set(wind);
            position.add(velocity.x * delta, velocity.y * delta);

            if (position.x > Gdx.graphics.getWidth()) position.x = -width;
            if (position.x < -width) position.x = Gdx.graphics.getWidth();
            if (position.y > Gdx.graphics.getHeight()) position.y = -height;
            if (position.y < -height) position.y = Gdx.graphics.getHeight();
        }

        public void draw(SpriteBatch batch) {
            batch.draw(texture, position.x, position.y, width, height);
        }
    }
}
