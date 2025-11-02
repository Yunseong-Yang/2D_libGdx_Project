package io.jbnu.test;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

// 적 클래스
public class Enemy {
    public Vector2 position;

    public Sprite enemySprite;

    public int facingDir = 1;

    private final float MOVE_AMOUNT = 5f; // 프레임 당 이동량
    private float limitMoveAmount; // 적의 직선 이동량 한계점

    private float currentMoveAmount; // 현재 직선 이동량

    public Enemy(int x, int y, float limitMoveAmount, Texture enemyTexture)
    {
        position = new Vector2(x, y);
        enemySprite = new Sprite(enemyTexture);

        enemySprite.setPosition(x, y);
        enemySprite.setSize(80, 150);
        enemySprite.flip(true, false);

        this.limitMoveAmount = limitMoveAmount;

        currentMoveAmount = 0;
    }

    public void draw(SpriteBatch batch) {
        enemySprite.draw(batch);
    }

    // 적을 이동시키기 위한 함수
    public void move()
    {
        currentMoveAmount += MOVE_AMOUNT;
        position.x += facingDir * MOVE_AMOUNT;

        if (currentMoveAmount >= limitMoveAmount)
        {
            facingDir *= -1;
            currentMoveAmount = 0;
            enemySprite.flip(true, false);
        }
        syncEnemySpritePosition();
    }

    public void syncEnemySpritePosition() { enemySprite.setPosition(position.x, position.y);}
}
