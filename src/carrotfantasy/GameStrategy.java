package carrotfantasy;

// Refactored with Strategy Pattern
public interface GameStrategy {
    void executeMonsterMovement(Monster monster, long deltaTime, int currentWave);
    boolean isMonsterReachedEnd(Monster monster);
    int getInitialMonsterHP(int mode, int currentWave);
    int getMonsterSpeed(int mode);
    int getMonsterMoney(int mode);
}

// Easy mode strategy
class EasyModeStrategy implements GameStrategy {
    private int[] pathX = {80, 300, 545, 785};
    private int[] pathY = {110, 250, 330, 110};
    private int[] directions = {0, 1, 2, 1, 2, 1, 2};

    @Override
    public void executeMonsterMovement(Monster monster, long deltaTime, int currentWave) {
        // Implementation would extract movement logic from MonsterThread
        // This is simplified for demonstration
        if (monster.yPos < 330) {
            monster.yPos += deltaTime * getMonsterSpeed(0);
        } else if (monster.xPos < 300) {
            monster.xPos += deltaTime * getMonsterSpeed(0);
        } else if (monster.yPos > 250) {
            monster.yPos -= deltaTime * getMonsterSpeed(0);
        }
    }

    @Override
    public boolean isMonsterReachedEnd(Monster monster) {
        return monster.xPos >= 785 && monster.yPos <= 110;
    }

    @Override
    public int getInitialMonsterHP(int mode, int currentWave) {
        return 100 + 50 * mode + 25 * currentWave;
    }

    @Override
    public int getMonsterSpeed(int mode) {
        return (int)(0.1 + 0.05 * mode * 1000); // Convert to pixels per second
    }

    @Override
    public int getMonsterMoney(int mode) {
        return 10;
    }
}

// Medium mode strategy
class MediumModeStrategy implements GameStrategy {
    @Override
    public void executeMonsterMovement(Monster monster, long deltaTime, int currentWave) {
        if (monster.xPos < 725) {
            monster.xPos += deltaTime * getMonsterSpeed(1);
        } else if (monster.yPos < 420) {
            monster.yPos += deltaTime * getMonsterSpeed(1);
        } else if (monster.xPos > 166) {
            monster.xPos -= deltaTime * getMonsterSpeed(1);
        } else if (monster.xPos < 680) {
            monster.xPos += deltaTime * getMonsterSpeed(1);
        }
    }

    @Override
    public boolean isMonsterReachedEnd(Monster monster) {
        return monster.xPos >= 680 && monster.yPos >= 420;
    }

    @Override
    public int getInitialMonsterHP(int mode, int currentWave) {
        return 100 + 50 * mode + 25 * currentWave;
    }

    @Override
    public int getMonsterSpeed(int mode) {
        return (int)(0.15 + 0.05 * mode * 1000);
    }

    @Override
    public int getMonsterMoney(int mode) {
        return 10;
    }
}

// Hard mode strategy
class HardModeStrategy implements GameStrategy {
    @Override
    public void executeMonsterMovement(Monster monster, long deltaTime, int currentWave) {
        if (monster.xPos < 160) {
            monster.xPos += deltaTime * getMonsterSpeed(2);
        } else if (monster.yPos < 420) {
            monster.yPos += deltaTime * getMonsterSpeed(2);
        } else if (monster.xPos < 800) {
            monster.xPos += deltaTime * getMonsterSpeed(2);
        }
    }

    @Override
    public boolean isMonsterReachedEnd(Monster monster) {
        return monster.xPos >= 800 && monster.yPos <= 110;
    }

    @Override
    public int getInitialMonsterHP(int mode, int currentWave) {
        return 100 + 50 * mode + 25 * currentWave;
    }

    @Override
    public int getMonsterSpeed(int mode) {
        return (int)(0.2 + 0.05 * mode * 1000);
    }

    @Override
    public int getMonsterMoney(int mode) {
        return 15;
    }
}

// Strategy context
class GameStrategyContext {
    private GameStrategy strategy;

    public GameStrategyContext(GameStrategy strategy) {
        this.strategy = strategy;
    }

    public void setStrategy(GameStrategy strategy) {
        this.strategy = strategy;
    }

    public void moveMonster(Monster monster, long deltaTime, int currentWave) {
        strategy.executeMonsterMovement(monster, deltaTime, currentWave);
    }

    public boolean isMonsterAtEnd(Monster monster) {
        return strategy.isMonsterReachedEnd(monster);
    }

    public int getMonsterHP(int mode, int currentWave) {
        return strategy.getInitialMonsterHP(mode, currentWave);
    }

    public int getMonsterSpeed(int mode) {
        return strategy.getMonsterSpeed(mode);
    }

    public int getMonsterMoney(int mode) {
        return strategy.getMonsterMoney(mode);
    }
}