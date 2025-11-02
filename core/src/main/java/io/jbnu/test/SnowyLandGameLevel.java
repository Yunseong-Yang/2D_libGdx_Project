package io.jbnu.test;

import com.badlogic.gdx.graphics.Texture;

// 눈 지형 레벨
public class SnowyLandGameLevel extends DefaultGameLevel {

    private Texture snowTileTexture;
    private Texture snowTexture;
    private Texture snowWallTexture;
    private Texture hookTileTexture;
    public SnowyLandGameLevel()
    {
        super();
        maxCoinAmount = 7;
        snowTileTexture = new Texture("SnowTile.png");
        snowTexture = new Texture("Snow.png");
        snowWallTexture = new Texture("SnowWall.png");
        hookTileTexture = new Texture("HookSnowTile.png");
        background = new Texture("SnowyBackground.png");

        setupLevel();
    }
    @Override
    protected void setupLevel() {
        loadEdge(false, true, snowWallTexture,14, -50);
        loadTopBorder(true, true, snowWallTexture, 168, 0);

        loadGround(false, true, snowTileTexture, 20, 0);
        loadGroundNoLastX(false, true, snowTexture, 3, -350, 670);
        loadIceSpikeSpawner(-340, 50);
        loadIceSpikeSpawner(-290, 50);
        loadIceSpikeSpawner(-240, 50);
        loadCoin(10, 50);

        loadTop(true, true, hookTileTexture, 3, 150);
        loadTop(true, true, hookTileTexture, 3, 200);
        loadTop(true, true, hookTileTexture, 3, 250); // coin
        loadCoin(400, 400);

        loadTop(true, true, hookTileTexture, 3, 650);
        loadTop(true, true, hookTileTexture, 3, 700);
        loadTop(true, true, hookTileTexture, 3, 750); // coin
        loadCoin(900, 600);

        loadTop(true, true, hookTileTexture, 3, 1150);
        loadTop(true, true, hookTileTexture, 3, 1200);
        loadTop(true, true, hookTileTexture, 3, 1250);

        loadGround(false, true, snowTileTexture, 15, 1700); // enemy
        loadEnemy(-700, 50, 400);
        loadGroundNoLastX(false, true, snowTexture, 1, -700, 670);
        loadIceSpikeSpawner(-690, 50);
        loadGroundNoLastX(false, true, snowTileTexture, 2, 50, 100);
        loadGroundNoLastX(false, true, snowTileTexture, 2, 150, 200);

        loadTop(true, true, hookTileTexture, 3, 400);
        loadTop(true, true, hookTileTexture, 4, 450);
        loadTop(true, true, hookTileTexture, 4, 500);
        loadTop(true, true, hookTileTexture, 3, 550); // coin
        loadGroundNoLastX(false, true, snowTexture, 1, 800, 670);
        loadIceSpikeSpawner(810, 50);
        loadCoin(1000, 400);
        loadTop(true, true, hookTileTexture, 3, 1050);
        loadTop(true, true, hookTileTexture, 4, 1100);
        loadTop(true, true, hookTileTexture, 4, 1150);
        loadTop(true, true, hookTileTexture, 3, 1200); // coin
        loadGroundNoLastX(false, true, snowTexture, 1, 1450, 670);
        loadIceSpikeSpawner(1460, 50);
        loadCoin(1650, 400);

        loadGround(false, true, snowTileTexture, 10, 1800);
        loadGroundNoLastX(false, true, snowTileTexture, 4, 50, 50);
        loadGroundNoLastX(false, true, snowTileTexture, 4, 250, 100);
        loadGroundNoLastX(false, true, snowTexture, 1, 100, 670);
        loadIceSpikeSpawner(110, 50);
        loadTop(true, true, hookTileTexture, 3, 850); // coin
        loadCoin(300, 500);
        loadTop(true, true, hookTileTexture, 4, 900);
        loadTop(true, true, hookTileTexture, 3, 950);


        loadGround(false, true, snowTileTexture, 20, 1900);
        loadEnemy(-1000, 50, 400);
        loadEnemy(-800, 50, 500);
        loadCoin(-700, 60);
        loadEdge(false, true, snowWallTexture,14, lastX);

        loadFlag(-200);
        deadZone.set(DEADZONE_START.x, DEADZONE_START.y, (lastX - DEADZONE_START.x), 200);
    }

    @Override
    public void dispose()
    {
        super.dispose();
        snowTileTexture.dispose();
        snowTexture.dispose();
        snowWallTexture.dispose();
        hookTileTexture.dispose();
        background.dispose();
    }
}
