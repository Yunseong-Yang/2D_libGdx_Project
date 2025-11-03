package io.jbnu.test;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

// 로프 관련 로직을 처리하기 위한 클래스
public class RopeSystem implements Disposable {
    public boolean isRopeAttached = false; // 블록에 로프가 붙었는가?
    public boolean isLeftClicked = false; // 좌클릭 여부
    public boolean isFrontButtonPressed = false;
    public boolean isBackButtonPressed = false;
    private Vector2 ropeAnchor; // 블록에 로프가 닿은 지점
    private float ropeLength; // 현재 로프 길이
    private ShapeRenderer shapeRenderer; // 로프 시각화용
    private float ropeThickness = 3f; // 로프 두께
    public final float ROPE_LENGTH = 500f; // 로프 최대 길이
    private final float SWING_FORCE = 700f; // 로프 움직임 가속

    public RopeSystem() {
        shapeRenderer = new ShapeRenderer();
        ropeAnchor = new Vector2();
        ropeLength = 0;
    }

    public void update(OrthographicCamera mainCamera, GameCharacter player, Array<Block> blocks, float delta)
    {
        if (isLeftClicked)
        {
            if (!isRopeAttached) // 로프를 걸지 않았다면 수행
            {
                Vector2 mousePos = getMousePosition(mainCamera);

                Vector2 playerCenter = getPlayerCenter(player);
                float distance = playerCenter.dst(mousePos);

                if (distance <= ROPE_LENGTH) {
                    // 간단한 충돌 검사. 마우스 좌표가 블록 내부에 닿으면 고정
                    for (Block block : blocks) {
                        if (block.canAttachRope && block.getBound().contains(mousePos)) {
                            ropeAnchor.set(mousePos);
                            ropeLength = playerCenter.dst(ropeAnchor);
                            isRopeAttached = true;
                            break;
                        }
                    }
                }
            }
            else // 로프를 걸었다면, 그에 따른 물리 연산 수행
            {
                handleRopePhysics(player, delta);
            }
        }
        else // 좌클릭을 떼면 로프를 해체
        {
            isRopeAttached = false;
        }
    }

    private void handleRopePhysics(GameCharacter player, float delta)
    {
        // 중심 계산(1)
        Vector2 center = getPlayerCenter(player);
        Vector2 anchorToPlayer = center.cpy().sub(ropeAnchor);
        float len = anchorToPlayer.len();
        if (len == 0f) return;

        // 방사 벡터 및 법선 벡터 계산(2)
        Vector2 ropeDir = anchorToPlayer.cpy().nor();
        Vector2 tangentDir = new Vector2(-ropeDir.y, ropeDir.x);

        // 중력 적용 및 좌/우 입력 가속(3)
        player.velocity.add(0, GameWorld.WORLD_GRAVITY * delta);

        if (isBackButtonPressed)
            player.velocity.add(tangentDir.cpy().scl(-SWING_FORCE * delta));
        if (isFrontButtonPressed)
            player.velocity.add(tangentDir.cpy().scl(SWING_FORCE * delta));

        // 위치 이동(4)
        player.position.add(player.velocity.x * delta, player.velocity.y * delta);

        // 거리 보정(5)
        Vector2 newCenter = getPlayerCenter(player);
        Vector2 newAnchorToPlayer = newCenter.cpy().sub(ropeAnchor);
        float currentLen = newAnchorToPlayer.len();

        if (currentLen > ropeLength) {
            // 초과한 거리만큼만 밀어내기(6)
            float correction = currentLen - ropeLength;
            newAnchorToPlayer.nor();
            player.position.sub(newAnchorToPlayer.scl(correction));

            // 방사속도 제거(7)
            float radialVel = player.velocity.dot(ropeDir);
            player.velocity.sub(ropeDir.scl(radialVel));

            // 멈춤 방지: 최소 속도 유지(8)
            if (player.velocity.len2() < 10f) {
                // 방향 유지한 채 최소 관성 부여
                player.velocity.add(tangentDir.scl(50f * delta));
            }
        }

        // 감쇠(9)
        player.velocity.scl(0.998f);
    }

    // 로프를 시각적으로 렌더링 하는 과정
    public void ropeRender(OrthographicCamera mainCamera, GameCharacter player)
    {
        if (!isRopeAttached || !isLeftClicked) return;

        shapeRenderer.setAutoShapeType(true);
        shapeRenderer.setProjectionMatrix(mainCamera.combined);

        Vector2 playerCenter = getPlayerCenter(player);
        Vector2 ropeEnd = isRopeAttached ? ropeAnchor : getMousePosition(mainCamera);

        shapeRenderer.begin();
        shapeRenderer.set(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.BROWN);
        shapeRenderer.rectLine(playerCenter, ropeEnd, ropeThickness);
        shapeRenderer.end();
    }

    private Vector2 getPlayerCenter(GameCharacter player) {
        return new Vector2(
            player.playerSprite.getX() + player.playerSprite.getWidth() / 2f,
            player.playerSprite.getY() + player.playerSprite.getHeight() / 2f
        );
    }

    private Vector2 getMousePosition(OrthographicCamera mainCamera) {
        Vector3 mouse3D = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        mainCamera.unproject(mouse3D);
        return new Vector2(mouse3D.x, mouse3D.y);
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
    }
}
