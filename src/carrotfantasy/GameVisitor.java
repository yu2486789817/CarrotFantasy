package carrotfantasy;

// Refactored with Visitor Pattern
public interface GameVisitor {
    void visit(Tower tower);
    void visit(Monster monster);
    void visit(Carrot carrot);
    void visit(GameElement element);
}

// Concrete visitor for saving game state
class SaveGameStateVisitor implements GameVisitor {
    private StringBuilder gameStateData;

    public SaveGameStateVisitor() {
        this.gameStateData = new StringBuilder();
    }

    @Override
    public void visit(Tower tower) {
        gameStateData.append("TOWER:").append(tower.getClass().getSimpleName())
                    .append(",level=").append(tower.getLevel())
                    .append(",power=").append(tower.power)
                    .append(",range=").append(tower.range)
                    .append(";");
    }

    @Override
    public void visit(Monster monster) {
        gameStateData.append("MONSTER:hp=").append(monster.HP)
                    .append(",alive=").append(monster.alive)
                    .append(",x=").append(monster.xPos)
                    .append(",y=").append(monster.yPos)
                    .append(";");
    }

    @Override
    public void visit(Carrot carrot) {
        gameStateData.append("CARROT:hp=").append(carrot.getHP())
                    .append(";");
    }

    @Override
    public void visit(GameElement element) {
        gameStateData.append("ELEMENT:").append(element.getImagePath())
                    .append(";");
    }

    public String getGameStateData() {
        return gameStateData.toString();
    }
}

// Concrete visitor for loading game state
class LoadGameStateVisitor implements GameVisitor {
    private String gameStateData;
    private int currentPosition;

    public LoadGameStateVisitor(String gameStateData) {
        this.gameStateData = gameStateData;
        this.currentPosition = 0;
    }

    @Override
    public void visit(Tower tower) {
        // Parse and load tower state from saved data
        // This is a simplified implementation
        String towerData = extractNextData("TOWER");
        if (towerData != null) {
            // Parse tower data and restore tower state
            parseAndRestoreTowerState(tower, towerData);
        }
    }

    @Override
    public void visit(Monster monster) {
        // Parse and load monster state from saved data
        String monsterData = extractNextData("MONSTER");
        if (monsterData != null) {
            // Parse monster data and restore monster state
            parseAndRestoreMonsterState(monster, monsterData);
        }
    }

    @Override
    public void visit(Carrot carrot) {
        // Parse and load carrot state from saved data
        String carrotData = extractNextData("CARROT");
        if (carrotData != null) {
            // Parse carrot data and restore carrot state
            parseAndRestoreCarrotState(carrot, carrotData);
        }
    }

    @Override
    public void visit(GameElement element) {
        // Parse and load element state from saved data
        String elementData = extractNextData("ELEMENT");
        if (elementData != null) {
            // Parse element data and restore element state
            parseAndRestoreElementState(element, elementData);
        }
    }

    private String extractNextData(String prefix) {
        // Simple parsing implementation
        int startIndex = gameStateData.indexOf(prefix + ":", currentPosition);
        if (startIndex == -1) return null;

        int endIndex = gameStateData.indexOf(";", startIndex);
        if (endIndex == -1) return null;

        currentPosition = endIndex + 1;
        return gameStateData.substring(startIndex + prefix.length() + 1, endIndex);
    }

    private void parseAndRestoreTowerState(Tower tower, String data) {
        // Simplified parsing - in real implementation would be more robust
        String[] parts = data.split(",");
        for (String part : parts) {
            if (part.startsWith("level=")) {
                // Restore tower level
            } else if (part.startsWith("power=")) {
                // Restore tower power
            } else if (part.startsWith("range=")) {
                // Restore tower range
            }
        }
    }

    private void parseAndRestoreMonsterState(Monster monster, String data) {
        // Simplified parsing implementation
        String[] parts = data.split(",");
        for (String part : parts) {
            if (part.startsWith("hp=")) {
                // Restore monster HP
            } else if (part.startsWith("x=")) {
                // Restore monster position
            } else if (part.startsWith("y=")) {
                // Restore monster position
            }
        }
    }

    private void parseAndRestoreCarrotState(Carrot carrot, String data) {
        // Simplified parsing implementation
        if (data.startsWith("hp=")) {
            // Restore carrot HP
        }
    }

    private void parseAndRestoreElementState(GameElement element, String data) {
        // Restore element state
    }
}

// Concrete visitor for calculating game statistics
class StatisticsVisitor implements GameVisitor {
    private int totalTowerValue;
    private int totalMonsterHP;
    private int towerCount;
    private int aliveMonsterCount;

    public StatisticsVisitor() {
        this.totalTowerValue = 0;
        this.totalMonsterHP = 0;
        this.towerCount = 0;
        this.aliveMonsterCount = 0;
    }

    @Override
    public void visit(Tower tower) {
        towerCount++;
        totalTowerValue += tower.price;
        System.out.println("Tower #" + towerCount + ": " +
                          tower.getClass().getSimpleName() +
                          " (Level " + tower.getLevel() +
                          ", Power: " + tower.power +
                          ", Range: " + tower.range + ")");
    }

    @Override
    public void visit(Monster monster) {
        totalMonsterHP += monster.HP;
        if (monster.alive) {
            aliveMonsterCount++;
        }
        System.out.println("Monster HP: " + monster.HP +
                          ", Alive: " + monster.alive +
                          ", Position: (" + monster.xPos + ", " + monster.yPos + ")");
    }

    @Override
    public void visit(Carrot carrot) {
        System.out.println("Carrot HP: " + carrot.getHP());
    }

    @Override
    public void visit(GameElement element) {
        System.out.println("Game Element: " + element.getImagePath());
    }

    public void printStatistics() {
        System.out.println("\n=== Game Statistics ===");
        System.out.println("Total Towers: " + towerCount);
        System.out.println("Total Tower Value: " + totalTowerValue);
        System.out.println("Total Monster HP: " + totalMonsterHP);
        System.out.println("Alive Monsters: " + aliveMonsterCount);
        System.out.println("========================\n");
    }

    // Getters for statistics
    public int getTotalTowerValue() { return totalTowerValue; }
    public int getTotalMonsterHP() { return totalMonsterHP; }
    public int getTowerCount() { return towerCount; }
    public int getAliveMonsterCount() { return aliveMonsterCount; }
}

// Concrete visitor for rendering game objects
class RenderVisitor implements GameVisitor {
    private boolean isPaused;
    private int currentWave;

    public RenderVisitor(boolean isPaused, int currentWave) {
        this.isPaused = isPaused;
        this.currentWave = currentWave;
    }

    @Override
    public void visit(Tower tower) {
        if (isPaused) {
            // Render tower in paused state
            renderPausedTower(tower);
        } else {
            // Render normal tower
            renderNormalTower(tower);
        }
    }

    @Override
    public void visit(Monster monster) {
        if (isPaused) {
            // Render monster in paused state
            renderPausedMonster(monster);
        } else {
            // Render normal monster with animations
            renderNormalMonster(monster);
        }
    }

    @Override
    public void visit(Carrot carrot) {
        if (isPaused) {
            renderPausedCarrot(carrot);
        } else {
            renderNormalCarrot(carrot);
        }
    }

    @Override
    public void visit(GameElement element) {
        renderGameElement(element);
    }

    private void renderNormalTower(Tower tower) {
        System.out.println("Rendering normal tower: " + tower.getClass().getSimpleName());
        // Tower-specific rendering logic would go here
    }

    private void renderPausedTower(Tower tower) {
        System.out.println("Rendering paused tower: " + tower.getClass().getSimpleName());
        // Tower-specific paused rendering logic would go here
    }

    private void renderNormalMonster(Monster monster) {
        System.out.println("Rendering normal monster with HP: " + monster.HP);
        // Monster animation logic would go here
    }

    private void renderPausedMonster(Monster monster) {
        System.out.println("Rendering paused monster with HP: " + monster.HP);
        // Monster paused state rendering would go here
    }

    private void renderNormalCarrot(Carrot carrot) {
        System.out.println("Rendering normal carrot with HP: " + carrot.getHP());
        // Carrot animation logic would go here
    }

    private void renderPausedCarrot(Carrot carrot) {
        System.out.println("Rendering paused carrot with HP: " + carrot.getHP());
        // Carrot paused state rendering would go here
    }

    private void renderGameElement(GameElement element) {
        System.out.println("Rendering game element: " + element.getImagePath());
        // Element rendering logic would go here
    }
}

// Visitable interface for objects that can accept visitors
interface Visitable {
    void accept(GameVisitor visitor);
}