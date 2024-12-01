package oopants.window;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;

public class MenuBar extends JMenuBar {
	/**
	 * This is the menu bar for the frame
	 */
	private static MenuBar bar;
	private MenuBar() {
		// file menu
		JMenu file = new JMenu("file");
		JMenuItem exit = new JMenuItem("exit");
		exit.addActionListener(e -> {
			Frame frame = (Frame)SwingUtilities.getWindowAncestor(this);
			frame.dispose();
			System.exit(0);
		});
		file.add(exit);
		add(file);
		// change speed menu
		JMenu speed = new JMenu("speed");
		JMenuItem plus20percent = new JMenuItem("+100%");
		plus20percent.addActionListener(e -> {
			Frame.simulationSpeed = (long)((double)Frame.simulationSpeed * 0.5);
		});
		speed.add(plus20percent);

		JMenuItem minus20percent = new JMenuItem("-100%");
		minus20percent.addActionListener(e -> {
			Frame.simulationSpeed = (long)((double)Frame.simulationSpeed * 2);
		});
		speed.add(minus20percent);

		JMenuItem visualize = new JMenuItem("ultra fast mode");
		visualize.addActionListener(e -> {
			Frame.visualize = !Frame.visualize;
			if(!Frame.visualize){
				Frame.simulationSpeed = 0;
			}
			else{
				Frame.simulationSpeed = 100;
			}
		});
		speed.add(visualize);
		add(speed);
	}
	public static MenuBar getBar() {
		if(bar == null) {
			bar = new MenuBar();
		}
		return bar;
	}
}
