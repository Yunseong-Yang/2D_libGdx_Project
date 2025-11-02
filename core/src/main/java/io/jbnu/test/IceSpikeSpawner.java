package io.jbnu.test;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

// 고드름 스포너 클래스. 고드름을 제어하는 데 사용
public class IceSpikeSpawner {
    private final Vector2 position;
    private IceSpike iceSpike;
    private Texture iceSpikeTexture;
    public IceSpikeSpawner(float x, float y, Texture iceSpikeTexture)
    {
        position = new Vector2(x, y);
        iceSpike = new IceSpike(x, y, iceSpikeTexture);
        this.iceSpikeTexture = iceSpikeTexture;
    }

    // 고드름의 이동 처리
    public void handleIceSpike()
    {
        float delta = Gdx.graphics.getDeltaTime();

        iceSpike.velocity.y += GameWorld.WORLD_GRAVITY * delta * iceSpike.speedMultiplier;
        iceSpike.position.y += iceSpike.velocity.y * delta;
    }

    public void render(SpriteBatch batch)
    {
        iceSpike.syncIceSpikeSpritePosition();
        iceSpike.draw(batch);
    }

    // 고드름이 다른 개체와 충돌하면 다시 생성
    public void respawnIceSpike()
    {
        iceSpike = new IceSpike(position.x, position.y, iceSpikeTexture);
    }

    public IceSpike getIceSpike()
    {
        return iceSpike;
    }
}
