package oopants;

import java.util.concurrent.Semaphore;

import oopants.setup.SetupWindow;
import oopants.window.Frame;

public class Main {
	public static Semaphore sem = new Semaphore(0);
	public static int size;
	public static int ants;
	public static int food;
	public static double mean;
	public static double std;
	public static int tileSize;
	public static int foodStd;

	public static void main(String[] args) throws InterruptedException {
		/**
		 * @author Marcin Leszczyński
		 * @author Robert Kowalczewski
		 */
		SetupWindow.start();
		sem.acquire();
		startSimulation();
	}

	public static void startSimulation() throws InterruptedException {
		Frame.start(size, ants, food, mean, std, tileSize, foodStd);
	}
}
