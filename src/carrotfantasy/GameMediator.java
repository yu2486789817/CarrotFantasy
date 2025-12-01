package carrotfantasy;

import java.util.ArrayList;
import java.util.List;

// Refactored with Mediator Pattern
public interface GameMediator {
    void registerGameComponent(GameComponent component);
    void notifyGameEvent(GameEvent event);
    void notifyGameEvent(GameEvent event, Object data);
    void removeGameComponent(GameComponent component);
}

// Game events that can be communicated
enum GameEvent {
    MONSTER_SPAWNED,
    MONSTER_DAMAGED,
    MONSTER_KILLED,
    MONSTER_REACHED_END,
    TOWER_BUILT,
    TOWER_UPGRADED,
    TOWER_SOLD,
    TOWER_ATTACKED,
    GAME_PAUSED,
    GAME_RESUMED,
    GAME_OVER,
    VICTORY,
    MONEY_CHANGED,
    WAVE_COMPLETED
}

// Game component interface
interface GameComponent {
    void receiveGameEvent(GameEvent event, Object data);
    String getComponentName();
}

// Concrete mediator
class ConcreteGameMediator implements GameMediator {
    private List<GameComponent> components;
    private GameState gameState;

    public ConcreteGameMediator() {
        this.components = new ArrayList<>();
        this.gameState = new GameState();
    }

    @Override
    public void registerGameComponent(GameComponent component) {
        if (!components.contains(component)) {
            components.add(component);
            // Notify component about current game state
            component.receiveGameEvent(GameEvent.MONEY_CHANGED, gameState.getMoney());
            component.receiveGameEvent(GameEvent.GAME_RESUMED, null);
        }
    }

    @Override
    public void removeGameComponent(GameComponent component) {
        components.remove(component);
    }

    @Override
    public void notifyGameEvent(GameEvent event) {
        notifyGameEvent(event, null);
    }

    public void notifyGameEvent(GameEvent event, Object data) {
        // Update game state based on event
        updateGameState(event, data);

        // Notify all relevant components
        for (GameComponent component : components) {
            component.receiveGameEvent(event, data);
        }
    }

    private void updateGameState(GameEvent event, Object data) {
        switch (event) {
            case MONSTER_KILLED:
                if (data instanceof Monster) {
                    Monster monster = (Monster) data;
                    gameState.addMoney(monster.money);
                }
                break;
            case TOWER_BUILT:
                if (data instanceof Tower) {
                    Tower tower = (Tower) data;
                    gameState.deductMoney(tower.price);
                }
                break;
            case TOWER_UPGRADED:
                if (data instanceof Tower) {
                    Tower tower = (Tower) data;
                    gameState.deductMoney(tower.upgradePrice);
                }
                break;
            case TOWER_SOLD:
                if (data instanceof Tower) {
                    Tower tower = (Tower) data;
                    gameState.addMoney((int)(tower.price * 0.8));
                }
                break;
            case GAME_PAUSED:
                gameState.setPaused(true);
                break;
            case GAME_RESUMED:
                gameState.setPaused(false);
                break;
        }
    }

    public GameState getGameState() {
        return gameState;
    }
}

// Game state management
class GameState {
    private int money;
    private int currentWave;
    private boolean paused;
    private boolean gameOver;
    private boolean victory;

    public GameState() {
        this.money = 250;
        this.currentWave = 1;
        this.paused = false;
        this.gameOver = false;
        this.victory = false;
    }

    // Getters and setters
    public int getMoney() { return money; }
    public void setMoney(int money) { this.money = money; }
    public void addMoney(int amount) { this.money += amount; }
    public void deductMoney(int amount) { this.money -= amount; }
    public boolean canAfford(int cost) { return money >= cost; }

    public int getCurrentWave() { return currentWave; }
    public void setCurrentWave(int wave) { this.currentWave = wave; }
    public void nextWave() { this.currentWave++; }

    public boolean isPaused() { return paused; }
    public void setPaused(boolean paused) { this.paused = paused; }

    public boolean isGameOver() { return gameOver; }
    public void setGameOver(boolean gameOver) { this.gameOver = gameOver; }

    public boolean isVictory() { return victory; }
    public void setVictory(boolean victory) { this.victory = victory; }
}

// Tower as a game component
class TowerGameComponent implements GameComponent {
    private Tower tower;
    private GameMediator mediator;

    public TowerGameComponent(Tower tower, GameMediator mediator) {
        this.tower = tower;
        this.mediator = mediator;
        mediator.registerGameComponent(this);
    }

    @Override
    public void receiveGameEvent(GameEvent event, Object data) {
        switch (event) {
            case GAME_PAUSED:
                tower.pause();
                break;
            case GAME_RESUMED:
                tower.myResume();
                break;
            case GAME_OVER:
                tower.gameOver();
                break;
            case MONSTER_DAMAGED:
                // Tower might have visual feedback for monster damage
                break;
        }
    }

    @Override
    public String getComponentName() {
        return tower.getClass().getSimpleName();
    }

    public void notifyAttack() {
        mediator.notifyGameEvent(GameEvent.TOWER_ATTACKED, this);
    }

    public void notifyUpgrade() {
        mediator.notifyGameEvent(GameEvent.TOWER_UPGRADED, tower);
    }

    public void notifySold() {
        mediator.notifyGameEvent(GameEvent.TOWER_SOLD, tower);
        mediator.removeGameComponent(this);
    }
}

// Monster as a game component
class MonsterGameComponent implements GameComponent {
    private Monster monster;
    private GameMediator mediator;

    public MonsterGameComponent(Monster monster, GameMediator mediator) {
        this.monster = monster;
        this.mediator = mediator;
        mediator.registerGameComponent(this);
    }

    @Override
    public void receiveGameEvent(GameEvent event, Object data) {
        switch (event) {
            case GAME_PAUSED:
                // Handle monster pause
                break;
            case GAME_RESUMED:
                // Handle monster resume
                break;
        }
    }

    @Override
    public String getComponentName() {
        return "Monster";
    }

    public void notifySpawned() {
        mediator.notifyGameEvent(GameEvent.MONSTER_SPAWNED, this);
    }

    public void notifyDamaged(int damage) {
        mediator.notifyGameEvent(GameEvent.MONSTER_DAMAGED, damage);
    }

    public void notifyKilled() {
        mediator.notifyGameEvent(GameEvent.MONSTER_KILLED, monster);
        mediator.removeGameComponent(this);
    }

    public void notifyReachedEnd() {
        mediator.notifyGameEvent(GameEvent.MONSTER_REACHED_END, monster);
        mediator.removeGameComponent(this);
    }
}

// UI Component as game component
class UIGameComponent implements GameComponent {
    private String componentName;
    private GameMediator mediator;

    public UIGameComponent(String componentName, GameMediator mediator) {
        this.componentName = componentName;
        this.mediator = mediator;
        mediator.registerGameComponent(this);
    }

    @Override
    public void receiveGameEvent(GameEvent event, Object data) {
        switch (event) {
            case MONEY_CHANGED:
                if (componentName.equals("MoneyDisplay")) {
                    updateMoneyDisplay((Integer) data);
                }
                break;
            case GAME_PAUSED:
                if (componentName.equals("GamePanel")) {
                    showPauseScreen();
                }
                break;
            case GAME_RESUMED:
                if (componentName.equals("GamePanel")) {
                    hidePauseScreen();
                }
                break;
            case GAME_OVER:
                if (componentName.equals("GamePanel")) {
                    showGameOverScreen(false);
                }
                break;
            case VICTORY:
                if (componentName.equals("GamePanel")) {
                    showGameOverScreen(true);
                }
                break;
        }
    }

    @Override
    public String getComponentName() {
        return componentName;
    }

    private void updateMoneyDisplay(int money) {
        // Update UI to show new money amount
        System.out.println("Updating money display: " + money);
    }

    private void showPauseScreen() {
        // Show pause UI
        System.out.println("Showing pause screen");
    }

    private void hidePauseScreen() {
        // Hide pause UI
        System.out.println("Hiding pause screen");
    }

    private void showGameOverScreen(boolean victory) {
        // Show game over UI
        System.out.println("Showing game over screen: " + (victory ? "Victory" : "Defeat"));
    }
}