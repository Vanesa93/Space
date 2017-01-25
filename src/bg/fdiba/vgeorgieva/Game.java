package bg.fdiba.vgeorgieva;

import java.awt.Font;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;
import org.newdawn.slick.openal.Audio;
import org.newdawn.slick.openal.AudioLoader;
import org.newdawn.slick.openal.SoundStore;
/**
 * 
 * This is a <em>very basic</em> skeleton to init a game and run it.
 * 
 * @author Vanesa Georgieva
 * @version 1.0 $Id$
 */
public class Game {

	/** Game title */
	public static final String GAME_TITLE = "My Game";

	/** Screen size */
	private static int SCREEN_SIZE_WIDTH = 800;
	private static int SCREEN_SIZE_HEIGHT = 600;

	/** Desired frame time */
	private static final int FRAMERATE = 60;

	/** Desired frame time */
	private static final int MAX_LIFES = 3;
	private static final int TREASURES_TO_COLLECT = 20;
	private static final int START_OBJECTS_SPEED = 5;
	private static final int MAX_MINES_COUNT = 5;
	private static final int CURRENT_LEVEL = 1;
	private static final int HERO_START_X = 50;
	private static final int HERO_START_Y = 50;
	private static final int SCORE = 0;
	private static final int RECORD = 0;
	private static final boolean GAME_PAUSED = false;
	private static int INITIAL_TREASURES_COLLECTED = 0;
	
	/** Exit the game */
	private boolean finished;
	/** Pause the game */
	private boolean gamePaused = GAME_PAUSED;
	private LevelTile levelTile;
	private ArrayList<Entity> entities;
	private ArrayList<Entity> levelsTreasures;
	private ArrayList<Entity> levelsMines;
	private Life[] life = new Life[MAX_LIFES];
	private HeroEntity heroEntity;
	private int currentLevel = CURRENT_LEVEL;
	private static int startObjectsSpeed = START_OBJECTS_SPEED;
	private int lifes = MAX_LIFES;
	private int treasuresToCollect = TREASURES_TO_COLLECT;
	private int treasuresCollected = INITIAL_TREASURES_COLLECTED;
	private int score = SCORE;
	private int record = RECORD;
	private Audio meteroidSound;
	private Audio treasureCollectedSound;
	private Audio levelUp;
	private TrueTypeFont font;
	private TrueTypeFont fontSmaller;
	
	

	/**
	 * Application init
	 * 
	 * @param args
	 *            Commandline args
	 */
	public static void main(String[] args) {
		Game myGame = new Game();
		myGame.start();
	}

	public void start() {		
		try {
			init();
			run();
		} catch (Exception e) {
			e.printStackTrace(System.err);
			Sys.alert(GAME_TITLE, "An error occured and the game will exit.");
		} finally {
			cleanup();
		}

		System.exit(0);
	}

	public void restart() {		
		// restart the game
		try {
			// close current screen
			Display.destroy();
			// set all variables to their initial states
			gamePaused = GAME_PAUSED;
			startObjectsSpeed = START_OBJECTS_SPEED;
			lifes = MAX_LIFES;
			currentLevel = CURRENT_LEVEL;
			treasuresCollected = INITIAL_TREASURES_COLLECTED;
			treasuresToCollect = TREASURES_TO_COLLECT;
			score = SCORE;
			// Initialise new game
			init();
			run();
		} catch (Exception e) {
			e.printStackTrace(System.err);
			Sys.alert(GAME_TITLE, "An error occured and the game will exit.");
		} 
	}

	
	/**
	 * Initialise the game
	 * 
	 * @throws Exception
	 *             if init fails
	 */
	private void init() throws Exception {
		// Create a fullscreen window with 1:1 orthographic 2D projection, and
		// with
		// mouse, keyboard, and gamepad inputs.
		try {
			initGL(SCREEN_SIZE_WIDTH, SCREEN_SIZE_HEIGHT);
			// Initialise all textures
			initTextures();
		} catch (IOException e) {
			e.printStackTrace();
			finished = true;
		}
	}

	private void initGL(int width, int height) {
		try {
			Display.setDisplayMode(new DisplayMode(width, height));
			Display.setTitle(GAME_TITLE);
			Display.setFullscreen(false);
			Display.create();

			// Enable vsync if we can
			Display.setVSyncEnabled(true);
			
		} catch (LWJGLException e) {
			e.printStackTrace();
			System.exit(0);
		}

		GL11.glEnable(GL11.GL_TEXTURE_2D);

		GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

		// enable alpha blending
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		GL11.glViewport(0, 0, width, height);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);

		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glOrtho(0, width, height, 0, 1, -1);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);

		Font awtFont = new Font("Times New Roman", Font.BOLD, 24);
		Font awtFontSmaller = new Font("Times New Roman", Font.BOLD, 18);
		font = new TrueTypeFont(awtFont, true);
		fontSmaller = new TrueTypeFont(awtFontSmaller, true);
	}

	private void initTextures() throws IOException {
		// Generate sounds
		initSounds();
		// Generate level tiles
		initLevel();
		// Generate lifes  
		initLifes();
		//Generate hero
		initHero();
		// Generate the treasures
		initTreasures();
		// Generate the meteroids
		initMeteroids();
	}
	
	private void initHero() throws IOException{
		// initialise hero entity
		entities = new ArrayList<Entity>();
		Texture texture;
		texture = TextureLoader.getTexture("PNG",
				ResourceLoader.getResourceAsStream("res/avatar.png"));
		heroEntity = new HeroEntity(this, new MySprite(texture), HERO_START_X,
				HERO_START_Y);
		
		entities.add(heroEntity);
	}
	
	
	private void initSounds() throws IOException {
		// load all sounds
		meteroidSound = AudioLoader.getAudio("WAV", ResourceLoader.getResourceAsStream("res/meteoroid.wav"));
		treasureCollectedSound = AudioLoader.getAudio("WAV", ResourceLoader.getResourceAsStream("res/treasure.wav"));
		levelUp = AudioLoader.getAudio("WAV", ResourceLoader.getResourceAsStream("res/levelUp.wav"));
	}

	private void initLevel() throws IOException {
		// Initialize all background tiles
		Texture texture;
		texture = TextureLoader.getTexture("PNG",
				ResourceLoader.getResourceAsStream("res/space.png"));
		levelTile = new LevelTile(texture);
	}
	
	private void initLifes() throws IOException {
		// Initialize all life textures only if the hero has lifes 
		Texture texture;
		for(int i=0; i<=lifes-1; i++){
			texture = TextureLoader.getTexture("PNG",
					ResourceLoader.getResourceAsStream("res/life.png"));
			life[i] = new Life(texture);
		}
	}		

	private void initTreasures() throws IOException {	
		// Initialize all life treasures
		Random rand = new Random();
		int objectX;
		int objectY;
		levelsTreasures = new ArrayList<Entity>();
		Texture texture = TextureLoader.getTexture("PNG",
				ResourceLoader.getResourceAsStream("res/chest.png"));
		for (int m = 0; m < treasuresToCollect; m++) {
			// x for every treasure should be greater than SCREEN_SIZE_WIDTH and to be random
			objectX =SCREEN_SIZE_WIDTH + rand.nextInt(SCREEN_SIZE_WIDTH);
			objectY = rand.nextInt(SCREEN_SIZE_HEIGHT- texture.getImageHeight());
			TreasureEntity objectEntity = new TreasureEntity(new MySprite(texture),
					objectX, objectY);				
			levelsTreasures.add(objectEntity);
		}
	}
	
	private void initMeteroids() throws IOException {
		Random rand = new Random();
		int objectX;
		int objectY;
		levelsMines = new ArrayList<Entity>();
		Texture texture = TextureLoader.getTexture("PNG",
				ResourceLoader.getResourceAsStream("res/meteoroid.png"));
		for (int m = 0; m < MAX_MINES_COUNT; m++) {
			// x for every meteoroid should be greater than SCREEN_SIZE_WIDTH and to be random
			objectX =SCREEN_SIZE_WIDTH + rand.nextInt(SCREEN_SIZE_WIDTH);
			objectY = rand.nextInt(SCREEN_SIZE_HEIGHT- texture.getImageHeight());
			MeteoroidEntity objectEntity = new MeteoroidEntity(new MySprite(texture),
					objectX, objectY);
			levelsMines.add(objectEntity);
		}
	}

	/**
	 * Runs the game (the "main loop")
	 */
	private void run() {
		while (!finished) {
			// Always call Window.update(), all the time
			Display.update();

			if (Display.isCloseRequested()) {
				// Check for O/S close requests
				finished = true;
			} else if (Display.isActive()) {
				// The window is in the foreground, so we should play the game
					logic();
					render();
				Display.sync(FRAMERATE);
			} else {
				// The window is not in the foreground, so we can allow other
				// stuff to run and
				// infrequently update
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
				}
				logic();
				if (Display.isVisible() || Display.isDirty()) {
					// Only bother rendering if the window is visible or dirty
					render();
				}
			}
		}
	}

	/**
	 * Do any game-specific cleanup
	 */
	private void cleanup() {
		// TODO: save anything you want to disk here

		// Close the window
		Display.destroy();
	}

	/**
	 * Do all calculations, handle input, etc.
	 */
	private void logic() {
		// Example input handler: we'll check for the ESC key and finish the
		// game instantly when it's pressed
		if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
			finished = true;
		}
		
		// Press R - restart 
		if (Keyboard.isKeyDown(Keyboard.KEY_R)) {
			restart();
		}		
	
		// Press P - pause 
		if (Keyboard.isKeyDown(Keyboard.KEY_P) && lifes > 0) {
			gamePaused = true;
		}
		
		// Press S - resume
		if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
			gamePaused = false;
		}
		
		if(gamePaused == false && lifes > 0) {
				SoundStore.get().poll(0);
				logicHero();
				logicTreasures();
				logicMines();
				checkForCollision();
		}
	}

	/**
	 * Render the current frame
	 */
	private void render() {
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_STENCIL_BUFFER_BIT);
		Color.white.bind();

			drawLevel();
	
			drawObjects();	
			
			heroEntity.draw();
			
			drawHUD();
	}
	
	private void drawLifesHUD() {
		int startWidth = SCREEN_SIZE_WIDTH;
		for (int i = 0; i < lifes; i++) {
			startWidth = startWidth-50;
			life[i].draw(startWidth, 20);					
		}	
		
	}

	private void drawLevel() {
		for (int a = 0; a * levelTile.getHeight() < SCREEN_SIZE_HEIGHT; a++) {
			for (int b = 0; b * levelTile.getWidth() < SCREEN_SIZE_WIDTH; b++) {
				int textureX = levelTile.getWidth() * b;
				int textureY = levelTile.getHeight() * a;
				levelTile.draw(textureX, textureY);
			}
		}
	}

	private void drawObjects() {
		for (Entity entity : levelsTreasures) {
			if (entity.isVisible()) {
				entity.draw();
			}
		}

		for (Entity entity : levelsMines) {
			if (entity.isVisible()) {
				entity.draw();
			}
		}
	}

	private void drawHUD() {
		drawLifesHUD();
		
		drawGameProgressionHUD();
		
		if(lifes == 0){
			drawGameOverHUD();
		}
		if(gamePaused == true){
			drawGamePausedHUD();
		}else if(gamePaused == false){
			drawMenuHUD();			
		}
	}
	
	private void drawGamePausedHUD(){
		font.drawString(SCREEN_SIZE_WIDTH/2-50,SCREEN_SIZE_HEIGHT/2-50, String.format(Messages.getPause()),
				Color.white);
		font.drawString(SCREEN_SIZE_WIDTH/2-100,SCREEN_SIZE_HEIGHT/2, String.format(Messages.getResume()),
				Color.white);
	}
	private void drawMenuHUD() {
		font.drawString(SCREEN_SIZE_WIDTH-100, SCREEN_SIZE_HEIGHT-70, String.format(Messages.getPauseWithKey()),
				Color.white);
		font.drawString(SCREEN_SIZE_WIDTH-100, SCREEN_SIZE_HEIGHT-50, String.format(Messages.getRestartWithK()),
				Color.white);
		font.drawString(SCREEN_SIZE_WIDTH-100, SCREEN_SIZE_HEIGHT-30, String.format(Messages.getExitWithKey()),
				Color.white);
	}
	private void drawGameProgressionHUD() {
		font.drawString(10, 0, String.format(Messages.getCurrentLevel(),currentLevel)
				,Color.white);
		fontSmaller.drawString(320, 0, String.format(Messages.getCollectTreasures(),treasuresToCollect)
				,Color.white);
		font.drawString(10, 20, String.format(Messages.getTreasuresCollected(),treasuresCollected, treasuresToCollect)
				,Color.white);
		font.drawString(10,60, String.format(Messages.getScore(),score),
				Color.white);
	}
	
	private void drawGameOverHUD() {
		font.drawString(SCREEN_SIZE_WIDTH/2-50,SCREEN_SIZE_HEIGHT/2-50, String.format(Messages.getGameOver()),
				Color.white);
		font.drawString(SCREEN_SIZE_WIDTH/2-40,SCREEN_SIZE_HEIGHT/2, String.format(Messages.getScore(),score),
				Color.white);
		if(score < record){
		font.drawString(SCREEN_SIZE_WIDTH/2-40,SCREEN_SIZE_HEIGHT/2+50, String.format(Messages.getRecord(),record),
				Color.white);
		} else {
			font.drawString(SCREEN_SIZE_WIDTH/2-60,SCREEN_SIZE_HEIGHT/2+50, String.format(Messages.getNewRecord(),record),
				Color.white);
		} 
	}

	private void logicHero() {	
		if (Keyboard.isKeyDown(Keyboard.KEY_UP)) {
			if (heroEntity.getY() > 0) {
				heroEntity.setY(heroEntity.getY() - 10);
			}
		}

		if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
			if (heroEntity.getY() + heroEntity.getHeight() < Display
					.getDisplayMode().getHeight()) {
				heroEntity.setY(heroEntity.getY() + 10);
			}
		}
	}
	
	private void logicTreasures() {
		for (int i = 0; i < levelsTreasures.size(); i++) {
			Entity treasure = levelsTreasures.get(i);
			if (treasure.getX()>0) {
				treasure.setX(treasure.getX() - startObjectsSpeed);
			}
			else {
				changeObjectCoordinate(treasure);
			}
		}		
	}
	
	private void logicMines() {
		for (int i = 0; i < levelsMines.size(); i++) {
			Entity mine = levelsMines.get(i);
			if (mine.getX()>0) {
				mine.setX(mine.getX() - startObjectsSpeed);
			}
			else {
				changeObjectCoordinate(mine);
			}
		}		
	}

	private void checkForCollision() {
		for (int p = 0; p < entities.size(); p++) {
			for (int s = p + 1; s < entities.size(); s++) {
				Entity me = entities.get(p);
				Entity him = entities.get(s);

				if (me.collidesWith(him)) {
					me.collidedWith(him);
					him.collidedWith(me);
				}
			}
		}

		for (int i = 0; i < levelsTreasures.size(); i++) {
			Entity him = levelsTreasures.get(i);

			if (heroEntity.collidesWith(him)) {
				heroEntity.collidedWith(him);
				him.collidedWith(heroEntity);
			}
		}

		for (int i = 0; i < levelsMines.size(); i++) {
			Entity him = levelsMines.get(i);

			if (heroEntity.collidesWith(him)) {
				heroEntity.collidedWith(him);
				him.collidedWith(heroEntity);
			}
		}
	}
	
	private void changeObjectCoordinate(Entity entity) {
		Random rand = new Random();
		entity.setX(SCREEN_SIZE_WIDTH + rand.nextInt(SCREEN_SIZE_WIDTH*2 - SCREEN_SIZE_WIDTH + 1));
		entity.setY(rand.nextInt(SCREEN_SIZE_HEIGHT));				
	}

	public void notifyObjectCollision(Entity notifier, Object object) {		
		if (object instanceof TreasureEntity) {
			notifyObjectCollisionTreasure(object);			
		} else if (object instanceof MeteoroidEntity) {
		    notifyObjectCollisionMine(object);
		}
	}

	private void notifyObjectCollisionMine(Object object) {
		Entity mine = (Entity) object;
		meteroidSound.playAsSoundEffect(1.0f, 1.0f, false);
		changeObjectCoordinate(mine);
		lifes--;
		if(lifes == 0){
			if(record == 0){					
				record = score;
			} else if(score > record){
				record = score;
			}
		}
		
	}

	private void notifyObjectCollisionTreasure(Object object) {
		Entity treasure = (Entity) object;
		changeObjectCoordinate(treasure);
		treasuresCollected++;
		score++;
		treasureCollectedSound.playAsSoundEffect(1.0f, 1.0f, false);
		if(treasuresCollected == treasuresToCollect){
			treasuresCollected = INITIAL_TREASURES_COLLECTED;
			treasuresToCollect = treasuresToCollect+10;
			levelUp.playAsSoundEffect(1.0f, 1.0f, false);
			currentLevel++;
			startObjectsSpeed = startObjectsSpeed + 1; 
		}
		
	}
}
