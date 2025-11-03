package io.jbnu.test;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

// 기본 레벨 세팅을 위한 클래스
public abstract class DefaultGameLevel implements Disposable {
    protected final int FRAME_COL = 12;
    public int maxCoinAmount;
    protected final Vector2 DEADZONE_START = new Vector2(-50, -500); // 데드존 시작 지점

    /* 각 요소 리스트 */
    protected Array<Block> blocks;
    protected Array<Coin> coins;
    protected Array<Enemy> enemies;
    protected Array<IceSpikeSpawner> iceSpikeSpawners;

    /* 텍스처 */
    protected Texture coinSheet;
    protected Texture flagTexture;
    protected Texture enemyTexture;
    protected Texture iceSpikeTexture;
    protected TextureRegion[][] tmp;
    protected TextureRegion[] coinFrames;
    protected Texture background;

    protected Flag flag;
    protected Rectangle deadZone;
    // 레벨을 구현하기 위한 Last Point
    protected int lastX = 0;

    public DefaultGameLevel() {
        coinSheet = new Texture("CoinSheet.png");
        flagTexture = new Texture("Flag.png");
        enemyTexture = new Texture("Sahur.png");
        iceSpikeTexture = new Texture("IceSpike.png");

        tmp = TextureRegion.split(coinSheet, 16, 16);
        coinFrames = new TextureRegion[FRAME_COL];
        for (int i = 0; i < FRAME_COL; i++)
        {
            coinFrames[i] = tmp[0][i];
        }

        blocks = new Array<>();
        coins = new Array<>();
        enemies = new Array<>();
        iceSpikeSpawners = new Array<>();

        deadZone = new Rectangle();
    }

    protected abstract void setupLevel();

    public void render(OrthographicCamera mainCamera, SpriteBatch batch) {
        float camX = mainCamera.position.x;
        // 배경을 3개 이어붙이고, 메인 카메라의 위치에 따라서 1:1로 이동할 수 있도록 구현
        batch.draw(background, camX - Main.DEFAULT_SCREEN_WIDTH, 0, Main.DEFAULT_SCREEN_WIDTH, Main.DEFAULT_SCREEN_HEIGHT);
        batch.draw(background, camX, 0, Main.DEFAULT_SCREEN_WIDTH, Main.DEFAULT_SCREEN_HEIGHT);
        batch.draw(background, camX + Main.DEFAULT_SCREEN_WIDTH, 0, Main.DEFAULT_SCREEN_WIDTH, Main.DEFAULT_SCREEN_HEIGHT);

        /* 각 요소 리스트들의 렌더링 처리 */
        for (Block block : blocks)
            block.render(batch);

        for (Coin coin : coins)
            coin.render(batch);

        for (Enemy enemy : enemies)
        {
            enemy.draw(batch);
        }

        for (IceSpikeSpawner iceSpikeSpawner : iceSpikeSpawners)
        {
            iceSpikeSpawner.render(batch);
            /* 블록 및 DeadZone 과 충돌하면 다시 재생 */
            for (Block block : blocks)
            {
                if (iceSpikeSpawner.getIceSpike().getBound().overlaps(block.getBound()) || iceSpikeSpawner.getIceSpike().getBound().overlaps(deadZone))
                {
                    iceSpikeSpawner.respawnIceSpike();
                    break;
                }
            }
        }

        if (flag != null)
            flag.render(batch);
    }
    /* 각 요소를 리스트에 추가 */
    protected void loadIceSpikeSpawner(int paddingX, int paddingY)
    {
        iceSpikeSpawners.add(new IceSpikeSpawner(lastX + paddingX, 720 - paddingY, iceSpikeTexture));
    }
    protected void loadEnemy(int paddingX, int paddingY, float limitMoveAmount)
    {
        enemies.add(new Enemy(lastX + paddingX, paddingY, limitMoveAmount, enemyTexture));
    }
    protected void loadCoin(int paddingX, int paddingY) {
        coins.add(new Coin(lastX + paddingX, paddingY, new Animation<>(0.1f, coinFrames)));
    }
    // 블록의 Edge 부분 구현
    protected void loadEdge(boolean canAttachRope, boolean textureFlag, Texture texture, int numberOfEdge, int padding) {
        float edgeX = padding;
        for (int i = 0; i < numberOfEdge; i++) {
            float edgeY = i * Block.getHeight();
            blocks.add(new Block(canAttachRope, textureFlag, edgeX, edgeY, texture));
        }
    }
    // 블록의 Top 부분 구현
    protected void loadTop(boolean canAttachRope, boolean textureFlag, Texture texture, int numberOfBlocks, int padding) {
        float startX = lastX + padding;
        for (int i = 2; i <= numberOfBlocks; i++) {
            float startY = 720 - (i * Block.getHeight());
            blocks.add(new Block(canAttachRope, textureFlag, startX, startY, texture));
        }
    }
    // 블록의 윗 경계 세팅
    protected void loadTopBorder(boolean canAttachRope, boolean textureFlag, Texture texture, int numberOfBlocks, int padding) {
        float startX = padding;
        for (int i = 0; i <= numberOfBlocks; i++) {
            startX = i * Block.getWidth();
            float startY = 720 - Block.getHeight();
            blocks.add(new Block(canAttachRope, textureFlag, startX, startY, texture));
        }
    }
    // Ground 블록 세팅
    protected void loadGround(boolean canAttachRope, boolean textureFlag, Texture texture, int numberOfBlocks, int padding) {
        float startX = lastX + padding;
        float startY = 0;
        for (int i = 0; i < numberOfBlocks; i++) {
            float x = startX + (i * Block.getWidth());
            lastX = (int) x;
            blocks.add(new Block(canAttachRope, textureFlag, x, startY, texture));
        }
    }

    // Ground 블록 세팅은 동일 하나, lastX를 업데이트 하지 않음
    protected void loadGroundNoLastX(boolean canAttachRope, boolean textureFlag, Texture texture, int numberOfBlocks, int paddingX, int paddingY) {
        float startX = lastX + paddingX;
        for (int i = 0; i < numberOfBlocks; i++) {
            float x = startX + (i * Block.getWidth());
            blocks.add(new Block(canAttachRope, textureFlag, x, paddingY, texture));
        }
    }

    protected void loadFlag(int padding) {
        flag = new Flag(lastX + padding, 50, flagTexture);
    }

    public Array<Block> getBlocks() { return blocks; }
    public Array<Coin> getCoins() { return coins; }
    public Array<Enemy> getEnemies() { return enemies; }
    public Array<IceSpikeSpawner> getIceSpikeSpawners() { return iceSpikeSpawners; }
    public TextureRegion getCoinTexture() { return coinFrames[0]; }
    public Flag getFlag() { return flag; }
    public Rectangle getDeadZone() { return deadZone; }

    @Override
    public void dispose() {
        background.dispose();
        coinSheet.dispose();
        flagTexture.dispose();
        enemyTexture.dispose();
        iceSpikeTexture.dispose();
    }
}
