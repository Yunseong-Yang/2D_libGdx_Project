package io.jbnu.test;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;
import java.util.List;

// 게임 월드 클래스
public class GameWorld {
    public static final float WORLD_GRAVITY = -9.8f * 200; // 현재 월드의 중력 값

    private List<DefaultGameLevel> levelList; // 레벨 리스트
    private int currentLevelIndex; // 현재 레벨을 추적하기 위한 인덱스
    private DefaultGameLevel currentLevel; // 현재 레벨

    private GameCharacter player;
    private RopeSystem ropeSystem;

    private Sound hurtSound;
    private Sound coinSound;
    private Sound enemyHitSound;
    private Sound iceSpikeHitSound;
    private Sound deadZoneSound;
    private Sound nextLevelSound;

    private float dashTimer; // 마지막 대시 이후 흘러간 시간
    private float stunTimer; // 스턴 상태 진입 후 흘러간 시간

    private final float BOUNCE_SPEED = 1000f;
    private final float BOUNCE_LIFT = 500f;

    private int coinCnt; // 현재 레벨의 코인 획득 수

    public boolean isAllLevelClear = false; // 모든 레벨 클리어 플래그

    public GameWorld(Texture playerTexture, Sound hurtSound, Sound coinSound, Sound iceSpikeHitSound, Sound enemyHitSound, Sound deadZoneSound, Sound nextLevelSound)
    {
        // 리스트에 레벨을 추가하여 관리
        levelList = new ArrayList<>();

        levelList.add(new ForestGameLevel());
        levelList.add(new DesertGameLevel());
        levelList.add(new SnowyLandGameLevel());

        currentLevelIndex = 0;
        currentLevel = levelList.get(currentLevelIndex);

        player = new GameCharacter(playerTexture, 0, 500);
        ropeSystem = new RopeSystem();

        this.hurtSound = hurtSound;
        this.coinSound = coinSound;
        this.enemyHitSound = enemyHitSound;
        this.iceSpikeHitSound = iceSpikeHitSound;
        this.deadZoneSound = deadZoneSound;
        this.nextLevelSound = nextLevelSound;

        dashTimer = 3f;
        coinCnt = 0;
    }

    public void update(float delta, OrthographicCamera mainCamera)
    {
        dashTimer += delta;

        handleVerticalMovement(delta);
        handleHorizontalMovement(delta);

        for (IceSpikeSpawner iceSpikeSpawner : currentLevel.getIceSpikeSpawners()) {
            iceSpikeSpawner.handleIceSpike();
        }

        for (Enemy enemy : currentLevel.getEnemies())
        {
            enemy.move();
        }

        if (player.actionState == GameCharacter.ActionState.Dash)
        {
            dashAction(delta, hurtSound);
        }
        if (player.actionState == GameCharacter.ActionState.STUNNED)
        {
            stunAction(delta);
        }

        ropeSystem.update(mainCamera, player, currentLevel.getBlocks(), Gdx.graphics.getDeltaTime());

        if (ropeSystem.isRopeAttached)
        {
            checkCollisionY(player.position.y);
            checkCollisionX(player.position.x);

            player.syncPlayerSpritePosition();
        }
    }

    // 스턴 상태 진입 시 수행
    private void stunAction(float delta) {
        stunTimer += delta;
        if (stunTimer < 0.2f) // 0.2초간 반작용 물리 수행
        {
            float expectedX = player.position.x + player.velocity.x * delta;
            boolean collision = checkCollisionX(expectedX);
            if (!collision)
            {
                player.position.x = expectedX;
            }
            player.syncPlayerSpritePosition();
        }
        else if (stunTimer > 2f) // 2초 후 스턴 종료
        {
            stunTimer = 0;
            player.endDash();
        }
        else // 스턴 상태에서는 움직일 수 없음
        {
            player.velocity.x = 0;
        }
    }

    // 대시 상태 진입 시 수행
    private void dashAction(float delta, Sound hurtSound) {
        if (!enoughDashCoolDown()) return;

        float expectedX = player.position.x + delta * player.velocity.x; // 예상 위치 계산
        boolean collision = checkCollisionX(expectedX); // X축 충돌 검사
        player.dashTime -= delta; // 남은 시간 감소

        if (collision) { // 충돌이 있다면 속도 값을 세팅하고 스턴 상태 진입
            float dashDir = player.facingDir;

            player.velocity.x = -dashDir * BOUNCE_SPEED;
            player.velocity.y = BOUNCE_LIFT;

            player.isGrounded = false;
            dashTimer = 0;

            player.actionState = GameCharacter.ActionState.STUNNED;
            hurtSound.play(0.5f);
            return;
        }

        if (player.dashTime < 0) // 충돌 없이 대시를 마쳤다면 대시로 인한 속도 값 초기화
        {
            player.endDash();
            dashTimer = 0;
        }

        // 대시 이동 값 동기화
        player.position.x = expectedX;
        player.syncPlayerSpritePosition();
    }

    // 대시 쿨타임이 충분한지?
    public boolean enoughDashCoolDown() {
        return dashTimer >= player.DASH_COOLDOWN;
    }

    // 수직 이동 처리
    private void handleVerticalMovement(float delta) {
        if (ropeSystem.isRopeAttached) // 로프 상태에 진입했다면 해당 물리 연산을 사용하지 않음
            return;

        // 플레이어의 중력 설정
        player.velocity.y += WORLD_GRAVITY * delta;
        float expectedY = player.position.y + player.velocity.y * delta;

        boolean collision = checkCollisionY(expectedY);

        if (!collision)
        {
            player.position.y = expectedY;
            player.isGrounded = false;
        }

        player.syncPlayerSpritePosition();
    }

    // Y축에 의한 충돌을 검사
    private boolean checkCollisionY(float expectedY) {
        boolean collision = false;

        // 플레이어 충돌 박스 설정
        Rectangle playerRectangle = new Rectangle(
            player.position.x,
            expectedY,
            player.playerSprite.getWidth(),
            player.playerSprite.getHeight()
        );

        if (deadZoneCollisionCheck(playerRectangle)) return true;
        if (iceSpikeCollisionCheck(playerRectangle)) return true;

        // 모든 블록에 대해 플레이어 충돌을 검사
        for (Block block : currentLevel.getBlocks())
        {
            if (playerRectangle.overlaps(block.getBound()))
            {
                collision = true;
                if (player.position.y > block.getPosition().y) // 플레이어가 블록보다 위에 있으면 블록 위에 플레이어 배치(바닥과의 충돌)
                {
                    player.position.y = block.getPosition().y + Block.getHeight();
                    player.isGrounded = true;
                }
                else // 플레이어가 블록보다 아래에 있으면 블록 아래에 플레이어 배치 (천장과의 충돌)
                {
                    player.position.y = block.getPosition().y - player.playerSprite.getHeight();
                }

                player.velocity.y = 0;
                break;
            }
        }
        return collision;
    }

    // 고드름과 플레이어의 충돌을 체크
    private boolean iceSpikeCollisionCheck(Rectangle playerRectangle) {
        for (IceSpikeSpawner iceSpikeSpawner: currentLevel.getIceSpikeSpawners())
        {
            if (playerRectangle.overlaps(iceSpikeSpawner.getIceSpike().getBound()))
            {
                ResetPlayerState();
                iceSpikeHitSound.play(0.7f);
                ropeSystem.isRopeAttached = false;
                return true;
            }
        }
        return false;
    }

    // 데드존과 플레이어의 충돌을 체크
    private boolean deadZoneCollisionCheck(Rectangle playerRectangle) {
        if (playerRectangle.overlaps(currentLevel.getDeadZone()))
        {
            ResetPlayerState();
            deadZoneSound.play(0.7f);
            return true;
        }
        return false;
    }

    // 플레이어의 위치를 처음으로 세팅
    private void ResetPlayerState() {
        player.actionState = GameCharacter.ActionState.Unoccupied;

        player.position.set(50f, 200f);
        player.velocity.set(0, 0);

        player.syncPlayerSpritePosition();
    }

    // 코인과의 충돌 체크
    private void coinCollisionCheck(Rectangle playerRectangle)
    {
        for (int i = 0; i < currentLevel.getCoins().size; i++)
        {
            Coin coin = currentLevel.getCoins().get(i);
            if (playerRectangle.overlaps(coin.getBound())) // 코인을 먹으면 해당 코인을 삭제
            {
                coinCnt++;
                coinSound.play(0.7f);
                currentLevel.getCoins().removeIndex(i);
                return;
            }
        }
    }

    // 플레이어의 수평 이동 처리
    private void handleHorizontalMovement(float delta) {
        // 대시, 스턴, 로프 상태라면 해당 물리 연산을 사용하지 않음
        if (player.actionState == GameCharacter.ActionState.Dash ||
            player.actionState == GameCharacter.ActionState.STUNNED ||
            ropeSystem.isRopeAttached)
            return;

        // 아무런 이동 input을 감지하지 않았다면 해당 물리 연산을 사용하지 않음
        if (!(player.isMovingLeft || player.isMovingRight)
            && player.actionState == GameCharacter.ActionState.Unoccupied)
        {
            player.velocity.x = 0f;
            return;
        }

        if (player.isMovingRight)
        {
            player.velocity.x = player.MOVE_SPEED;
        }
        else if (player.isMovingLeft)
        {
            player.velocity.x = -player.MOVE_SPEED;
        }

        float expectedX = player.position.x + player.velocity.x * delta;

        boolean collision = checkCollisionX(expectedX);

        if (!collision)
        {
            player.position.x = expectedX;
        }

        player.syncPlayerSpritePosition();

        player.isMovingLeft = false;
        player.isMovingRight = false;
    }

    private boolean checkCollisionX(float expectedX) {
        Rectangle playerBounds = new Rectangle(
            expectedX,
            player.position.y,
            player.playerSprite.getWidth(),
            player.playerSprite.getHeight()
        );

        boolean collision = false;

        // 로프 상태 및 대시 충돌로 인한 반작용으로 인해서 정면을 보고 있지만 뒤에서 충돌이 발생하는 경우도 존재. 이를 감지하기 위한 변수
        boolean isBackCollision;

        if (player.velocity.x > 0 && player.facingDir == 1)
        {
            isBackCollision = false;
        } else if (player.velocity.x < 0 && player.facingDir == 1) // 가속을 뒤로 받는데, 앞을 보고있다면 뒤에서 충돌
        {
            isBackCollision = true;
        } else if (player.velocity.x > 0 && player.facingDir == -1) // 가속을 앞으로 받는데, 뒤를 보고있다면 뒤에서 충돌
        {
            isBackCollision = true;
        } else {
            isBackCollision = false;
        }

        coinCollisionCheck(playerBounds);
        boolean clearFlag = flagCollisionCheck(playerBounds);
        if (clearFlag) return true;

        if (enemyCollisionCheck(playerBounds)) return true;

        for (Block block : currentLevel.getBlocks()) {
            if (playerBounds.overlaps(block.getBound())) {
                collision = true;

                // 과정을 간소화 하기 위한 임시 변수
                float blockLeft = block.getBound().x;
                float blockRight = block.getBound().x + block.getBound().width;
                float playerWidth = player.playerSprite.getWidth();

                float newX;

                if (isBackCollision) {
                    newX = (player.facingDir == 1)
                        ? blockRight                // 오른쪽 바라보지만 뒤쪽 충돌
                        : blockLeft - playerWidth;  // 왼쪽 바라보지만 뒤쪽 충돌
                }
                else
                {
                    newX = (player.facingDir == 1)
                        ? blockLeft - playerWidth
                        : blockRight;
                }
                // 최종 위치 적용 후 반환
                player.position.x = newX;
                break;
            }
        }
        return collision;
    }

    // 적과의 충돌 체크
    private boolean enemyCollisionCheck(Rectangle playerBounds) {
        for (Enemy enemy : currentLevel.getEnemies())
        {
            if (playerBounds.overlaps(enemy.enemySprite.getBoundingRectangle()))
            {
                if (player.actionState == GameCharacter.ActionState.Dash) // 대시 상태에서 충돌한거라면 적을 처치
                {
                    int index = currentLevel.getEnemies().indexOf(enemy, false);
                    currentLevel.getEnemies().removeIndex(index);
                }
                else // 그 외라면 적의 공격을 받아 처음 위치로 돌아감
                {
                    enemyHitSound.play(0.7f);
                    ResetPlayerState();
                    ropeSystem.isRopeAttached = false;
                }
                return true;
            }
        }
        return false;
    }

    // 깃발과의 충돌 체크
    private boolean flagCollisionCheck(Rectangle playerBounds) {
        if (currentLevel.getFlag() != null && playerBounds.overlaps(currentLevel.getFlag().getBound()) && coinCnt == currentLevel.maxCoinAmount)
        {
            // 모든 스테이지를 클리어 했다면 다음 레벨로 업데이트 하지 않음
            if (currentLevelIndex + 1 >= levelList.size()) {
                isAllLevelClear = true;
                return false;
            }
            ResetPlayerState(); // 플레이어 위치 초기화

            currentLevel.dispose();
            currentLevelIndex++;
            currentLevel = levelList.get(currentLevelIndex);
            nextLevelSound.play(0.5f);

            coinCnt = 0;
            return true;
        }
        return false;
    }

    public void drawLevel(OrthographicCamera mainCamera, SpriteBatch batch)
    {
        currentLevel.render(mainCamera, batch);
    }

    public GameCharacter getPlayer() { return player; }

    public RopeSystem getRopeSystem() { return ropeSystem; }

    public void onPlayerRight() {
        if (!ropeSystem.isRopeAttached)
        {
            player.moveRight();
        }
    }

    public void onPlayerLeft() {
        if (!ropeSystem.isRopeAttached)
        {
            player.moveLeft();
        }
    }

    public void onPlayerJump(Sound jumpSound) {
        player.jump(jumpSound);
    }

    public void onPlayerDash(Sound dashSound){
        player.startDash(dashSound);
    }

    public DefaultGameLevel getCurrentLevel()
    {
        return currentLevel;
    }

    public int getCoinCnt()
    {
        return coinCnt;
    }
}
