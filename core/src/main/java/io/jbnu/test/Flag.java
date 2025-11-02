package io.jbnu.test;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

// 깃발 클래스
public class Flag {
    private Vector2 position;
    private Texture texture;
    private Rectangle bound;

    private static int width = 70;
    private static int height = 92;

    public Flag(float x, float y, Texture texture) {
        this.position = new Vector2(x, y);
        this.texture = texture;

        bound = new Rectangle();
        bound.set(x,y, texture.getWidth(), texture.getHeight());
    }

    public static int getWidth() {
        return width;
    }

    public static int getHeight() {
        return height;
    }

    public void render(SpriteBatch batch)
    {
        batch.draw(texture, position.x, position.y, width, height);
    }

    public Rectangle getBound(){
        return bound;
    }
}
