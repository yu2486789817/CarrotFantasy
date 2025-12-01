package carrotfantasy;

import javax.swing.*;

// Refactored with Facade Pattern
public class GameFacade {
    private GamePanel gamePanel;
    private MonsterThread monsterThread;
    private MusicModule musicModule;
    private GameEntityFactory entityFactory;
    private GameStrategyContext strategyContext;

    public GameFacade(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        this.musicModule = new MusicModule();

        // Initialize based on game mode
        int mode = gamePanel.getMode();
        this.entityFactory = FactoryProvider.getFactory(mode);

        // Set up strategy
        GameStrategy strategy;
        switch (mode) {
            case 0: strategy = new EasyModeStrategy(); break;
            case 1: strategy = new MediumModeStrategy(); break;
            case 2: strategy = new HardModeStrategy(); break;
            default: strategy = new EasyModeStrategy();
        }
        this.strategyContext = new GameStrategyContext(strategy);
    }

    // Simplified method to start game
    public void startGame() {
        musicModule.play("BGMusic");
        initializeGameElements();
        startMonsterThread();
    }

    // Simplified method to create tower
    public boolean createTower(int towerType, int x, int y) {
        if (gamePanel.getMoney() >= getTowerPrice(towerType)) {
            Tower tower = entityFactory.createTower(towerType, x, y,
                gamePanel.getMonsters(), gamePanel.getMonsterCount(), gamePanel.getCell(x, y));
            if (tower != null) {
                gamePanel.addTower(tower, x, y);
                gamePanel.deductMoney(tower.getPrice());
                musicModule.play("towerBuild");
                return true;
            }
        }
        return false;
    }

    // Simplified method to upgrade tower
    public boolean upgradeTower(int x, int y) {
        Tower tower = gamePanel.getTower(x, y);
        if (tower != null && gamePanel.getMoney() >= tower.getUpgradePrice()) {
            tower.upgrade();
            gamePanel.deductMoney(tower.getUpgradePrice());
            musicModule.play("towerUpgrade");
            return true;
        }
        return false;
    }

    // Simplified method to sell tower
    public void sellTower(int x, int y) {
        Tower tower = gamePanel.getTower(x, y);
        if (tower != null) {
            int sellPrice = (int)(tower.getPrice() * 0.8);
            gamePanel.addMoney(sellPrice);
            gamePanel.removeTower(x, y);
            musicModule.play("towerSell");
        }
    }

    // Simplified method to handle pause/resume
    public void togglePause() {
        if (gamePanel.isPaused()) {
            resumeGame();
        } else {
            pauseGame();
        }
    }

    private void pauseGame() {
        gamePanel.setPaused(true);
        monsterThread.pause();
        gamePanel.getCarrot().pause();
        gamePanel.pauseAllTowers();
        musicModule.play("select");
    }

    private void resumeGame() {
        gamePanel.setPaused(false);
        monsterThread.myResume();
        gamePanel.getCarrot().myResume();
        gamePanel.resumeAllTowers();
        musicModule.play("select");
    }

    // Simplified method to handle monster damage
    public void damageMonster(Monster monster, int damage) {
        monster.HP -= damage;
        if (monster.HP <= 0) {
            monster.alive = false;
            monster.setVisible(false);
            gamePanel.addMoney(monster.money);
        }
    }

    // Simplified method to check game over
    public boolean checkGameOver() {
        if (gamePanel.getCarrot().getHP() <= 0) {
            musicModule.play("lose");
            gamePanel.showGameOver(false);
            return true;
        }
        return false;
    }

    // Simplified method to check victory
    public boolean checkVictory() {
        if (gamePanel.isAllWavesCompleted()) {
            musicModule.play("perfect");
            gamePanel.showGameOver(true);
            return true;
        }
        return false;
    }

    // Helper methods
    private void initializeGameElements() {
        // Initialize game elements using factory
        GameElement bg = entityFactory.createGameElement("background", gamePanel.getMode());
        GameElement path = entityFactory.createGameElement("path", gamePanel.getMode());

        // Set up game panel with elements
        gamePanel.setupBackground(bg);
        gamePanel.setupPath(path);
    }

    private void startMonsterThread() {
        monsterThread = new MonsterThread(
            gamePanel.getMonsters(),
            gamePanel.getMode(),
            gamePanel.getWaveNums(),
            gamePanel.getMoneyNums(),
            gamePanel.getBottleButton(),
            gamePanel.getSunFlowerButton(),
            gamePanel.getCarrot(),
            gamePanel.getGameOverPanel(),
            gamePanel.getTowers(),
            gamePanel.getUpgradeButton(),
            gamePanel.getCountDownLabel()
        );
        monsterThread.start();
    }

    private int getTowerPrice(int towerType) {
        switch (towerType) {
            case 1: return 100; // TBottle
            case 2: return 180; // TSunFlower
            default: return 0;
        }
    }

    // Getters for external access
    public GameStrategyContext getStrategyContext() {
        return strategyContext;
    }

    public GameEntityFactory getEntityFactory() {
        return entityFactory;
    }

    public MusicModule getMusicModule() {
        return musicModule;
    }
}