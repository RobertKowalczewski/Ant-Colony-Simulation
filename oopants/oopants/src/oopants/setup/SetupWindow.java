package oopants.setup;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JOptionPane;

import oopants.Main;

public class SetupWindow extends JFrame implements ActionListener {
	/**
	 * this is where youset up all the parameters for the simulation
	 */
	private static SetupWindow window;
	JTextField mapSize;
	JLabel sizeDescription;
	JTextField numberOfAnts;
	JLabel antsDescription;
	JTextField numberOfFood;
	JLabel foodDescription;
	JTextField mu;
	JLabel muDesc;
	JTextField si;
	JLabel siDesc;
	JLabel tileSizeDesc;
	JTextField tileSize;
	JLabel foodSigmaDesc;
	JTextField foodSigma;
	JButton submit;
	JLabel message;
	JButton help;

	private SetupWindow() {
		setSize(400, 330);
		setResizable(false);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLayout(null);
		setTitle("Welcome to ant simulator");
		setLocationRelativeTo(null);

		sizeDescription = new JLabel("Enter map size (it will be square):");
		sizeDescription.setBounds(10, 10, 200, 20);
		add(sizeDescription);

		mapSize = new JTextField("100");
		mapSize.setBounds(240, 10, 100, 20);
		add(mapSize);

		antsDescription = new JLabel("Enter number of ants for each colony: ");
		antsDescription.setBounds(10, 40, 230, 20);
		add(antsDescription);

		numberOfAnts = new JTextField("100");
		numberOfAnts.setBounds(240, 40, 100, 20);
		add(numberOfAnts);

		foodDescription = new JLabel("Enter amount of food: ");
		foodDescription.setBounds(10, 70, 200, 20);
		add(foodDescription);

		numberOfFood = new JTextField("400");
		numberOfFood.setBounds(240, 70, 100, 20);
		add(numberOfFood);

		muDesc = new JLabel("Enter mu: ");
		muDesc.setBounds(10, 100, 200, 20);
		add(muDesc);

		mu = new JTextField("200");
		mu.setBounds(240, 100, 100, 20);
		add(mu);

		siDesc = new JLabel("Enter si: ");
		siDesc.setBounds(10, 130, 200, 20);
		add(siDesc);

		si = new JTextField("50");
		si.setBounds(240, 130, 100, 20);
		add(si);

		tileSizeDesc = new JLabel("Enter tile size: ");
		tileSizeDesc.setBounds(10, 160, 200, 20);
		add(tileSizeDesc);

		tileSize = new JTextField("4");
		tileSize.setBounds(240, 160, 100, 20);
		add(tileSize);

		foodSigmaDesc = new JLabel("Enter food std: ");
		foodSigmaDesc.setBounds(10, 190, 200, 20);
		add(foodSigmaDesc);
		
		foodSigma = new JTextField("10");
		foodSigma.setBounds(240, 190, 100, 20);
		add(foodSigma);
		
		submit = new JButton("submit");
		submit.setBounds(50, 220, 100, 20);
		submit.addActionListener(this);
		submit.setFocusable(false);
		add(submit);

		message = new JLabel();
		message.setBounds(10, 250, 300, 20);
		add(message);

		help = new JButton("help");
		help.setBounds(220, 220, 100, 20);
		help.addActionListener(e -> {
			JOptionPane.showMessageDialog(null, "- map size - size of map (in squares)\n- mu - normal distribution parameter - average length of life of ants\n- si - normal distribution parameter - standard deviation of how life of ants is distributed\n- food std is how far from the middle food is distributed");
		});
		help.setFocusable(false);
		add(help);
	}

	public static void start() {
		if (window == null) {
			window = new SetupWindow();
			window.setVisible(true);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			Main.size = Integer.parseInt(mapSize.getText());
			Main.ants = Integer.parseInt(numberOfAnts.getText());
			Main.food = Integer.parseInt(numberOfFood.getText());
			Main.mean = Double.parseDouble(mu.getText());
			Main.std = Double.parseDouble(si.getText());
			Main.tileSize = Integer.parseInt(tileSize.getText());
			Main.foodStd = Integer.parseInt(foodSigma.getText());
		} catch (Exception ex) {
			message.setText("ERROR: fields must be numbers!");
			return;
		}
		int s = Main.size * Main.tileSize;
		if(s <= 0 || s > 1000){
			message.setText("ERROR: map size out of bounds!");
			return;
		}
		if(Main.ants < 0 || Main.food < 0 || Main.mean < 0 || Main.std < 0 || Main.tileSize < 0 || Main.foodStd < 0){
			message.setText("ERROR: everything has to be greater than 0!");
			return;
		}
		if(Main.size*Main.size < Main.food + 2*Main.ants){
			message.setText("ERROR: map is too small");
			return;
		}
		dispose();
		Main.sem.release();
	}
}
