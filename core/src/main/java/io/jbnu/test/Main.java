package io.jbnu.test;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {

    public enum GameState {
        RUNNING, // 게임이 활발하게 진행 중인 상태
        PAUSED, // 일시정지
        CLEAR // 게임 클리어
    }
    public static final int DEFAULT_SCREEN_WIDTH = 1280;
    public static final int DEFAULT_SCREEN_HEIGHT = 720;

    private GameWorld gameWorld;

    private SpriteBatch batch;
    private Texture playerTexture;
    private Texture dashActiveTexture;
    private Texture dashInActiveTexture;
    private Texture pauseTexture;
    private ShapeRenderer shapeRenderer;

    private Music backgroundSound;
    private Sound coinSound;
    private Sound hurtSound;
    private Sound jumpSound;
    private Sound dashSound;
    private Sound iceSpikeHitSound;
    private Sound enemyHitSound;
    private Sound deadZoneSound;
    private Sound nextLevelSound;
    private Sound finishSound;

    private BitmapFont coinProgress;

    private OrthographicCamera mainCamera;
    private Viewport viewport;
    private OrthographicCamera uiCamera;
    private Viewport uiViewport;

    private GameState currentState;
    private boolean isJustFrame;

    @Override
    public void create() {
        mainCamera = new OrthographicCamera();
        viewport = new FitViewport(1280,720, mainCamera);
        mainCamera.setToOrtho(false,1280,720);

        uiCamera = new OrthographicCamera();
        uiViewport = new FitViewport(1280, 720, uiCamera);

        batch = new SpriteBatch();

        playerTexture = new Texture("Tral.png");
        dashActiveTexture = new Texture("Dash_Active.png");
        dashInActiveTexture = new Texture("Dash_InActive.png");
        pauseTexture = new Texture("Pause.png");
        shapeRenderer = new ShapeRenderer();

        coinProgress = new BitmapFont();
        coinProgress.getData().setScale(2);

        coinSound = Gdx.audio.newSound(Gdx.files.internal("CoinSound.wav"));
        hurtSound = Gdx.audio.newSound(Gdx.files.internal("HurtSound.wav"));
        jumpSound = Gdx.audio.newSound(Gdx.files.internal("JumpSound.wav"));
        dashSound = Gdx.audio.newSound(Gdx.files.internal("Swoosh.mp3"));
        iceSpikeHitSound = Gdx.audio.newSound(Gdx.files.internal("IceSpikeHitSound.wav"));
        enemyHitSound = Gdx.audio.newSound(Gdx.files.internal("SahurSound.mp3"));
        deadZoneSound = Gdx.audio.newSound(Gdx.files.internal("DeadZoneSound.wav"));
        nextLevelSound = Gdx.audio.newSound(Gdx.files.internal("NextLevelSound.wav"));
        finishSound = Gdx.audio.newSound(Gdx.files.internal("FinishSound.mp3"));
        backgroundSound = Gdx.audio.newMusic(Gdx.files.internal("BGM.mp3"));
        backgroundSound.setLooping(true);
        backgroundSound.setVolume(0.2f);
        backgroundSound.play();

        gameWorld = new GameWorld(playerTexture, hurtSound, coinSound, iceSpikeHitSound, enemyHitSound, deadZoneSound, nextLevelSound);
        currentState = GameState.RUNNING;

        mainCamera.position.set(Math.round(gameWorld.getPlayer().position.x+playerTexture.getWidth()/2f),360,0);
    }

    @Override
    public void render() {
        ScreenUtils.clear(1f, 1f, 1f, 1f);
        input();
        logic();
        draw();
    }

    private void logic()
    {
        if (gameWorld.isAllLevelClear) { currentState = GameState.CLEAR; }
        if (currentState != GameState.RUNNING) return;

        gameWorld.update(Gdx.graphics.getDeltaTime(), mainCamera);

        mainCamera.position.set(Math.round(gameWorld.getPlayer().position.x+playerTexture.getWidth()/2f),360,0);
    }

    private void draw() {
        /* Main 세팅 */
        mainCamera.update();
        batch.setProjectionMatrix(viewport.getCamera().combined);

        batch.begin();
        gameWorld.drawLevel(mainCamera, batch);
        gameWorld.getPlayer().draw(batch);
        batch.end();

        /* UI 세팅 */
        uiCamera.update();
        batch.setProjectionMatrix(uiCamera.combined);

        batch.begin();
        batch.draw(gameWorld.getCurrentLevel().getCoinTexture(), 50, 620, 80, 80);
        Texture dashIcon = gameWorld.enoughDashCoolDown() ? dashActiveTexture : dashInActiveTexture;
        coinProgress.draw(batch, gameWorld.getCoinCnt() + " / " + gameWorld.getCurrentLevel().maxCoinAmount, 150, 670);

        float iconX = 1150;
        float iconY = 650;
        batch.draw(dashIcon, iconX, iconY, 100, 50);
        batch.end();

        /* Rope 세팅 */
        gameWorld.getRopeSystem().ropeRender(mainCamera, gameWorld.getPlayer());

        /* 일시정지 세팅 */
        if (currentState == GameState.PAUSED || currentState == GameState.CLEAR)
        {
            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

            shapeRenderer.setProjectionMatrix(uiCamera.combined);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(0, 0, 0, 0.5f); // 회색 투명도 50%
            shapeRenderer.rect(0, 0, DEFAULT_SCREEN_WIDTH, DEFAULT_SCREEN_HEIGHT);
            shapeRenderer.end();

            Gdx.gl.glDisable(GL20.GL_BLEND);

            batch.begin();
            if (currentState == GameState.CLEAR)
            {
                BitmapFont finishText = new BitmapFont();
                finishText.getData().setScale(2);
                finishText.draw(batch, "Game Clear! Press ESC to exit the game", 380, 300);
                if (!isJustFrame)
                {
                    finishSound.play(0.5f);
                    isJustFrame = true;
                }
            } else {
                batch.draw(pauseTexture, DEFAULT_SCREEN_WIDTH / 2 - 300, DEFAULT_SCREEN_HEIGHT - 300, 600, 180);
            }
            batch.end();
        }
    }

    private void input()
    {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            if (currentState == GameState.PAUSED)
            {
                currentState = GameState.RUNNING;
                backgroundSound.play();
            } else if (currentState == GameState.RUNNING)
            {
                currentState = GameState.PAUSED;
                backgroundSound.pause();
            } else if (currentState == GameState.CLEAR)
            {
                Gdx.app.exit();
            }
        }

        if (currentState != GameState.RUNNING || gameWorld.getPlayer().actionState != GameCharacter.ActionState.Unoccupied) return;

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)){
            gameWorld.onPlayerRight();
            gameWorld.getRopeSystem().isFrontButtonPressed = true;
            gameWorld.getRopeSystem().isBackButtonPressed = false;
        } else if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) {
            gameWorld.onPlayerLeft();
            gameWorld.getRopeSystem().isFrontButtonPressed = false;
            gameWorld.getRopeSystem().isBackButtonPressed = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)){
            gameWorld.onPlayerJump(jumpSound);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) && gameWorld.enoughDashCoolDown() && !gameWorld.getRopeSystem().isRopeAttached){
            gameWorld.onPlayerDash(dashSound);
        }

        gameWorld.getRopeSystem().isLeftClicked = Gdx.input.isButtonPressed(Input.Buttons.LEFT);
    }


    @Override
    public void dispose() {
        batch.dispose();
        shapeRenderer.dispose();
        coinProgress.dispose();

        playerTexture.dispose();
        dashActiveTexture.dispose();
        dashInActiveTexture.dispose();
        pauseTexture.dispose();

        coinSound.dispose();
        hurtSound.dispose();
        jumpSound.dispose();
        dashSound.dispose();
        enemyHitSound.dispose();
    }

    @Override
    public void resize(int width, int height){
        viewport.update(width, height, true);
        uiViewport.update(width, height, true);
    }
}
