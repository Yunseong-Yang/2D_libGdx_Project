package io.jbnu.test;

import com.badlogic.gdx.graphics.Texture;

// Forest 게임 레벨 클래스
public class ForestGameLevel extends DefaultGameLevel{
    private Texture grassTileTexture;
    private Texture grassTexture;
    private Texture dirtWallTexture;
    private Texture hookTileTexture;

    public ForestGameLevel()
    {
        super();
        maxCoinAmount = 3;
        grassTileTexture = new Texture("GrassTile.png");
        grassTexture = new Texture("Grass.png");
        dirtWallTexture = new Texture("DirtWall.png");
        hookTileTexture = new Texture("HookForestTile.png");
        background = new Texture("ForestBackground.png");

        setupLevel();
    }

    protected void setupLevel()
    {
        loadEdge(false, true, dirtWallTexture,14, -50);
        loadTopBorder(true, true, dirtWallTexture, 100, 0);

        loadGround(false, true, grassTileTexture, 10, 0);
        loadGroundNoLastX(false, true, grassTexture, 1, -250, 50);
        loadGround(false, true, grassTileTexture,10, 300);
        loadGroundNoLastX(false, true, grassTexture, 1, -250, 50);
        loadGroundNoLastX(false, true, grassTexture, 1, -100, 50);
        loadGroundNoLastX(false, true, grassTexture, 1, -100, 100);

        loadGroundNoLastX(false, true, grassTileTexture, 5, -500, 400); // coin
        loadCoin(-390, 470);
        loadGroundNoLastX(false, true, grassTileTexture, 5, 500, 400);
        loadGroundNoLastX(false, true, grassTileTexture, 5, 1000, 500); // coin
        loadCoin(1120, 570);

        loadTop(true, true, hookTileTexture,4, 100);
        loadTop(true, true, hookTileTexture, 5, 50);
        loadTop(true, true, hookTileTexture, 4, 0);

        loadGround(false, true, grassTileTexture,10, 700);
        loadTop(true, true, hookTileTexture,4, 400);
        loadTop(true, true, hookTileTexture, 5, 350);
        loadTop(true, true, hookTileTexture, 4, 300);

        loadGroundNoLastX(false, true, grassTexture, 1, 400, 50);
        loadGroundNoLastX(false, true, grassTexture, 1, 400, 100);
        loadGroundNoLastX(false, true, grassTexture, 2, 800, 50);
        loadGround(false, true, grassTileTexture,16, 300);

        loadGroundNoLastX(false, true, grassTileTexture, 5, -300, 250);
        loadGroundNoLastX(false, true, grassTileTexture, 3, 400, 340); // coin
        loadCoin(460, 410);
        loadTop(false, true, dirtWallTexture, 4, 400);

        loadGround(false, true, grassTileTexture,17, 800);
        loadEdge(false, true, dirtWallTexture,14, lastX);

        loadFlag(-200);
        deadZone.set(DEADZONE_START.x, DEADZONE_START.y, (lastX - DEADZONE_START.x), 200);
    }

    @Override
    public void dispose()
    {
        super.dispose();
        grassTileTexture.dispose();
        grassTexture.dispose();
        dirtWallTexture.dispose();
        hookTileTexture.dispose();
        background.dispose();
    }
}
