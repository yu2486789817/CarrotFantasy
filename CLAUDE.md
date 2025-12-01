# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

CarrotFantasy is a Java-based tower defense game inspired by the popular "保卫萝卜" (Carrot Fantasy) mobile game. This was implemented as a term project for Tsinghua University's JAVA and Object-Oriented Programming course.

## Development Commands

### Build and Run
```bash
# Compile the project
javac -cp . src/carrotfantasy/*.java -d .

# Run the game
java carrotfantasy.MainMenu
```

The main executable JAR file `CarrotFantasy.jar` is also available in the root directory.

### Project Structure
- **Source Code**: `src/carrotfantasy/` - All Java source files
- **Resources**:
  - `Images/` - Game sprites and UI elements (organized by theme)
  - `Music/` - Audio files for sound effects and background music
- **Documentation**: `README.md` - Basic project description

## Architecture Overview

### Core Game Components

**Main Application Flow:**
1. `MainMenu` - Entry point, handles difficulty selection (3 modes)
2. `GamePanel` - Main game canvas and logic controller
3. `GameOverPanel` - End game screen with restart/exit options

**Game Entities:**
- `Carrot` - The player's protected objective that monsters attack
- `Monster` - Enemy units with health, movement, and pathfinding
- `Tower` (Abstract) - Base class for all defensive towers
  - `TBottle` - Projectile-based tower with directional attacks
  - `TSunFlower` - Area-effect tower with flame attacks

**Support Systems:**
- `ImageReader` - Utility for loading and scaling image assets
- `MusicModule` - Audio system for playing sound effects and music
- `MonsterThread` - Controls monster spawning and wave management
- `WhiteNum`/`YellowNum` - UI number display components

### Game Architecture Patterns

**Grid-Based Layout:**
- Game field divided into 12x6 grid cells (72 total positions)
- Each cell can contain one tower or be blocked by obstacles
- Path predefined for each of the 3 difficulty levels

**Threading Model:**
- Main game loop runs in `GamePanel.run()`
- Each tower runs in its own thread for attack logic
- `MonsterThread` manages monster spawning and waves
- All entities support pause/resume functionality

**Resource Management:**
- Image assets loaded via `ImageReader` with coordinate-based sprite sheets
- Audio system using Java's `Clip` for sound playback
- Memory management through setVisible() and thread termination

### Game Mechanics

**Difficulty Levels:**
- Mode 0: Easy - Less obstacles, simpler enemy path
- Mode 1: Medium - More obstacles, moderate complexity
- Mode 2: Hard - Most obstacles, complex pathing

**Tower System:**
- TBottle: 100 coins, projectile attacks, upgrades increase damage/range
- TSunFlower: 180 coins, area damage, upgrades increase power
- Both towers support 3 upgrade levels with different visuals/stats

**Economy:**
- Starting money varies by difficulty
- Towers can be sold for 80% of construction cost
- Money displayed via 4-digit number components in UI

### Key Technical Notes

**Coordinate System:**
- Game uses a 960x640 pixel window
- UI elements positioned via absolute coordinates
- Sprite sheets use pixel-perfect coordinate extraction

**Event Handling:**
- Mouse interactions handled through `ActionListener` interfaces
- Grid cell clicks for tower placement/management
- UI button clicks for game controls

**Performance Considerations:**
- Image assets loaded once and reused via static references
- Thread sleep intervals used for animation timing
- setVisible() calls used extensively for UI state management