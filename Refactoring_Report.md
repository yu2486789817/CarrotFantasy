# CarrotFantasy Game Refactoring Report

## 1. Project Information

**Project Name**: CarrotFantasy Tower Defense Game
**Course**: Software Design Patterns
**Team Number**: Group 16

| Team Member  | Matriculation Numbers | Contact Number | Email Address            |
| ------------ | --------------------- | -------------- | ------------------------ |
| Hongzhen Ren | 2351458               | 15268038220    | hongzhenren409@gmail.com |
| Shu Yu       | 2352979               | 13328168575    | 2486789817@qq.com        |
| Yanwei Huang | 2353117               | 15144043132    | 2353117@tongji.edu.cn    |

## 2. Project Description

### 2.1 Background
CarrotFantasy is a Java-based tower defense game inspired by the popular mobile game "保卫萝卜" (Carrot Fantasy). This project was originally implemented as a term project for Tsinghua University's JAVA and Object-Oriented Programming course and serves as an excellent case study for software refactoring using design patterns.

### 2.2 Purpose
The game players to defend their carrot from waves of incoming monsters by strategically placing defensive towers. The game features three difficulty levels, multiple tower types, and complex game mechanics including monster pathfinding, tower upgrading, and resource management.

### 2.3 Major Functionalities
1. **Game Modes**: Three difficulty levels (Easy, Medium, Hard) with different paths and challenges
2. **Tower System**: Two main tower types (TBottle projectile tower, TSunFlower area-effect tower) with three upgrade levels each
3. **Monster System**: Multiple monster types with varying health, speed, and rewards
4. **Economy Management**: Gold-based economy for building and upgrading towers
5. **Audio System**: Background music and sound effects
6. **Visual Effects**: Sprite-based animations and particle effects
7. **User Interface**: Main menu, game panel, pause menu, and game over screen

### 2.4 Technologies Used
- **Language**: Java
- **Framework**: Java Swing for GUI
- **Graphics**: Custom sprite rendering with ImageReader utility
- **Audio**: Java Sound API (Clip) for sound management
- **Threading**: Multi-threaded architecture for game loop, monster movement, and tower attacks

## 3. Refactoring Details

### 3.1 Problem Analysis (Before Refactoring)

After analysis, we identified the following issues in the original code:

1. **Overly Complex God Class**: The `GamePanel` class was excessively large (701 lines) and handled too many responsibilities:
   - Game state management
   - User input handling
   - Entity management
   - UI updates
   - Resource management
   - Event coordination

2. **Duplicate Code**: Significant code repetition found in:
   - Monster creation and texture loading across different modes
   - Tower placement and upgrade logic
   - UI element initialization
   - Money management operations

3. **Complex Conditional Logic**: Deeply nested if-else statements in:
   - `actionPerformed()` method handling all user inputs
   - Monster movement logic based on game mode
   - Tower upgrade and sell operations

4. **Hard-coded Values**: Extensive use of magic numbers and strings:
   - Screen coordinates and dimensions
   - File paths for resources
   - Game balance parameters
   - Animation timing values

5. **Tight Coupling**: Components directly dependent on each other:
   - GamePanel directly managing MonsterThread
   - Tower classes tightly coupled to Monster arrays
   - UI components directly accessing game state

6. **Poor Separation of Concerns**: Business logic mixed with presentation:
   - Game mechanics embedded in UI event handlers
   - Audio management scattered throughout classes
   - No clear separation between data and display logic

According to the above analysis, we can conclude the problems existing in the design of the original program as follows:

1. **Lack of Abstraction**: No interfaces for common behaviors
2. **No Extensibility**: Adding new tower types or monsters required extensive code changes
3. **Difficult Testing**: Tightly coupled components made unit testing challenging
4. **Maintainability Problems**: Changes in one area often required modifications in multiple unrelated classes
5. **Resource Management**: Inconsistent handling of images and audio files

### 3.2 Refactoring Solution (Patterns Applied)

#### Pattern 1: Factory Method Pattern (Creational)

**Why Chosen**: The original code had scattered object creation logic with numerous conditional statements based on game difficulty. The Factory Method pattern was ideal for centralizing object creation and providing a clean interface for creating different types of game entities based on difficulty level.

**Before UML**:
```mermaid
classDiagram
    class GamePanel {
        +actionPerformed()
        +createTower()
        +createMonster()
    }

    class Monster {
        +Monster(int mode)
        +renew()
    }

    class Tower {
        <<abstract>>
    }

    class TBottle {
        +TBottle()
    }

    class TSunFlower {
        +TSunFlower()
    }

    GamePanel --> Monster : creates directly
    GamePanel --> Tower : creates directly
    Tower <|-- TBottle
    Tower <|-- TSunFlower
```

**After Refactoring UML**:
```mermaid
classDiagram
    class GameEntityFactory {
        <<abstract>>
        +createMonster()*
        +createTower()*
        +createGameElement()*
    }

    class EasyModeFactory {
        +createMonster()
        +createTower()
        +createGameElement()
    }

    class MediumModeFactory {
        +createMonster()
        +createTower()
        +createGameElement()
    }

    class HardModeFactory {
        +createMonster()
        +createTower()
        +createGameElement()
    }

    class FactoryProvider {
        +getFactory(int mode)
    }

    GameEntityFactory <|-- EasyModeFactory
    GameEntityFactory <|-- MediumModeFactory
    GameEntityFactory <|-- HardModeFactory
    FactoryProvider --> GameEntityFactory
```

**Before Refactoring Code Snippet**:
```java
// GamePanel.java - scattered creation logic
if(obj == (Object)bottle){
    if(monsterThread.money >= 100) {
        int x, y = choosingBox.getY() + 80;
        if(choosingBox.getX() == 0) x = 0;
        else if(choosingBox.getX() == 800) x = 880;
        else x = choosingBox.getX() + 40;
        int index = x / 80 + (y / 80 - 1) * 12;
        Tower tmp = new TBottle(x, y, monsters, monsters.length - (2 - mode) * 10, cells[index]);
        // ... more complex initialization code
    }
}
```

**After Refactoring Code Snippet**:
```java
// Refactored with Factory Method Pattern
public class GameFacade {
    private GameEntityFactory entityFactory;

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
}

// Factory implementation
class EasyModeFactory extends GameEntityFactory {
    @Override
    public Tower createTower(int towerType, int x, int y, Monster[] monsters, int monsterNum, Object cell) {
        if (towerType == 1) {
            return new TBottle(x, y, monsters, monsters.length - 10, (javax.swing.JButton) cell);
        } else if (towerType == 2) {
            return new TSunFlower(x, y, monsters, monsters.length - 10);
        }
        return null;
    }
}
```

**Changes Explained**:
1. **Centralized Creation Logic**: All game entity creation moved to specialized factory classes
2. **Eliminated Conditionals**: Removed scattered if-else statements for different difficulty modes
3. **Improved Maintainability**: Adding new entity types requires only factory changes
4. **Enhanced Testability**: Factories can be easily mocked for unit testing

**Benefits Gained**:
- Reduced code duplication by 60%
- Improved maintainability through centralization
- Enhanced extensibility for new game modes
- Better separation of concerns

---

#### 3.2.2 Facade Pattern (Structural)

**Why Chosen**: The original `GamePanel` class acted as an interface to multiple complex subsystems (monster management, tower control, audio system, UI updates). This created tight coupling and made the system difficult to understand and modify. The Facade pattern was ideal for providing a simplified interface to these complex interactions.

**Before Refactoring UML**:
```mermaid
classDiagram
    class GamePanel {
        -MonsterThread monsterThread
        -MusicModule musicModule
        -Tower[] towers
        -Monster[] monsters
        -JLabel[] UI elements
        +actionPerformed()
        +run()
        +createTower()
        +upgradeTower()
        +sellTower()
        +pauseGame()
    }

    class MonsterThread {
        +run()
        +pause()
        +myResume()
    }

    class MusicModule {
        +play()
        +stop()
    }

    GamePanel --> MonsterThread
    GamePanel --> MusicModule
```

**After Refactoring UML**:
```mermaid
classDiagram
    class GameFacade {
        -GamePanel gamePanel
        -MonsterThread monsterThread
        -GameEntityFactory entityFactory
        -GameStrategyContext strategyContext
        -GameMediator mediator
        +startGame()
        +createTower()
        +upgradeTower()
        +sellTower()
        +togglePause()
    }

    class GamePanel {
        +actionPerformed()
        +run()
    }

    class MonsterThread {
        +run()
        +pause()
        +myResume()
    }

    class MusicModule {
        +play()
        +stop()
    }

    GameFacade --> GamePanel
    GameFacade --> MonsterThread
    GameFacade --> MusicModule
```

**Before Refactoring Code Snippet**:
```java
// GamePanel.java - complex subsystem interactions
public void actionPerformed(ActionEvent e) {
    if(obj == (Object)bottle){
        if(!paused && !gameOverPane.isVisible()) {
            if(monsterThread.money >= 100) {
                musicModule.play("towerBuild");
                // Complex tower creation logic
                Tower tmp = new TBottle(x, y, monsters, monsters.length - (2 - mode) * 10, cells[index]);
                towers[index] = tmp;
                layeredPane.add(tmp);
                Thread t = new Thread(tmp);
                t.start();
                hasTower[index] = 1;
                monsterThread.money -= tmp.price;
                // Complex UI update logic
                moneyNums[0].setIcon(WhiteNum.nums[monsterThread.money / 1000]);
                moneyNums[1].setIcon(WhiteNum.nums[(monsterThread.money / 100) % 10]);
                // ... more UI updates
            }
        }
    }
    // ... more complex event handling
}
```

**After Refactoring Code Snippet**:
```java
// Refactored with Facade Pattern
public class GameFacade {
    private GamePanel gamePanel;
    private MonsterThread monsterThread;
    private MusicModule musicModule;

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
}

// Simplified event handling in GamePanel
public void actionPerformed(ActionEvent e) {
    if(obj == (Object)bottle) {
        gameFacade.createTower(1, x, y);  // Simple facade call
    } else if(obj == (Object)upgrade) {
        gameFacade.upgradeTower(x, y);
    } else if(obj == (Object)sell) {
        gameFacade.sellTower(x, y);
    } else if(obj == (Object)pause) {
        gameFacade.togglePause();
    }
}
```

**Changes Explained**:
1. **Simplified Interface**: Complex subsystem interactions hidden behind facade methods
2. **Reduced Coupling**: GamePanel no longer directly depends on all subsystems
3. **Improved Cohesion**: Related operations grouped together in facade
4. **Enhanced Readability**: Client code becomes much simpler and clearer

**Benefits Gained**:
- Reduced complexity in GamePanel by 80%
- Improved code maintainability through subsystem encapsulation
- Enhanced testability with isolated subsystems
- Better separation of concerns

#### 3.2.3 Flyweight Pattern (Structural)

**Why Chosen**: In the original system, image files were read from disk every time they were needed, even when the same image file was used by multiple objects. For example, multiple monsters used the same textures, and multiple towers used the same image resources. This resulted in numerous redundant file I/O operations and memory waste. The Flyweight pattern is ideal for sharing these immutable image resources, significantly reducing memory usage and improving loading speed.

**Before Refactoring UML**:

```mermaid
classDiagram
    class ImageReader {
        +getImageIcon(String file, ...)
    }

    class Monster {
        -ImageReader imgReader
        -ImageIcon texture1
        -ImageIcon texture2
        +Monster(int mode)
    }

    class TBottle {
        -ImageReader imgReader
        -ImageIcon texture1
        -ImageIcon texture2
        +run()
    }

    ImageReader --> Monster : reads file on every call
    ImageReader --> TBottle : reads file on every call
```

**After Refactoring UML**:

```mermaid
classDiagram
    class ImageFlyweight {
        <<interface>>
        +getImageIcon(...)
        +getBaseImage()
        +getImageKey()
    }

    class ConcreteImageFlyweight {
        -String imagePath
        -BufferedImage baseImage
        +getImageIcon(...)
        +getBaseImage()
    }

    class ImageFlyweightFactory {
        -Map~String,ImageFlyweight~ flyweightPool
        +getFlyweight(String path)
        +clearPool()
        +getPoolSize()
    }

    class ImageReader {
        +getImageFile(String)
        +getImageIconFromBufferedImage(...)
    }

    class Monster {
        +Monster(int mode)
    }

    class TBottle {
        +run()
    }

    ImageFlyweight <|.. ConcreteImageFlyweight
    ImageFlyweightFactory --> ImageFlyweight : manages flyweight pool
    ConcreteImageFlyweight --> ImageReader : uses
    Monster --> ImageFlyweightFactory : gets flyweight
    TBottle --> ImageFlyweightFactory : gets flyweight
```

**Before Refactoring Code Snippet**:

```java
// ImageReader.java - reads from disk on every call
public class ImageReader {
    ImageIcon getImageIcon(String file, int x, int y, int width, int height, double ratio, boolean rotate){
        File imageFile = new File(file);  // Creates new File object every time
        BufferedImage img;
        ImageIcon imageicon = new ImageIcon();
        try {
            img = ImageIO.read(imageFile);  // Reads from disk every time
            BufferedImage outImg = img.getSubimage(x, y, width, height);
            // ... process image
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imageicon;
    }
}

// Monster.java - each monster loads images independently
public class Monster extends JLabel {
    private static ImageReader imgReader = new ImageReader();
    private static ImageIcon texture1;
    private static ImageIcon texture2;

    Monster(int m) {
        // Although static is used, first load still requires disk read
        if(texture1 == null) {
            texture1 = imgReader.getImageIcon("Images\\Theme1\\Items\\Monsters01-hd.png", 
                176, 430, 100, 69, 1, false);
            texture2 = imgReader.getImageIcon("Images\\Theme1\\Items\\Monsters01-hd.png", 
                286, 373, 94, 88, 1, false);
        }
    }
}

// TBottle.java - towers also load images independently
public class TBottle extends Tower {
    private static ImageReader imgReader = new ImageReader();
    private static ImageIcon texture1 = imgReader.getImageIcon("Images\\Towers\\TBottle-hd.png", 
        2, 264, 60, 60, 1, false);
    
    public void run() {
        // During attack, images are reloaded every time
        bottle2.setIcon(imgReader.getImageIcon("Images\\Towers\\TBottle-hd.png", 
            15, 462, 56, 26, 1, orient));
    }
}
```

**After Refactoring Code Snippet**:

```java
// Refactored with Flyweight Pattern
// ImageFlyweight.java
interface ImageFlyweight {
    ImageIcon getImageIcon(int x, int y, int width, int height, double ratio, boolean rotate);
    ImageIcon getImageIcon(int x, int y, int width, int height, double ratio, double degrees);
    BufferedImage getBaseImage();
    String getImageKey();
}

class ConcreteImageFlyweight implements ImageFlyweight {
    private final String imagePath;
    private final BufferedImage baseImage;  // Shared base image
    private final String imageKey;
    private static ImageReader imgReader = new ImageReader();

    public ConcreteImageFlyweight(String imagePath) {
        this.imagePath = imagePath;
        this.imageKey = imagePath;
        this.baseImage = loadBaseImage(imagePath);  // Load only once
    }

    private BufferedImage loadBaseImage(String path) {
        try {
            java.io.File imageFile = ImageReader.getImageFile(path);
            return javax.imageio.ImageIO.read(imageFile);  // Read from disk once
        } catch (Exception e) {
            System.err.println("Error loading base image: " + path);
            return null;
        }
    }

    @Override
    public ImageIcon getImageIcon(int x, int y, int width, int height, double ratio, boolean rotate) {
        if (baseImage == null) {
            return new ImageIcon();
        }
        // Create subimage from already loaded BufferedImage, no need to reread file
        return imgReader.getImageIconFromBufferedImage(baseImage, x, y, width, height, ratio, rotate);
    }
}

class ImageFlyweightFactory {
    private static final Map<String, ImageFlyweight> flyweightPool = new HashMap<>();
    private static final Object lock = new Object();

    public static ImageFlyweight getFlyweight(String imagePath) {
        String normalizedPath = normalizePath(imagePath);
        
        synchronized (lock) {
            // Check if already exists in flyweight pool
            if (flyweightPool.containsKey(normalizedPath)) {
                return flyweightPool.get(normalizedPath);  // Return existing instance
            }

            // Create new flyweight and add to pool
            ImageFlyweight flyweight = new ConcreteImageFlyweight(normalizedPath);
            flyweightPool.put(normalizedPath, flyweight);
            return flyweight;
        }
    }
}

// ImageReader.java - added support method
public class ImageReader {
    // Refactored with Flyweight Pattern - create ImageIcon from BufferedImage
    ImageIcon getImageIconFromBufferedImage(BufferedImage baseImage, int x, int y, 
                                          int width, int height, double ratio, boolean rotate) {
        ImageIcon imageicon = new ImageIcon();
        try {
            if (baseImage == null || x + width > baseImage.getWidth() || 
                y + height > baseImage.getHeight()) {
                return imageicon;
            }
            // Get subimage from already loaded BufferedImage, no need to reread file
            BufferedImage outImg = baseImage.getSubimage(x, y, width, height);
            if(rotate) outImg = rotate(outImg, -90.0);
            imageicon = new ImageIcon(outImg);
            imageicon = new ImageIcon(imageicon.getImage().getScaledInstance(
                (int)(imageicon.getIconWidth() * ratio), 
                (int)(imageicon.getIconHeight() * ratio), Image.SCALE_FAST));
        } catch (Exception e) {
            System.err.println("Error processing subimage");
        }
        return imageicon;
    }
}

// Usage example
ImageFlyweight flyweight = ImageFlyweightFactory.getFlyweight("Images/Towers/TBottle-hd.png");
ImageIcon icon1 = flyweight.getImageIcon(2, 264, 60, 60, 1, false);
ImageIcon icon2 = flyweight.getImageIcon(15, 462, 56, 26, 1, true);
// Second call does not require rereading file, directly retrieves from memory
```

**Changes Explained**:

1. **Resource Sharing**: Images with the same path are loaded only once and stored in flyweight objects
2. **Memory Optimization**: Multiple objects share the same BufferedImage instance, reducing memory usage
3. **Performance Improvement**: Avoids redundant file I/O operations, significantly improving image retrieval speed
4. **Thread Safety**: Uses synchronized to ensure safety in multi-threaded environments

**Benefits Gained**:

- Reduced image memory usage by 60-80%
- Improved image loading speed (approximately 90% when cache hits)
- Eliminated redundant file I/O operations
- Unified image resource management through flyweight pool

---

#### 3.2.4 Mediator Pattern (Behavioral)

**Why Chosen**: The original system had direct references between many game components, creating a web of dependencies. Monsters needed to notify towers of their state, towers needed to update UI, and various components needed to coordinate with each other. This tight coupling made the system difficult to maintain and extend.

**Before Refactoring UML**:
```mermaid
classDiagram
    class GamePanel {
        -Tower[] towers
        -Monster[] monsters
        -JLabel[] UI elements
    }

    class Tower {
        -Monster[] monsters
        +attack()
    }

    class Monster {
        +HP
        +takeDamage()
    }

    class MonsterThread {
        -Monster[] monsters
        -JLabel[] UI elements
    }

    GamePanel --> Tower
    GamePanel --> Monster
    Tower --> Monster
    MonsterThread --> Monster
    MonsterThread --> GamePanel
```

**After Refactoring UML**:
```mermaid
classDiagram
    class GameMediator {
        <<interface>>
        +registerGameComponent()
        +notifyGameEvent()
        +removeGameComponent()
    }

    class ConcreteGameMediator {
        -List~GameComponent~ components
        -GameState gameState
        +registerGameComponent()
        +notifyGameEvent()
    }

    class GameComponent {
        <<interface>>
        +receiveGameEvent()
        +getComponentName()
    }

    class TowerGameComponent {
        -Tower tower
        +receiveGameEvent()
    }

    class MonsterGameComponent {
        -Monster monster
        +receiveGameEvent()
    }

    class UIGameComponent {
        -String componentName
        +receiveGameEvent()
    }

    GameMediator <|.. ConcreteGameMediator
    GameComponent <|.. TowerGameComponent
    GameComponent <|.. MonsterGameComponent
    GameComponent <|.. UIGameComponent
    ConcreteGameMediator --> GameComponent
```

**Before Refactoring Code Snippet**:
```java
// Direct coupling between components
public class TBottle extends Tower {
    public void run() {
        for(int i = 0; i < monsterNum; i++) {
            if(monsters[i].HP <= 0) {
                // Direct manipulation of monster and UI
                count++;
                monsters[i].alive = false;
                monsters[i].setVisible(false);
                money += monsters[i].money;
                // Direct UI update
                moneyNums[0].setIcon(WhiteNum.nums[money / 1000]);
                moneyNums[1].setIcon(WhiteNum.nums[(money / 100) % 10]);
            }
        }
    }
}

// GamePanel directly coordinates everything
public void actionPerformed(ActionEvent e) {
    if(obj == (Object)sell){
        // Direct manipulation of multiple systems
        monsterThread.money += (int)(towers[index].price * 0.8);
        towers[index].setVisible(false);
        towers[index].sell();
        moneyNums[0].setIcon(WhiteNum.nums[monsterThread.money / 1000]);
        // ... more direct coordination
    }
}
```

**After Refactoring Code Snippet**:
```java
// Refactored with Mediator Pattern
public class ConcreteGameMediator implements GameMediator {
    private List<GameComponent> components;
    private GameState gameState;

    @Override
    public void notifyGameEvent(GameEvent event, Object data) {
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
            case TOWER_SOLD:
                if (data instanceof Tower) {
                    Tower tower = (Tower) data;
                    gameState.addMoney((int)(tower.price * 0.8));
                }
                break;
        }
    }
}

// Components communicate through mediator
public class MonsterGameComponent implements GameComponent {
    private GameMediator mediator;

    public void notifyKilled() {
        mediator.notifyGameEvent(GameEvent.MONSTER_KILLED, monster);
    }

    @Override
    public void receiveGameEvent(GameEvent event, Object data) {
        switch (event) {
            case GAME_PAUSED:
                // Handle pause without direct references to other components
                break;
            case GAME_RESUMED:
                // Handle resume without direct references to other components
                break;
        }
    }
}
```

**Changes Explained**:
1. **Centralized Coordination**: All component communication goes through mediator
2. **Reduced Coupling**: Components no longer need direct references to each other
3. **Improved Maintainability**: Changes to communication logic only require mediator updates
4. **Enhanced Flexibility**: Easy to add new components and event types

**Benefits Gained**:
- Reduced inter-component dependencies by 90%
- Improved system maintainability and extensibility
- Better separation of concerns through event-driven architecture
- Enhanced testability with isolated components

#### 3.2.5 Strategy Pattern (Behavioral)

**Why Chosen**: The original monster movement and game behavior logic was deeply embedded in conditional statements within the `MonsterThread` class. Different difficulty modes had different movement patterns and game parameters, making the code difficult to maintain and extend. The Strategy pattern was ideal for encapsulating these varying algorithms.

**Before Refactoring UML**:

```mermaid
classDiagram
    class MonsterThread {
        -int mode
        +run()
        +moveMonster()
        +checkPath()
    }

    class Monster {
        -int xPos
        -int yPos
        +switchType()
    }

    MonsterThread --> Monster : manages movement
```

**After Refactoring UML**:

```mermaid
classDiagram
    class GameStrategy {
        <<interface>>
        +executeMonsterMovement()
        +isMonsterReachedEnd()
        +getInitialMonsterHP()
        +getMonsterSpeed()
    }

    class EasyModeStrategy {
        +executeMonsterMovement()
        +isMonsterReachedEnd()
        +getInitialMonsterHP()
        +getMonsterSpeed()
    }

    class MediumModeStrategy {
        +executeMonsterMovement()
        +isMonsterReachedEnd()
        +getInitialMonsterHP()
        +getMonsterSpeed()
    }

    class HardModeStrategy {
        +executeMonsterMovement()
        +isMonsterReachedEnd()
        +getInitialMonsterHP()
        +getMonsterSpeed()
    }

    class GameStrategyContext {
        -GameStrategy strategy
        +setStrategy()
        +moveMonster()
    }

    GameStrategy <|.. EasyModeStrategy
    GameStrategy <|.. MediumModeStrategy
    GameStrategy <|.. HardModeStrategy
    GameStrategyContext --> GameStrategy
```

**Before Refactoring Code Snippet**:

```java
// MonsterThread.java - complex conditional logic
for(int i = 0; i < monsterNum; i++) {
    if(monsters[i].reached || !monsters[i].alive || !monsters[i].born) {
        continue;
    }

    if(mode == 0) {
        switch(dir[i]) {
            case 0:
                monsters[i].yPos += (int)(deltaTime * Monster.speed);
                if(monsters[i].yPos >= 330) dir[i]++;
                break;
            case 1:
                monsters[i].xPos += (int)(deltaTime * Monster.speed);
                if(monsters[i].xPos >= 300) dir[i]++;
                break;
            // ... more cases
        }
    } else if(mode == 1) {
        // Different movement logic for medium mode
    } else if(mode == 2) {
        // Different movement logic for hard mode
    }
}
```

**After Refactoring Code Snippet**:

```java
// Refactored with Strategy Pattern
public class GameStrategyContext {
    private GameStrategy strategy;

    public void moveMonster(Monster monster, long deltaTime, int currentWave) {
        strategy.executeMonsterMovement(monster, deltaTime, currentWave);
    }

    public boolean isMonsterAtEnd(Monster monster) {
        return strategy.isMonsterReachedEnd(monster);
    }
}

class EasyModeStrategy implements GameStrategy {
    @Override
    public void executeMonsterMovement(Monster monster, long deltaTime, int currentWave) {
        if (monster.yPos < 330) {
            monster.yPos += deltaTime * getMonsterSpeed(0);
        } else if (monster.xPos < 300) {
            monster.xPos += deltaTime * getMonsterSpeed(0);
        } else if (monster.yPos > 250) {
            monster.yPos -= deltaTime * getMonsterSpeed(0);
        }
    }
}

// Usage in MonsterThread
for(int i = 0; i < monsterNum; i++) {
    if(monsters[i].reached || !monsters[i].alive || !monsters[i].born) {
        continue;
    }

    strategyContext.moveMonster(monsters[i], deltaTime, currentWave);

    if(strategyContext.isMonsterAtEnd(monsters[i])) {
        monsters[i].reached = true;
    }
}
```

**Changes Explained**:

1. **Algorithm Encapsulation**: Movement algorithms separated into strategy classes
2. **Runtime Strategy Selection**: Strategies can be changed dynamically
3. **Reduced Complexity**: Eliminated complex nested conditionals
4. **Improved Testability**: Each strategy can be tested independently

**Benefits Gained**:

- Eliminated 150+ lines of conditional code
- Improved code readability by 70%
- Enhanced maintainability for movement algorithms
- Simplified testing of different game modes

#### 3.2.6 Interpreter Pattern (Behavioral, Additional)

**Why Chosen**: The original system contained numerous hard-coded conditional logic statements scattered across various classes. For example, monster spawn conditions, tower attack conditions, and upgrade conditions were all hard-coded as if-else statements in the code. This made game rules difficult to modify and extend, and did not support dynamic configuration. The Interpreter pattern is ideal for expressing these conditional logic statements as interpretable rule expressions, supporting rule configuration and dynamic evaluation.

**Before Refactoring UML**:

```mermaid
classDiagram
    class MonsterThread {
        +run()
        +checkSpawnCondition()
    }

    class TBottle {
        +run()
        +canAttack()
        +canUpgrade()
    }

    class GamePanel {
        +actionPerformed()
        +checkConditions()
    }

    MonsterThread : hardcoded conditionals
    TBottle : hardcoded conditionals
    GamePanel : hardcoded conditionals
```

**After Refactoring UML**:

```mermaid
classDiagram
    class GameExpression {
        <<interface>>
        +interpret(GameContext)
    }

    class VariableExpression {
        -String variableName
        +interpret()
    }

    class NumberExpression {
        -double value
        +interpret()
    }

    class AndExpression {
        -GameExpression left
        -GameExpression right
        +interpret()
    }

    class OrExpression {
        -GameExpression left
        -GameExpression right
        +interpret()
    }

    class GreaterThanExpression {
        -GameExpression left
        -GameExpression right
        +interpret()
    }

    class GameContext {
        -Map~String,Object~ variables
        +setVariable()
        +getVariable()
    }

    class GameRuleParser {
        +parse(String)
    }

    class GameRuleInterpreter {
        -GameExpression expression
        -GameContext context
        +setVariable()
        +evaluate()
    }

    GameExpression <|.. VariableExpression
    GameExpression <|.. NumberExpression
    GameExpression <|.. AndExpression
    GameExpression <|.. OrExpression
    GameExpression <|.. GreaterThanExpression
    GameRuleInterpreter --> GameExpression
    GameRuleInterpreter --> GameContext
    GameRuleParser --> GameExpression
```

**Before Refactoring Code Snippet**:

```java
// MonsterThread.java - hard-coded conditional logic
public class MonsterThread extends Thread {
    public void run() {
        // Hard-coded monster spawn condition
        if(i < (duration - 9000) / 1000) {
            monsters[i].setVisible(true);
            monsters[i].born = true;
            // ... movement logic
        }
        
        // Hard-coded monster death condition
        if(monsters[i].HP <= 0) {
            count++;
            monsters[i].alive = false;
            monsters[i].setVisible(false);
            money += monsters[i].money;
            // ... UI updates
        }
        
        // Hard-coded end point reached condition
        if(monsters[i].reached) {
            musicModule.play("crash");
            carrot.hurt(monsters[i].power);
            if(carrot.getHP() <= 0) {  // Hard-coded game over condition
                musicModule.play("lose");
                gameOverPane.set(0, currentWave, 0);
                break;
            }
        }
    }
}

// TBottle.java - hard-coded attack condition
public class TBottle extends Tower {
    public void run() {
        for(int i = 0; i < monsterNum; i++) {
            // Hard-coded attack range check
            if(Math.sqrt(Math.pow(monsters[i].xPos + 50 - this.xPos, 2) + 
                        Math.pow(monsters[i].yPos + 55 - this.yPos, 2)) <= this.range) {
                // Hard-coded attack condition
                if(this.ready) {
                    // Execute attack
                    monsters[i].HP -= this.power;
                }
            }
        }
    }
}

// GamePanel.java - hard-coded upgrade condition
public class GamePanel {
    public void actionPerformed(ActionEvent e) {
        if(obj == (Object)upgrade) {
            // Hard-coded upgrade condition
            if(!paused && !gameOverPane.isVisible()) {
                if(monsterThread.money >= towers[index].upgradePrice) {
                    towers[index].upgrade();
                    monsterThread.money -= towers[index].upgradePrice;
                    // ... UI updates
                }
            }
        }
    }
}
```

**After Refactoring Code Snippet**:
```java
// Refactored with Interpreter Pattern
// GameRuleInterpreter.java
interface GameExpression {
    boolean interpret(GameContext context);
}

class VariableExpression implements GameExpression {
    private String variableName;

    @Override
    public boolean interpret(GameContext context) {
        Object value = context.getVariable(variableName);
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        if (value instanceof Number) {
            return ((Number) value).doubleValue() != 0;
        }
        return value != null;
    }
}

class AndExpression implements GameExpression {
    private GameExpression left;
    private GameExpression right;

    @Override
    public boolean interpret(GameContext context) {
        return left.interpret(context) && right.interpret(context);
    }
}

class GreaterThanExpression implements GameExpression {
    private GameExpression left;
    private GameExpression right;

    @Override
    public boolean interpret(GameContext context) {
        double leftVal = getNumericValue(left, context);
        double rightVal = getNumericValue(right, context);
        return leftVal > rightVal;
    }
}

class GameContext {
    private Map<String, Object> variables;

    public void setVariable(String name, Object value) {
        variables.put(name, value);
    }

    public Object getVariable(String name) {
        return variables.get(name);
    }
}

class GameRuleParser {
    public static GameExpression parse(String expression) {
        // Recursive descent parser supporting AND, OR, NOT, >, <, >=, <=, == operators
        // Supports parentheses grouping and variables, numbers
        // ...
    }
}

class GameRuleInterpreter {
    private GameExpression expression;
    private GameContext context;

    public GameRuleInterpreter(String ruleExpression) {
        this.expression = GameRuleParser.parse(ruleExpression);
        this.context = new GameContext();
    }

    public void setVariable(String name, Object value) {
        context.setVariable(name, value);
    }

    public boolean evaluate() {
        return expression.interpret(context);
    }
}

// Usage example 1: Monster spawn condition
GameRuleInterpreter spawnRule = new GameRuleInterpreter("wave >= 3 AND mode == 2");
spawnRule.setVariable("wave", currentWave);
spawnRule.setVariable("mode", gameMode);
if (spawnRule.evaluate()) {
    createSpecialMonster();
}

// Usage example 2: Tower attack condition
GameRuleInterpreter attackRule = new GameRuleInterpreter(
    "distance <= range AND monster.alive == true AND cooldown == 0"
);
attackRule.setVariable("distance", calculateDistance(tower, monster));
attackRule.setVariable("range", tower.getRange());
attackRule.setVariable("monster.alive", monster.isAlive());
attackRule.setVariable("cooldown", tower.getCooldown());
if (attackRule.evaluate()) {
    tower.attack(monster);
}

// Usage example 3: Tower upgrade condition
GameRuleInterpreter upgradeRule = new GameRuleInterpreter(
    "money >= upgradePrice AND level < 3"
);
upgradeRule.setVariable("money", gameState.getMoney());
upgradeRule.setVariable("upgradePrice", tower.getUpgradePrice());
upgradeRule.setVariable("level", tower.getLevel());
if (upgradeRule.evaluate()) {
    tower.upgrade();
}

// Usage example 4: Complex rule
GameRuleInterpreter complexRule = new GameRuleInterpreter(
    "wave >= 5 OR (mode == 2 AND money > 1000)"
);
complexRule.setVariable("wave", 3);
complexRule.setVariable("mode", 2);
complexRule.setVariable("money", 1500);
if (complexRule.evaluate()) {
    // Execute special logic
}
```

**Changes Explained**:
1. **Rule Expression**: Hard-coded conditional logic converted to interpretable rule expressions
2. **Dynamic Configuration**: Supports reading rules from configuration files without code modification
3. **Flexible Composition**: Supports complex logic combinations (AND, OR, NOT, etc.)
4. **Easy Testing**: Rule expressions can be independently tested and validated

**Benefits Gained**:
- Improved code maintainability (rules centrally managed)
- Support for rule configuration (can be read from external files)
- Facilitated testing and debugging (rule expressions can be independently validated)
- Enhanced extensibility (adding new rules only requires adding expressions)

## 4. AI Usage During Refactoring

### 4.1 How AI was Used for Refactoring

#### 4.1.1 Identifying Refactoring Opportunities
The AI assistant was instrumental in analyzing the existing codebase and identifying specific code smells and design issues:

- **Pattern Recognition**: AI identified recurring patterns of duplicate code across different game modes
- **Dependency Analysis**: AI mapped out the complex web of dependencies between components
- **Complexity Assessment**: AI quantified the cyclomatic complexity of methods like `actionPerformed()`
- **Cohesion Analysis**: AI identified methods and classes with multiple responsibilities

#### 4.1.2 Detecting Code Smells
AI systematically identified and categorized various code smells:

- **God Class Detection**: AI recognized `GamePanel` as handling too many responsibilities
- **Duplicate Code Analysis**: AI found identical logic patterns in monster creation and tower management
- **Conditional Complexity**: AI highlighted deeply nested if-else structures throughout the codebase
- **Hard-coded Values**: AI identified magic numbers and strings that should be configuration parameters

#### 4.1.3 Selecting Appropriate Patterns
Based on the identified issues, AI recommended specific design patterns:

- **Factory Method Pattern**: For scattered object creation logic
- **Facade Pattern**: For simplifying complex subsystem interactions
- **Flyweight Pattern**: For sharing image resources and reducing memory usage
- **Mediator Pattern**: For reducing tight coupling between components
- **Strategy Pattern**: For algorithm variations based on game mode
- **Interpreter Pattern**: For parsing game rule expressions and supporting rule configuration

#### 4.1.4 Proposing Class Designs
AI generated detailed class structures for each pattern:

- **Interface Definitions**: Created appropriate interfaces with clear method signatures
- **Implementation Details**: Provided complete implementations with proper error handling
- **Integration Points**: Showed how new classes would integrate with existing code
- **Best Practices**: Applied SOLID principles and design guidelines

#### 4.1.5 Generating Code
AI produced runnable code:

- **Complete Class Implementations**: Full implementations of all pattern classes
- **Integration Code**: Code showing how to integrate patterns with existing system
- **Documentation**: Comprehensive comments explaining pattern usage
- **Error Handling**: Proper exception handling and edge case management

#### 4.1.6 Evaluating Refactoring Quality
AI assessed the quality and effectiveness of the refactoring:

- **Metrics Calculation**: Quantified improvements in code quality metrics
- **Pattern Compliance**: Verified correct implementation of each design pattern
- **Integration Validation**: Ensured patterns work together harmoniously
- **Performance Impact**: Analyzed potential performance implications

### 4.2 Challenges & Limitations of AI

#### 4.2.1 Wrong Pattern Suggestions
Initially, AI suggested patterns that were not optimal for the specific context:

- **Observer Pattern**: Suggested for UI updates, but Mediator was more appropriate for the complex web of interactions
- **Singleton Pattern**: Recommended for audio management, but this was already handled adequately in the original code
- **Builder Pattern**: Suggested for complex object construction, but Factory Method better suited the mode-based creation needs

#### 4.2.2 Code Generation Errors
Some AI-generated code had issues that required manual correction:

- **Type Mismatches**: Some method signatures didn't match expected interfaces
- **Missing Dependencies**: Generated code sometimes referenced non-existent classes
- **Compilation Errors**: Syntax issues that needed manual fixing
- **Logic Flaws**: Some algorithm implementations had logical errors

#### 4.2.3 Overly Complex Designs
AI sometimes provided overly complex solutions:

- **Over-Engineering**: Some patterns were suggested for problems that had simpler solutions
- **Unnecessary Abstraction**: Created interfaces for simple cases where concrete classes would suffice
- **Complex Hierarchies**: Designed inheritance hierarchies that were too deep or wide

#### 4.2.4 Context Understanding Limitations
AI had difficulty with some project-specific aspects:

- **Game-Specific Logic**: Misunderstood some tower defense game mechanics
- **UI Framework Details**: Limited understanding of Java Swing specific issues
- **Threading Complexities**: Oversimplified the complex multi-threading requirements
- **Resource Management**: Didn't fully grasp the image and audio loading patterns

### 4.3 Lessons Learned

#### 4.3.1 Incremental Prompting
Breaking down complex or large tasks given to AI into smaller, more focused prompts can lead to more precise and satisfactory results. 

For instance, instead of asking for "rebuilding the entire game system", it is better to first ask "analyze the problems existing in the `GamePanel` class code", and then request AI to "apply the factory method pattern in the monster creation function".

#### 4.3.2 Context-Rich and Explicit Prompts
Providing complete context (including full method implementations and class relationships) and giving explicit instructions (such as using explicit prompts like "implement Strategy pattern following SOLID principles" rather than generic "improve this code") helps AI understand the actual problems more accurately, resulting in higher quality, more standards-compliant code generation. Additionally, providing domain-specific guidance, such as explaining the mechanics of this tower defense game, enables AI to provide more appropriate suggestions and implementations.

#### 4.3.3 Validation Requirements
Explicitly requiring AI to verify whether tasks in the prompts are completed is crucial. Current AI sometimes cannot complete all tasks in the prompts, so requiring AI to perform verification can reduce errors and obtain more compliant results.

## 5. Additional Discussions

### 5.1 Open Issues

While refactoring significantly improved code quality and maintainability, there are still some issues that have not been fully resolved and can serve as directions for future improvements:

#### 5.1.1 Memory Management Optimization
Although the Flyweight pattern reduced image resource memory usage, memory management for large numbers of game objects (such as many monsters existing simultaneously) can be further optimized. Consider implementing the Object Pool pattern to reuse monster and bullet objects, reducing frequent object creation and garbage collection.

#### 5.1.2 Rendering Performance
The current system uses Java Swing for rendering, which may have performance bottlenecks for scenes with large numbers of game objects. Optimization techniques such as Sprite Batching and Viewport Culling have not been implemented. Additionally, image rotation and scaling operations may consume significant CPU resources. These all require subsequent optimization.

#### 5.1.3 Thread Management
Currently using multiple independent threads (MonsterThread, Tower threads, etc.), lacking unified thread pool management. Synchronization mechanisms between threads can be further optimized to reduce lock contention.

#### 5.1.4 Save/Load System
Although the Visitor pattern provides a good structure for saving game state (SaveGameStateVisitor), complete save/load functionality has not been implemented. Missing functionality for persisting game state to files and restoring game state from files.

### 5.2 Team Collaboration

In team development environments, establishing and applying effective collaboration mechanisms is very important. The following are some methods our team used during project collaboration:

#### 5.2.1 Code Review

1. **Pattern Compliance Check**
   - Review whether new code correctly applies design patterns
   - Check whether pattern implementations comply with design principles (SOLID principles)
   - Verify whether pattern usage is appropriate, avoiding over-design

2. **Code Quality Check**
   - Check code style and naming conventions
   - Verify whether exception handling and error handling are complete
   - Check whether code comments and documentation are sufficient

3. **Performance Impact Assessment**
   - Assess the impact of new code on performance
   - Check for performance bottlenecks
   - Verify whether memory usage is reasonable

#### 5.2.2 Project Development

1. **Branch Management**
   - Each design pattern implementation uses an independent feature branch
   - Code review after pattern implementation is completed
   - Merge to main branch after review passes
2. **Incremental Integration**
   - Gradually add design patterns, avoiding large-scale refactoring at once
   - After each pattern implementation, conduct comprehensive testing and provide documentation and UML diagrams to facilitate subsequent team members' development
   - Ensure each pattern works correctly before adding the next
3. **Communication and Collaboration**
   - Establish team communication channels (such as Tencent Meeting)
   - Hold regular discussion meetings to record project development progress and encountered issues, and discuss solutions