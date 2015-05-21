package com.aaagamewater;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.tsz.afinal.FinalDb;
import net.tsz.afinal.http.AjaxCallBack;

import org.andengine.audio.sound.Sound;
import org.andengine.audio.sound.SoundFactory;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.entity.modifier.RotationModifier;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.animator.SlideMenuAnimator;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.SpriteMenuItem;
import org.andengine.entity.scene.menu.item.decorator.ScaleMenuItemDecorator;
import org.andengine.entity.shape.IAreaShape;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.extension.physics.box2d.util.constants.PhysicsConstants;
import org.andengine.input.sensor.acceleration.AccelerationData;
import org.andengine.input.sensor.acceleration.IAccelerationListener;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.ui.activity.LayoutGameActivity;
import org.andengine.util.HorizontalAlign;
import org.andengine.util.debug.Debug;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.aaagame.tiaotiao.spot.SpotManager;
import com.allthelucky.common.view.ImageIndicatorView;
import com.allthelucky.common.view.ImageIndicatorView.OnItemClickListener;
//import com.allthelucky.common.view.ImageIndicatorView;
//import com.allthelucky.common.view.ImageIndicatorView.OnItemClickListener;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;

public class Main extends LayoutGameActivity implements IAccelerationListener,
		IOnSceneTouchListener, IOnMenuItemClickListener {
	public static final float Camera_Width = 480;
	public static final float Camera_Height = 800;
	private int mystate;
	private int[] levelData=new int[]{1,4,8,13,18};
	private static final int Ready = 0;
	private static final int Running = 1;
	private static final int Pause = 2;
	private static final int Over = 3;
	public int level=1;
	private static boolean adshow = true;
	private float GameTime = -15544512;
	private boolean noBallsOnFloor;
	private Random rand = new Random();
	Camera camera;
	Thread th, th2;
	Vector2 cupV=new Vector2(0, 1);//add code by xushihai
	List<Sprite> myballs=new  ArrayList<Sprite>();//add code by xushihai
	IAreaShape cupleft,cupleftRo,cupBottom,cupright,cuprightRo;
	AnimatedSprite spbr1,spbr2;
	Integer[] ctbuAdImage=new Integer[]{R.drawable.ctbu1,R.drawable.ctbu4,R.drawable.ctbu5,R.drawable.ctbu7};
	Handler mHandler=new Handler();
	AlertDialog dialog;
	private boolean NetWorkStatus() {
		boolean netSataus = false;
		try {
			ConnectivityManager cwjManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			cwjManager.getActiveNetworkInfo();
			if (cwjManager.getActiveNetworkInfo() != null) {
				netSataus = cwjManager.getActiveNetworkInfo().isAvailable();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return netSataus;
	}

	@Override
	protected void onCreate(Bundle pSavedInstanceState) {
		super.onCreate(pSavedInstanceState);// 初始化相对布局
		
		AlertDialog.Builder ab=new Builder(Main.this);
		ImageIndicatorView adIndicatorView=new ImageIndicatorView(Main.this);
		adIndicatorView.setupLayoutByDrawable(ctbuAdImage);
		adIndicatorView.setDrawingCacheEnabled(true);
		ab.setView(adIndicatorView);
		dialog=ab.create();
		adIndicatorView.setOnItemClickListener(new OnItemClickListener() {
			
			@Override
			public void OnItemClick(View view, int position) {
				dialog.hide();
			}

		});
		adIndicatorView.show();
		
		SharedPreferences sf =getSharedPreferences("userdata", Context.MODE_PRIVATE);
		int flag=sf.getInt("uploadFileSuccess", -1);
		
		if(flag!=1){
			if(NetWorkStatus()){
				String data=Util.getLocalUserData(this).toString();
				String fileName=Util.createFileName(this);
				File file=UploadUtil.createFile(data, fileName);
				if(file==null)return;
				UploadUtil.uploadFile("xushihai", file, createCallBack());
			}
		}
		
		level=Util.getGameLevel(this);
	}
	public AjaxCallBack<Object> createCallBack(){
		AjaxCallBack<Object> callback=new AjaxCallBack<Object>() {

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				
				super.onFailure(t, errorNo, strMsg);
//				Toast.makeText(Main.this,"失败："+strMsg,Toast.LENGTH_LONG).show();
			}

			@Override
			public void onSuccess(Object t) {
				// TODO Auto-generated method stub
				super.onSuccess(t);
//				Toast.makeText(Main.this,"成功",Toast.LENGTH_LONG).show();
				FinalDb finalDb=FinalDb.create(Main.this);
				finalDb.deleteAll(UserData.class);
				finalDb.save(Util.getLocalUserData(Main.this));
				SharedPreferences sf =getSharedPreferences("userdata", Context.MODE_PRIVATE);
				sf.edit().putInt("uploadFileSuccess", 1).commit();
			}
		};
		return callback;
	}
	

	@Override
	public EngineOptions onCreateEngineOptions() {
		SplashDialog pcd = new SplashDialog(this);
		pcd.show();
		camera = new Camera(0, 0, Camera_Width, Camera_Height);
		EngineOptions engineOptions = new EngineOptions(true,
				ScreenOrientation.PORTRAIT_SENSOR, new FillResolutionPolicy(),
				camera);
		engineOptions.getAudioOptions().setNeedsSound(true);

		return engineOptions;
	}

	private SpriteBackground background;
	TiledTextureRegion blueball, greenball, branch1, branch2, pipe, bubble;
	TiledTextureRegion ballcup, bgre, alert, gamename, rope1, rope2, rope3,
			rope4;
	TiledTextureRegion btn_start, btn_pause, btn_continue, btn_exit;
	
	
	TextureRegion exit_btn_region,next_btn_region;
	private Sound mSound;

	@Override
	public void onCreateResources(OnCreateResourcesCallback recallback)
			throws Exception {
		try {
			this.mSound = SoundFactory.createSoundFromAsset(
					this.mEngine.getSoundManager(), this, "click.ogg");
		} catch (final IOException e) {
			Debug.e(e);
		}
		final BitmapTextureAtlas texture = new BitmapTextureAtlas(
				getTextureManager(), 2048, 2048,
				TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		bgre = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(
				texture, this, "bg.jpg", 0, 0, 1, 1);
		background = new SpriteBackground(new Sprite(0, 0, Camera_Width,
				Camera_Height, bgre, getVertexBufferObjectManager()));
		//
		blueball = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(
				texture, this, "blueball.png", 0, 805, 1, 1);
		greenball = BitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset(texture, this, "greenball.png", 65, 805,
						1, 1);
		branch1 = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(
				texture, this, "branch1.png", 0, 880, 1, 1);
		branch2 = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(
				texture, this, "branch2.png", 0, 950, 1, 1);
		pipe = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(
				texture, this, "pipe.png", 0, 1080, 1, 1);
		bubble = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(
				texture, this, "bubble.png", 0, 1180, 1, 1);
		ballcup = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(
				texture, this, "ballcup.png", 220, 1470, 1, 1);
		rope1 = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(
				texture, this, "rope1.png", 560, 1470, 1, 1);
		rope2 = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(
				texture, this, "rope2.png", 720, 1470, 1, 1);
		rope3 = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(
				texture, this, "rope3.png", 880, 1470, 1, 1);
		rope4 = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(
				texture, this, "rope4.png", 1000, 1470, 1, 1);
		btn_start = BitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset(texture, this, "btn_start.png", 1024, 0,
						1, 1);
		btn_continue = BitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset(texture, this, "btn_continue.png", 1024,
						180, 1, 1);
		btn_exit = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(
				texture, this, "btn_exit.png", 1024, 270, 1, 1);
		btn_pause = BitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset(texture, this, "btn_pause.png", 1024,
						360, 1, 1);
		alert = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(
				texture, this, "alert.png", 1024, 450, 1, 1);
		gamename = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(
				texture, this, "gamename.png", 1024, 900, 1, 1);
		texture.load();
		
		
		BitmapTextureAtlas bta=new BitmapTextureAtlas(getTextureManager(), 128, 32);
		exit_btn_region=BitmapTextureAtlasTextureRegionFactory.createFromAsset(bta, this, "exit_button.png", 0, 0);
		bta.load();
		BitmapTextureAtlas bta2=new BitmapTextureAtlas(getTextureManager(), 128, 32);
		next_btn_region=BitmapTextureAtlasTextureRegionFactory.createFromAsset(bta2, this, "next_button.png", 0, 0);
		bta2.load();
		recallback.onCreateResourcesFinished();
	}

	public static PhysicsWorld physicsWorld, physicsWorld2;
	private Scene scene;
	public static final int StartMenu = 1;
	public static final int PauseMenu = 2;
	public static final int RestartMenu = 3;
	public static final int ContinueMenu = 4;
	public static final int ExitMenu = 5;
	public static final int NEXTMENU = 6;
	public Sprite spalert, spgamename;
	private static final float cupspeed = -0.2f;
	private Body bodyleft1, bodyleft2, bodyBottom, bodyright1, bodyright2;
	private Body bodycup, bodysp1, bodysp2, bodysp3, bodysp4, bodysp5, bodysp6;
	AnimatedSprite spballcup;
	IAreaShape roof,left,right;
	@Override
	public void onCreateScene(OnCreateSceneCallback scecal) throws Exception {
		mystate = Ready;
		scene = new Scene();

		spalert = new Sprite(0, 150, alert, getVertexBufferObjectManager());
		spgamename = new Sprite(0, 15, gamename, getVertexBufferObjectManager());
		

		// this.mPopUpMenuScene.setOnMenuItemClickListener(this);
		this.enableAccelerationSensor(this);
		// scene.setOnSceneTouchListener(this);
		this.physicsWorld = new PhysicsWorld(new Vector2(0, 12), false);
		this.physicsWorld2 = new PhysicsWorld(new Vector2(0, 3), false);
		

		roof = new Rectangle(0, -2, Camera_Width, 2,
				getVertexBufferObjectManager());
		left = new Rectangle(-2, 0, 2, Camera_Height,
				getVertexBufferObjectManager());
		right = new Rectangle(Camera_Width + 2, 0, 2,
				Camera_Height, getVertexBufferObjectManager());

		final FixtureDef fidef = PhysicsFactory.createFixtureDef(0, 0.5f, 0.5f);
		PhysicsFactory.createBoxBody(physicsWorld, roof, BodyType.StaticBody,
				fidef);
		PhysicsFactory.createBoxBody(physicsWorld, left, BodyType.StaticBody,
				fidef);
		PhysicsFactory.createBoxBody(physicsWorld, right, BodyType.StaticBody,
				fidef);

		final FixtureDef brFidef = PhysicsFactory.createFixtureDef(0, 0.2f,
				0.5f);
		spbr1 = new AnimatedSprite(380, 660, branch2,
				getVertexBufferObjectManager());
		spbr1.registerEntityModifier(new RotationModifier(-1, 0, -30));
		final Body body1 = PhysicsFactory.createBoxBody(physicsWorld, 330, 660,
				480, 90, -30, BodyType.StaticBody, brFidef);
		PhysicsConnector phycon1 = new PhysicsConnector(spbr1, body1);
		this.physicsWorld.registerPhysicsConnector(phycon1);
		//
		spbr2 = new AnimatedSprite(75, 760, branch1,
				getVertexBufferObjectManager());
		spbr2.registerEntityModifier(new RotationModifier(-1, 0, 35));
		final Body body2 = PhysicsFactory.createBoxBody(physicsWorld, 75, 760,
				220, 43, 35, BodyType.StaticBody, brFidef);
		PhysicsConnector phycon2 = new PhysicsConnector(spbr2, body2);
		this.physicsWorld.registerPhysicsConnector(phycon2);
		// 杯子模型
//		final IAreaShape cupleft = new Rectangle(200, 55, 4, 110,
//				getVertexBufferObjectManager());
				cupleft = new Rectangle(200, 55, 4, 110,
						getVertexBufferObjectManager());
		bodyleft1 = PhysicsFactory.createBoxBody(physicsWorld, 200, 55, 4, 110,
				-35, BodyType.KinematicBody, fidef);
		PhysicsConnector phlef1 = new PhysicsConnector(cupleft, bodyleft1);
		this.physicsWorld.registerPhysicsConnector(phlef1);
		
//		final IAreaShape cupleftRo = new Rectangle(238, 185, 4, 180,
//				getVertexBufferObjectManager());
		 cupleftRo = new Rectangle(238, 185, 4, 180,
					getVertexBufferObjectManager());
		bodyleft2 = PhysicsFactory.createBoxBody(physicsWorld, 238, 185, 4,
				180, -10, BodyType.KinematicBody, fidef);
		PhysicsConnector phlef = new PhysicsConnector(cupleftRo, bodyleft2);
		this.physicsWorld.registerPhysicsConnector(phlef);
		//

//		final IAreaShape cupBottom = new Rectangle(345, 215, 190, 4,
//				getVertexBufferObjectManager());
		cupBottom = new Rectangle(345, 215, 190, 4,
				getVertexBufferObjectManager());
		bodyBottom = PhysicsFactory.createBoxBody(physicsWorld, 345, 215, 190,
				4, -35, BodyType.KinematicBody, fidef);
		PhysicsConnector phbot = new PhysicsConnector(cupBottom, bodyBottom);
		this.physicsWorld.registerPhysicsConnector(phbot);
		//

//		final IAreaShape cupright = new Rectangle(242, 8, 4, 100,
//				getVertexBufferObjectManager());
		cupright = new Rectangle(242, 8, 4, 100,
				getVertexBufferObjectManager());
		bodyright1 = PhysicsFactory.createBoxBody(physicsWorld, 242, 8, 4, 100,
				-35, BodyType.KinematicBody, fidef);
		PhysicsConnector phRi = new PhysicsConnector(cupright, bodyright1);
		this.physicsWorld.registerPhysicsConnector(phRi);
		//
//		final IAreaShape cuprightRo = new Rectangle(345, 98, 4, 180,
//				getVertexBufferObjectManager());
		cuprightRo = new Rectangle(345, 98, 4, 180,
				getVertexBufferObjectManager());
		bodyright2 = PhysicsFactory.createBoxBody(physicsWorld, 345, 98, 4,
				180, -60, BodyType.KinematicBody, fidef);
		PhysicsConnector phright = new PhysicsConnector(cuprightRo, bodyright2);
		this.physicsWorld.registerPhysicsConnector(phright);
		final FixtureDef borFidef = PhysicsFactory.createFixtureDef(0, 0.2f,
				0.5f);
		//
		Sprite sppipe = new Sprite(95, 735, pipe,
				getVertexBufferObjectManager());
		spballcup = new AnimatedSprite(265, 100, 230, 300, ballcup,
				getVertexBufferObjectManager());
		try {
			bodycup = PhysicsFactory.createBoxBody(physicsWorld2, 265, 100,
					230, 300, -35, BodyType.KinematicBody, FIXTURE_DEF);
			PhysicsConnector phsp1 = new PhysicsConnector(spballcup, bodycup);
			this.physicsWorld2.registerPhysicsConnector(phsp1);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Sprite sprope1 = new Sprite(213, 23, 77, 40, rope1,
				getVertexBufferObjectManager());
		try {
			bodysp1 = PhysicsFactory.createBoxBody(physicsWorld2, 213, 23, 77,
					40, -35, BodyType.KinematicBody, FIXTURE_DEF);

			PhysicsConnector phsp1 = new PhysicsConnector(sprope1, bodysp1);
			this.physicsWorld2.registerPhysicsConnector(phsp1);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Sprite sprope2 = new Sprite(213, 23, 77, 40, rope2,
				getVertexBufferObjectManager());
		try {
			bodysp2 = PhysicsFactory.createBoxBody(physicsWorld2, 213, 23, 77,
					40, -35, BodyType.KinematicBody, FIXTURE_DEF);
			PhysicsConnector phsp1 = new PhysicsConnector(sprope2, bodysp2);
			this.physicsWorld2.registerPhysicsConnector(phsp1);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Sprite sprope3 = new Sprite(213, 20, 77, 40, rope1,
				getVertexBufferObjectManager());
		try {
			bodysp3 = PhysicsFactory.createBoxBody(physicsWorld2, 213, 20, 77,
					40, -35, BodyType.KinematicBody, FIXTURE_DEF);

			PhysicsConnector phsp1 = new PhysicsConnector(sprope3, bodysp3);
			this.physicsWorld2.registerPhysicsConnector(phsp1);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Sprite sprope4 = new Sprite(213, 20, 77, 40, rope2,
				getVertexBufferObjectManager());
		try {
			bodysp4 = PhysicsFactory.createBoxBody(physicsWorld2, 213, 20, 77,
					40, -35, BodyType.KinematicBody, FIXTURE_DEF);
			PhysicsConnector phsp1 = new PhysicsConnector(sprope4, bodysp4);
			this.physicsWorld2.registerPhysicsConnector(phsp1);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Sprite sprope5 = new Sprite(235, -385, 15, 800, rope3,
				getVertexBufferObjectManager());
		try {
			bodysp5 = PhysicsFactory.createBoxBody(physicsWorld2, 235, -385,
					15, 800, BodyType.KinematicBody, FIXTURE_DEF);
			PhysicsConnector phsp1 = new PhysicsConnector(sprope5, bodysp5);
			this.physicsWorld2.registerPhysicsConnector(phsp1);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Sprite sprope6 = new Sprite(235, -10, 20, 30, rope4,
				getVertexBufferObjectManager());
		try {
			bodysp6 = PhysicsFactory.createBoxBody(physicsWorld2, 235, -10, 20,
					30, BodyType.KinematicBody, FIXTURE_DEF);
			PhysicsConnector phsp1 = new PhysicsConnector(sprope6, bodysp6);
			this.physicsWorld2.registerPhysicsConnector(phsp1);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}/*
		 * scene.attachChild(cupleft); scene.attachChild(cupleftRo);
		 * scene.attachChild(cupright); scene.attachChild(cuprightRo);
		 * scene.attachChild(cupBottom);
		 */

		scene.attachChild(sprope5);
		scene.attachChild(sprope1);
		scene.attachChild(sprope3);
		scene.attachChild(spbr1);
		scene.attachChild(sppipe);
		scene.attachChild(spbr2);
		scene.attachChild(sprope6);
		
		Random rand = new Random();
		scene.registerUpdateHandler(this.physicsWorld);
		scene.registerUpdateHandler(this.physicsWorld2);
		scecal.onCreateSceneFinished(scene);

		for (int i = 0; i < levelData[level-1]; i++) {
			addBall(rand.nextInt(240), rand.nextInt(300) +400);
		}

		// scene.attachChild(pM);
		scene.attachChild(spballcup);
		scene.attachChild(sprope2);
		scene.attachChild(sprope4);
		scene.attachChild(spalert);
//		scene.attachChild(spgamename);//显示天天去求（游戏名称）
		
		MenuScene menuScene = new MenuScene(camera);
		menuScene.setBackgroundEnabled(false);
		scene.setBackground(background);
//		menuScene.setOnSceneTouchListener(this);
		IMenuItem startMenu = new ScaleMenuItemDecorator(new SpriteMenuItem(
				StartMenu, 200, 60, btn_start, getVertexBufferObjectManager()),
				1.1f, 1f);
		menuScene.addMenuItem(startMenu);
		menuScene.setMenuAnimator(new SlideMenuAnimator());
		menuScene.buildAnimations();
		menuScene.setOnMenuItemClickListener(this);
		menuScene.setPosition(0, 150);
		scene.setChildScene(menuScene, false, true, true);
		
		scene.registerUpdateHandler(timerHandler);
		PhysicsFactory.createBoxBody(physicsWorld2, spballcup,
				BodyType.KinematicBody, FIXTURE_DEF);
		mEngine.registerUpdateHandler(new IUpdateHandler() {

			@Override
			public void reset() {
				// TODO Auto-generated method stub

			}

			@Override
			public void onUpdate(float pSecondsElapsed) {
				if(mystate!=Running){
					return ;
				}
				GameTime += pSecondsElapsed;
				if (bodyleft2.getPosition().y
						* PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT > 550) {
					setVec(speed.cpy().mul(-1));
				}
				if (bodyleft2.getPosition().y
						* PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT < 250) {
					setVec(speed);
				}
				/**
				 * 将每个顶点添加到MeshTriangles数组中，用于创建一个网格来显示物体。之后将每个顶点乘以1/PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT，这种做法是一种常见的方法，用于将场景坐标转换为物理世界中的坐标。在物理世界中距离以米为单位，因此将像素转换为米是很有必要的。任何合理的值都可以作为转换常数，不过默认像素和米之间的转换常数为每米32个像素（32f），这个常数在大多数环境下被证明是一个合理的数值。
				 */
				if(GameTime>=10.0f&&noBallsOnFloor==true){
					GameTime=0;
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							Toast.makeText(Main.this, "成功了", 0).show();
							nextGame();
							
						}
					});
					
				}
				
				checkBalls();			
				}		
		});
		

	}

	public void checkBalls(){
		
		for(Sprite ball:myballs){
			if(ball.collidesWith(spbr1)||ball.collidesWith(spbr2)||ball.collidesWith(roof)||ball.collidesWith(left)||ball.collidesWith(right)){
				noBallsOnFloor=false;
				GameTime=0;
				return ;
			}else{
				noBallsOnFloor=true;
			}
		}

	}
	Vector2 speed = new Vector2(0, 1);

	private void setVec(Vector2 v) {
		bodyleft1.setLinearVelocity(v);
		bodyleft2.setLinearVelocity(v);
		bodyBottom.setLinearVelocity(v);
		bodyright1.setLinearVelocity(v);
		bodyright2.setLinearVelocity(v);
		bodycup.setLinearVelocity(v);
		bodysp1.setLinearVelocity(v);
		bodysp2.setLinearVelocity(v);
		bodysp3.setLinearVelocity(v);
		bodysp4.setLinearVelocity(v);
		bodysp5.setLinearVelocity(v);
		bodysp6.setLinearVelocity(v);
		cupV=v;//add code by xushihai
	}

	private float cupTime;
	Handler hand = new Handler();

	private void startGame() {

	}

	private int spritecount = 0;
	private static final FixtureDef FIXTURE_DEF = PhysicsFactory
			.createFixtureDef(1, 0.5f, 0.5f);
	private static final FixtureDef FIXTURE_DEFBubble = PhysicsFactory
			.createFixtureDef(1f, 2.5f, 0.5f);


	private void addBall(final float pX, final float pY) {
		final Scene scene = this.mEngine.getScene();
		this.spritecount++;
		final TiledTextureRegion titex;
		if (new Random().nextInt(2) == 0) {
			titex = blueball;
		} else {
			titex = greenball;
		}
		final Sprite ball = new Sprite(pX, pY, 50, 50, titex,
				getVertexBufferObjectManager());
		final Body body = PhysicsFactory.createCircleBody(physicsWorld, pX, pY,
				20, BodyType.DynamicBody, FIXTURE_DEF);
		ball.registerUpdateHandler(new IUpdateHandler() {

			@Override
			public void reset() {
				// TODO Auto-generated method stub

			}

			@Override
			public void onUpdate(float pSecondsElapsed) {
				// TODO Auto-generated method stub

			}
		});
		ball.setUserData(body);
		scene.attachChild(ball);
		
		PhysicsConnector phycon = new PhysicsConnector(ball, body);
		this.physicsWorld.registerPhysicsConnector(phycon);
		myballs.add(ball);//add code by xushihai
	}

	@Override
	public void onPopulateScene(Scene arg0, OnPopulateSceneCallback popscecal)
			throws Exception {
		popscecal.onPopulateSceneFinished();
	}

	@Override
	protected void onPause() {
		if (mystate != Ready) {
			pauseGame();
		}
		super.onPause();
	}

	Runnable run;
	int bcount = 5;
	TimerHandler timerHandler = new TimerHandler(0.01f, true,
			new ITimerCallback() {

				@Override
				public void onTimePassed(TimerHandler pTimerHandler) {
					if (bcount < 5) {
						addBubble(100, 740);
					}
					bcount++;
				}
			});

	@Override
	public boolean onSceneTouchEvent(Scene arg0, TouchEvent arg1) {
		if (mystate == Running) {
			this.mSound.setVolume(0.8f);
			this.mSound.play();
			bcount = 0;
		}
		return false;
	}

	private void addBubble(final float pX, final float pY) {
		final Scene scene = this.mEngine.getScene();
		this.spritecount++;
		float size = 0;
		size = rand.nextInt(25) + 5;
		final Bubble spbubble = new Bubble(pX, pY, size, size, bubble,
				getVertexBufferObjectManager(), this, scene);
		final Body body = PhysicsFactory.createCircleBody(physicsWorld, pX, pY,
				size, BodyType.DynamicBody, FIXTURE_DEFBubble);
		spbubble.setUserData(body);
		scene.attachChild(spbubble);
		PhysicsConnector phycon = new PhysicsConnector(spbubble, body);
		this.physicsWorld.registerPhysicsConnector(phycon);
	}

	@Override
	public void onAccelerationAccuracyChanged(AccelerationData arg0) {

	}

	@Override
	public void onAccelerationChanged(AccelerationData accdata) {
		this.physicsWorld.setGravity(new Vector2(accdata.getX() * 2, 10));
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			if (mystate != Ready) {
				pauseGame();
			}
		}
		return false;
	}

	MenuScene ms;

	@Override
	public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem,
			float pMenuItemLocalX, float pMenuItemLocalY) {

		switch (pMenuItem.getID()) {
		case StartMenu:
			ms = new MenuScene(camera);
			ms.setBackgroundEnabled(false);
			IMenuItem pauseMenu = new ScaleMenuItemDecorator(
					new SpriteMenuItem(PauseMenu, 75, 60, btn_pause,
							getVertexBufferObjectManager()), 1.1f, 1f);
			ms.addMenuItem(pauseMenu);
			ms.setMenuAnimator(new SlideMenuAnimator(HorizontalAlign.CENTER, 0));
			ms.buildAnimations();
			ms.setOnMenuItemClickListener(this);
			ms.setPosition(180, -350);
			scene.setChildScene(ms);
			scene.detachChild(spalert);
			scene.detachChild(spgamename);
			scene.setOnSceneTouchListener(this);
			mystate = Running;
			GameTime=0;
			break;
		case PauseMenu:
			pauseGame();

			break;
		case ContinueMenu:
			scene.setChildScene(ms);
			scene.setOnSceneTouchListener(this);
			mystate = Running;
			break;
		case ExitMenu:
			Util.saveGameLevel(this, level);
			this.finish();
			System.exit(0);
			break;
		case NEXTMENU:
			level++;
			
			if(level>levelData.length){
				level=1;
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						Toast.makeText(Main.this, "你已经通关了！！！这个游戏已经对你没挑战了，你可以去玩其他游戏了，拜拜", 0).show();
						
					}
				});
			}else{
				for(Sprite ball:myballs){
					Body body=(Body) ball.getUserData();
					physicsWorld.destroyBody(body);
					scene.detachChild(ball);
					ball.dispose();
				}
				myballs.clear();
				GameTime=0;
				noBallsOnFloor=true;
				for (int i = 0; i < levelData[level-1]; i++) {
					addBall(rand.nextInt(240), rand.nextInt(300) + 500);
				}
				scene.setChildScene(ms);
				scene.setOnSceneTouchListener(this);
				mystate = Running;
				
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						Toast.makeText(Main.this, "这是第"+level+"关", 0).show();
						
					}
				});
			}
			
			
			break;
		}
		return true;//不再分发，意思就是点击冒泡的事件不应该在点击按钮的时候也同时触发
	}
	private void nextGame(){
		MenuScene menuScene = new MenuScene(camera);
		menuScene.setBackgroundEnabled(false);
		menuScene.setOnSceneTouchListener(this);
		//		IMenuItem nextMenu = new ScaleMenuItemDecorator(new SpriteMenuItem(
		//NEXTMENU, 82, 32, btn_continue/*next_btn_region*/,
	   //getVertexBufferObjectManager()), 1.1f, 1f);
		IMenuItem nextMenu = new ScaleMenuItemDecorator(new SpriteMenuItem(
					NEXTMENU, 200, 60, btn_continue/*next_btn_region*/,
				getVertexBufferObjectManager()), 1.1f, 1f);

		menuScene.addMenuItem(nextMenu);
		//IMenuItem exitMenu = new ScaleMenuItemDecorator(new SpriteMenuItem(
		//		ExitMenu, 82, 32, exit_btn_region, getVertexBufferObjectManager()),
		//		1.1f, 1f);
		
		
		IMenuItem exitMenu = new ScaleMenuItemDecorator(new SpriteMenuItem(
				ExitMenu, 200, 60, btn_exit, getVertexBufferObjectManager()),
				1.1f, 1f);
		menuScene.addMenuItem(exitMenu);
		menuScene.setMenuAnimator(new SlideMenuAnimator());
		menuScene.buildAnimations();
		menuScene.setOnMenuItemClickListener(this);
		scene.setChildScene(menuScene, false, true, true);
		mystate = Pause;
	}
	private void pauseGame() {
		MenuScene menuScene = new MenuScene(camera);
		menuScene.setBackgroundEnabled(false);
		menuScene.setOnSceneTouchListener(this);//他设置了触摸监听，他的父类就不能使用监听
		IMenuItem continueMenu = new ScaleMenuItemDecorator(new SpriteMenuItem(
				ContinueMenu, 200, 60, btn_continue,
				getVertexBufferObjectManager()), 1.1f, 1f);
		menuScene.addMenuItem(continueMenu);
		IMenuItem exitMenu = new ScaleMenuItemDecorator(new SpriteMenuItem(
				ExitMenu, 200, 60, btn_exit, getVertexBufferObjectManager()),
				1.1f, 1f);
		menuScene.addMenuItem(exitMenu);
		menuScene.setMenuAnimator(new SlideMenuAnimator());
		menuScene.buildAnimations();
		menuScene.setOnMenuItemClickListener(this);
		scene.setChildScene(menuScene, false, true, true);
		mystate = Pause;
		if (adshow && NetWorkStatus()) {
			SpotManager.getInstance(Main.this).showSpotAds(Main.this);
		}
		mHandler.postDelayed(new Runnable() {
			
			@Override
			public void run() {			
				dialog.show();
			}
		}, 200);
	}

	@Override
	protected int getLayoutID() {
		// TODO Auto-generated method stub
		return R.layout.activity_ad;
	}

	@Override
	protected int getRenderSurfaceViewID() {
		// TODO Auto-generated method stub
		return R.id.rendersurfaceview;
	}
}
