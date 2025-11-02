package io.jbnu.test;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

// 코인 클래스
public class Coin {
    private Vector2 position;
    Animation<TextureRegion> rotateAnimation; // Coin 애니메이션
    private Rectangle bound;

    private float animationTime;

    private static int width = 30;
    private static int height = 30;

    public Coin(float x, float y, Animation<TextureRegion> rotateAnimation)
    {
        this.position = new Vector2(x, y);
        this.rotateAnimation = rotateAnimation;

        bound = new Rectangle();
        bound.set(x,y, width, height);

        animationTime = 0f;
    }

    public static int getWidth() {
        return width;
    }

    public static int getHeight() {
        return height;
    }

    public void render(SpriteBatch batch)
    {
        animationTime += Gdx.graphics.getDeltaTime();
        TextureRegion currentFrame = rotateAnimation.getKeyFrame(animationTime, true);
        batch.draw(currentFrame, position.x, position.y, width, height);
    }

    public Rectangle getBound()
    {
        return bound;
    }

    public Vector2 getPosition()
    {
        return position;
    }
}
