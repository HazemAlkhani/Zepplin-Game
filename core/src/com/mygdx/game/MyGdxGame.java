package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.assets.AssetManager;

public class MyGdxGame extends Game {
	private SpriteBatch batch;
	private AssetManager assetManager;
	private Texture mapTexture;
	private Texture zeppelinTexture;

	@Override
	public void create() {
		batch = new SpriteBatch();
		assetManager = new AssetManager();

		// Load textures using AssetManager
		assetManager.load("map1.png", Texture.class);
		assetManager.load("Zepplin L19.png", Texture.class);
		assetManager.finishLoading(); // Block until assets are loaded

		// Retrieve the textures once loaded
		if (assetManager.isLoaded("map1.png") && assetManager.isLoaded("Zepplin L19.png")) {
			mapTexture = assetManager.get("map1.png", Texture.class);
			zeppelinTexture = assetManager.get("Zepplin L19.png", Texture.class);
		}

		// Start the initial game screen
		startGameScreen();
	}

	/**
	 * Starts or restarts the main game screen.
	 */
	public void startGameScreen() {
		// Ensure textures are properly loaded before switching to the screen
		if (mapTexture != null && zeppelinTexture != null) {
			setScreen(new GameScreen(this, batch, mapTexture, zeppelinTexture));
		} else {
			System.err.println("Error: Textures not properly loaded.");
		}
	}

	@Override
	public void dispose() {
		// Dispose of all resources
		if (batch != null) {
			batch.dispose();
		}
		if (assetManager != null) {
			assetManager.dispose(); // This will dispose of all textures loaded via the asset manager
		}
	}
}
