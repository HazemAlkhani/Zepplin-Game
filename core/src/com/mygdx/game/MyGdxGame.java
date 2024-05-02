package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.assets.AssetManager; // Use AssetManager for better asset handling

public class MyGdxGame extends Game {
	SpriteBatch batch;
	AssetManager assetManager; // Manage all assets in one place

	@Override
	public void create() {
		batch = new SpriteBatch();
		assetManager = new AssetManager(); // Initialize the asset manager

		// Load textures using AssetManager
		assetManager.load("map1.png", Texture.class);
		assetManager.load("Zepplin L19.png", Texture.class);
		assetManager.finishLoading(); // Blocks until all assets are loaded

		if (assetManager.isLoaded("map1.png") && assetManager.isLoaded("Zepplin L19.png")) {
			Texture mapTexture = assetManager.get("map1.png", Texture.class);
			Texture zeppelinTexture = assetManager.get("Zepplin L19.png", Texture.class);
			setScreen(new GameScreen(this, batch, mapTexture, zeppelinTexture)); // Pass loaded textures to GameScreen
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
