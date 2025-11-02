package io.jbnu.test;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

// 블록 클래스
public class Block {
    private Vector2 position;
    private Texture texture;
    private Rectangle bound;

    private boolean textureFlag; // 텍스처를 투명하게 할 것인지?
    public boolean canAttachRope; // 해당 블록에 로프를 걸 수 있는지?

    private static int width = 50;
    private static int height = 50;

    public Block(boolean canAttachRope, boolean textureFlag, float x, float y, Texture texture) {
        this.canAttachRope = canAttachRope;
        this.textureFlag = textureFlag;
        this.position = new Vector2(x, y);
        this.texture = texture;

        bound = new Rectangle();
        bound.set(x,y, width, height);
    }

    public static int getWidth() {
        return width;
    }

    public static int getHeight() {
        return height;
    }

    public void render(SpriteBatch batch) {
        if (textureFlag)
        {
            batch.draw(texture, position.x, position.y, width, height);
        }
    }

    public Rectangle getBound(){
        return bound;
    }

    public Vector2 getPosition()
    {
        return position;
    }
}
