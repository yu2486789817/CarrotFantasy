package carrotfantasy;

// Refactored with Factory Method Pattern
public abstract class GameEntityFactory {

    // Factory method for creating monsters
    public abstract Monster createMonster(int mode, int currentWave);

    // Factory method for creating towers
    public abstract Tower createTower(int towerType, int x, int y, Monster[] monsters, int monsterNum, Object cell);

    // Factory method for creating game elements based on difficulty
    public abstract GameElement createGameElement(String elementType, int mode);
}

// Concrete factory for Easy mode
class EasyModeFactory extends GameEntityFactory {

    @Override
    public Monster createMonster(int mode, int currentWave) {
        return new Monster(0);
    }

    @Override
    public Tower createTower(int towerType, int x, int y, Monster[] monsters, int monsterNum, Object cell) {
        if (towerType == 1) {
            return new TBottle(x, y, monsters, monsters.length - 10, (javax.swing.JButton) cell);
        } else if (towerType == 2) {
            return new TSunFlower(x, y, monsters, monsters.length - 10);
        }
        return null;
    }

    @Override
    public GameElement createGameElement(String elementType, int mode) {
        switch (elementType) {
            case "background":
                return new GameElement("Images\\Theme1\\BG0\\BG1-hd.png", 0, 0, 960, 640);
            case "path":
                return new GameElement("Images\\Theme1\\BG1\\BG-hd.png", 0, 120, 960, 482);
            default:
                return null;
        }
    }
}

// Concrete factory for Medium mode
class MediumModeFactory extends GameEntityFactory {

    @Override
    public Monster createMonster(int mode, int currentWave) {
        return new Monster(1);
    }

    @Override
    public Tower createTower(int towerType, int x, int y, Monster[] monsters, int monsterNum, Object cell) {
        if (towerType == 1) {
            return new TBottle(x, y, monsters, monsters.length - 5, (javax.swing.JButton) cell);
        } else if (towerType == 2) {
            return new TSunFlower(x, y, monsters, monsters.length - 5);
        }
        return null;
    }

    @Override
    public GameElement createGameElement(String elementType, int mode) {
        switch (elementType) {
            case "background":
                return new GameElement("Images\\Theme2\\BG0\\BG1-hd.png", 0, 0, 960, 640);
            case "path":
                return new GameElement("Images\\Theme2\\BG1\\BG-hd.png", 0, 110, 960, 492);
            default:
                return null;
        }
    }
}

// Concrete factory for Hard mode
class HardModeFactory extends GameEntityFactory {

    @Override
    public Monster createMonster(int mode, int currentWave) {
        return new Monster(2);
    }

    @Override
    public Tower createTower(int towerType, int x, int y, Monster[] monsters, int monsterNum, Object cell) {
        if (towerType == 1) {
            return new TBottle(x, y, monsters, monsters.length, (javax.swing.JButton) cell);
        } else if (towerType == 2) {
            return new TSunFlower(x, y, monsters, monsters.length);
        }
        return null;
    }

    @Override
    public GameElement createGameElement(String elementType, int mode) {
        switch (elementType) {
            case "background":
                return new GameElement("Images\\Theme3\\BG0\\BG1-hd.png", 0, 0, 960, 640);
            case "path":
                return new GameElement("Images\\Theme3\\BG1\\BG-hd.png", 0, 140, 960, 464);
            default:
                return null;
        }
    }
}

// Factory provider
class FactoryProvider {
    public static GameEntityFactory getFactory(int mode) {
        switch (mode) {
            case 0: return new EasyModeFactory();
            case 1: return new MediumModeFactory();
            case 2: return new HardModeFactory();
            default: return new EasyModeFactory();
        }
    }
}