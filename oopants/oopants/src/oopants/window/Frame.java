package oopants.window;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import oopants.ants.Ant;
import oopants.ants.BlueAnt;
import oopants.ants.RedAnt;
import oopants.grid.Grid;

public class Frame extends JFrame {
	/**
	 * This class displays the simulation
	 */
	private static Frame frame = null;
	private static boolean started = false;
	public static boolean running = true;
	public static long simulationSpeed = 50;
	public static boolean visualize = true;

	public static int panelWidth;
	public static int panelHeight;

	private JLabel blueLabel;
	private JLabel redLabel;

	private Frame(int size, int tSize) {
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setResizable(false);

		setLayout(null);

		setTitle("Ant Simulator");
		setJMenuBar(MenuBar.getBar());

		JPanel panel = new JPanel();
		panel.setLayout(null);
		panel.setOpaque(true);
		panel.setBackground(Color.black);
		setContentPane(panel);

		blueLabel = new JLabel("0");
		blueLabel.setBounds(size * tSize, 10, 200, 20);
		blueLabel.setForeground(Color.blue);
		add(blueLabel);

		redLabel = new JLabel("0");
		redLabel.setBounds(size * tSize, 30, 200, 20);
		redLabel.setForeground(Color.red);
		add(redLabel);
	}

	public static void start(int n, int nAnts, int nFood, double mu, double si, int tileSize, int foodStd)
			throws InterruptedException {
		if (!started) {
			started = true;
			frame = new Frame(n, tileSize);
			frame.setVisible(true);
			panelWidth = frame.getContentPane().getWidth();
			panelHeight = frame.getContentPane().getHeight();

			// initialize dictionary of positions
			for (int i = 0; i < n; i++) {
				for (int j = 0; j < n; j++) {
					BlueAnt.blueAntPositions.put(new Point(i, j), new ArrayList<BlueAnt>());
					RedAnt.redAntPositions.put(new Point(i, j), new ArrayList<RedAnt>());
				}
			}

			Grid grid = new Grid(n, nAnts, nFood, mu, si, foodStd);
			// Grid grid = new Grid(300, 1000, 1000, 150, 50);
			TileCanvas canvas = new TileCanvas(tileSize, grid);

			// cheesy but works
			frame.setSize(new Dimension(canvas.getWindowSize() + 80, canvas.getWindowSize() + 23));
			frame.setLocationRelativeTo(null);
			frame.getContentPane().add(canvas);
			frame.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent e) {
					Frame.running = false;
					System.exit(0);
				}
			});

			int amountRedAnts = grid.getAmountOfAnts();
			int amountBlueAnts = grid.getAmountOfAnts();
			ArrayList<RedAnt> newRedChildren = new ArrayList<RedAnt>();
			ArrayList<BlueAnt> newBlueChildren = new ArrayList<BlueAnt>();

			// run simulation
			for (RedAnt ant : grid.getRedAnts()) {
				ant.start();
			}
			for (BlueAnt ant : grid.getBlueAnts()) {
				ant.start();
			}

			while (Frame.running) {
				long nextGameTick = System.currentTimeMillis();

				if (!newRedChildren.isEmpty()) {
					for (RedAnt ant : newRedChildren) {
						grid.addRedAnt(ant);
						ant.start();
					}
					newRedChildren.clear();
				}

				ArrayList<RedAnt> aliveRedAnts = new ArrayList<RedAnt>();
				for (RedAnt ant : grid.getRedAnts()) {
					if (!ant.isDead()) {
						aliveRedAnts.add(ant);
						ant.setInput(grid.getAntInputs(ant));
						//ant.setInput(grid.getRedAntInputs(ant));
						ant.allowNextMove();
					} else {
						ant.putOutOfMisery();
						ant.allowNextMove();
						try {
							ant.join();
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}
						amountRedAnts -= 1;
					}
				}
				while (grid.getAmountOfAnts() - amountRedAnts >= 2) {
					amountRedAnts += 2;
					RedAnt[] children = grid.CreateNewRedAnts();
					newRedChildren.add(children[0]);
					newRedChildren.add(children[1]);
				}
				grid.setRedAnts(aliveRedAnts);

				if (!newBlueChildren.isEmpty()) {
					for (BlueAnt ant : newBlueChildren) {
						grid.addBlueAnt(ant);
						ant.start();
					}
					newBlueChildren.clear();

				}
				ArrayList<BlueAnt> aliveBlueAnts = new ArrayList<BlueAnt>();
				for (BlueAnt ant : grid.getBlueAnts()) {
					if (!ant.isDead()) {
						aliveBlueAnts.add(ant);
						ant.setInput(grid.getAntInputs(ant));
						//ant.setInput(grid.getBlueAntInputs(ant));
						ant.allowNextMove();
					} else {
						ant.putOutOfMisery();
						ant.allowNextMove();
						try {
							ant.join();
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}
						amountBlueAnts -= 1;
					}
				}
				while (grid.getAmountOfAnts() - amountBlueAnts >= 2) {
					amountBlueAnts += 2;
					BlueAnt[] children = grid.CreateNewBlueAnts();
					newBlueChildren.add(children[0]);
					newBlueChildren.add(children[1]);
				}
				grid.setBlueAnts(aliveBlueAnts);

				for (RedAnt ant : grid.getRedAnts()) {
					try {
						ant.doneMakingMove.acquire();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					ant.makeMove(grid.getSize());
					grid.checkForCombatRed(ant);
					foodLogic(grid, ant);
				}
				for (BlueAnt ant : grid.getBlueAnts()) {
					try {
						ant.doneMakingMove.acquire();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					ant.makeMove(grid.getSize());
					grid.checkForCombatBlue(ant);
					foodLogic(grid, ant);
				}

				if (Frame.visualize) {
					canvas.repaint();
				}

				// simulation speed control
				long timeDiff = System.currentTimeMillis() - nextGameTick;
				if (timeDiff < Frame.simulationSpeed) {
					try {
						Thread.sleep(Frame.simulationSpeed - timeDiff);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
				}

			}
			for (RedAnt ant : grid.getRedAnts()) {
				try {
					ant.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			for (BlueAnt ant : grid.getBlueAnts()) {
				try {
					ant.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

		}
	}

	public static void foodLogic(Grid grid, Ant ant) {
		Set<Point> food = grid.getFood();
		if (food.contains(ant.getPosition())) {
			ant.fitness.onCollectedNewFood(1);
			if (ant.identify().equals("Blue")) {
				frame.blueLabel.setText(String.valueOf(Integer.parseInt(frame.blueLabel.getText()) + 1));
			} else {
				frame.redLabel.setText(String.valueOf(Integer.parseInt(frame.redLabel.getText()) + 1));
			}
			food.remove(ant.getPosition());
			grid.refillFood();
		}
	}

	public static int currentWidth() {
		return frame.getContentPane().getWidth();
	}

	public static int currentHeight() {
		return frame.getContentPane().getHeight();
	}

	public static double getScale() {
		return frame.getWidth() / panelWidth;
	}

}
