package com.aaagamewater;

import java.util.Random;

import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.activity.BaseGameActivity;

import com.badlogic.gdx.physics.box2d.Body;

public class Bubble extends Sprite {

	private static final float BALL_VELOCITY = -100f;
	float mVelocityX = BALL_VELOCITY;
	float mVelocityY = BALL_VELOCITY;
	BaseGameActivity activity;
	Scene scene;
	private float times;

	public Bubble(float pX, float pY, float pWidth, float pHeight,
			ITiledTextureRegion pTiledTextureRegion,
			VertexBufferObjectManager pVertexBufferObjectManager,
			BaseGameActivity activity, Scene scene) {
		super(pX, pY, pWidth, pHeight, pTiledTextureRegion,
				pVertexBufferObjectManager);
		mX = pX;
		mY = pY;
		this.activity = activity;
		this.scene = scene;
	}

	@Override
	protected void onManagedUpdate(float pSecondsElapsed) {
		times += pSecondsElapsed;
		final Body body = (Body) this.getUserData();

		body.setLinearVelocity((new Random().nextFloat() - 0.5f) * 3, -10);
		if (this.getY() < 30 || this.getX() < 30 || this.getX() > 450
				|| times > (new Random().nextFloat() + 0.6)) {
			activity.runOnUpdateThread(new Runnable() {

				@Override
				public void run() {
					Main.physicsWorld.destroyBody(body);
					scene.detachChild(Bubble.this);
				}
			});
		}
		super.onManagedUpdate(pSecondsElapsed);
	}
}
