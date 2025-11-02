package io.jbnu.test;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

// 고드름 클래스
public class IceSpike {
    public float speedMultiplier = 0.8f;
    private float boundOffset = 10f;
    private float boundHeight = 30f;

    public Vector2 position;
    public Vector2 velocity;
    private Sprite iceSpikeSprite;

    // 스프라이트의 전체 충돌이 아닌 고드름의 하단부만 충돌을 설정하기 위해 따로 충돌 박스 구현
    private Rectangle bound;

    public IceSpike(float x, float y, Texture iceTexture)
    {
        iceSpikeSprite = new Sprite(iceTexture);
        iceSpikeSprite.setSize(30, 70);

        velocity = new Vector2(0, 0);
        position = new Vector2(x, y - iceSpikeSprite.getHeight());

        iceSpikeSprite.setPosition(position.x, position.y);

        bound = new Rectangle();
        bound.set(position.x, position.y - boundOffset, iceSpikeSprite.getWidth(), boundHeight);
    }

    public void draw(SpriteBatch batch) {
        iceSpikeSprite.draw(batch);
    }

    public Rectangle getBound()
    {
        return bound;
    }

    public void syncIceSpikeSpritePosition()
    {
        bound.set(position.x, position.y, iceSpikeSprite.getWidth(), 30f);
        iceSpikeSprite.setPosition(position.x, position.y);
    }
}
