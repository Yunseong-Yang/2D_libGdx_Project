package io.jbnu.test;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

// 게임 캐릭터 클래스
public class GameCharacter {
    public enum ActionState{
        Unoccupied, // 아래 두 액션 이외의 행동
        Dash, // 대시 중일 때
        STUNNED // 기절 상태일 때
    }
    public final float MOVE_SPEED = 400f; // 초 당 이동량
    public Vector2 position;
    public Vector2 velocity;
    public Sprite playerSprite;

    public boolean isGrounded = false; // 지면에 닿아있는가?
    public ActionState actionState;
    public final float DASH_COOLDOWN = 3f; // 대시 사용 쿨타임
    public final float DASH_DURATION = 0.2f; // 초기 대시 지속 시간

    public float dashTime = 0f; // 남은 대시 지속 시간

    public boolean isMovingRight = false;
    public boolean isMovingLeft = false;
    public int facingDir = 1; // 캐릭터의 방향 -1: 좌, 1: 우

    public GameCharacter(Texture playerTexture, float x, float y)
    {
        position = new Vector2(x, y);
        velocity = new Vector2(0, 0);
        playerSprite = new Sprite(playerTexture);

        playerSprite.setPosition(position.x, position.y);
        playerSprite.setSize(150, 100);
        playerSprite.flip(true, false);

        actionState = ActionState.Unoccupied;
    }

    public void moveRight()
    {
        if (facingDir == -1)
            playerSprite.flip(true, false);

        facingDir = 1;
        isMovingRight = true;
        isMovingLeft = false;
    }

    public void moveLeft()
    {
        if (facingDir == 1)
            playerSprite.flip(true, false);

        facingDir = -1;
        isMovingRight = false;
        isMovingLeft = true;
    }

    public void jump(Sound jumpSound) {
        if (isGrounded) {
            velocity.y = 800f; // Y축으로 점프 속도 설정
            isGrounded = false; // 점프했으니 땅에서 떨어짐

            jumpSound.play(0.4f);
        }
    }

    public void startDash(Sound dashSound)
    {
        dashSound.play(0.5f);
        actionState = ActionState.Dash; // Dash State로 전환
        velocity.x += 1000f * facingDir;
        dashTime = DASH_DURATION;
    }

    public void endDash()
    {
        velocity.x = 0;
        actionState = ActionState.Unoccupied; // 대시가 끝났다면 다시 기본으로 돌림
    }

    public void syncPlayerSpritePosition() { playerSprite.setPosition(position.x, position.y);}
    public void draw(SpriteBatch batch) {
        playerSprite.draw(batch);
    }
}
