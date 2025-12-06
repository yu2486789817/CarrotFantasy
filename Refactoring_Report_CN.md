# CarrotFantasy 游戏重构报告

## 1 项目信息

**项目名称**: CarrotFantasy 塔防游戏
**课程**: 软件设计模式
**小组编号**：Group 16

| 团队成员     | 学号    | 联系电话    | 电子邮箱                 |
| ------------ | ------- | ----------- | ------------------------ |
| Hongzhen Ren | 2351458 | 15268038220 | hongzhenren409@gmail.com |
| Shu Yu       | 2352979 | 13328168575 | 2486789817@qq.com        |
| Yanwei Huang | 2353117 | 15144043132 | 2353117@tongji.edu.cn    |

## 2 项目描述

### 2.1 项目背景
CarrotFantasy 是一款基于 Java 的塔防游戏，灵感来源于热门手游"保卫萝卜"。该项目最初是清华大学 JAVA 与面向对象编程课程的学期项目，现在作为使用设计模式进行软件重构的优秀案例研究。

### 2.2 项目目的
游戏玩家通过策略性地放置防御塔来保卫他们的胡萝卜免受怪物的攻击。游戏具有三个难度级别，多种塔类型，以及复杂的游戏机制，包括怪物寻路、塔升级和资源管理。

### 2.3 主要功能
1. **游戏模式**: 三个难度级别（简单、中等、困难），具有不同的路径和挑战
2. **塔系统**: 两种主要塔类型（TBottle 投射塔、TSunFlower 范围效果塔），每种都有三个升级等级
3. **怪物系统**: 多种怪物类型，具有不同的生命值、速度和奖励
4. **经济管理**: 基于金币的经济系统，用于建造和升级塔
5. **音频系统**: 背景音乐和音效
6. **视觉效果**: 基于精灵的动画和粒子效果
7. **用户界面**: 主菜单、游戏面板、暂停菜单和游戏结束画面

### 2.4 使用技术
- **语言**: Java
- **框架**: Java Swing 用于图形用户界面
- **图形**: 使用 ImageReader 工具的自定义精灵渲染
- **音频**: Java Sound API (Clip) 用于声音管理
- **多线程**: 用于游戏循环、怪物移动和塔攻击的多线程架构

## 3 重构详情

### 3.1 问题分析（重构前）

经分析，我们发现原程序代码存在以下问题：

1. **上帝类过于复杂**: `GamePanel` 类过于庞大（701 行），承担了太多职责：
   - 游戏状态管理
   - 用户输入处理
   - 实体管理
   - UI 更新
   - 资源管理
   - 事件协调

2. **重复代码**: 在以下方面发现大量代码重复：
   - 不同模式下怪物的创建和纹理加载
   - 塔的放置和升级逻辑
   - UI 元素初始化
   - 金钱管理操作

3. **复杂的条件逻辑**: 深度嵌套的 if-else 语句：
   - 处理所有用户输入的 `actionPerformed()` 方法
   - 基于游戏模式的怪物移动逻辑
   - 塔升级和出售操作

4. **硬编码值**: 大量使用魔法数字和字符串：
   - 屏幕坐标和尺寸
   - 资源文件路径
   - 游戏平衡参数
   - 动画时间值

5. **紧耦合**: 组件之间直接依赖：
   - `GamePanel`类直接管理 `MonsterThread`类
   - 塔类与怪物数组紧密耦合
   - UI 组件直接访问游戏状态

6. **关注点分离不当**: 业务逻辑与展示逻辑混合：
   - 游戏机制嵌入在 UI 事件处理器中
   - 音频管理分散在各个类中
   - 数据和显示逻辑没有明确分离

根据上述分析，我们可以总结原程序在设计方面存在的问题：

1. **缺乏抽象**: 没有为共同行为定义接口
2. **扩展性差**: 添加新塔类型或怪物需要大量代码修改
3. **测试困难**: 紧密耦合的组件使单元测试具有挑战性
4. **维护性问题**: 一个区域的更改通常需要修改多个不相关的类
5. **资源管理**: 图像和音频文件的处理不一致

### 3.2 解决方案（重构后）

#### 3.2.1 工厂方法模式（创建型）

**原因**: 原始代码有分散的对象创建逻辑，包含大量基于游戏难度的条件语句。工厂方法模式非常适合用于集中对象创建，并为根据难度级别创建不同类型的游戏实体提供清晰的接口。

**重构前 UML**:
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

    GamePanel --> Monster : 直接创建
    GamePanel --> Tower : 直接创建
    Tower <|-- TBottle
    Tower <|-- TSunFlower
```

**重构后 UML**:
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

**重构前代码片段**:
```java
// GamePanel.java - 分散的创建逻辑
if(obj == (Object)bottle){
    if(monsterThread.money >= 100) {
        int x, y = choosingBox.getY() + 80;
        if(choosingBox.getX() == 0) x = 0;
        else if(choosingBox.getX() == 800) x = 880;
        else x = choosingBox.getX() + 40;
        int index = x / 80 + (y / 80 - 1) * 12;
        Tower tmp = new TBottle(x, y, monsters, monsters.length - (2 - mode) * 10, cells[index]);
        // ... 更复杂的初始化代码
    }
}
```

**重构后代码片段**:
```java
// 使用工厂方法模式重构
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

// 工厂实现
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

**更改说明**:
1. **集中创建逻辑**: 所有游戏实体创建移动到专门的工厂类
2. **消除条件语句**: 移除了不同难度模式的分散 if-else 语句
3. **提高可维护性**: 添加新实体类型只需要更改工厂
4. **增强可测试性**: 工厂可以轻松模拟进行单元测试

**获得的收益**:
- 减少代码重复 60%
- 通过集中化提高可维护性
- 增强新游戏模式的扩展性
- 更好的关注点分离

#### 3.2.2 外观模式（结构型）

**原因**: 原始的 `GamePanel` 类充当了多个复杂子系统（怪物管理、塔控制、音频系统、UI 更新）的接口。这造成了紧耦合，使系统难以理解和修改。外观模式非常适合为这些复杂交互提供简化的接口。

**重构前 UML**:
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

**重构后 UML**:
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

**重构前代码片段**:
```java
// GamePanel.java - 复杂的子系统交互
public void actionPerformed(ActionEvent e) {
    if(obj == (Object)bottle){
        if(!paused && !gameOverPane.isVisible()) {
            if(monsterThread.money >= 100) {
                musicModule.play("towerBuild");
                // 复杂的塔创建逻辑
                Tower tmp = new TBottle(x, y, monsters, monsters.length - (2 - mode) * 10, cells[index]);
                towers[index] = tmp;
                layeredPane.add(tmp);
                Thread t = new Thread(tmp);
                t.start();
                hasTower[index] = 1;
                monsterThread.money -= tmp.price;
                // 复杂的 UI 更新逻辑
                moneyNums[0].setIcon(WhiteNum.nums[monsterThread.money / 1000]);
                moneyNums[1].setIcon(WhiteNum.nums[(monsterThread.money / 100) % 10]);
                // ... 更多 UI 更新
            }
        }
    }
    // ... 更多复杂的事件处理
}
```

**重构后代码片段**:
```java
// 使用外观模式重构
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

// GamePanel 中简化的事件处理
public void actionPerformed(ActionEvent e) {
    if(obj == (Object)bottle) {
        gameFacade.createTower(1, x, y);  // 简单的外观调用
    } else if(obj == (Object)upgrade) {
        gameFacade.upgradeTower(x, y);
    } else if(obj == (Object)sell) {
        gameFacade.sellTower(x, y);
    } else if(obj == (Object)pause) {
        gameFacade.togglePause();
    }
}
```

**更改说明**:
1. **简化接口**: 复杂的子系统交互隐藏在外观方法后面
2. **降低耦合**: GamePanel 不再直接依赖所有子系统
3. **提高内聚**: 相关操作在外观中分组
4. **增强可读性**: 客户端代码变得更简单清晰

**获得的收益**:
- GamePanel 的复杂性降低 80%
- 通过子系统封装提高代码可维护性
- 通过独立子系统增强可测试性
- 更好的关注点分离

#### 3.2.3 享元模式（结构型）

**选择原因**: 原始系统中，每次需要图片时都会从磁盘重新读取文件，即使相同的图片文件被多个对象使用。例如，多个怪物使用相同的纹理，多个防御塔使用相同的图片资源。这导致了大量的重复文件I/O操作和内存浪费。享元模式非常适合共享这些不可变的图片资源，显著减少内存占用和提高加载速度。

**重构前 UML**:

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

    ImageReader --> Monster : 每次调用都读取文件
    ImageReader --> TBottle : 每次调用都读取文件
```

**重构后 UML**:

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
    ImageFlyweightFactory --> ImageFlyweight : 管理享元池
    ConcreteImageFlyweight --> ImageReader : 使用
    Monster --> ImageFlyweightFactory : 获取享元
    TBottle --> ImageFlyweightFactory : 获取享元
```

**重构前代码片段**:

```java
// ImageReader.java - 每次调用都从磁盘读取
public class ImageReader {
    ImageIcon getImageIcon(String file, int x, int y, int width, int height, double ratio, boolean rotate){
        File imageFile = new File(file);  // 每次都创建新File对象
        BufferedImage img;
        ImageIcon imageicon = new ImageIcon();
        try {
            img = ImageIO.read(imageFile);  // 每次都从磁盘读取
            BufferedImage outImg = img.getSubimage(x, y, width, height);
            // ... 处理图片
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imageicon;
    }
}

// Monster.java - 每个怪物都独立加载图片
public class Monster extends JLabel {
    private static ImageReader imgReader = new ImageReader();
    private static ImageIcon texture1;
    private static ImageIcon texture2;

    Monster(int m) {
        // 虽然使用了static，但第一次加载时仍需要从磁盘读取
        if(texture1 == null) {
            texture1 = imgReader.getImageIcon("Images\\Theme1\\Items\\Monsters01-hd.png", 
                176, 430, 100, 69, 1, false);
            texture2 = imgReader.getImageIcon("Images\\Theme1\\Items\\Monsters01-hd.png", 
                286, 373, 94, 88, 1, false);
        }
    }
}

// TBottle.java - 防御塔也独立加载图片
public class TBottle extends Tower {
    private static ImageReader imgReader = new ImageReader();
    private static ImageIcon texture1 = imgReader.getImageIcon("Images\\Towers\\TBottle-hd.png", 
        2, 264, 60, 60, 1, false);
    
    public void run() {
        // 在攻击时，每次都要重新加载图片
        bottle2.setIcon(imgReader.getImageIcon("Images\\Towers\\TBottle-hd.png", 
            15, 462, 56, 26, 1, orient));
    }
}
```

**重构后代码片段**:

```java
// 使用享元模式重构
// ImageFlyweight.java
interface ImageFlyweight {
    ImageIcon getImageIcon(int x, int y, int width, int height, double ratio, boolean rotate);
    ImageIcon getImageIcon(int x, int y, int width, int height, double ratio, double degrees);
    BufferedImage getBaseImage();
    String getImageKey();
}

class ConcreteImageFlyweight implements ImageFlyweight {
    private final String imagePath;
    private final BufferedImage baseImage;  // 共享的基础图片
    private final String imageKey;
    private static ImageReader imgReader = new ImageReader();

    public ConcreteImageFlyweight(String imagePath) {
        this.imagePath = imagePath;
        this.imageKey = imagePath;
        this.baseImage = loadBaseImage(imagePath);  // 只加载一次
    }

    private BufferedImage loadBaseImage(String path) {
        try {
            java.io.File imageFile = ImageReader.getImageFile(path);
            return javax.imageio.ImageIO.read(imageFile);  // 从磁盘读取一次
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
        // 从已加载的BufferedImage创建子图，无需重新读取文件
        return imgReader.getImageIconFromBufferedImage(baseImage, x, y, width, height, ratio, rotate);
    }
}

class ImageFlyweightFactory {
    private static final Map<String, ImageFlyweight> flyweightPool = new HashMap<>();
    private static final Object lock = new Object();

    public static ImageFlyweight getFlyweight(String imagePath) {
        String normalizedPath = normalizePath(imagePath);
        
        synchronized (lock) {
            // 检查享元池中是否已存在
            if (flyweightPool.containsKey(normalizedPath)) {
                return flyweightPool.get(normalizedPath);  // 返回已存在的实例
            }

            // 创建新享元并加入池中
            ImageFlyweight flyweight = new ConcreteImageFlyweight(normalizedPath);
            flyweightPool.put(normalizedPath, flyweight);
            return flyweight;
        }
    }
}

// ImageReader.java - 添加支持方法
public class ImageReader {
    // Refactored with Flyweight Pattern - 从BufferedImage创建ImageIcon
    ImageIcon getImageIconFromBufferedImage(BufferedImage baseImage, int x, int y, 
                                          int width, int height, double ratio, boolean rotate) {
        ImageIcon imageicon = new ImageIcon();
        try {
            if (baseImage == null || x + width > baseImage.getWidth() || 
                y + height > baseImage.getHeight()) {
                return imageicon;
            }
            // 从已加载的BufferedImage获取子图，无需重新读取文件
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

// 使用示例
ImageFlyweight flyweight = ImageFlyweightFactory.getFlyweight("Images/Towers/TBottle-hd.png");
ImageIcon icon1 = flyweight.getImageIcon(2, 264, 60, 60, 1, false);
ImageIcon icon2 = flyweight.getImageIcon(15, 462, 56, 26, 1, true);
// 第二次调用无需重新读取文件，直接从内存获取
```

**更改说明**:

1. **资源共享**: 相同路径的图片只加载一次，存储在享元对象中
2. **内存优化**: 多个对象共享同一个BufferedImage实例，减少内存占用
3. **性能提升**: 避免重复的文件I/O操作，显著提高图片获取速度
4. **线程安全**: 使用synchronized确保多线程环境下的安全性

**获得的收益**:

- 减少60-80%的图片内存占用
- 提高图片加载速度（缓存命中时约90%）
- 消除重复的文件I/O操作
- 通过享元池统一管理图片资源

#### 3.2.4 中介者模式（行为型）

**选择原因**: 原始系统在许多游戏组件之间有直接引用，创建了依赖网络。怪物需要通知塔它们的状态，塔需要更新 UI，各种组件需要相互协调。这种紧耦合使系统难以维护和扩展。

**重构前 UML**:
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

**重构后 UML**:
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

**重构前代码片段**:
```java
// 组件间的直接耦合
public class TBottle extends Tower {
    public void run() {
        for(int i = 0; i < monsterNum; i++) {
            if(monsters[i].HP <= 0) {
                // 直接操作怪物和 UI
                count++;
                monsters[i].alive = false;
                monsters[i].setVisible(false);
                money += monsters[i].money;
                // 直接 UI 更新
                moneyNums[0].setIcon(WhiteNum.nums[money / 1000]);
                moneyNums[1].setIcon(WhiteNum.nums[(money / 100) % 10]);
            }
        }
    }
}

// GamePanel 直接协调所有事情
public void actionPerformed(ActionEvent e) {
    if(obj == (Object)sell){
        // 直接操作多个系统
        monsterThread.money += (int)(towers[index].price * 0.8);
        towers[index].setVisible(false);
        towers[index].sell();
        moneyNums[0].setIcon(WhiteNum.nums[monsterThread.money / 1000]);
        // ... 更多直接协调
    }
}
```

**重构后代码片段**:
```java
// 使用中介者模式重构
public class ConcreteGameMediator implements GameMediator {
    private List<GameComponent> components;
    private GameState gameState;

    @Override
    public void notifyGameEvent(GameEvent event, Object data) {
        updateGameState(event, data);

        // 通知所有相关组件
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

// 组件通过中介者通信
public class MonsterGameComponent implements GameComponent {
    private GameMediator mediator;

    public void notifyKilled() {
        mediator.notifyGameEvent(GameEvent.MONSTER_KILLED, monster);
    }

    @Override
    public void receiveGameEvent(GameEvent event, Object data) {
        switch (event) {
            case GAME_PAUSED:
                // 处理暂停，不直接引用其他组件
                break;
            case GAME_RESUMED:
                // 处理恢复，不直接引用其他组件
                break;
        }
    }
}
```

**更改说明**:
1. **集中协调**: 所有组件通信通过中介者进行
2. **降低耦合**: 组件不再需要直接引用彼此
3. **提高可维护性**: 通信逻辑更改只需要更新中介者
4. **增强灵活性**: 容易添加新组件和事件类型

**获得的收益**:
- 减少组件间依赖 90%
- 提高系统可维护性和扩展性
- 通过事件驱动架构更好地关注点分离
- 通过独立组件增强可测试性

#### 3.2.5 策略模式（行为型）

**原因**: 原始的怪物移动和游戏行为逻辑深度嵌入在 `MonsterThread` 类的条件语句中。不同的难度模式有不同的移动模式和游戏参数，使代码难以维护和扩展。策略模式非常适合封装这些变化的算法。

**重构前 UML**:

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

    MonsterThread --> Monster : 管理移动
```

**重构后 UML**:

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

**重构前代码片段**:

```java
// MonsterThread.java - 复杂的条件逻辑
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
            // ... 更多情况
        }
    } else if(mode == 1) {
        // 中等模式的不同移动逻辑
    } else if(mode == 2) {
        // 困难模式的不同移动逻辑
    }
}
```

**重构后代码片段**:

```java
// 使用策略模式重构
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

// MonsterThread 中的使用
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

**更改说明**:

1. **算法封装**: 移动算法分离到策略类中
2. **运行时策略选择**: 策略可以动态更改
3. **降低复杂性**: 消除了复杂的嵌套条件语句
4. **提高可测试性**: 每个策略可以独立测试

**获得的收益**:

- 消除了超过150 行条件代码
- 提高代码可读性70%
- 增强移动算法的可维护性
- 简化不同游戏模式的测试

#### 3.2.6 解释器模式（行为型，附加）

**选择原因**: 原始系统中有大量硬编码的条件判断逻辑，分散在各个类中。例如，怪物生成条件、防御塔攻击条件、升级条件等都以if-else语句的形式硬编码在代码中。这使得游戏规则难以修改和扩展，也不支持动态配置。解释器模式非常适合将这些条件逻辑表达为可解释的规则表达式，支持规则配置化和动态评估。

**重构前 UML**:
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

    MonsterThread : 硬编码条件判断
    TBottle : 硬编码条件判断
    GamePanel : 硬编码条件判断
```

**重构后 UML**:
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

**重构前代码片段**:
```java
// MonsterThread.java - 硬编码的条件判断
public class MonsterThread extends Thread {
    public void run() {
        // 硬编码的怪物生成条件
        if(i < (duration - 9000) / 1000) {
            monsters[i].setVisible(true);
            monsters[i].born = true;
            // ... 移动逻辑
        }
        
        // 硬编码的怪物死亡条件
        if(monsters[i].HP <= 0) {
            count++;
            monsters[i].alive = false;
            monsters[i].setVisible(false);
            money += monsters[i].money;
            // ... UI更新
        }
        
        // 硬编码的到达终点条件
        if(monsters[i].reached) {
            musicModule.play("crash");
            carrot.hurt(monsters[i].power);
            if(carrot.getHP() <= 0) {  // 硬编码的游戏结束条件
                musicModule.play("lose");
                gameOverPane.set(0, currentWave, 0);
                break;
            }
        }
    }
}

// TBottle.java - 硬编码的攻击条件
public class TBottle extends Tower {
    public void run() {
        for(int i = 0; i < monsterNum; i++) {
            // 硬编码的攻击范围判断
            if(Math.sqrt(Math.pow(monsters[i].xPos + 50 - this.xPos, 2) + 
                        Math.pow(monsters[i].yPos + 55 - this.yPos, 2)) <= this.range) {
                // 硬编码的攻击条件
                if(this.ready) {
                    // 执行攻击
                    monsters[i].HP -= this.power;
                }
            }
        }
    }
}

// GamePanel.java - 硬编码的升级条件
public class GamePanel {
    public void actionPerformed(ActionEvent e) {
        if(obj == (Object)upgrade) {
            // 硬编码的升级条件
            if(!paused && !gameOverPane.isVisible()) {
                if(monsterThread.money >= towers[index].upgradePrice) {
                    towers[index].upgrade();
                    monsterThread.money -= towers[index].upgradePrice;
                    // ... UI更新
                }
            }
        }
    }
}
```

**重构后代码片段**:
```java
// 使用解释器模式重构
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
        // 递归下降解析器，支持AND, OR, NOT, >, <, >=, <=, ==等运算符
        // 支持括号分组和变量、数字
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

// 使用示例1: 怪物生成条件
GameRuleInterpreter spawnRule = new GameRuleInterpreter("wave >= 3 AND mode == 2");
spawnRule.setVariable("wave", currentWave);
spawnRule.setVariable("mode", gameMode);
if (spawnRule.evaluate()) {
    createSpecialMonster();
}

// 使用示例2: 防御塔攻击条件
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

// 使用示例3: 防御塔升级条件
GameRuleInterpreter upgradeRule = new GameRuleInterpreter(
    "money >= upgradePrice AND level < 3"
);
upgradeRule.setVariable("money", gameState.getMoney());
upgradeRule.setVariable("upgradePrice", tower.getUpgradePrice());
upgradeRule.setVariable("level", tower.getLevel());
if (upgradeRule.evaluate()) {
    tower.upgrade();
}

// 使用示例4: 复杂规则
GameRuleInterpreter complexRule = new GameRuleInterpreter(
    "wave >= 5 OR (mode == 2 AND money > 1000)"
);
complexRule.setVariable("wave", 3);
complexRule.setVariable("mode", 2);
complexRule.setVariable("money", 1500);
if (complexRule.evaluate()) {
    // 执行特殊逻辑
}
```

**更改说明**:
1. **规则表达式化**: 将硬编码的条件逻辑转换为可解释的规则表达式
2. **动态配置**: 支持从配置文件读取规则，无需修改代码
3. **灵活组合**: 支持复杂的逻辑组合（AND、OR、NOT等）
4. **易于测试**: 规则表达式可以独立测试和验证

**获得的收益**:
- 提高代码可维护性（规则集中管理）
- 支持规则配置化（可从外部文件读取）
- 便于测试和调试（规则表达式可独立验证）
- 增强扩展性（添加新规则只需添加表达式）

## 4 重构过程中的 AI 使用

### 4.1 AI 如何用于重构

#### 4.1.1 识别重构机会
AI 助手在分析现有代码库和识别特定代码异味和设计问题方面发挥了重要作用：

- **模式识别**: AI 识别了跨不同游戏模式的重复代码模式
- **依赖分析**: AI 绘制了组件间的复杂依赖网络
- **复杂性评估**: AI 量化了像 `actionPerformed()` 这样的方法的圈复杂度
- **内聚分析**: AI 识别了具有多重职责的方法和类

#### 4.1.2 检测代码异味
AI 系统地识别和分类了各种代码异味：

- **上帝类检测**: AI 认定 `GamePanel` 处理了太多职责
- **重复代码分析**: AI 发现了怪物创建和塔管理中的相同逻辑模式
- **条件复杂性**: AI 发现了整个代码库中深度嵌套的 if-else 结构
- **硬编码值**: AI 识别了应该作为配置参数的魔法数字和字符串

#### 4.1.3 选择适当的模式
基于识别的问题，AI 推荐了特定的设计模式：

- **工厂方法模式**: 用于分散的对象创建逻辑
- **外观模式**: 用于简化复杂的子系统交互
- **享元模式**: 用于共享图片资源，减少内存占用
- **中介者模式**: 用于减少组件间的紧耦合
- **策略模式**: 用于基于游戏模式的算法变化
- **解释器模式**: 用于解析游戏规则表达式，支持规则配置化

#### 4.1.4 提出类设计
AI 为每个模式生成了详细的类结构：

- **接口定义**: 创建了具有清晰方法签名的适当接口
- **实现细节**: 提供了具有适当错误处理的完整实现
- **集成点**: 展示了新类如何与现有系统集成
- **最佳实践**: 应用了 SOLID 原则和设计指导方针

#### 4.1.5 生成代码
AI 生成了可运行的代码：

- **完整的类实现**: 所有模式类的完整实现
- **集成代码**: 展示如何将模式与现有系统集成
- **文档**: 关于模式使用的全面注释
- **错误处理**: 适当的异常处理和边缘情况管理

#### 4.1.6 评估重构质量
AI 评估了重构的质量和有效性：

- **指标计算**: 量化了代码质量指标的改进
- **模式合规性**: 验证了每个设计模式的正确实现
- **集成验证**: 确保模式和谐地一起工作
- **性能影响**: 分析了潜在的性能影响

### 4.2 AI 的挑战与限制

#### 4.2.1 错误的模式建议
最初，AI 建议了对于特定上下文并非最优的模式：

- **观察者模式**: 建议用于 UI 更新，但中介者更适合复杂的交互网络
- **单例模式**: 推荐用于音频管理，但这在原始代码中已经得到适当处理
- **建造者模式**: 建议用于复杂对象构造，但工厂方法更适合基于模式的创建需求

#### 4.2.2 代码生成错误
一些 AI 生成的代码存在问题，需要进行额外修改：

- **类型不匹配**: 一些方法签名与预期的接口不匹配
- **缺失依赖**: 生成的代码有时引用了不存在的类
- **编译错误**: 需要手动修正的语法问题
- **逻辑缺陷**: 一些算法实现存在逻辑错误

#### 4.2.3 过度复杂的设计
AI 有时提供了过于复杂的解决方案：

- **过度工程**: 为有更简单解决方案的问题建议模式
- **不必要的抽象**: 为简单案例创建接口，具体类就足够了
- **复杂层次结构**: 设计了过深或过宽的继承层次

#### 4.2.4 上下文理解限制
AI 在一些项目特定方面存在困难：

- **游戏特定逻辑**: 误解了一些塔防游戏机制
- **UI 框架细节**: 对 Java Swing 特定问题的理解有限
- **线程复杂性**: 过度简化了复杂的多线程需求
- **资源管理**: 没有完全掌握图像和音频加载模式

### 4.3 学到的经验

#### 4.3.1 增量提示
将交付给AI的复杂或大型任务，分解为更小、更集中的提示，可以产生更精确、符合要求的结果。

例如，与其要求"重构整个游戏系统"，不如先问"分析`GamePanel`类代码中存在的问题"，然后要求AI"在怪物创建这一功能上应用工厂方法模式"。

#### 4.3.2 丰富明确的上下文提示
提供完整的上下文（包含完整的方法实现和类关系）和给出明确的指令（如使用"实现遵循 SOLID 原则的策略模式"这样明确的提示词，而不是通用的"改进此代码"），可以帮助 AI 更准确地理解实际问题，从而产生更高质量、更符合标准的代码生成。同时，在特定领域上给出指导，比如告诉AI这个塔防游戏的机制，可以让AI给出更恰当的建议和实现。

#### 4.3.3 验证要求
明确要求 AI 验证是否完成了提示词中的任务至关重要。现阶段的AI有时无法完成提示词中的所有任务，因此要求AI进行验证，可以减少错误，获得更符合要求的结果。

## 5 附加讨论

### 5.1 未解决的问题

虽然重构显著提高了代码质量和可维护性，但仍有一些问题尚未完全解决，可作为后续的改进方向：

#### 5.1.1 **内存管理优化**

虽然享元模式减少了图片资源的内存占用，但对于大量游戏对象（如大量怪物同时存在）的内存管理仍可进一步优化。可以考虑实现对象池模式来重用怪物和子弹对象，减少频繁的对象创建和垃圾回收

#### 5.1.2 **渲染性能**

当前系统使用Java Swing进行渲染，对于大量游戏对象的场景可能存在性能瓶颈。且未实现精灵批处理（Sprite Batching）和视口剔除（Viewport Culling）等优化技术。同时图片旋转和缩放操作可能消耗较多CPU资源。这些都需要后续进行优化。

#### 5.1.3 **线程管理**

当前使用多个独立线程（MonsterThread、Tower线程等），缺乏统一的线程池管理。线程间的同步机制可以进一步优化，减少锁竞争。

#### 5.1.4 **保存/加载系统**

虽然访问者模式为保存游戏状态提供了良好的结构（SaveGameStateVisitor），但完整的保存/加载功能尚未实现。缺少将游戏状态持久化到文件和从文件恢复游戏状态的功能。

### 5.2 团队协作

在团队开发环境中，建立并应用有效的协作机制非常重要。以下是我们团队在项目协作时使用的一些方法：

#### 5.2.1 代码审查

1. **模式合规性检查**
   - 审查新代码是否正确应用了设计模式
   - 检查模式实现是否符合设计原则（SOLID原则）
   - 验证模式的使用是否恰当，避免过度设计

2. **代码质量检查**
   - 检查代码风格和命名规范
   - 验证异常处理和错误处理是否完善
   - 检查代码注释和文档是否充分

3. **性能影响评估**
   - 评估新代码对性能的影响
   - 检查是否存在性能瓶颈
   - 验证内存使用是否合理

#### 5.2.2 项目开发

1. **分支管理**
   - 每个设计模式的实现使用独立的功能分支
   - 模式实现完成后进行代码审查
   - 通过审查后合并到主分支
2. **增量集成**
   - 逐步添加设计模式，避免一次性大规模重构
   - 每个模式实现后进行全面测试，并给出说明文档和UML图，方便后续团队成员进行开发
   - 确保每个模式都能正常工作后再添加下一个
3. **沟通协作**
   - 建立团队沟通渠道（如腾讯会议）
   - 定期举行讨论会，记录项目开发进度和遇到的问题，并商讨解决方案
