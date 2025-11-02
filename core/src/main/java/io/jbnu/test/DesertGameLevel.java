package io.jbnu.test;

import com.badlogic.gdx.graphics.Texture;

// 사막 클래스
public class DesertGameLevel extends DefaultGameLevel {

    private Texture sandTileTexture;
    private Texture stoneTexture;
    private Texture sandWallTexture;
    private Texture hookTileTexture;
    public DesertGameLevel()
    {
        super();
        maxCoinAmount = 5;
        sandTileTexture = new Texture("SandTile.png");
        stoneTexture = new Texture("Stone.png");
        sandWallTexture = new Texture("SandWall.png");
        hookTileTexture = new Texture("HookDesertTile.png");
        background = new Texture("DesertBackground.png");

        setupLevel();
    }
    @Override
    protected void setupLevel()
    {
        loadEdge(false, true, sandWallTexture,14, -50);
        loadTopBorder(true, true, sandWallTexture, 150, 0);

        loadGround(false, true, sandTileTexture, 20, 0); // enemy, coin
        loadEnemy(-400, 50, 200f);
        loadCoin(-200, 70);
        loadGroundNoLastX(false, true, stoneTexture, 3, -100, 50);
        loadGroundNoLastX(false, true, stoneTexture, 2, -50, 100);
        loadGroundNoLastX(false, true, stoneTexture, 1, 0, 150);
        loadTop(true, true, hookTileTexture, 3, 150);
        loadTop(true, true, hookTileTexture, 4, 200);
        loadTop(true, true, hookTileTexture, 4, 250);
        loadTop(true, true, hookTileTexture, 3, 300);

        loadCoin(400, 600);
        loadGround(false, true, sandTileTexture, 12, 850); // coin
        loadGroundNoLastX(false, true, stoneTexture, 1, -350, 50);
        loadGroundNoLastX(false, true, stoneTexture, 1, -150, 50);
        loadGroundNoLastX(false, true, sandTileTexture, 7, -400, 380); // coin
        loadEnemy(-320, 430, 200f);
        loadCoin(-120, 450);
        loadTop(true, true, hookTileTexture, 4, 200);
        loadTop(true, true, hookTileTexture, 5, 250);
        loadTop(true, true, hookTileTexture, 4, 300);
        loadTop(true, true, hookTileTexture, 4, 800);
        loadTop(true, true, hookTileTexture, 5, 850);
        loadTop(true, true, hookTileTexture, 4, 900);

        loadGround(false, true, sandTileTexture, 12, 1200); // enemy
        loadTop(false, true, sandWallTexture, 3, -500); // coin
        loadCoin(-420, 600);
        loadEnemy(-550, 50, 300f);
        loadGroundNoLastX(false, true, stoneTexture, 4, -150, 50);
        loadGroundNoLastX(false, true, stoneTexture, 3, -100, 100);
        loadGroundNoLastX(false, true, stoneTexture, 2, -50, 150);
        loadGroundNoLastX(false, true, sandTileTexture, 3, 50, 150);

        loadTop(true, true, hookTileTexture, 3, 350);
        loadTop(true, true, hookTileTexture, 4, 400);
        loadTop(true, true, hookTileTexture, 4, 450);
        loadTop(true, true, hookTileTexture, 3, 500);

        loadTop(true, true, hookTileTexture, 3, 1650);
        loadTop(true, true, hookTileTexture, 4, 1700);
        loadTop(true, true, hookTileTexture, 4, 1750);
        loadTop(true, true, hookTileTexture, 3, 1800);

        loadGround(false, true, sandTileTexture, 20, 2500); // coin
        loadCoin(-1100, 480);
        loadEdge(false, true, sandWallTexture,14, lastX);

        loadFlag(-200);
        deadZone.set(DEADZONE_START.x, DEADZONE_START.y, (lastX - DEADZONE_START.x), 200);
    }

    public void dispose()
    {
        super.dispose();
        sandTileTexture.dispose();
        stoneTexture.dispose();
        sandWallTexture.dispose();
        hookTileTexture.dispose();
        background.dispose();
    }
}
