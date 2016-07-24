package com.mygdx.game;

import enemies.TankBoss;
import hudElements.GameOverMenu;
import hudElements.JumpButton;
import hudElements.LevelCompletedMenu;
import hudElements.PauseMenu;

import java.util.ArrayList;

import network.JoinHeroBullet;
import powerups.HealthPU;
import powerups.LaserPU;
import powerups.LifePU;
import powerups.PickUp;
import powerups.ShieldPU;
import powerups.TripleshotPU;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.Hero.State;

import enemies.AlienBoss;
import enemies.EnemyBullet;
import enemies.EnemyHoming;
import enemies.EnemySmallBullet;
import enemies.EnemySoldier;
import enemies.EnemyTower;

/**
 * Screen: Level 3 der SciFi Welt
 * @Leveldesign Lars
 * @author Fabian, Lars, Hermann, Tim
 */
public class LevelScifi3 implements Screen {

	//Zeit die zu unterbieten ist in Sekunden:
	final int timeToBeat = 134;
	//Gewichtung Sekunden zu Punkten:
	final int timeFactor = 10;

	MyGdxGame game;
	public OrthographicCamera gamecam;
	OrthographicCamera hud;
	Viewport gameport;
	Viewport hudport;
	SpriteBatch batch;
	ShapeRenderer shape;
	Stage hudStage;
	Stage pauseStage;
	
	boolean paused;
	
	//objekte um die map zu laden, welche mit TILED erstellt wurde
	TmxMapLoader maploader;
	TiledMap map;
	OrthogonalTiledMapRenderer renderer;
	
	//Box2d Kram
	World world;
	Box2DDebugRenderer b2dr;
	B2WorldCreatorCustom b2creator;
	
	//Player
	Hero hero;
	Shield shield;
	
	//Anzeige fuer Leben, Punkte, Munition
	Sprite statBar;
	Sprite healthBar;
	BitmapFont font;
	Vector2 scorePosition;
	Vector2 livesPosition;
	Vector2 ammoPosition;
	Vector2 timePosition;
	float levelTime;
	float barWidth;
	
	//Boss-Lebenleiste
	Sprite bossBar;
	Sprite bossHealthBar;
	float bossBarWidth;
	
	//Pads
	hudElements.Joystick runningStick;
	hudElements.Joystick aimStick;
	JumpButton jumpButton;
	
	//Pause-Menue
	PauseMenu pauseMenu;
	
	//Gegner
	EnemySoldier soldier1;
	EnemySoldier soldier2;
	EnemySoldier soldier3;
	EnemySoldier soldier4;
	EnemySoldier soldier5;
	ArrayList<EnemySoldier> enemySoldierList;
	
	EnemyHoming homing1;
	ArrayList<EnemyHoming> enemyHomingList;
	
	EnemyTower tower1;
	EnemyTower tower2;
	EnemyTower tower3;
	ArrayList<EnemyTower> enemyTowerList;
	
	//Geschosse von bereits besiegten Gegnern
	ArrayList<EnemyBullet> remainingEnemyBulletList;
	ArrayList<EnemySmallBullet> remainingEnemySmallBulletList;
	
	//PowerUps
	ArrayList<PickUp> powerUpList;
	HealthPU healthPU1;
	ShieldPU shieldPU1;
	LifePU lifePU1;
	TripleshotPU tripleshotPU1;
	LaserPU laserPU1;

	//Checkpoints
	Checkpoint[] checkpoints = new Checkpoint[2];
	float nextCheckpoint;
	int checkpointCounter;
	int respawnTimer;

	//GameOver
	boolean gameOver;
	GameOverMenu gameOverMenu;
	Stage gameOverStage;
	float labelX;
	float buttonX;

	//Bewegende Platformen
	ArrayList<MovingPlatform> platformList;
	MovingPlatform platform1;
	MovingPlatform platform2;
	MovingPlatform platform3;
	
	boolean bossfight;
	private boolean allowInput;
	
	AlienBoss alienBoss;
	float bossalphalevel;
	boolean spawned;
	
	boolean levelCompleted = false;
	
	LevelCompletedMenu levelCompletedMenu;
	Stage levelCompleteStage;

	/**
	 * Konstruktor zum Erzeugen eines LevelSciFi3-Objekts
	 * @param game Startklassen-Instanz: Zugriff auf globale Ressourcen, haelt aktuellen Screen
	 */
	public LevelScifi3(MyGdxGame game){
		this.game = game;
		this.game.playLevelThemeW4();
		gamecam = new OrthographicCamera();
		gameport = new StretchViewport(MyGdxGame.V_WIDTH / MyGdxGame.PPM, MyGdxGame.V_HEIGHT / MyGdxGame.PPM, gamecam);
		gamecam.position.set(gameport.getWorldWidth() / 2, gameport.getWorldHeight() / 2, 0);
		world = new World(new Vector2(0, -9.81f), true);
		world.setContactListener(new WorldContactListener());
		hero = new Hero(world, game, gamecam);
		this.levelTime = 0;
	}
	/**
	 * Konstruktor der Stats des Heros uebergeben bekommt (Fuer Laden/Uebergang ins naechste Level)
	 * @param game Startklassen-Instanz: Zugriff auf globale Ressourcen, haelt aktuellen Screen
	 * @param health Verbleibende Lebenspunkte
     * @param lives Anzahl verbleibender Leben
     * @param ammo Verbleibende Munition
     * @param score Bisher erreichte Punktzahl
     * @param laserAmmo Verbleibende Laser-Munition
     * @param tripleAmmo Verbleibende Triple-Munition
	 */
	public LevelScifi3(MyGdxGame game, int health, int lives, int ammo, int score, int laserAmmo, int tripleAmmo){
		this(game);
		
		if(hero != null){
			world.destroyBody(hero.getBody());
		}
		
		hero = new Hero(world, game, gamecam, health, lives, ammo, score, laserAmmo, tripleAmmo);
	}
	

	@Override
	public void show() {

        Gdx.input.setCatchBackKey(true);
        Gdx.input.setCatchMenuKey(true);
		batch = new SpriteBatch();
		shape = new ShapeRenderer();
		
		hud= new OrthographicCamera();
		hudport= new StretchViewport(Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
		hudport.setCamera(hud);
		
		paused = false;

		maploader = new TmxMapLoader();
		map = maploader.load("level/scifiLevel3.tmx");
		renderer = new OrthogonalTiledMapRenderer(map, 1 / (MyGdxGame.PPM * 4));

		b2dr = new Box2DDebugRenderer();
		
		
		hero.b2body.setTransform(220 / MyGdxGame.PPM, 200 / MyGdxGame.PPM, 0);
		//hero.b2body.setTransform(24.081911f, 3.5849996f, 0);
		hero.b2body.setLinearVelocity(0f, 0f);
		
		shield = new Shield(hero);
		
		//Anzeigerahmen fuer Leben, Score, Munition
		statBar = new Sprite(new Texture("sprites/statbar.png"));
		System.out.println(statBar.getHeight());
		//statBar.setBounds(10, Gdx.graphics.getHeight()-statBar.getHeight()*1.5f-10, statBar.getWidth()*1.5f, statBar.getHeight()*1.5f);
		statBar.setBounds(Gdx.graphics.getWidth()/(MyGdxGame.V_WIDTH/(10)), Gdx.graphics.getHeight()/(MyGdxGame.V_HEIGHT/(720-statBar.getHeight()*1.5f-10)),
				Gdx.graphics.getWidth()/(MyGdxGame.V_WIDTH/(statBar.getWidth()*1.5f)), Gdx.graphics.getHeight()/(MyGdxGame.V_HEIGHT/(statBar.getHeight()*1.5f)));
		
		//Lebensleiste
		healthBar = new Sprite(new Texture("sprites/health.png"));
		//healthBar.setBounds(10+5*1.5f, Gdx.graphics.getHeight()-16-healthBar.getHeight()*1.5f, healthBar.getWidth()*1.5f*hero.getHealth(), healthBar.getHeight()*1.5f);
		healthBar.setBounds(Gdx.graphics.getWidth()/(MyGdxGame.V_WIDTH/(10+5*1.5f)), Gdx.graphics.getHeight()/(MyGdxGame.V_HEIGHT/(720-10-(4+healthBar.getHeight())*1.5f)),
				Gdx.graphics.getWidth()/(MyGdxGame.V_WIDTH/(healthBar.getWidth()*1.5f*hero.getHealth())), Gdx.graphics.getHeight()/(MyGdxGame.V_HEIGHT/(healthBar.getHeight()*1.5f)));
		
		
		Texture fontTexture = new Texture(Gdx.files.internal("fonts/futura.png"));
		fontTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		font= new BitmapFont(Gdx.files.internal("fonts/futura.fnt"),new TextureRegion(fontTexture), false);
		font.setColor(Color.SCARLET);
		//Schrift skalieren
		if(Gdx.graphics.getWidth()*Gdx.graphics.getHeight() < MyGdxGame.V_WIDTH*MyGdxGame.V_HEIGHT){
			font.getData().setScale((Gdx.graphics.getWidth()*Gdx.graphics.getHeight())/((MyGdxGame.V_WIDTH*MyGdxGame.V_HEIGHT)/0.9f));
		}
		else{
			font.getData().setScale(0.9f);
		}
		scorePosition =  new Vector2(Gdx.graphics.getWidth()/(MyGdxGame.V_WIDTH/(10+5*1.5f)), Gdx.graphics.getHeight()/(MyGdxGame.V_HEIGHT/(720-10-(20*1.5f))));
		livesPosition =  new Vector2(Gdx.graphics.getWidth()/(MyGdxGame.V_WIDTH/(11+128*1.5f)), Gdx.graphics.getHeight()/(MyGdxGame.V_HEIGHT/(720-10-(4f*1.5f))));
		ammoPosition =  new Vector2(Gdx.graphics.getWidth()/(MyGdxGame.V_WIDTH/(10+121*1.5f)), Gdx.graphics.getHeight()/(MyGdxGame.V_HEIGHT/(720-10-(20*1.5f))));
		timePosition =  new Vector2(Gdx.graphics.getWidth()/(MyGdxGame.V_WIDTH/(10+741*1.5f)), Gdx.graphics.getHeight()/(MyGdxGame.V_HEIGHT/(720-10-(4*1.5f))));
		barWidth = (Gdx.graphics.getWidth()/(MyGdxGame.V_WIDTH/(100*1.5f)));
		
		//Anzeigerahmen fuer BossLeben
		bossBar = new Sprite(new Texture("sprites/bossbar.png"));
		bossBar.setBounds(Gdx.graphics.getWidth()/(MyGdxGame.V_WIDTH/(640-(bossBar.getWidth()/2*3f))), Gdx.graphics.getHeight()/(MyGdxGame.V_HEIGHT/(720-bossBar.getHeight()*3f-10)),
				Gdx.graphics.getWidth()/(MyGdxGame.V_WIDTH/(bossBar.getWidth()*3f)), Gdx.graphics.getHeight()/(MyGdxGame.V_HEIGHT/(bossBar.getHeight()*3f)));
		//Boss-Lebensleiste
		bossHealthBar = new Sprite(new Texture("sprites/bosshealth.png"));
		bossHealthBar.setBounds(bossBar.getX() + Gdx.graphics.getWidth()/(MyGdxGame.V_WIDTH/(20*3f)), bossBar.getY() + Gdx.graphics.getHeight()/(MyGdxGame.V_HEIGHT/(4*3f)) ,
				Gdx.graphics.getWidth()/(MyGdxGame.V_WIDTH/(bossHealthBar.getWidth()*3f)), Gdx.graphics.getHeight()/(MyGdxGame.V_HEIGHT/(bossHealthBar.getHeight()*3f)));
		
		bossBarWidth = (Gdx.graphics.getWidth()/(MyGdxGame.V_WIDTH/(100*3f)));
		
		b2creator = new B2WorldCreatorCustom(world, map);

		//für Android Steuerung
		if(Application.ApplicationType.Android==Gdx.app.getType()) {
			//Sticks initialisiert
			runningStick = new hudElements.Joystick(0, 0, Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
			aimStick = new hudElements.Joystick(Gdx.graphics.getWidth() - 200*Gdx.graphics.getWidth()/ MyGdxGame.V_WIDTH, 1, Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
			//JumpButton init
			jumpButton = new JumpButton();
			jumpButton.jumpButton.addListener(new InputListener() {
				@Override
				public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
					if (hero.getState() != Hero.State.JUMPING && hero.getState() != Hero.State.FALLING)
						hero.b2body.applyLinearImpulse(new Vector2(0, 5.5f), hero.b2body.getWorldCenter(), true);
					return true;

				}
			});

			//Stage für HUD-Elemente
			hudStage = new Stage(hudport);
			hudStage.addActor(runningStick.touchpad);
			hudStage.addActor(aimStick.touchpad);
			hudStage.addActor(jumpButton.jumpButton);
			Gdx.input.setInputProcessor(hudStage);
		}
		
		//Pausemenue: Tabelle samt Buttons
		pauseMenu = new PauseMenu();
			
		pauseMenu.continueButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				game.playButtonClicked();
				paused = false;
				Gdx.input.setInputProcessor(hudStage);
				pauseMenu.disableButtons();			
				}});
	   pauseMenu.saveButton.addListener(new ChangeListener() {
		   @Override
	       public void changed(ChangeEvent event, Actor actor) {
				game.playButtonClicked();
				XMLInteraction.saveGame(hero, "levelscifi3");
	       }});
	   pauseMenu.menuButton.addListener(new ChangeListener() {
		   @Override
		   public void changed(ChangeEvent event, Actor actor) {
			   	pauseMenu.disableButtons();
			   	game.playButtonClicked();
				game.setScreen(new MainMenuScreen(game));}});
	        
		pauseMenu.quitButton.addListener(new ChangeListener() {
	     	@Override
	      	public void changed(ChangeEvent event, Actor actor) {
	     		game.playButtonClicked();
	          	game.dispose();
	          	System.exit(0);}});
		
		
		pauseStage = new Stage(hudport);
		pauseStage.addActor(pauseMenu.pauseTable);
		
		//world.setContactListener(new WorldContactListener());
		
		
		
		
		
		
		
		
		/*
		 * BEREICH FUER GEGNER ERSTELLUNG
		 */
		enemyHomingList = new ArrayList<EnemyHoming>();
		
//		homing1 = new EnemyHoming(world, hero, 18.794228f * MyGdxGame.PPM, 7.2539577f * MyGdxGame.PPM, game);
//		enemyHomingList.add(homing1);
		
		enemySoldierList = new ArrayList<EnemySoldier>();
		
		soldier1 = new EnemySoldier(world, hero, 6.835058f * MyGdxGame.PPM, 2.731666f * MyGdxGame.PPM, game);
		enemySoldierList.add(soldier1);
//		
		soldier2 = new EnemySoldier(world, hero, 13.466519f * MyGdxGame.PPM, 6.1383324f * MyGdxGame.PPM, game);
		enemySoldierList.add(soldier2);
//		
//		soldier3 = new EnemySoldier(world, hero, 49.49139f * MyGdxGame.PPM, 2.1716661f * MyGdxGame.PPM, game);
//		enemySoldierList.add(soldier3);
//		
//		soldier4 = new EnemySoldier(world, hero, 24.488941f * MyGdxGame.PPM, 4.1716657f * MyGdxGame.PPM, game);
//		enemySoldierList.add(soldier4);
//		
//		soldier5 = new EnemySoldier(world, hero, 25.329876f * MyGdxGame.PPM, 4.1716657f * MyGdxGame.PPM, game);
//		enemySoldierList.add(soldier5);
		
		
		
		enemyTowerList = new ArrayList<EnemyTower>();
		
		tower1 = new EnemyTower(world, hero, 8.76841f * MyGdxGame.PPM, 1.4516664f * MyGdxGame.PPM - 11, false, game);
		enemyTowerList.add(tower1);
		
		tower2 = new EnemyTower(world, hero, 10.040187f * MyGdxGame.PPM, 2.3005009f * MyGdxGame.PPM -11, false, game);
		enemyTowerList.add(tower2);
		
		tower3 = new EnemyTower(world, hero, 11.313223f * MyGdxGame.PPM,  3.1516662f * MyGdxGame.PPM -11, false, game);
		enemyTowerList.add(tower3);
		
//		X: 28.174326 Y: 3.5049996
//		X: 31.814869 Y: 3.1716661
		
		remainingEnemyBulletList = new ArrayList<EnemyBullet>();
		remainingEnemySmallBulletList = new ArrayList<EnemySmallBullet>();
		
		
		/*
		 * BEREICH FUER POWERUP ERSTELLUNG
		 */

		powerUpList = new ArrayList<PickUp>();
		healthPU1 = new HealthPU(world, 18.653114f * MyGdxGame.PPM, 6.144999f * MyGdxGame.PPM -11);
		powerUpList.add(healthPU1);
//		shieldPU1 = new ShieldPU(world, 15.693604f * MyGdxGame.PPM, 1.4149998f * MyGdxGame.PPM);
//		powerUpList.add(shieldPU1);
		lifePU1 = new LifePU(world, 0.7061814f * MyGdxGame.PPM, 4.864999f * MyGdxGame.PPM -11);
		powerUpList.add(lifePU1);
		
//		tripleshotPU1 = new TripleshotPU(world, 280, 114);
//		powerUpList.add(tripleshotPU1);
//		laserPU1 = new LaserPU(world, 380, 114);
//		powerUpList.add(laserPU1);
		
		
		/*
		 * BEREICH FUER PLATFORM ERSTELLUNG
		 */
		
		platformList = new ArrayList<MovingPlatform>();
//		platform1 = new MovingPlatform(hero, world, new Vector2(17.828794f * MyGdxGame.PPM, 1.9633348f * MyGdxGame.PPM), new Vector2(20.268723f * MyGdxGame.PPM, 1.9633348f * MyGdxGame.PPM), new Vector2(1, 0));
//		platformList.add(platform1);
//
//		platform2 = new MovingPlatform(hero, world, new Vector2(20.912457f * MyGdxGame.PPM, 3.3688898f * MyGdxGame.PPM), new Vector2(22.268723f * MyGdxGame.PPM, 3.3688898f * MyGdxGame.PPM), new Vector2(1, 0));
//		platformList.add(platform2);
//		
//		platform3 = new MovingPlatform(hero, world, new Vector2(44.50979f * MyGdxGame.PPM, 2.1716661f * MyGdxGame.PPM), new Vector2(47.80979f * MyGdxGame.PPM, 2.1716661f * MyGdxGame.PPM), new Vector2(1, 0));
//		platformList.add(platform3);
		
		initCheckpoints();

		//GameOver
		gameOverStage= new Stage(hudport);
		gameOverMenu = new GameOverMenu();
		gameOverMenu.retry.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				gameOverMenu.disable();
				game.playButtonClicked();
				restart();
			}});
		gameOverMenu.mainMenu.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				gameOverMenu.disable();
				game.playButtonClicked();
				game.setScreen(new MainMenuScreen(game));
			}});
		gameOverStage.addActor(gameOverMenu.mainMenu);
		gameOverStage.addActor(gameOverMenu.retry);
		gameOver=false;
		labelX = Gdx.graphics.getWidth()/2-gameOverMenu.gameOverLabel.getWidth()/2;
		buttonX = -50;

		//Level complete
		levelCompleteStage= new Stage(hudport);
		levelCompletedMenu= new LevelCompletedMenu();
		levelCompletedMenu.continueButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				game.playButtonClicked();
				levelCompletedMenu.disable();
				game.setScreen(new MainMenuScreen(game));
			}});
		levelCompleteStage.addActor(levelCompletedMenu.continueButton);
		levelCompleted=false;
		
		allowInput = true;
		
	}
	
	/**
	 * Welt samt Positionen von Objekten (fuer das zeichnen von Sprites) aktualisieren und Kamera ausrichten
	 * @param delta Deltatime (Zeit in Sekunden seit dem letzten render Aufruf)
	 */
	public void update(float delta){
		handleInput(delta);
		handleCheckpoints();
		
		if(!paused){
			levelTime += delta;
			world.step(delta, 6, 2);
			if(hero.b2body.getPosition().y<-2.5 || hero.getHealth() == 0)
			{
				game.playDeath();
				respawn();
				hero.setDamageEffect(1);
			}
			hero.update(delta);
			shield.update(delta, hero);
			
			for(int i = 0; i < enemySoldierList.size(); i++){
				enemySoldierList.get(i).update(delta);
			}
			for(int i = 0; i < enemyHomingList.size(); i++){
				enemyHomingList.get(i).update(delta);
			}
			
			for(int i = 0; i < enemyTowerList.size(); i++){
				enemyTowerList.get(i).update(delta);
			}
			
			for(int i = 0; i < powerUpList.size(); i++){
				powerUpList.get(i).update();
			}
			
			for(int i = 0; i < platformList.size(); i++){
				platformList.get(i).update(delta);
			}
			
		}		
		
		//Groesse der Lebenleiste an das Leben des Helden anpassen
		if(hero.getHealth()>0){
			healthBar.setSize(barWidth*((float)hero.getHealth()/100), healthBar.getHeight());
			//healthBar.setSize(1.5f*hero.getHealth(), healthBar.getHeight());
		}
		else{
			healthBar.setSize(0, healthBar.getHeight());
		}
		
		//Groesse der Boss-Lebensleiste an das Leben des Bosses anpassen 
		if(bossfight && alienBoss != null){
			if(alienBoss.getLife()>0){
				bossHealthBar.setSize(bossBarWidth*((float)alienBoss.getLife()/5000), bossHealthBar.getHeight());
			}
			else{
				bossHealthBar.setSize(0, bossHealthBar.getHeight());
			}
			
		}
		
		
		
		if(hero.getBody().getPosition().x * MyGdxGame.PPM > MyGdxGame.V_WIDTH / 2 && !bossfight)
		gamecam.position.x = hero.getBody().getPosition().x;
		
		if(hero.getBody().getPosition().y * MyGdxGame.PPM > 500 && !bossfight)
		gamecam.position.y = hero.getBody().getPosition().y;
		
		
		//Camera erhoehen und Heromovement blockieren
		if(bossfight && gamecam.position.y < 5f && !spawned){
			gamecam.position.y += 0.012f;
			allowInput = false;
			hero.b2body.setLinearVelocity(0f, -2f);
			
			//Input wieder aktivieren beim letzten triggern des if-statements
			if(gamecam.position.y + 0.012f >= 5f){
				gamecam.position.y = 5f;
				
				//Boss spawnen
				if(alienBoss == null){
					alienBoss = new AlienBoss(world, hero, hero.b2body.getPosition().x, 6f, gamecam, game);
					spawned = true;
					alienBoss.setShake(31);
					game.playSpawnAlien();
				}
			}		
		}
		
		//Camera absenken und Heromovement blockieren
		else if(bossfight && gamecam.position.y > 5f && !spawned){
			gamecam.position.y -= 0.012f;
			allowInput = false;
			hero.b2body.setLinearVelocity(0f, -2f);
			
			//Input wieder aktivieren beim letzten triggern des if-statements
			if(gamecam.position.y - 0.012f <= 5f){
				gamecam.position.y = 5f;
				
				//Boss spawnen
				if(alienBoss == null){
					alienBoss = new AlienBoss(world, hero, hero.b2body.getPosition().x, 6f, gamecam, game);
					spawned = true;
					alienBoss.setShake(31);
					game.playSpawnAlien();
				}
			}
		}
		
		//Projektile (EnemyBullet) von bereits besiegten Gegnern manuell updaten (passiert sonst in jeweiliger Gegner-Update-Methode
		for(int i = 0; i < remainingEnemyBulletList.size(); i++){
			remainingEnemyBulletList.get(i).update(delta);
			//Wenn Objekt getroffen Fixture loeschen und aus Liste entfernen
			if(remainingEnemyBulletList.get(i).isHitObject()){
				remainingEnemyBulletList.get(i).getBody().destroyFixture(remainingEnemyBulletList.get(i).getBody().getFixtureList().get(0));
				remainingEnemyBulletList.remove(i);
			}
		}
		
		//Projektile (EnemySmallBullet) von bereits besiegten Gegnern manuell updaten (passiert sonst in jeweiliger Gegner-Update-Methode
		for(int i = 0; i < remainingEnemySmallBulletList.size(); i++){
			remainingEnemySmallBulletList.get(i).update(delta);
			//Wenn Objekt getroffen aus Liste entfernen
			if(remainingEnemySmallBulletList.get(i).isHitObject()){
				remainingEnemySmallBulletList.get(i).getBody().destroyFixture(remainingEnemySmallBulletList.get(i).getBody().getFixtureList().get(0));
				remainingEnemySmallBulletList.remove(i);
			}
		}
		
		//PowerUp aus der Liste entfernen, wenn es aufgesammelt wurde und den zugehoerigen Body zerstoeren
		for(int i = 0; i < powerUpList.size(); i++){
			if(powerUpList.get(i).isCollected() && powerUpList.get(i).getBody().getFixtureList().size != 0)
			{
				powerUpList.get(i).getBody().destroyFixture(powerUpList.get(i).getBody().getFixtureList().get(0));
				powerUpList.remove(i);
			}
		}
		
		//Soldaten-Gegner, die besiegt wurden aus der Liste entfernen und den zugehoerigen Body zerstoeren
		for(int i = 0; i < enemySoldierList.size(); i++){
			if(enemySoldierList.get(i).getLife() <= 0 && enemySoldierList.get(i).getBody().getFixtureList().size != 0)
			{
				hero.setScore(hero.getScore()+enemySoldierList.get(i).getPoints());
				enemySoldierList.get(i).getBody().destroyFixture(enemySoldierList.get(i).getBody().getFixtureList().get(0));

				//Verbleibende Projektile in Liste sammeln
				for(int j = 0; j < enemySoldierList.get(i).getBulletList().size(); j++){
					if(enemySoldierList.get(i).getBulletList().get(j).isHitObject()){
						enemySoldierList.get(i).getBulletList().get(j).getBody().destroyFixture(enemySoldierList.get(i).getBulletList().get(j).getBody().getFixtureList().get(0));
					}
					else{
						remainingEnemyBulletList.add(enemySoldierList.get(i).getBulletList().get(j));
					}
					
				}
				
				enemySoldierList.remove(i);
			}
		}
		
		if(alienBoss != null){
			for(int i = 0; i < alienBoss.getSoldierList().size(); i++){
				if(alienBoss.getSoldierList().get(i).getLife() <= 0 && alienBoss.getSoldierList().get(i).getBody().getFixtureList().size != 0)
				{
					hero.setScore(hero.getScore()+alienBoss.getSoldierList().get(i).getPoints());
					alienBoss.getSoldierList().get(i).getBody().destroyFixture(alienBoss.getSoldierList().get(i).getBody().getFixtureList().get(0));

					//Verbleibende Projektile in Liste sammeln
					for(int j = 0; j < alienBoss.getSoldierList().get(i).getBulletList().size(); j++){
						if(alienBoss.getSoldierList().get(i).getBulletList().get(j).isHitObject()){
							alienBoss.getSoldierList().get(i).getBulletList().get(j).getBody().destroyFixture(alienBoss.getSoldierList().get(i).getBulletList().get(j).getBody().getFixtureList().get(0));
						}
						else{
							remainingEnemyBulletList.add(alienBoss.getSoldierList().get(i).getBulletList().get(j));
						}
						
					}
					
					alienBoss.getSoldierList().remove(i);
				}
			}
		}
		
		//Homing-Gegner entfernen wenn getoetet
		if(alienBoss != null){
			for(int i = 0; i < alienBoss.getHomingList().size(); i++){
				if(alienBoss.getHomingList().get(i).getLife() <= 0 && alienBoss.getHomingList().get(i).getBody().getFixtureList().size != 0)
				{
					hero.setScore(hero.getScore()+alienBoss.getHomingList().get(i).getPoints());
					alienBoss.getHomingList().get(i).getBody().destroyFixture(alienBoss.getHomingList().get(i).getBody().getFixtureList().get(0));
					alienBoss.getHomingList().remove(i);
				}
			}
		}
		
		//Tower-Gegner, die besiegt wurden aus der Liste entfernen und den zugehoerigen Body zerstoeren
		for(int i = 0; i < enemyTowerList.size(); i++){
			if(enemyTowerList.get(i).getLife() <= 0 && enemyTowerList.get(i).getBody().getFixtureList().size != 0)
			{
				hero.setScore(hero.getScore()+enemyTowerList.get(i).getPoints());
				enemyTowerList.get(i).getBody().destroyFixture(enemyTowerList.get(i).getBody().getFixtureList().get(0));

				//Verbleibende Projektile in Liste sammeln
				for(int j = 0; j < enemyTowerList.get(i).getBulletList().size(); j++){
					if(enemyTowerList.get(i).getBulletList().get(j).isHitObject()){
						enemyTowerList.get(i).getBulletList().get(j).getBody().destroyFixture(enemyTowerList.get(i).getBulletList().get(j).getBody().getFixtureList().get(0));
					}
					else{
						remainingEnemySmallBulletList.add(enemyTowerList.get(i).getBulletList().get(j));
					}
					
				}
				
				enemyTowerList.remove(i);
			}
			
		}
		
		if(alienBoss != null && !alienBoss.defeated){
			alienBoss.update(delta);
			
			if(!alienBoss.defeated){
				for(int i = 0; i < alienBoss.getBulletList().size(); i++){
					alienBoss.getBulletList().get(i).update(delta);
				}
			}
			
			for(int i = 0; i < alienBoss.getStachelList().size(); i++){
				alienBoss.getStachelList().get(i).update();
			}
			
			for(int i = 0; i < alienBoss.getSoldierList().size(); i++){
				alienBoss.getSoldierList().get(i).update(delta);

				for(int j = 0; j < alienBoss.getSoldierList().get(i).getBulletList().size(); j++){
					if(!alienBoss.getSoldierList().get(i).getBulletList().get(j).isHitObject()){
						alienBoss.getSoldierList().get(i).getBulletList().get(j).update(delta);
					}
				}
			}
			
			for(int i = 0; i < alienBoss.getHomingList().size(); i++){
				alienBoss.getHomingList().get(i).update(delta);
			}
			
			if(alienBoss.defeated){
				alienBoss.destroyBoss();
			}
		}
		
		if(hero.getBody().getPosition().x >= 29.135097f && !bossfight && !levelCompleted){
			bossfight = true;
			
			createBossWalls();
			
			game.playBossTheme4();
		}
		
		gamecam.update();
		renderer.setView(gamecam);
	}
	
	/**
	 * Waende fuer den Bosskampf erzeugen
	 */
	private void createBossWalls() {
		
		/*
		 * Linke Wand im Bossraum erstellen
		 */
		BodyDef leftbdef = new BodyDef();
		PolygonShape leftshape = new PolygonShape();
		FixtureDef leftfdef = new FixtureDef();
		Body leftbody;
		
		leftbdef.type = BodyDef.BodyType.StaticBody;
		leftbdef.position.set(24.99682f - 1.15f, 3.3f);
		
		leftbody = world.createBody(leftbdef);
		
		leftshape.setAsBox(1f, 15f);
		leftfdef.shape = leftshape;
		leftbody.createFixture(leftfdef).setUserData("leftbosswall");
		
		
		
		/*
		 * Rechte Wand im Bossraum erstellen
		 */
		BodyDef rightbdef = new BodyDef();
		PolygonShape rightshape = new PolygonShape();
		FixtureDef rightfdef = new FixtureDef();
		Body rightbody;
		rightbdef.type = BodyDef.BodyType.StaticBody;
		rightbdef.position.set(33.50658f + 1f, 3.3f);
		
		rightbody = world.createBody(rightbdef);
		
		rightshape.setAsBox(1f, 15f);
		rightfdef.shape = rightshape;
		rightbody.createFixture(rightfdef).setUserData("rightbosswall");
	}
	
	/**
	 * Nutzereingaben interpretieren -> Realisiert Steuerung des Helden
	 * @param delta Deltatime (Zeit in Sekunden seit dem letzten render Aufruf)
	 */
	public void handleInput(float delta) {
		if (!gameOver && allowInput && !levelCompleted){
			if (Application.ApplicationType.Desktop == Gdx.app.getType()) {

				if (!paused) {

					//Sprung
					if ((Gdx.input.isKeyJustPressed(Input.Keys.W) || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) && hero.getState() != State.JUMPING && hero.getState() != State.FALLING)
						hero.getBody().applyLinearImpulse(new Vector2(0, 5.5f), hero.getBody().getWorldCenter(), true);

					//Nach rechts laufen
					if (Gdx.input.isKeyPressed(Input.Keys.D) && hero.getBody().getLinearVelocity().x <= 3) {
						hero.getBody().applyLinearImpulse(new Vector2(0.15f, 0), hero.getBody().getWorldCenter(), true);

						if (hero.getBody().getLinearVelocity().x < 0) {
							//hero.getBody().setLinearVelocity(0, hero.getBody().getLinearVelocity().y);
							hero.getBody().applyLinearImpulse(new Vector2(0.15f, 0), hero.getBody().getWorldCenter(), true);
						}
					}

					//Nach links laufen
					if (Gdx.input.isKeyPressed(Input.Keys.A) && hero.getBody().getLinearVelocity().x >= -3) {
						hero.getBody().applyLinearImpulse(new Vector2(-0.15f, 0), hero.getBody().getWorldCenter(), true);

						if (hero.getBody().getLinearVelocity().x > 0) {
							//hero.getBody().setLinearVelocity(0, hero.getBody().getLinearVelocity().y);
							hero.getBody().applyLinearImpulse(new Vector2(-0.15f, 0), hero.getBody().getWorldCenter(), true);
						}
					}

					//Bremst den Spieler aus wenn er sich nicht bewegt
					if (!Gdx.input.isKeyPressed(Input.Keys.D) && !Gdx.input.isKeyPressed(Input.Keys.A) && hero.getState() == State.RUNNING)
						hero.getBody().applyLinearImpulse(new Vector2(hero.getBody().getLinearVelocity().x / 40 * (-1), 0), hero.getBody().getWorldCenter(), true);

					//Schiessen in Richtung Mausposition bei Mausklick
					if (Gdx.input.isTouched()) {

						hero.shoot(gamecam.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0)).x, gamecam.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0)).y, delta);


					}
					if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
						System.out.println("X: " + hero.b2body.getPosition().x + " Y: " + hero.b2body.getPosition().y);
					}
				}
				//Pausieren
				if(Gdx.input.isKeyJustPressed(Keys.ESCAPE)){

					if (paused) {
						Gdx.input.setInputProcessor(hudStage);
						pauseMenu.disableButtons();
					} else {
						Gdx.input.setInputProcessor(pauseStage);
						pauseMenu.enableButtons();
					}

					paused = !paused;


				}
			} else {
				//Android Steuerung
				//Pausieren
				if(Gdx.input.isKeyJustPressed(Keys.BACK) || Gdx.input.isKeyJustPressed(Keys.MENU)){

					if (paused) {
						Gdx.input.setInputProcessor(hudStage);
						pauseMenu.disableButtons();
					} else {
						Gdx.input.setInputProcessor(pauseStage);
						pauseMenu.enableButtons();
					}

					paused = !paused;
				}
				//Hero Steuerung
				if (!paused) {

					if (runningStick.touchpad.isTouched()) {
						if (runningStick.touchpad.getKnobPercentX() > 0.3 && hero.b2body.getLinearVelocity().x <= 3) {
							hero.b2body.applyLinearImpulse(new Vector2(0.15f, 0), hero.b2body.getWorldCenter(), true);
						} else if (runningStick.touchpad.getKnobPercentX() < -0.3 && hero.b2body.getLinearVelocity().x >= -3) {
							hero.b2body.applyLinearImpulse(new Vector2(-0.15f, 0), hero.b2body.getWorldCenter(), true);
						}
					}
					if (aimStick.touchpad.isTouched() && (Math.abs(aimStick.touchpad.getKnobPercentX()) > 0.2 || Math.abs(aimStick.touchpad.getKnobPercentY()) > 0.2)) {
						hero.shoot((aimStick.touchpad.getKnobPercentX() > 0) ? (100f - Math.abs(aimStick.touchpad.getKnobPercentY() * 100)) : -(100f - Math.abs(aimStick.touchpad.getKnobPercentY() * 100)), ((aimStick.touchpad.getKnobPercentY() > 0) ? Math.abs(aimStick.touchpad.getKnobPercentY()) * 30 : Math.abs(aimStick.touchpad.getKnobPercentY()) * -30), delta);
					}
				}
			}
	}
		else{
			if(gameOver) {
				if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
					gameOverMenu.retry.setY(Gdx.graphics.getHeight() / 2 - 75);
					gameOverMenu.setLabelY(Gdx.graphics.getHeight() / 2 + 75);
				}
				Gdx.input.setInputProcessor(gameOverStage);
			}
			else
			{
				if(Gdx.input.isKeyJustPressed(Input.Keys.ENTER))
				{
					levelCompletedMenu.continueButton.setX(Gdx.graphics.getWidth()/2-levelCompletedMenu.continueButton.getWidth()/2);
					levelCompletedMenu.setLabelX(Gdx.graphics.getWidth()/2-levelCompletedMenu.levelCompleted.getWidth()/2);
				}
				Gdx.input.setInputProcessor(levelCompleteStage);
			}
		}

	}

	@Override
	public void render(float delta) {
		
		update(delta);
		
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		
		batch.setProjectionMatrix(gamecam.combined);
		renderer.render();
		
		//DebugRenderer
		//b2dr.render(world, gamecam.combined);

		
		batch.begin();

		
		//Alle Platformen malen
		for(int i = 0; i < platformList.size(); i++){
			platformList.get(i).draw(batch);
		}
		
		//Alle PowerUps zeichnen
		for(int i = 0; i < powerUpList.size(); i++){
			((Sprite)powerUpList.get(i)).draw(batch);
		}
		
		//Checkpoints zeichnen
		for(int i=0;i<checkpoints.length;i++)
		{
			checkpoints[i].draw(batch);
		}
		
		//Respawn "Animation"
		if(respawnTimer<10)
		{
			hero.draw(batch);
			respawnTimer++;
		}
		else if(respawnTimer<20)
		{
			respawnTimer++;
		}
		else if(respawnTimer<30)
		{
			hero.draw(batch);
			respawnTimer++;
		}
		else if(respawnTimer<40)
		{
			respawnTimer++;
		}
		else if(respawnTimer<50)
		{
			hero.draw(batch);
			respawnTimer++;
		}
		else if(respawnTimer<60) {
			respawnTimer++;
		}
		else if(respawnTimer==100)
		{
			//Falls erschossen, nicht zeichnen
		}
		else
		{		
			hero.draw(batch);
		}
		
		if(shield.isActive())
		{
			shield.draw(batch);
		}	
		
		//Alle Projektile malen, die noch nichts getroffen haben
		for(int i = 0; i < hero.getBulletList().size(); i++){
			if(!hero.getBulletList().get(i).hitObject)
			{
				hero.getBulletList().get(i).draw(batch);
			}
		}
		
		for(int i = 0; i < hero.getLaserBulletList().size(); i++){
			if(!hero.getLaserBulletList().get(i).hitObject)
			{
				hero.getLaserBulletList().get(i).draw(batch);
			}
		}
		
		//Alle Gegner malen - Drohnen
		for(int i = 0; i < enemyHomingList.size(); i++){
			enemyHomingList.get(i).draw(batch);
		}
		
		//Alle Gegner + Projektile malen - Soldaten
		for(int i = 0; i < enemySoldierList.size(); i++){
			enemySoldierList.get(i).draw(batch);
			
			for(int j = 0; j < enemySoldierList.get(i).getBulletList().size(); j++){
				if(!enemySoldierList.get(i).getBulletList().get(j).isHitObject()){
					enemySoldierList.get(i).getBulletList().get(j).draw(batch);
				}
			}
		}
		
		//Alle Gegner + Projektile malen - Tower
		for(int i = 0; i < enemyTowerList.size(); i++){
			enemyTowerList.get(i).draw(batch);
			
			for(int j = 0; j < enemyTowerList.get(i).getBulletList().size(); j++){
				if(!enemyTowerList.get(i).getBulletList().get(j).isHitObject()){
					enemyTowerList.get(i).getBulletList().get(j).draw(batch);
				}
			}
		}
		
		//Projektile (EnemyBullet) von bereits besiegten Gegnern malen
		for(int i = 0; i < remainingEnemyBulletList.size(); i++){
			if(!remainingEnemyBulletList.get(i).isHitObject()){
				remainingEnemyBulletList.get(i).draw(batch);
			}
		}
		
		//Projektile (EnemySmallBullet) von bereits besiegten Gegnern malen
		for(int i = 0; i < remainingEnemySmallBulletList.size(); i++){
			if(!remainingEnemySmallBulletList.get(i).isHitObject()){
				remainingEnemySmallBulletList.get(i).draw(batch);
			}
		}
		
		if(alienBoss != null && bossalphalevel < 1){
			alienBoss.draw(batch, bossalphalevel);
			bossalphalevel += 0.005f;
			
			if(bossalphalevel >= 1){
				bossalphalevel = 1;
				alienBoss.setSpawned(true);
				allowInput = true;
				alienBoss.setAlpha(1f);
			}
		}
		
		else if(alienBoss != null && !alienBoss.defeated){
			alienBoss.draw(batch);

			for(int i = 0; i < alienBoss.getBulletList().size(); i++){
				alienBoss.getBulletList().get(i).draw(batch);
			}
			
			for(int i = 0; i < alienBoss.getStachelList().size(); i++){
				alienBoss.getStachelList().get(i).draw(batch);
			}
			
			for(int i = 0; i < alienBoss.getSoldierList().size(); i++){
				alienBoss.getSoldierList().get(i).draw(batch);
				
				for(int j = 0; j < alienBoss.getSoldierList().get(i).getBulletList().size(); j++){
					if(!alienBoss.getSoldierList().get(i).getBulletList().get(j).isHitObject()){
						alienBoss.getSoldierList().get(i).getBulletList().get(j).draw(batch);;
					}
				}
			}
			
			for(int i = 0; i < alienBoss.getHomingList().size(); i++){
				alienBoss.getHomingList().get(i).draw(batch);
			}
		}


		batch.end();
		
		batch.setProjectionMatrix(hud.combined);
		batch.begin();
		//Anzeige fuer Leben, Score, Munition
		statBar.draw(batch);
		//Lebensleiste
		healthBar.draw(batch);
		//Beschriftung Anzeige
		font.draw(batch, getTimeFormatted(), timePosition.x, timePosition.y);
		font.draw(batch, "Score: " + hero.getScore(), scorePosition.x, scorePosition.y);
		font.draw(batch, Integer.toString(hero.getLives()), livesPosition.x, livesPosition.y);
		font.draw(batch, Integer.toString(hero.getCurrentAmmo()), ammoPosition.x, ammoPosition.y);

		//Anzeige fuer Boss-Lebensleiste zeichnen
		if(bossfight && alienBoss != null){
			bossBar.draw(batch);
			bossHealthBar.draw(batch);
		}
			
		/*
	 	font.draw(batch, "Score: " + hero.getScore(), 10+5*1.5f, 720-10-(20*1.5f));
		font.draw(batch, Integer.toString(hero.getLives()), 11+128*1.5f, 720-10-(5));
		font.draw(batch, Integer.toString(hero.getAmmo()), 10+121*1.5f, 720-10-(20*1.5f));
		
		Gdx.graphics.getWidth()/(MyGdxGame.V_WIDTH/(highscoreButtonBody.getPosition().x*MyGdxGame.PPM-70)),
								Gdx.graphics.getHeight()/(MyGdxGame.V_HEIGHT/(highscoreButtonBody.getPosition().y*MyGdxGame.PPM-15)));//x-70,y-15
		 */
		
		batch.end();
		
		//Android-Steuerelemente 
		if(Application.ApplicationType.Android==Gdx.app.getType()) {
			hudStage.act(Gdx.graphics.getDeltaTime());
			hudStage.draw();
		}

		
		if(paused || gameOver || levelCompleted){
			//Hintergrund dunkler
        	Gdx.gl.glEnable(GL20.GL_BLEND);
        	Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        	shape.setProjectionMatrix(gamecam.combined);
        	shape.begin(ShapeType.Filled);
        	shape.setColor(new Color(0, 0, 0, 0.5f));
        	shape.rect(0, 0, MyGdxGame.V_WIDTH, MyGdxGame.V_HEIGHT);
        	shape.end();
        	Gdx.gl.glDisable(GL20.GL_BLEND);
        	//Menu
			if(paused) {
				pauseStage.act(Gdx.graphics.getDeltaTime());
				pauseStage.draw();
			}
			else if(gameOver)
			{
				if(gameOverMenu.getLabelY()>Gdx.graphics.getHeight()/2+75)
				{
					gameOverMenu.setLabelY(gameOverMenu.getLabelY()-3f);
				}
				batch.begin();
				batch.draw(gameOverMenu.gameOverLabel,labelX,gameOverMenu.getLabelY());
				batch.end();
				if(gameOverMenu.retry.getY()<Gdx.graphics.getHeight()/2-75)
				{
					gameOverMenu.retry.setY(gameOverMenu.retry.getY()+3f);
				}
				if(gameOverMenu.mainMenu.getY()<Gdx.graphics.getHeight()/2-150)
				{
					gameOverMenu.mainMenu.setY(gameOverMenu.mainMenu.getY()+3f);
				}
				gameOverStage.act(Gdx.graphics.getDeltaTime());
				gameOverStage.draw();
			}
			else
			{
				if(levelCompletedMenu.getLabelX()<Gdx.graphics.getWidth()/2-levelCompletedMenu.getLevelCompleted().getWidth()/2)
				{
					levelCompletedMenu.setLabelX(levelCompletedMenu.getLabelX()+6f);
				}
				batch.begin();
				batch.draw(levelCompletedMenu.getLevelCompleted(),levelCompletedMenu.getLabelX(),Gdx.graphics.getHeight()/2+75);
				batch.end();
				if(levelCompletedMenu.continueButton.getX()>Gdx.graphics.getWidth()/2-levelCompletedMenu.continueButton.getWidth()/2)
				{
					levelCompletedMenu.continueButton.setX(levelCompletedMenu.continueButton.getX()-6f);
				}
				levelCompleteStage.act(delta);
				levelCompleteStage.draw();

			}
		}

		
		if(bossfight && alienBoss != null && alienBoss.defeated)
		{
			bossfight = false;
			levelCompleted = true;
			game.playLevelUp();
			stageCompleted();
		}


		Gdx.graphics.setTitle("Delta Commander | " + Gdx.graphics.getFramesPerSecond() + " FPS");
	}

	@Override
	public void resize(int width, int height) {
		gameport.update(width, height);
		hudport.update(width, height);
	}

	@Override
	public void pause() {
		
	}

	@Override
	public void resume() {
		
	}

	@Override
	public void hide() {
		
	}

	@Override
	public void dispose() {
		map.dispose();
		renderer.dispose();
		world.dispose();
		b2dr.dispose();
		shape.dispose();
		batch.dispose();
	}
	
	
	
	/**
	 * ContactListener, der alle Kollisionen zwischen Fixtures in der Box2D-Welt interpretiert
	 */
  	public class WorldContactListener implements ContactListener {

  		@Override
  		public void beginContact(Contact contact) {
  			Fixture fixA = contact.getFixtureA();
  			Fixture fixB = contact.getFixtureB();
  			
  			//Kollisionen von Hero-Fixture (Spielercharakter) mit anderen Objekten
  			if(fixA.getUserData() == "hero" || fixB.getUserData() == "hero"){
  				Fixture heroFix = fixA.getUserData() == "hero" ? fixA : fixB;
  				Fixture object = heroFix == fixA ? fixB : fixA;
  				
  				//Kollision mit MovingPlatform
  				if(object.getUserData() instanceof MovingPlatform){
  					((MovingPlatform) object.getUserData()).onHit();
  				}
  				//Kollision mit Health-PowerUp
  				else if(object.getUserData() == "health"){
  					game.playPowerUp();
  					hero.setHealth(hero.getHealth()+25);
  					((HealthPU)object.getBody().getUserData()).setCollected(true);
  				}
  				//Kollision mit Life-PowerUp
  				else if(object.getUserData() == "life"){
  					game.playPowerUp();
  					hero.setLives(hero.getLives()+1);
  					((LifePU)object.getBody().getUserData()).setCollected(true);
  				}
  				//Kollision mit Shield-PowerUp
  				else if(object.getUserData() == "shield"){
  					game.playPowerUp();
  					shield.activate();
  					((ShieldPU)object.getBody().getUserData()).setCollected(true);  			
  				}
  				//Kollision mit Tripleshot-PowerUp
  				else if(object.getUserData() == "triple"){
  					game.playPowerUp();
  					hero.setTripleAmmo(hero.getTripleAmmo() + 15);
  					((TripleshotPU)object.getBody().getUserData()).setCollected(true);
  				}
  				//Kollision mit Laser-PowerUp
  				else if(object.getUserData() == "laser"){
  					game.playPowerUp();
  					hero.setLaserAmmo(hero.getLaserAmmo() + 30);
  					((LaserPU)object.getBody().getUserData()).setCollected(true);
  				}
  				
  				//Kollision mit Alienboss
  				else if(object.getUserData() == "alienboss" && alienBoss.currentState == enemies.AlienBoss.State.SWIPING && !shield.isActive() && !hero.isInvulnerable()){
  					hero.setHealth(hero.getHealth() - 25);
  					hero.setDamageEffect(0.25f);
  				}
  			}
  			
  			//Kollision von Bullet-Fixture (Projektil, das von Hero geschossen wird) mit anderen Objekten
  			if(fixA.getUserData() == "bullet" || fixB.getUserData() == "bullet" && fixA.getUserData() != fixB.getUserData())
  			{
  				Fixture bullet = fixA.getUserData() == "bullet" ? fixA : fixB;
  				Fixture object = bullet == fixA ? fixB : fixA;
  			 	
  				if((!((Bullet)bullet.getBody().getUserData()).isHitObject()) && object.getUserData() != "hero" && object.getUserData() != null)
  				{
  					//Kollision mit EnemySoldier-Gegner
  					if(object.getUserData() == "enemysoldier")
  					{
  						((EnemySoldier)object.getBody().getUserData()).setLife(((EnemySoldier)object.getBody().getUserData()).getLife()-((Bullet)bullet.getBody().getUserData()).getDamage());
  						((Bullet)bullet.getBody().getUserData()).setHitObject(true);
  					}
  					//Kollision mit EnemyTower-Gegner
  					else if(object.getUserData() == "enemytower")
  					{
  						((EnemyTower)object.getBody().getUserData()).setLife(((EnemyTower)object.getBody().getUserData()).getLife()-((Bullet)bullet.getBody().getUserData()).getDamage());
  						((Bullet)bullet.getBody().getUserData()).setHitObject(true);
  					}
  					
  					//Kollision mit EnemyHoming-Gegner
  					else if(object.getUserData() == "enemyhoming")
  					{
  						((EnemyHoming)object.getBody().getUserData()).setLife(((EnemyHoming)object.getBody().getUserData()).getLife()-((Bullet)bullet.getBody().getUserData()).getDamage());
  						((Bullet)bullet.getBody().getUserData()).setHitObject(true);
  					}
  					

  					//Kollision mit Boss
  					else if(object.getUserData() == "alienboss")
  					{
  						((AlienBoss)object.getBody().getUserData()).setLife(((AlienBoss)object.getBody().getUserData()).getLife()-((Bullet)bullet.getBody().getUserData()).getDamage());
  						((Bullet)bullet.getBody().getUserData()).setHitObject(true);
  					}
  					
  					//Kollision anderem Objekt, das keine Projektile durchlassen soll
  					else if(object.getUserData() == "map" || object.getUserData() instanceof MovingPlatform)
  					{
  						((Bullet)bullet.getBody().getUserData()).setHitObject(true);
  					}			
  					
  					//Andere Bullets, JoinHero nicht betrachtet	
  				}	
  			}
			//Kollision von Bullet-Fixture (Projektil, das von Join-Hero geschossen wird) mit anderen Objekten
			if(fixA.getUserData() == "joinherobullet" || fixB.getUserData() == "joinherobullet" && fixA.getUserData() != fixB.getUserData())
			{
				Fixture bullet = fixA.getUserData() == "joinherobullet" ? fixA : fixB;
				Fixture object = bullet == fixA ? fixB : fixA;
				if(object.getUserData() != null&&(!((JoinHeroBullet)bullet.getBody().getUserData()).isHitObject()) && object.getUserData() != "joinhero" )
				{
					//Kollision mit EnemySoldier-Gegner
					if(object.getUserData() == "enemysoldier")
					{
						((EnemySoldier)object.getBody().getUserData()).setLife(((EnemySoldier)object.getBody().getUserData()).getLife()-((JoinHeroBullet)bullet.getBody().getUserData()).getDamage());
						((JoinHeroBullet)bullet.getBody().getUserData()).setHitObject(true);
					}
					//Kollision mit EnemyTower-Gegner
					else if(object.getUserData() == "enemytower")
					{
						((EnemyTower)object.getBody().getUserData()).setLife(((EnemyTower)object.getBody().getUserData()).getLife()-((JoinHeroBullet)bullet.getBody().getUserData()).getDamage());
						((JoinHeroBullet)bullet.getBody().getUserData()).setHitObject(true);
					}
					//Kollision mit EnemyHoming-Gegner
					else if(object.getUserData() == "enemyhoming")
					{
						((EnemyHoming)object.getBody().getUserData()).damage(((JoinHeroBullet)bullet.getBody().getUserData()).getDamage());
						((JoinHeroBullet)bullet.getBody().getUserData()).setHitObject(true);
					}
					//Kollision mit TankBoss Gegner
					else if(object.getUserData() == "alienboss")
					{
						((AlienBoss)object.getBody().getUserData()).setLife(((AlienBoss)object.getBody().getUserData()).getLife()-((JoinHeroBullet)bullet.getBody().getUserData()).getDamage());
						((JoinHeroBullet)bullet.getBody().getUserData()).setHitObject(true);

					}
					//Kollision anderem Objekt, das keine Projektile durchlassen soll
					else if(object.getUserData() == "map" || object.getUserData() instanceof MovingPlatform)
					{
						((JoinHeroBullet)bullet.getBody().getUserData()).setHitObject(true);
					}

				}
			}
  			
  			//Kollision von LaserBullet-Fixture (Projektil, das von Hero geschossen wird) mit anderen Objekten
  			if(fixA.getUserData() == "laserbullet" || fixB.getUserData() == "laserbullet" && fixA.getUserData() != fixB.getUserData())
  			{
  				Fixture bullet = fixA.getUserData() == "laserbullet" ? fixA : fixB;
  				Fixture object = bullet == fixA ? fixB : fixA;
  			 	
  				if((!((Laserbullet)bullet.getBody().getUserData()).isHitObject()) && object.getUserData() != "hero" && object.getUserData() != null)
  				{
  					//Kollision mit EnemySoldier-Gegner
  					if(object.getUserData() == "enemysoldier")
  					{
  						((EnemySoldier)object.getBody().getUserData()).setLife(((EnemySoldier)object.getBody().getUserData()).getLife()-((Laserbullet)bullet.getBody().getUserData()).getDamage());
  						((Laserbullet)bullet.getBody().getUserData()).setHitObject(true);
  					}
  					//Kollision mit EnemyTower-Gegner
  					else if(object.getUserData() == "enemytower")
  					{
  						((EnemyTower)object.getBody().getUserData()).setLife(((EnemyTower)object.getBody().getUserData()).getLife()-((Laserbullet)bullet.getBody().getUserData()).getDamage());
  						((Laserbullet)bullet.getBody().getUserData()).setHitObject(true);
  					}
  					//Kollision mit EnemyHoming-Gegner
  					else if(object.getUserData() == "enemyhoming")
  					{
  						((EnemyHoming)object.getBody().getUserData()).damage(((Laserbullet)bullet.getBody().getUserData()).getDamage());
  						((Laserbullet)bullet.getBody().getUserData()).setHitObject(true);
  					}
  					//Kollision anderem Objekt, das keine Projektile durchlassen soll
  					else if(object.getUserData() == "map" || object.getUserData() instanceof MovingPlatform)
  					{
  						((Laserbullet)bullet.getBody().getUserData()).setHitObject(true);
  					}			
  					
  				}	
  			}
  			
  			//Kollision von EnemyBullet-Fixture (Projektil, das von EnemySoldier geschossen wird) mit anderen Objekten
  			if(fixA.getUserData() == "enemybullet" || fixB.getUserData() == "enemybullet" && fixA.getUserData() != fixB.getUserData())
  			{
  				Fixture bullet = fixA.getUserData() == "enemybullet" ? fixA : fixB;
  				Fixture object = bullet == fixA ? fixB : fixA;
  			 	
  				if((!((EnemyBullet)bullet.getBody().getUserData()).isHitObject()) && object.getUserData() != "enemysoldier" && object.getUserData() != null)
  				{
  					//Kollision mit Hero
  					if(object.getUserData() == "hero")
  					{
  						//Leben nur abziehen, wenn kein Schild
  						if(!shield.isActive() && !hero.isInvulnerable())
  						{
  							hero.setHealth(hero.getHealth()-((EnemyBullet)bullet.getBody().getUserData()).getDamage());
  							hero.setDamageEffect(0.25f);
  						}
  						((EnemyBullet)bullet.getBody().getUserData()).setHitObject(true);
  					}
  					//Kollision anderem Objekt, das keine Projektile durchlassen soll
  					else if(object.getUserData() == "map" || object.getUserData() instanceof MovingPlatform)
  					{
  						((EnemyBullet)bullet.getBody().getUserData()).setHitObject(true);
  					}			
  					
  					//Andere Bullets, JoinHero nicht betrachtet	
  				}	
  			}
  			
  			//Kollision von EnemyBullet-Fixture (Projektil, das von EnemySoldier geschossen wird) mit anderen Objekten
  			if(fixA.getUserData() == "alienbossbullet" || fixB.getUserData() == "alienbossbullet" && fixA.getUserData() != fixB.getUserData())
  			{
  				Fixture bullet = fixA.getUserData() == "alienbossbullet" ? fixA : fixB;
  				Fixture object = bullet == fixA ? fixB : fixA;
  			 	
  					//Kollision mit Hero
  					if(object.getUserData() == "hero")
  					{
  						//Leben nur abziehen, wenn kein Schild
  						if(!shield.isActive() && !hero.isInvulnerable())
  						{
  							hero.setHealth(hero.getHealth()-((EnemyBullet)bullet.getBody().getUserData()).getDamage());
  							hero.setDamageEffect(0.25f);
  						}
  						((EnemyBullet)bullet.getBody().getUserData()).setHitObject(true);
  					}
  					
  					//Andere Bullets, JoinHero nicht betrachtet	
  			}
  			
  			//Kollision von EnemySmallBullet-Fixture (Projektil, das von EnemyTower geschossen wird) mit anderen Objekten
  			if(fixA.getUserData() == "enemysmallbullet" || fixB.getUserData() == "enemysmallbullet" && fixA.getUserData() != fixB.getUserData())
  			{
  				Fixture bullet = fixA.getUserData() == "enemysmallbullet" ? fixA : fixB;
  				Fixture object = bullet == fixA ? fixB : fixA;
  			 	
  				if((!((EnemySmallBullet)bullet.getBody().getUserData()).isHitObject()) && object.getUserData() != "enemytower" && object.getUserData() != null)
  				{
  					//Kollision mit Hero
  					if(object.getUserData() == "hero")
  					{
  						//Leben nur abziehen, wenn kein Schild
  						if(!shield.isActive() && !hero.isInvulnerable())
  						{
  							hero.setHealth(hero.getHealth()-((EnemySmallBullet)bullet.getBody().getUserData()).getDamage());
  							hero.setDamageEffect(0.25f);
  						}
  						((EnemySmallBullet)bullet.getBody().getUserData()).setHitObject(true);
  					}
  					//Kollision anderem Objekt, das keine Projektile durchlassen soll
  					else if(object.getUserData() == "map" || object.getUserData() instanceof MovingPlatform)
  					{
  						((EnemySmallBullet)bullet.getBody().getUserData()).setHitObject(true);
  					}			
  					
  					//Andere Bullets, JoinHero nicht betrachtet	
  				}	
  			}
  			
  			//Kollision von Hero mit Stacheln oder Saeure
  			if(fixA.getUserData() == "hero" || fixB.getUserData() == "hero" && fixA.getUserData() != fixB.getUserData())
  			{
  				Fixture herofix = fixA.getUserData() == "hero" ? fixA : fixB;
  				Fixture object = herofix == fixA ? fixB : fixA;
  			 	
  					//Kollision mit Hero
  					if(object.getUserData() == "death" || object.getUserData() == "alienstacheln")
  					{
  						//Leben nur abziehen, wenn kein Schild
  						if(!shield.isActive() && !hero.isInvulnerable())
  						{
  							hero.setHealth(hero.getHealth() - 50);
  							hero.setDamageEffect(0.50f);
  						}
  					}
  					
  			}
  			
  			
  		}

  		@Override
  		public void endContact(Contact contact) {
  			Fixture fixA = contact.getFixtureA();
  			Fixture fixB = contact.getFixtureB();
  			
  			if(fixA.getUserData() == "hero" || fixB.getUserData() == "hero"){
  				Fixture heroFix = fixA.getUserData() == "hero" ? fixA : fixB;
  				Fixture object = heroFix == fixA ? fixB : fixA;
  				
  				if(object.getUserData() instanceof MovingPlatform){
  					((MovingPlatform) object.getUserData()).onEnd();
  				}
  			}
  		}
  		@Override
  		public void preSolve(Contact contact, Manifold oldManifold) {}
  		@Override
  		public void postSolve(Contact contact, ContactImpulse impulse) {}
  	}
  	
  	/**
  	 * Hero am letzten erreichten Checkpoint respawnen, wenn er gestorben ist. <br>
  	 * Lebenspunkte wiederherstellen und Anzahl der Versuche um 1 verringern. <br>
  	 * Alte Projektile entfernen.
  	 */
	public void respawn()
	{
		hero.setLives(hero.getLives()-1);
		hero.setHealth(100);
		if(hero.lives==0 && gameOver == false)
		{
			hero.b2body.setLinearVelocity(0,0);
			gameOver=true;
			game.playGameOver();
		}
		else {
			hero.b2body.setTransform(checkpoints[checkpointCounter].getX() + 0.4f, checkpoints[checkpointCounter].getY(), 0);
			hero.b2body.setLinearVelocity(0,0);
			
			if(!bossfight)
			gamecam.position.set(gameport.getWorldWidth() / 2, gameport.getWorldHeight() / 2, 0);
			
//			if(bossfight){
//				gamecam.position.x = 29.224142f;
//				gamecam.position.y = 5f;
//			}
			
			if(hero.getBody().getPosition().x * MyGdxGame.PPM > MyGdxGame.V_WIDTH / 2 && !bossfight)
			gamecam.position.x = hero.getBody().getPosition().x;
				
			if(hero.getBody().getPosition().y * MyGdxGame.PPM > 500 && !bossfight)
			gamecam.position.y = hero.getBody().getPosition().y;
			
			System.out.println("Respawn");
			
			for (EnemySoldier e : enemySoldierList) {

				for (EnemyBullet b : e.getBulletList()) {
					world.destroyBody(b.getBody());
					b.getBody().setUserData(null);
					b.setBody(null);
				}
				e.setBulletList(new ArrayList<EnemyBullet>());

			}
			for (EnemyTower e : enemyTowerList) {
				for (EnemySmallBullet b : e.getBulletList()) {
					world.destroyBody(b.getBody());
					b.getBody().setUserData(null);
					b.setBody(null);
				}
				e.setBulletList(new ArrayList<EnemySmallBullet>());
			}
			for (Bullet b : hero.getBulletList()) {
				world.destroyBody(b.getBody());
				b.getBody().setUserData(null);
				b.setBody(null);
			}
			hero.setBulletList(new ArrayList<Bullet>());

			shield.deactivate();
			respawnTimer = 0;
		}
	}
	/**
	 * Checkpoints initialisieren: Position setzen und ersten aktivieren.
	 */
	public void initCheckpoints()
	{
		//Checkpoints initialisieren
		
		checkpoints[0]= new Checkpoint(160 / MyGdxGame.PPM, 194 / MyGdxGame.PPM);
		checkpoints[1]= new Checkpoint(24.387583f, 3.5849996f - hero.getHeight()/2);
		checkpoints[0].switchOn();
		nextCheckpoint= checkpoints[1].getPosX();
		checkpointCounter=0;
		respawnTimer =80;

	}
	/**
	 * Checkpoints verwalten: Aktivieren, wenn Hero einen Checkpoint erreicht
	 */
	public void handleCheckpoints()
	{
		if(checkpointCounter!=checkpoints.length-1) {
			if (hero.b2body.getPosition().x > nextCheckpoint) {
				checkpointCounter++;
				checkpoints[checkpointCounter].switchOn();
				game.playCheckpoint();
				if (checkpointCounter < checkpoints.length - 1) {
					nextCheckpoint = checkpoints[checkpointCounter + 1].posX;
				}
			}
		}

	}

	
	
	/**
	 * Eigener World Creator fuer SciFi Level um Stacheln und Saeure (DeathLayer) zu realisieren
	 */
	public class B2WorldCreatorCustom {

		Hero hero;
		
		public B2WorldCreatorCustom(World world, TiledMap map){
			
			BodyDef bdef = new BodyDef();
			PolygonShape shape = new PolygonShape();
			FixtureDef fdef = new FixtureDef();
			Body body;
			
			//Elemente der Enviroment-Layer als Rectangle machen
			for(MapObject object : map.getLayers().get(2).getObjects().getByType(RectangleMapObject.class)){
				Rectangle rect = ((RectangleMapObject) object).getRectangle();
				
				bdef.type = BodyDef.BodyType.StaticBody;
				bdef.position.set((rect.getX() + rect.getWidth() / 2)  / (MyGdxGame.PPM * 4), (rect.getY() + rect.getHeight() / 2) / (MyGdxGame.PPM * 4));
				
				body = world.createBody(bdef);
				
				shape.setAsBox(rect.getWidth() / 2 / (MyGdxGame.PPM * 4), rect.getHeight() / 2 / (MyGdxGame.PPM * 4));
				fdef.shape = shape;
				body.createFixture(fdef).setUserData("map");;
			}
			
			
			//Elemente der Death-Layer als Rectangle machen
			for(MapObject object : map.getLayers().get(3).getObjects().getByType(RectangleMapObject.class)){
				Rectangle rect = ((RectangleMapObject) object).getRectangle();
				
				bdef.type = BodyDef.BodyType.StaticBody;
				bdef.position.set((rect.getX() + rect.getWidth() / 2)  / (MyGdxGame.PPM * 4), (rect.getY() + rect.getHeight() / 2) / (MyGdxGame.PPM * 4));
				
				body = world.createBody(bdef);
				
				shape.setAsBox(rect.getWidth() / 2 / (MyGdxGame.PPM * 4), rect.getHeight() / 2 / (MyGdxGame.PPM * 4));
				fdef.shape = shape;
				fdef.isSensor = true;
				body.createFixture(fdef).setUserData("death");;
			}
			
		}
	}

	/**
	 * Levelzeit formatieren
	 * @return formatierte Levelzeit
	 */
	private String getTimeFormatted(){
		String minutes = "00";
		String seconds = "00";
		if(levelTime > 600){
			minutes = Integer.toString((int) (levelTime/60));
		} else if (levelTime > 60){
			minutes = "0" + Integer.toString((int) (levelTime/60));
		}
		if((levelTime%60) > 10){
			seconds = Integer.toString((int) (levelTime%60));
		} else {
			seconds = "0" + Integer.toString((int) (levelTime%60));
		}
		return minutes + ":" + seconds;
	}
	
	/**
	 *	Score berechnen und speichern -> Beim Beenden des Levels aufrufen. <br>
	 *	Gegebenenfalls ins naechste Level wechseln.
	 */
	private void stageCompleted(){
		if(levelTime < timeToBeat){
			hero.setScore((int) (hero.getScore() + timeFactor * (timeToBeat - levelTime)));
		}
		XMLInteraction.saveScore("levelscifi3", game.getUsername(), hero.getScore());
		hero.updateSavedScore();
	}
	/**
	 * seperate Methode macht das erneute Starten im Multiplayer einfacher
	 */
	public void restart()
	{
		game.setScreen(new LevelScifi3(game));
	}
	
}