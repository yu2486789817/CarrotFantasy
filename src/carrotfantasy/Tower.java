package carrotfantasy;

import javax.swing.JLayeredPane;

// Refactored with Visitor Pattern Integration
public abstract class Tower extends JLayeredPane implements Runnable, Visitable{
	protected int power;
	protected int range;
	protected int price, upgradePrice;
	protected int level;
	protected int cd;
	protected int xPos, yPos;
	protected int monsterNum;
	protected boolean attacking;
	protected boolean paused, sold, isInterrupted;
	protected static MusicModule musicModule = new MusicModule();

	public abstract void run();

	public void pause() {
		paused = true;
	}

	public void myResume() {
		paused = false;
	}

	public void sell() {
		sold = true;
	}

	public void interrupt() {
		this.setVisible(false);
		isInterrupted = true;
	}

	public void gameOver() {
		isInterrupted = true;
	}

	public int getLevel() {
		return this.level;
	}

	public int getPrice() {
		return this.price;
	}

	public int getUpgradePrice() {
		return this.upgradePrice;
	}
	
	public abstract void upgrade();

    // Refactored with Visitor Pattern Integration
    public void accept(GameVisitor visitor) {
        visitor.visit(this);
    }
}