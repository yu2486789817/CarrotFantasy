package carrotfantasy;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

public class GamePanel extends JFrame implements ActionListener, Runnable{
	private int mode;
	private MainMenu mainMenu;
	private JLabel bg, path, menuBG, menuCenter, obstacle1, obstacle2, start, chosenCell, range, popupMenuBG, countDown;
	private JLabel[] waveNums;
	private JLabel[] moneyNums;
	private JButton[] cells;
	private JButton pause, bottle, sunFlower, sell, upgrade, menu, resume, restart, chooseLevel;
	private JLayeredPane layeredPane, operatingBox;
	private JPanel contentPane, choosingBox;
	private Carrot carrot;
	private ImageReader imgReader;
	private Monster[] monsters;
	private Tower[] towers;
	private boolean paused;
	private int[] hasTower; 
	private HashSet<Integer> block;
	private ImageIcon sell_80, sell_144, sell_208, sell_256, upgrade_180, upgrade_260, upgrade_320, _upgrade_180, _upgrade_260, _upgrade_320,
						cant_upgrade, range_150, range_200, range_210, range_280, range_360;
	private GameOverPanel gameOverPane;
	private static MusicModule musicModule = new MusicModule();
	MonsterThread monsterThread;
	
	public GamePanel(int m, MainMenu mm) {
		mode = m;
		mainMenu = mm;
		this.setTitle("Carrot Fantasy");
		this.setSize(975, 640);
		this.setLocation(mm.getX(), mm.getY());
		this.setResizable(false);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		imgReader = new ImageReader();
		monsters = new Monster[30];
		cells = new JButton[72];
		hasTower = new int[72];
		paused = false;
		towers = new Tower[72];
		obstacle1 = new JLabel();
		obstacle2 = new JLabel();
		choosingBox = new JPanel();
		pause = new JButton();
		menu = new JButton();
		menuBG = new JLabel();
		menuCenter = new JLabel();
		sell = new JButton();
		upgrade = new JButton();
		operatingBox = new JLayeredPane();
		start = new JLabel();
		chosenCell = new JLabel();
		range = new JLabel();
		waveNums = new JLabel[4];
		moneyNums = new JLabel[4];
		for(int i = 0; i < 4; i++) {
			waveNums[i] = new JLabel();
			moneyNums[i] = new JLabel();
		}
		bottle = new JButton();
		sunFlower = new JButton();
		resume = new JButton();
		restart = new JButton();
		chooseLevel = new JButton();
		popupMenuBG = new JLabel();
		countDown = new JLabel();
		gameOverPane = new GameOverPanel(restart, chooseLevel);
	}
	
	public void run(){
		layeredPane = this.getLayeredPane();
		JLabel loading = new JLabel(imgReader.getImageIcon("Images/loading-hd.png", 0, 0, 960, 640, 1, false));
		loading.setBounds(0, 0, 960, 640);
		loading.setVisible(true);
		layeredPane.add(loading, Integer.valueOf(500));
		
		bg = getMyBackground();
		path = getPath();
		menuBG.setBounds(12, -1, 926, 78);
		menuBG.setIcon(imgReader.getImageIcon("Images/Items\\Items02-hd.png", 2, 2, 926, 78, 1, false));
		menuCenter.setBounds(362, 5, 236, 52);
		menuCenter.setIcon(imgReader.getImageIcon("Images/Items\\Items02-hd.png", 268, 358, 52, 236, 1, true));
		pause.setBounds(780, 5, 56, 56);
		pause.setIcon(imgReader.getImageIcon("Images/Items\\Items02-hd.png", 1474, 628, 56, 56, 1, false));
		pause.setBorder(null);
		pause.setContentAreaFilled(false);
		pause.addActionListener(this);
		menu.setBounds(850, 5, 56, 56);
		menu.setIcon(imgReader.getImageIcon("Images/Items\\Items02-hd.png", 1474, 686, 56, 56, 1, false));
		menu.setBorder(null);
		menu.setContentAreaFilled(false);
		menu.addActionListener(this);
		waveNums[0].setBounds(365, 10, 44, 40);
		waveNums[1].setBounds(409, 10, 44, 40);
		waveNums[2].setBounds(480, 10, 20, 40);
		waveNums[3].setBounds(500, 10, 20, 40);
		moneyNums[0].setBounds(100, 10, 44, 40);
		moneyNums[1].setBounds(120, 10, 44, 40);
		moneyNums[2].setBounds(140, 10, 44, 40);
		moneyNums[3].setBounds(160, 10, 44, 40);
		countDown.setBounds(0, 0, 960, 640);
		countDown.setVisible(false);
		countDown.setHorizontalAlignment(JLabel.CENTER);
		
		layeredPane.add(bg, Integer.valueOf(-30010));
		layeredPane.add(path, Integer.valueOf(-30009));
		layeredPane.add(obstacle1, Integer.valueOf(-30006));
		layeredPane.add(obstacle2, Integer.valueOf(-30006));
		layeredPane.add(start, Integer.valueOf(-30008));
		layeredPane.add(menuBG, Integer.valueOf(-30008));
		layeredPane.add(menuCenter, Integer.valueOf(-30007));
		layeredPane.add(pause, Integer.valueOf(-30007));
		layeredPane.add(menu, Integer.valueOf(-30007));
		layeredPane.add(choosingBox, Integer.valueOf(100));
		layeredPane.add(chosenCell, Integer.valueOf(100));
		layeredPane.add(operatingBox, Integer.valueOf(100));
		layeredPane.add(popupMenuBG, Integer.valueOf(200));
		layeredPane.add(resume, Integer.valueOf(201));
		layeredPane.add(restart, Integer.valueOf(201));
		layeredPane.add(chooseLevel, Integer.valueOf(201));
		layeredPane.add(gameOverPane, Integer.valueOf(200));
		layeredPane.add(countDown, Integer.valueOf(200));
		
		for(int i = 0; i < 4; i++) {
			layeredPane.add(waveNums[i]);
			layeredPane.add(moneyNums[i]);
		}
		
		for(int i = 0; i < 30; i++) {
			monsters[i] = new Monster(mode);
			layeredPane.add(monsters[i], Integer.valueOf(-30000));
		}
		
		contentPane = (JPanel)this.getContentPane();
		contentPane.setOpaque(false);
		contentPane.setLayout(null);
	
		this.setLayout();
		
		for(int i = 0; i < 72; i++) {
			hasTower[i] = 0;
			cells[i] = new JButton();
			cells[i].setBounds(i % 12 * 80 , i / 12 * 80 + 120, 80, 80);
			cells[i].addActionListener(this);
			cells[i].setBorder(null);
			cells[i].setContentAreaFilled(false);
			contentPane.add(cells[i]);
		}
		chosenCell.setBounds(0, 0, 80, 80);
		chosenCell.setIcon(imgReader.getImageIcon("Images/Items\\Items02-hd.png", 766, 806, 72, 72, 1, false));
		chosenCell.setVisible(false);
		bottle.setBounds(0, 0, 80, 80);
		bottle.setBorder(null);
		bottle.setContentAreaFilled(false);
		bottle.setFocusPainted(false);
		bottle.addActionListener(this);
		sunFlower.setBounds(80, 0, 80, 80);
		sunFlower.setBorder(null);
		sunFlower.setContentAreaFilled(false);
		sunFlower.setFocusPainted(false);
		sunFlower.addActionListener(this);
		choosingBox.setOpaque(false);
		choosingBox.setLayout(null);
		choosingBox.setVisible(false);
		choosingBox.add(bottle);
		choosingBox.add(sunFlower);
		sell.setBounds(360, 280, 80, 80);
		sell.setBorder(null);
		sell.setContentAreaFilled(false);
		sell.setFocusable(false);
		sell.addActionListener(this);
		sell.setHorizontalAlignment(JLabel.CENTER);

		upgrade.setBorder(null);
		upgrade.setContentAreaFilled(false);
		upgrade.setFocusable(false);
		upgrade.addActionListener(this);
		upgrade.setHorizontalAlignment(JLabel.CENTER);
		operatingBox.setOpaque(false);
		operatingBox.setLayout(null);
		operatingBox.setVisible(false);
		operatingBox.add(sell, Integer.valueOf(-30009));
		operatingBox.add(upgrade, Integer.valueOf(-30009));
		operatingBox.add(range, Integer.valueOf(-30010));
		
		popupMenuBG.setBounds(259, 151, 442, 338);
		popupMenuBG.setIcon(imgReader.getImageIcon("Images/scene\\gamemenu-hd.png", 2, 2, 442, 338, 1, false));
		popupMenuBG.setVisible(false);
		resume.setBounds(350, 180, 236, 72);
		resume.setIcon(imgReader.getImageIcon("Images/scene\\gamemenu-hd.png", 240, 342, 236, 72, 1, false));
		resume.setVisible(false);
		resume.setBorder(null);
		resume.setContentAreaFilled(false);
		resume.setFocusPainted(false);
		resume.addActionListener(this);
		restart.setBounds(350, 275, 236, 72);
		restart.setIcon(imgReader.getImageIcon("Images/scene\\gamemenu-hd.png", 2, 490, 236, 72, 1, false));
		restart.setVisible(false);
		restart.setBorder(null);
		restart.setContentAreaFilled(false);
		restart.setFocusPainted(false);
		restart.addActionListener(this);
		chooseLevel.setBounds(350, 370, 236, 72);
		chooseLevel.setIcon(imgReader.getImageIcon("Images/scene\\gamemenu-hd.png", 240, 490, 236, 72, 1, false));
		chooseLevel.setVisible(false);
		chooseLevel.setBorder(null);
		chooseLevel.setContentAreaFilled(false);
		chooseLevel.setFocusPainted(false);
		chooseLevel.addActionListener(this);
		
		sell_80 = imgReader.getImageIcon("Images/Items\\Items02-hd.png", 1082, 648, 76, 78, 0.8, false);
		sell_144 = imgReader.getImageIcon("Images/Items\\Items02-hd.png", 1226, 936, 76, 78, 0.8, false);
		sell_208 = imgReader.getImageIcon("Images/Items\\Items02-hd.png", 1396, 807, 76, 78, 0.8, false);
		sell_256 = imgReader.getImageIcon("Images/Items\\Items02-hd.png", 1396, 647, 76, 78, 0.8, false);
		upgrade_180 = imgReader.getImageIcon("Images/Items\\Items02-hd.png", 994, 808, 78, 76, 0.8, true);
		upgrade_260 = imgReader.getImageIcon("Images/Items\\Items02-hd.png", 906, 886, 78, 76, 0.8, true);
		upgrade_320 = imgReader.getImageIcon("Images/Items\\Items02-hd.png", 924, 730, 78, 76, 0.8, true);
		_upgrade_180 = imgReader.getImageIcon("Images/Items\\Items02-hd.png", 1260, 487, 76, 78, 0.8, false);
		_upgrade_260 = imgReader.getImageIcon("Images/Items\\Items02-hd.png", 1102, 498, 78, 76, 0.8, true);
		_upgrade_320 = imgReader.getImageIcon("Images/Items\\Items02-hd.png", 1004, 730, 78, 76, 0.8, true);
		cant_upgrade = imgReader.getImageIcon("Images/Items\\Items02-hd.png", 1318, 807, 76, 76, 0.8, false);
		range_150 = imgReader.getImageIcon("Images/Items\\Items00-hd.png", 644, 343, 282, 282, 1, false);
		range_200 = imgReader.getImageIcon("Images/Items\\Items00-hd.png", 484, 855, 424, 424, 1, false);
		range_210 = imgReader.getImageIcon("Images/Items\\Items00-hd.png", 644, 2, 339, 339, 1, false);
		range_280 = imgReader.getImageIcon("Images/Items\\Items00-hd.png", 2, 644, 480, 480, 1, false);
		range_360 = imgReader.getImageIcon("Images/Items\\Items00-hd.png", 2, 2, 640, 640, 1, false);
		while(true) {
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {}
			if(this.isVisible()) {
				monsterThread = new MonsterThread(monsters, mode, waveNums, moneyNums, bottle, sunFlower, carrot, gameOverPane, towers, upgrade,
													countDown);
				monsterThread.start();
				loading.setVisible(false);
				break;
			}
		}
	}
	
	public void setLayout() {
		switch (mode) {
		case 0:
			obstacle1.setIcon(imgReader.getImageIcon("Images/Theme1\\Items\\Object01-hd.png", 2, 2, 160, 156, 1, false));
			obstacle1.setBounds(400, 440, 160, 160);
			obstacle2.setVisible(false);
			start.setIcon(imgReader.getImageIcon("Images/Theme1\\Items\\Object01-hd.png", 320, 2, 93, 73, 0.8, false));
			start.setBounds(80, 110, 75, 60);
			carrot = new Carrot(805, 60, 20);
			layeredPane.add(carrot, Integer.valueOf(-30006));
			new Thread((Runnable) carrot).start();
			block = new HashSet<Integer>();
			block.add(1); block.add(10); block.add(13); block.add(22); block.add(25); block.add(28);
			block.add(29); block.add(30); block.add(31); block.add(34); block.add(37); block.add(38);
			block.add(39); block.add(40); block.add(43); block.add(44); block.add(45); block.add(46);
			block.add(48); block.add(49); block.add(53); block.add(54); block.add(58); block.add(59);
			block.add(60); block.add(61); block.add(62); block.add(63); block.add(65); block.add(66);
			block.add(68); block.add(69); block.add(70); block.add(71);
			break;
		case 1:
			obstacle1.setIcon(imgReader.getImageIcon("Images/Theme2\\Items\\Object01-hd.png", 145, 82, 75, 64, 1, true));
			obstacle1.setBounds(10, 285, 80, 80);
			obstacle2.setIcon(imgReader.getImageIcon("Images/Theme2\\Items\\Object01-hd.png", 2, 442, 152, 64, 1, false));
			obstacle2.setBounds(645, 370, 152, 64);
			start.setIcon(imgReader.getImageIcon("Images/Theme1\\Items\\Object01-hd.png", 320, 2, 93, 73, 0.8, false));
			start.setBounds(0, 110, 75, 60);
			carrot = new Carrot(725, 380, 60);
			layeredPane.add(carrot, Integer.valueOf(-30005));
			Thread t = new Thread((Runnable) carrot);
			t.start();
			block = new HashSet<Integer>();
				for(int i = 0; i < 10; i++) {
					block.add(i);
				}
				block.add(21); block.add(24);
				for(int i = 26; i < 34; i++) {
					block.add(i);
				}
				block.add(38); block.add(44); block.add(45);
				for(int i = 48; i < 72; i++) {
					if(i == 65 || i == 66) continue;
					block.add(i);
				}
			break;
		case 2:
			obstacle1.setIcon(imgReader.getImageIcon("Images/Theme3\\Items\\Object01-hd.png", 2, 2, 156, 116, 1, false));
			obstacle1.setBounds(5, 300, 156, 116);
			obstacle2.setIcon(imgReader.getImageIcon("Images/Theme3\\Items\\Object01-hd.png", 2, 246, 124, 132, 1, false));
			obstacle2.setBounds(655, 135, 124, 132);
			start.setIcon(imgReader.getImageIcon("Images/Theme1\\Items\\Object01-hd.png", 320, 2, 93, 73, 0.8, false));
			start.setBounds(20, 200, 75, 60);
			carrot = new Carrot(805, 60, 20);
			layeredPane.add(carrot, Integer.valueOf(-30005));
			new Thread((Runnable) carrot).start();
			block = new HashSet<Integer>();
			for(int i = 0; i < 3; i++) {
				for(int j = 1; j < 6; j++) {
					block.add(12 * j + i);
				}
			}
			for(int i = 0; i < 6; i++) {
				block.add(12 * i + 10);
			}
			block.add(8); block.add(9); block.add(20); block.add(21);
			for(int i = 28; i < 32; i++) {
				block.add(i);
			}
			block.add(40); block.add(43); block.add(51); block.add(52);
			block.add(55); block.add(56); block.add(57); block.add(59);
			block.add(63); block.add(68); block.add(69); block.add(71);
			break;		
		}
	}
	
	public JLabel getMyBackground() {
		JLabel bg = new JLabel();
		bg.setBounds(0, 0, 960, 640);
		switch (mode) {
		case 0:
			bg.setIcon(imgReader.getImageIcon("Images/Theme1\\BG0\\BG1-hd.png", 0, 0, 960, 640, 1, false));
			break;
		case 1:
			bg.setIcon(imgReader.getImageIcon("Images/Theme2\\BG0\\BG1-hd.png", 0, 0, 960, 640, 1, false));
			break;
		case 2:
			bg.setIcon(imgReader.getImageIcon("Images/Theme3\\BG0\\BG1-hd.png", 0, 0, 960, 640, 1, false));
			break;
		}
		return bg;
	}

	public JLabel getPath() {
		JLabel path = new JLabel();
		switch (mode) {
		case 0:
			path.setBounds(0, 120, 960, 482);
			path.setIcon(imgReader.getImageIcon("Images/Theme1\\BG1\\BG-hd.png", 0, 0, 960, 482, 1, false));
			break;
		case 1:
			path.setBounds(0, 110, 960, 492);
			path.setIcon(imgReader.getImageIcon("Images/Theme2\\BG1\\BG-hd.png", 0, 0, 960, 492, 1, false));
			break;
		case 2:
			path.setBounds(0, 140, 960, 464);
			path.setIcon(imgReader.getImageIcon("Images/Theme3\\BG1\\BG-hd.png", 0, 0, 464, 960, 1, true));
			break;
		}
		return path;
	}
	
	public void restart() {
		layeredPane.remove(carrot);
		switch(mode) {
		case 0:
			carrot = new Carrot(805, 60, 20);
			break;
		case 1:
			carrot = new Carrot(725, 380, 60);
			break;
		case 2:
			carrot = new Carrot(805, 60, 20);
			break;
		}
		layeredPane.add(carrot, Integer.valueOf(-30005));
		new Thread((Runnable) carrot).start();		
		for(int i = 0; i < 72; i++) {
			hasTower[i] = 0;
			towers[i] = null;
		}
		for(int i = 0; i < 30; i++) {
			monsters[i].renew(1);
		}
		paused = false;
		monsterThread = new MonsterThread(monsters, mode, waveNums, moneyNums, bottle, sunFlower, carrot, gameOverPane, towers, upgrade, countDown);
		monsterThread.start();
	}
	
	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();
		if(obj == (Object)pause) {
			if(!popupMenuBG.isVisible() && !gameOverPane.isVisible()) {
				musicModule.play("select");
				if(paused) {
					pause.setIcon(imgReader.getImageIcon("Images/Items\\Items02-hd.png", 1474, 628, 56, 56, 1, false));
					paused = false;
					monsterThread.myResume();
					carrot.myResume();
					choosingBox.setVisible(false);
					chosenCell.setVisible(false);
					operatingBox.setVisible(false);
					for(int i = 0; i < towers.length; i++) {
						if(hasTower[i] != 0) towers[i].myResume();
					}
				}else {
					pause.setIcon(imgReader.getImageIcon("Images/Items\\Items02-hd.png", 1418, 566, 56, 56, 1, false));
					paused = true;
					monsterThread.pause();
					carrot.pause();
					choosingBox.setVisible(false);
					chosenCell.setVisible(false);
					operatingBox.setVisible(false);
					for(int i = 0; i < towers.length; i++) {
						if(hasTower[i] != 0) towers[i].pause();
					}
				}
			}
		}else if(obj == (Object)bottle){
			if(!paused && !gameOverPane.isVisible()) {
				if(monsterThread.money >= 100) {
					musicModule.play("towerBuild");
					int x;
					int y = choosingBox.getY() + 80;
					if(choosingBox.getX() == 0) x = 0;
					else if(choosingBox.getX() == 800) x = 880;
					else x = choosingBox.getX() + 40;
					int index = x / 80 + (y / 80 - 1) * 12;
					Tower tmp = new TBottle(x, y, monsters, monsters.length - (2 - mode) * 10, cells[index]);
					towers[index] = tmp;
					layeredPane.add(tmp);
					Thread t = new Thread(tmp);
					t.start();
					hasTower[index] = 1;
					monsterThread.money -= tmp.price;
					if(monsterThread.money < 100) bottle.setIcon(imgReader.getImageIcon("Images/Towers\\TBottle-hd.png", 55, 80, 76, 78, 0.8, true));
					if(monsterThread.money < 180) sunFlower.setIcon(imgReader.getImageIcon("Images/Towers\\TSun-hd.png", 522, 1000, 76, 78, 0.8, false));
					moneyNums[0].setIcon(WhiteNum.nums[monsterThread.money / 1000]);
					moneyNums[1].setIcon(WhiteNum.nums[(monsterThread.money / 100) % 10]);
					moneyNums[2].setIcon(WhiteNum.nums[(monsterThread.money / 10) % 10]);
					moneyNums[3].setIcon(WhiteNum.nums[monsterThread.money % 10]);
					choosingBox.setVisible(false);
					chosenCell.setVisible(false);
				}
			}
		}else if(obj == (Object)sunFlower){
			if(!paused && !gameOverPane.isVisible()) {
				if(monsterThread.money >= 180) {
					musicModule.play("towerBuild");
					int x;
					int y = choosingBox.getY() + 80;
					if(choosingBox.getX() == 0) x = 0;
					else if(choosingBox.getX() == 800) x = 880;
					else x = choosingBox.getX() + 40;
					int index = x / 80 + (y / 80 - 1) * 12;
					Tower tmp = new TSunFlower(x, y, monsters, monsters.length - (2 - mode) * 10);
					Thread t = new Thread(tmp);
					t.start();
					towers[index] = tmp;
					layeredPane.add(tmp);
					hasTower[index] = 2;
					monsterThread.money -= tmp.price;
					if(monsterThread.money < 100) {
						bottle.setIcon(imgReader.getImageIcon("Images/Towers\\TBottle-hd.png", 55, 80, 76, 78, 0.8, true));
					}
					if(monsterThread.money < 180) {
						sunFlower.setIcon(imgReader.getImageIcon("Images/Towers\\TSun-hd.png", 522, 1000, 76, 78, 0.8, false));
					}
					moneyNums[0].setIcon(WhiteNum.nums[monsterThread.money / 1000]);
					moneyNums[1].setIcon(WhiteNum.nums[(monsterThread.money / 100) % 10]);
					moneyNums[2].setIcon(WhiteNum.nums[(monsterThread.money / 10) % 10]);
					moneyNums[3].setIcon(WhiteNum.nums[monsterThread.money % 10]);
					choosingBox.setVisible(false);
					chosenCell.setVisible(false);
				}
			}
		}else if(obj == (Object)sell){
			if(!paused && !gameOverPane.isVisible()) {
				musicModule.play("towerSell");
				int x = operatingBox.getX() + 360;
				int y = operatingBox.getY() + 360;
				int index = x / 80 + (y / 80 - 1) * 12;
				monsterThread.money += (int)(towers[index].price * 0.8);
				towers[index].setVisible(false);
				towers[index].sell();
				towers[index] = null;
				hasTower[index] = 0;
				moneyNums[0].setIcon(WhiteNum.nums[monsterThread.money / 1000]);
				moneyNums[1].setIcon(WhiteNum.nums[(monsterThread.money / 100) % 10]);
				moneyNums[2].setIcon(WhiteNum.nums[(monsterThread.money / 10) % 10]);
				moneyNums[3].setIcon(WhiteNum.nums[monsterThread.money % 10]);
				if(monsterThread.money >= 100) {
					bottle.setIcon(imgReader.getImageIcon("Images/Towers\\TBottle-hd.png", 55, 2, 76, 78, 0.8, true));
				}
				if(monsterThread.money >= 180) {
					sunFlower.setIcon(imgReader.getImageIcon("Images/Towers\\TSun-hd.png", 442, 1000, 76, 78, 0.8, true));
				}
				operatingBox.setVisible(false);
			}
		}else if(obj == (Object)upgrade){
			if(!paused && !gameOverPane.isVisible()) {
				int index;
				int x = operatingBox.getX() + 360;
				int y = operatingBox.getY() + 360;
				index = x / 80 + (y / 80 - 1) * 12;
				if(monsterThread.money >= towers[index].upgradePrice) {
					musicModule.play("towerUpgrade");
					monsterThread.money -= towers[index].upgradePrice;
					towers[index].upgrade();
					moneyNums[0].setIcon(WhiteNum.nums[monsterThread.money / 1000]);
					moneyNums[1].setIcon(WhiteNum.nums[(monsterThread.money / 100) % 10]);
					moneyNums[2].setIcon(WhiteNum.nums[(monsterThread.money / 10) % 10]);
					moneyNums[3].setIcon(WhiteNum.nums[monsterThread.money % 10]);
					operatingBox.setVisible(false);
					if(monsterThread.money < 100) {
						bottle.setIcon(imgReader.getImageIcon("Images/Towers\\TBottle-hd.png", 55, 80, 76, 78, 0.8, true));
					}
					if(monsterThread.money < 180) {
						sunFlower.setIcon(imgReader.getImageIcon("Images/Towers\\TSun-hd.png", 522, 1000, 76, 78, 0.8, false));
					}
				}
			}
		}else if(obj == (Object)menu){
			if(!gameOverPane.isVisible() && !popupMenuBG.isVisible()) {
				musicModule.play("select");
				popupMenuBG.setVisible(true);
				resume.setVisible(true);
				restart.setVisible(true);
				chooseLevel.setVisible(true);
				paused = true;
				monsterThread.pause();
				carrot.pause();
				choosingBox.setVisible(false);
				chosenCell.setVisible(false);
				operatingBox.setVisible(false);
				for(int i = 0; i < towers.length; i++) {
					if(hasTower[i] != 0) towers[i].pause();
				}
			}
		}else if(obj == (Object)resume){
			musicModule.play("select");
			popupMenuBG.setVisible(false);
			resume.setVisible(false);
			restart.setVisible(false);
			chooseLevel.setVisible(false);
			pause.setIcon(imgReader.getImageIcon("Images/Items\\Items02-hd.png", 1474, 628, 56, 56, 1, false));
			paused = false;
			monsterThread.myResume();
			carrot.myResume();
			choosingBox.setVisible(false);
			chosenCell.setVisible(false);
			operatingBox.setVisible(false);
			for(int i = 0; i < towers.length; i++) {
				if(hasTower[i] != 0) towers[i].myResume();
			}
		}else if(obj == (Object)restart){
			musicModule.play("select");
			popupMenuBG.setVisible(false);
			resume.setVisible(false);
			restart.setVisible(false);
			chooseLevel.setVisible(false);
			gameOverPane.setVisible(false);
			operatingBox.setVisible(false);
			pause.setIcon(imgReader.getImageIcon("Images/Items\\Items02-hd.png", 1474, 628, 56, 56, 1, false));
			monsterThread.interrupt();
			carrot.interrupt();
			for(int i = 0; i < towers.length; i++) {
				if(hasTower[i] != 0) towers[i].interrupt();
			}
			restart();
		}else if(obj == (Object)chooseLevel){
			musicModule.play("select");
			musicModule.stop("BGMusic");
			popupMenuBG.setVisible(false);
			resume.setVisible(false);
			restart.setVisible(false);
			chooseLevel.setVisible(false);
			gameOverPane.setVisible(false);
			monsterThread.interrupt();
			carrot.interrupt();
			for(int i = 0; i < towers.length; i++) {
				if(hasTower[i] != 0) towers[i].interrupt();
			}
			Monster.setTexNull();
			mainMenu.setLocation(this.getX(),this.getY());
			mainMenu.show();
			this.dispose();
		}else {
			if(!paused && !gameOverPane.isVisible()) {
				for(int i = 0; i < 72; i++) {
					if(obj == (Object)cells[i]) {
						if(block.contains(Integer.valueOf(i))){
							choosingBox.setVisible(false);
							chosenCell.setVisible(false);
							operatingBox.setVisible(false);
						}else if(hasTower[i] == 1) {
							if(operatingBox.getX() == cells[i].getX() - 360 && operatingBox.getY() == cells[i].getY() - 360 &&
									operatingBox.isVisible()) {
								musicModule.play("towerDeselect");
								operatingBox.setVisible(false);
								break;
							}
							musicModule.play("towerSelect");
							if(i % 12 == 0) upgrade.setBounds(440, 360, 80, 80);
							else upgrade.setBounds(280, 360, 80, 80);
							choosingBox.setVisible(false);
							chosenCell.setVisible(false);
							switch(towers[i].getLevel()) {
							case 1:
								sell.setIcon(sell_80);
								if(monsterThread.money < towers[i].upgradePrice) {
									upgrade.setIcon(_upgrade_180);
									upgrade.setName("_upgrade_180");
								}
								else
									upgrade.setIcon(upgrade_180);
								range.setBounds(188, 188, 424, 424);
								range.setIcon(range_200);
								break;
							case 2:
								sell.setIcon(sell_144);
								if(monsterThread.money < towers[i].upgradePrice) {
									upgrade.setIcon(_upgrade_260);
									upgrade.setName("_upgrade_260");
								}
								else
									upgrade.setIcon(upgrade_260);
								range.setBounds(160, 160, 480, 480);
								range.setIcon(range_280);
								break;
							case 3:
								sell.setIcon(sell_208);
								upgrade.setIcon(cant_upgrade);
								range.setBounds(80, 80, 640, 640);
								range.setIcon(range_360);
								break;
							}
							operatingBox.setBounds(cells[i].getX() - 360, cells[i].getY() - 360, 800, 800);
							operatingBox.setVisible(true);
						}else if(hasTower[i] == 2) {
							
							if(operatingBox.getX() == cells[i].getX() - 360 && operatingBox.getY() == cells[i].getY() - 360 && 
									operatingBox.isVisible()) {
								musicModule.play("towerDeselect");
								operatingBox.setVisible(false);
								break;
							}
							musicModule.play("towerSelect");
							if(i % 12 == 0) upgrade.setBounds(440, 360, 80, 80);
							else upgrade.setBounds(280, 360, 80, 80);
							choosingBox.setVisible(false);
							chosenCell.setVisible(false);
							switch(towers[i].getLevel()) {
							case 1:
								sell.setIcon(sell_144);
								if(monsterThread.money < towers[i].upgradePrice) {
									upgrade.setIcon(_upgrade_260);
									upgrade.setName("_upgrade_260");
								}
								
								else
									upgrade.setIcon(upgrade_260);
								range.setBounds(259, 259, 282, 282);
								range.setIcon(range_150);
								break;
							case 2:
								sell.setIcon(sell_208);
								if(monsterThread.money < towers[i].upgradePrice) {
									upgrade.setIcon(_upgrade_320);
									upgrade.setName("_upgrade_320");
								}
								else
									upgrade.setIcon(upgrade_320);
								range.setBounds(230, 230, 339, 339);
								range.setIcon(range_210);
								break;
							case 3:
								sell.setIcon(sell_256);
								upgrade.setIcon(cant_upgrade);
								range.setBounds(230, 230, 339, 339);
								range.setIcon(range_210);
								break;
							}
							operatingBox.setBounds(cells[i].getX() - 360, cells[i].getY() - 360, 800, 800);
							operatingBox.setVisible(true);
						}else {
							if(chosenCell.getX() == cells[i].getX() + 4 && chosenCell.getY() == cells[i].getY() + 4 && 
									chosenCell.isVisible()) {
								choosingBox.setVisible(false);
								chosenCell.setVisible(false);
								break;
							}
							operatingBox.setVisible(false);
							if(i % 12 == 0) choosingBox.setBounds(cells[i].getX(), cells[i].getY() - 80, 160, 80);
							else if(i % 12 == 11) choosingBox.setBounds(cells[i].getX() - 80, cells[i].getY() - 80, 160, 80); 
							else choosingBox.setBounds(cells[i].getX() - 40, cells[i].getY() - 80, 160, 80);
							choosingBox.setVisible(true);
							chosenCell.setBounds(cells[i].getX() + 4, cells[i].getY() + 4, 80, 80);
							chosenCell.setVisible(true);
						}
					}	
				}	
			}
		}
	}
	
	// Methods added for Facade Pattern integration
	public int getMode() {
		return mode;
	}
	
	public Monster[] getMonsters() {
		return monsters;
	}
	
	public int getMonsterCount() {
		return monsters.length;
	}
	
	public Object getCell(int x, int y) {
		int index = x / 80 + (y / 80 - 1) * 12;
		if (index >= 0 && index < cells.length) {
			return cells[index];
		}
		return null;
	}
	
	public void addTower(Tower tower, int x, int y) {
		int index = x / 80 + (y / 80 - 1) * 12;
		if (index >= 0 && index < towers.length) {
			towers[index] = tower;
			layeredPane.add(tower);
			Thread t = new Thread(tower);
			t.start();
			hasTower[index] = 1;
		}
	}
	
	public void removeTower(int x, int y) {
		int index = x / 80 + (y / 80 - 1) * 12;
		if (index >= 0 && index < towers.length && towers[index] != null) {
			towers[index].setVisible(false);
			towers[index].sell();
			towers[index] = null;
			hasTower[index] = 0;
		}
	}
	
	public Tower getTower(int x, int y) {
		int index = x / 80 + (y / 80 - 1) * 12;
		if (index >= 0 && index < towers.length) {
			return towers[index];
		}
		return null;
	}
	
	public int getMoney() {
		return monsterThread != null ? monsterThread.money : 250;
	}
	
	public void deductMoney(int amount) {
		if (monsterThread != null) {
			monsterThread.money -= amount;
			updateMoneyDisplay();
		}
	}
	
	public void addMoney(int amount) {
		if (monsterThread != null) {
			monsterThread.money += amount;
			updateMoneyDisplay();
		}
	}
	
	public boolean isPaused() {
		return paused;
	}
	
	public void setPaused(boolean paused) {
		this.paused = paused;
	}
	
	public Carrot getCarrot() {
		return carrot;
	}
	
	public void pauseAllTowers() {
		for (int i = 0; i < towers.length; i++) {
			if (hasTower[i] != 0 && towers[i] != null) {
				towers[i].pause();
			}
		}
	}
	
	public void resumeAllTowers() {
		for (int i = 0; i < towers.length; i++) {
			if (hasTower[i] != 0 && towers[i] != null) {
				towers[i].myResume();
			}
		}
	}
	
	public void showGameOver(boolean victory) {
		gameOverPane.setVisible(true);
		gameOverPane.setVictory(victory);
	}
	
	public boolean isAllWavesCompleted() {
		// Simplified implementation - would check actual game state
		return false;
	}
	
	public JButton getBottleButton() {
		return bottle;
	}
	
	public JButton getSunFlowerButton() {
		return sunFlower;
	}
	
	public GameOverPanel getGameOverPanel() {
		return gameOverPane;
	}
	
	public Tower[] getTowers() {
		return towers;
	}
	
	public JButton getUpgradeButton() {
		return upgrade;
	}
	
	public JLabel getCountDownLabel() {
		return countDown;
	}
	
	public JLabel[] getWaveNums() {
		return waveNums;
	}
	
	public JLabel[] getMoneyNums() {
		return moneyNums;
	}
	
	public void setupBackground(GameElement bg) {
		// Implementation for setting up background using GameElement
		this.bg = new JLabel();
		this.bg.setBounds(bg.getX(), bg.getY(), bg.getWidth(), bg.getHeight());
		this.bg.setIcon(imgReader.getImageIcon(bg.getImagePath(), 0, 0, bg.getWidth(), bg.getHeight(), 1, false));
	}
	
	public void setupPath(GameElement path) {
		// Implementation for setting up path using GameElement
		this.path = new JLabel();
		this.path.setBounds(path.getX(), path.getY(), path.getWidth(), path.getHeight());
		this.path.setIcon(imgReader.getImageIcon(path.getImagePath(), 0, 0, path.getWidth(), path.getHeight(), 1, false));
	}
	
	private void updateMoneyDisplay() {
		if (monsterThread != null && moneyNums != null) {
			int money = monsterThread.money;
			moneyNums[0].setIcon(WhiteNum.nums[money / 1000]);
			moneyNums[1].setIcon(WhiteNum.nums[(money / 100) % 10]);
			moneyNums[2].setIcon(WhiteNum.nums[(money / 10) % 10]);
			moneyNums[3].setIcon(WhiteNum.nums[money % 10]);
		}
	}
	
	// Method to set GameFacade for integration
	public void setGameFacade(GameFacade gameFacade) {
		// This method allows to GamePanel to work with refactored GameFacade
	}
}