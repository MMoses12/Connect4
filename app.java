package ce326.hw3;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;

import javax.swing.*;
import javax.swing.UIManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

public class app implements KeyListener {
	static JFrame frame;
	JFrame helpFrame;
	JFrame menuFrame;
	static JToggleButton darkMode;
	JMenuBar bar;
	board playBoard;
    static JLayeredPane boardImg, menuImg;
	static JRadioButtonMenuItem rad1 = new JRadioButtonMenuItem("You");
	static JRadioButtonMenuItem rad2 = new JRadioButtonMenuItem("AI");
	static boolean keyPressed = false;

	// Find the path of the class to find the Extras folder.
	static Class<?> cls = app.class;
	
	// Main menu frame.
	public app () {
		menuFrame = new JFrame("Connect 4");

		// Make the Nimbus feel.
		makeModern();

		menuImg = new JLayeredPane();

		URL backgroundURL = cls.getResource("./Extras/score4.jpg");
		ImageIcon backgroundImg = new ImageIcon(backgroundURL);
		JLabel background = new JLabel(backgroundImg);
		background.setBounds(0, 0, backgroundImg.getIconWidth(), backgroundImg.getIconHeight());

		menuImg.setLayout(new BorderLayout());
		menuImg.add(background);

		JButton playBtn = new JButton("Play");
		playBtn.setPreferredSize(new Dimension(80,80));
        playBtn.setContentAreaFilled(true);
        playBtn.setBorderPainted(true);
		playBtn.addActionListener(new ActionListener() {
			public void actionPerformed (ActionEvent e) {
				menuFrame.setVisible(false);
				play();
			}
		});
		playBtn.setBounds(260, 290, 80, 50);
		background.add(playBtn);

		menuFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		menuFrame.setVisible(true);
		menuFrame.setLayout(new BorderLayout());

		menuFrame.add(menuImg, BorderLayout.WEST);
		menuFrame.pack();
		menuFrame.setResizable(false);
	}

	public void makeComponents () {
		makeModern();

		boardImg = new JLayeredPane();
        boardImg.setPreferredSize(new Dimension(640, 480));
		
		// Make the background board.
		URL imgURL = cls.getResource("./Extras/board.png");
		ImageIcon imgBoard = new ImageIcon(imgURL);
		JLabel img = new JLabel(imgBoard);
		img.setBounds(0,0, imgBoard.getIconWidth(), imgBoard.getIconHeight());

		boardImg.add(img);

		// Make the menu for the app.
        JMenu gameMenu = new JMenu("New Game");

		JMenu player = new JMenu("1st Player");

        JButton history = new JButton("History");
		history.addActionListener(new ActionListener() {
			public void actionPerformed (ActionEvent act) {
				showHistory();

				frame.requestFocus();
			}
		});
		history.setPreferredSize(player.getPreferredSize());
		history.setBackground(player.getBackground());
		history.setContentAreaFilled(false);
		history.setBorderPainted(false);
		Insets margin1 = history.getMargin();
		history.setMargin(new Insets(margin1.top, -10, margin1.bottom, margin1.right));

		JButton help = new JButton("Help");
		help.addActionListener(new ActionListener() {
			public void actionPerformed (ActionEvent act) {
				createHelpFrame();

				frame.requestFocus();
			}
		});
		help.setPreferredSize(player.getPreferredSize());
		help.setBackground(player.getBackground());
		help.setContentAreaFilled(false);
		help.setBorderPainted(false);
		Insets margin2 = help.getMargin();
		help.setMargin(new Insets(margin2.top, -10, margin2.bottom, margin2.right));

		darkMode = new JToggleButton("Light Mode", true);

		darkMode.setPreferredSize(player.getPreferredSize());
		darkMode.setBackground(player.getBackground());
		darkMode.setContentAreaFilled(false);
		darkMode.setBorderPainted(false);
		Insets margin = darkMode.getMargin();
		darkMode.setMargin(new Insets(margin.top, +320, margin.bottom, margin.right));
		
        bar = new JMenuBar();
        bar.add(gameMenu);
		bar.add(player);
        bar.add(history);
		bar.add(help);
		bar.add(darkMode);
        frame.setJMenuBar(bar);

		ButtonGroup btns = new ButtonGroup();
		rad1.setBackground(new Color(128, 128, 128));
		rad1.setForeground(Color.BLACK);

		rad2.setBackground(new Color(128, 128, 128));
		rad2.setForeground(Color.BLACK);

		if (!rad1.isSelected() && !rad2.isSelected())	
			rad2.setSelected(true);

		darkMode.addActionListener(new ActionListener() {
			public void actionPerformed (ActionEvent e) {
				if (darkMode.isSelected()) {
					setDarkMode();
					darkMode.setText("Light Mode");
				}
				else {
					setLightMode();
					darkMode.setText("Dark Mode");
				}
			}
		});
		
		JMenuItem pvp = new JMenuItem("2 Players");
		pvp.addActionListener(new ActionListener() {
			public void actionPerformed (ActionEvent e) {	
				board.AIOpp = false;
				createNewGame();
			}
		});
		gameMenu.add(pvp);

		JMenuItem trivial = new JMenuItem("Trivial");
		trivial.addActionListener(new ActionListener() {
			public void actionPerformed (ActionEvent e) {	
				board.AIOpp = true;

				AIPlayer.depth = 1;
				createNewGame();
			}
		});
		gameMenu.add(trivial);

		JMenuItem medium = new JMenuItem("Medium");
		medium.addActionListener(new ActionListener() {
			public void actionPerformed (ActionEvent e) {	
				board.AIOpp = true;

				AIPlayer.depth = 3;	
				createNewGame();
			}
		});
		gameMenu.add(medium);

		JMenuItem hard = new JMenuItem("Hard");
		hard.addActionListener(new ActionListener() {
			public void actionPerformed (ActionEvent e) {	
				board.AIOpp = true;

				AIPlayer.depth = 5;
				createNewGame();
			}
		});
		gameMenu.add(hard);
		
		btns.add(rad1);
		btns.add(rad2);

		player.add(rad1);
		player.add(rad2);

		// Make the layout for the buttons on the image.
        img.setLayout(new GridLayout(1,7, 4, 4));

		// Make the buttons for the columns.
        JButton btn1 = new JButton();
        btn1.setPreferredSize(new Dimension(80,80));
        btn1.setContentAreaFilled(false);
        btn1.setBorderPainted(false);

		btn1.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					// double click detected on myButton
					if (board.gameEnded == false) {
						board.move(0);
						board.checkAI();
					}
				}
				frame.requestFocus();
			}
		});
        img.add(btn1);
        
        JButton btn2 = new JButton();
        btn2.setPreferredSize(new Dimension(80,80));
        btn2.setContentAreaFilled(false);
        btn2.setBorderPainted(false);

		btn2.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					// double click detected on myButton
					if (board.gameEnded == false) {
						board.move(1);
						board.checkAI();
					}
				}
				frame.requestFocus();
			}
		});
        img.add(btn2);
        
        JButton btn3 = new JButton();
        btn3.setPreferredSize(new Dimension(80,80));
        btn3.setContentAreaFilled(false);
        btn3.setBorderPainted(false);

		btn3.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					// double click detected on myButton
					if (board.gameEnded == false) {
						board.move(2);
						board.checkAI();
					}
				}
				frame.requestFocus();
			}
		});
        img.add(btn3);
        
        JButton btn4 = new JButton();
        btn4.setPreferredSize(new Dimension(80,80));
        btn4.setContentAreaFilled(false);
        btn4.setBorderPainted(false);

		btn4.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					// double click detected on myButton
					if (board.gameEnded == false) {
						board.move(3);
						board.checkAI();
					}
				}
				frame.requestFocus();
			}
		});
        img.add(btn4);
        
        JButton btn5 = new JButton();
        btn5.setPreferredSize(new Dimension(80,80));
        btn5.setContentAreaFilled(false);
        btn5.setBorderPainted(false);

		btn5.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					// double click detected on myButton
					if (board.gameEnded == false) {
						board.move(4);
						board.checkAI();
					}
				}
				frame.requestFocus();
			}
		});
        img.add(btn5);
        
        JButton btn6 = new JButton();
        btn6.setPreferredSize(new Dimension(80,80));
        btn6.setContentAreaFilled(false);
        btn6.setBorderPainted(false);

		btn6.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					// double click detected on myButton
					if (board.gameEnded == false) {
						board.move(5);
						board.checkAI();
					}
				}
				frame.requestFocus();
			}
		});
        img.add(btn6);
        
        JButton btn7 = new JButton();
        btn7.setPreferredSize(new Dimension(80,80));
        btn7.setContentAreaFilled(false);
        btn7.setBorderPainted(false);

		btn7.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					// double click detected on myButton
					if (board.gameEnded == false) {
						board.move(6);
						board.checkAI();
					}
				}
				frame.requestFocus();
			}
		});
        img.add(btn7);

		// Add the final board to the JFrame.
        frame.add(boardImg);
		playBoard = new board();
		frame.pack();
		frame.setVisible(true);
		frame.addKeyListener(this);
		frame.setFocusable(true);
	}

	public void makeHistoryComponents () {
		makeModern();

		boardImg = new JLayeredPane();
        boardImg.setPreferredSize(new Dimension(640, 480));
		
		// Make the background board.
		URL imgURL = cls.getResource("./Extras/board.png");
		ImageIcon imgBoard = new ImageIcon(imgURL);
		JLabel img = new JLabel(imgBoard);
		img.setBounds(0,0, imgBoard.getIconWidth(), imgBoard.getIconHeight());

		boardImg.add(img);

		// Make the menu for the app.
        JMenu gameMenu = new JMenu("New Game");

		JMenu player = new JMenu("1st Player");

        JButton history = new JButton("History");
		history.addActionListener(new ActionListener() {
			public void actionPerformed (ActionEvent act) {
				showHistory();

				frame.requestFocus();
			}
		});
		history.setPreferredSize(player.getPreferredSize());
		history.setBackground(player.getBackground());
		history.setContentAreaFilled(false);
		history.setBorderPainted(false);
		Insets margin1 = history.getMargin();
		history.setMargin(new Insets(margin1.top, -10, margin1.bottom, margin1.right));

		JButton help = new JButton("Help");
		help.addActionListener(new ActionListener() {
			public void actionPerformed (ActionEvent act) {
				createHelpFrame();

				frame.requestFocus();
			}
		});
		help.setPreferredSize(player.getPreferredSize());
		help.setBackground(player.getBackground());
		help.setContentAreaFilled(false);
		help.setBorderPainted(false);
		Insets margin2 = help.getMargin();
		help.setMargin(new Insets(margin2.top, -10, margin2.bottom, margin2.right));

		darkMode = new JToggleButton("Light Mode", true);

		darkMode.setPreferredSize(player.getPreferredSize());
		darkMode.setBackground(player.getBackground());
		darkMode.setContentAreaFilled(false);
		darkMode.setBorderPainted(false);
		Insets margin = darkMode.getMargin();
		darkMode.setMargin(new Insets(margin.top, +320, margin.bottom, margin.right));
		
        bar = new JMenuBar();
        bar.add(gameMenu);
		bar.add(player);
        bar.add(history);
		bar.add(help);
		bar.add(darkMode);
        frame.setJMenuBar(bar);

		ButtonGroup btns = new ButtonGroup();
		rad1.setBackground(new Color(128, 128, 128));
		rad1.setForeground(Color.BLACK);

		rad2.setBackground(new Color(128, 128, 128));
		rad2.setForeground(Color.BLACK);

		if (!rad1.isSelected() && !rad2.isSelected())	
			rad2.setSelected(true);

		darkMode.addActionListener(new ActionListener() {
			public void actionPerformed (ActionEvent e) {
				if (darkMode.isSelected()) {
					setDarkMode();
					darkMode.setText("Light Mode");
				}
				else {
					setLightMode();
					darkMode.setText("Dark Mode");
				}
			}
		});
		
		JMenuItem pvp = new JMenuItem("2 Players");
		pvp.addActionListener(new ActionListener() {
			public void actionPerformed (ActionEvent e) {	
				board.AIOpp = false;
				createNewGame();
			}
		});
		gameMenu.add(pvp);

		JMenuItem trivial = new JMenuItem("Trivial");
		trivial.addActionListener(new ActionListener() {
			public void actionPerformed (ActionEvent e) {	
				board.AIOpp = true;

				AIPlayer.depth = 1;
				createNewGame();
			}
		});
		gameMenu.add(trivial);

		JMenuItem medium = new JMenuItem("Medium");
		medium.addActionListener(new ActionListener() {
			public void actionPerformed (ActionEvent e) {	
				board.AIOpp = true;

				AIPlayer.depth = 3;	
				createNewGame();
			}
		});
		gameMenu.add(medium);

		JMenuItem hard = new JMenuItem("Hard");
		hard.addActionListener(new ActionListener() {
			public void actionPerformed (ActionEvent e) {	
				board.AIOpp = true;

				AIPlayer.depth = 5;
				createNewGame();
			}
		});
		gameMenu.add(hard);
		
		btns.add(rad1);
		btns.add(rad2);

		player.add(rad1);
		player.add(rad2);

		// Add the final board to the JFrame.
        frame.add(boardImg);
		playBoard = new board();
		frame.pack();
		frame.setVisible(true);
		frame.addKeyListener(this);
		frame.setFocusable(true);
	}

	// Play frame.
    public void play () {
		frame = new JFrame("Connect four - v.1.0");
		
		frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

		makeComponents();

		board.checkAI();
        
		((JComponent)frame.getRootPane()).setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		((JComponent) frame.getContentPane()).setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
    }

	// Determine which color piece must fall.
	static void determinePiece (int col) {
		if (board.player == 2) {	
			dropYellow(board.colPlayed[col], col);
		}
		else {
			dropRed(board.colPlayed[col], col);
		}
	}

	// Key pressed method for keyListener.
	@Override
	public void keyPressed(KeyEvent k) {
		int key = k.getKeyCode();
		int col = 0;

		if (keyPressed == false) {
			keyPressed = true;
			// Check if a number key was pressed
			if (board.gameEnded == false) {	
				if (key >= KeyEvent.VK_0 && key <= KeyEvent.VK_6) {
				// Get the column number from the key code
					col = k.getKeyChar() - '0';
					
					if (board.gameEnded == false) {
						board.move(col);
						board.checkAI();
					}
				}
				else if (key >= KeyEvent.VK_NUMPAD0 && key <= KeyEvent.VK_NUMPAD6) {
					// Get the column number from the key code
					col = k.getKeyChar() - KeyEvent.VK_NUMPAD0 + '0';
					
					if (board.gameEnded == false) {
						board.move(col);
						board.checkAI();
					}
				}

				frame.requestFocus();
			}
		}
	}
	@Override
	public void keyReleased(KeyEvent e) { 
		keyPressed = false;
	}
	@Override
	public void keyTyped(KeyEvent e) { }

	// Create the frame for the help instructions.
	public void createHelpFrame() {
		JDialog helpDialog = new JDialog(frame, "Rules", true);
		helpDialog.setBounds(400, 400, 650, 400);
		helpDialog.setResizable(false);
	
		JPanel helpPanel = new JPanel(new BorderLayout());

		// Take the text from the rules.txt file.
		ClassLoader classLoader = getClass().getClassLoader();
		InputStream inputStream = classLoader.getResourceAsStream("ce326/hw3/Extras/rules.txt");
		Scanner scanner = new Scanner(inputStream).useDelimiter("\\A");
		String text = scanner.hasNext() ? scanner.next() : "";
		JTextArea label = new JTextArea(text);
		JScrollPane helpPane = new JScrollPane(label);
		if (darkMode.isSelected()) {
			label.setBackground(Color.DARK_GRAY);
			label.setForeground(Color.WHITE);
			helpPane.setBackground(Color.DARK_GRAY);
		}
		else {
			label.setBackground(Color.WHITE);
			label.setForeground(Color.BLACK);
			helpPane.setBackground(Color.DARK_GRAY);
		}
		
		scanner.close();
	
		label.setEditable(false);
		label.setFont(label.getFont().deriveFont(14f));
	
		helpPanel.add(helpPane);
	
		helpDialog.add(helpPanel, BorderLayout.CENTER);
		helpDialog.setVisible(true);
	}

	// Create new game method.
	public void createNewGame () {
		Component[] components = frame.getContentPane().getComponents();
		for (Component component : components) {
			frame.remove(component);
		}

		frame.revalidate();
		frame.repaint();

		makeComponents();
		
		board.checkAI();
	}

	// Show the history games in JList.
	public void showHistory() {
		frame.dispose();
		frame = new JFrame("History");
		frame.setBackground(new Color(128, 128, 128));
	
		// Create a JList to display the buttons
		JList<File> fileList = new JList<>();
		DefaultListModel<File> listModel = new DefaultListModel<>();

		fileList.setBackground(new Color(128, 128, 128));
	
		String homePath = System.getProperty("user.home");
		String directoryPath = homePath + "/Connect4";
	
		File directory = new File(directoryPath);
	
		// Get an array of File objects representing the files in the directory
		File[] files = directory.listFiles();
	
		// Add each file to the ListModel
		for (File file : files) {
			listModel.addElement(file);
		}
		fileList.setModel(listModel);
	
		fileList.setCellRenderer(new DefaultListCellRenderer() {
			public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
				File file = (File) value;
		
				// Create a button-like label for each file
				JLabel label = new JLabel(createFileName(file));
				label.setOpaque(true);
		
				// Set the Nimbus look and feel colors
				label.setBackground(isSelected ? UIManager.getColor("nimbusSelectionBackground") : UIManager.getColor("nimbusDisabledText"));
				label.setForeground(isSelected ? UIManager.getColor("nimbusSelectedText") : UIManager.getColor("text"));
		
				return label;
			}
		});
		
		
		fileList.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					JList<?> list = (JList<?>) e.getSource();
					int index = list.locationToIndex(e.getPoint());
					if (index >= 0) {
						File selectedFile = listModel.getElementAt(index);
						showGame(selectedFile);
					}
				}
			}
		});
	
		frame.setPreferredSize(new Dimension(500, 400));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(new JScrollPane(fileList));
		frame.pack();
		frame.setVisible(true);
		frame.setFocusable(true);
		frame.setResizable(false);
	}	

	// Change the name of the files to correspond to the date and time
	// of the played game.
	private String createFileName (File file) {
		StringBuilder JSONstr = new StringBuilder();
		try (Scanner sc = new Scanner(file)) {
			while (sc.hasNextLine()) {
				String str = sc.nextLine();
				JSONstr.append(str);
				JSONstr.append("\n");
			}
		} catch (IOException ex) {
			return null;
		}
	
		StringBuilder message = new StringBuilder("");
		JSONObject json = new JSONObject(JSONstr.toString());
		message.append(json.getString("Date") + "   L: ");
	
		if (json.getString("Diff").equals("Hard")) {
			message.append("Hard     ");
		} else if (json.getString("Diff").equals("Medium")) {
			message.append("Medium    ");
		} else if (json.getString("Diff").equals("Trivial")) {
			message.append("Trivial   ");
		} else {
			message.append("2Player ");
		}
	
		message.append("   W:");
	
		if (json.getString("Winner").equals("Player")) {
			message.append("P");
		} else if (json.getString("Winner").equals("AI")) {
			message.append("AI");
		} else if (json.getString("Winner").equals("Player1")) {
			message.append("P1");
		} else {
			message.append("P2");
		}
	
		return message.toString();
	}
	
	// Show the game moves like they were done when playing.
	public void showGame(File file) {
		frame.dispose();
		frame = new JFrame("Connect four - v.1.0");
		
		frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

		makeHistoryComponents();
		playHistory(file);
        
		((JComponent)frame.getRootPane()).setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		((JComponent) frame.getContentPane()).setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
	}

	// Play the history game.
	public void playHistory (File file) {
		StringBuilder JSONstr = new StringBuilder();
    	try(Scanner sc = new Scanner(file)) {
			while( sc.hasNextLine() ) {
				String str = sc.nextLine();
				JSONstr.append(str);
				JSONstr.append("\n");
    		}
    	} catch(IOException ex) {
     	 	return;
  	  	}

		JSONObject json = new JSONObject(JSONstr.toString());
		if (json.getString("First").equals("Player")) {
			board.player = 1;
			board.playerFirst = true;
		}

		JSONArray moveArr = json.getJSONArray("Moves");

		int delay = 2000; // Delay in milliseconds (3 seconds)
		AtomicInteger moves = new AtomicInteger(0); // Counter variable

		Timer timer = new Timer(delay, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int currentMove = moves.getAndIncrement();

				if (currentMove < moveArr.length()) {
					board.move(moveArr.getInt(currentMove));
				} else {
					((Timer) e.getSource()).stop(); // Stop the timer when all moves are completed
				}
			}
		});

		timer.setInitialDelay(delay * moves.get()); // Set the initial delay based on the current move

		timer.start();
	}
	
	// Drop the yellow piece in the board.
	public static void dropYellow(int row, int col) {
	int xOffset = 90 * col;
	int yOffset = 80 * (6 - row);

	URL yellowURL = cls.getResource("./Extras/yellow.png");
	ImageIcon yellowImg = new ImageIcon(yellowURL);
	JLabel yellowPiece = new JLabel(yellowImg);
	yellowPiece.setPreferredSize(new Dimension(75, 75));
	yellowPiece.setBounds(15 + xOffset, 60 - 75, yellowImg.getIconWidth(), yellowImg.getIconHeight());
	boardImg.add(yellowPiece);

	int delay = 17; // milliseconds

	Timer timer = new Timer(delay, new ActionListener() {
		int y = 60 - 75;
		int dy = 5;

		public void actionPerformed(ActionEvent e) {
			y += dy;
			dy += 2;
			yellowPiece.setLocation(15 + xOffset, y);
			boardImg.repaint();

			if (y >= yOffset) {
				((Timer) e.getSource()).stop();
				yellowPiece.setLocation(15 + xOffset, yOffset + 5);
			}
		}
	});

		timer.start();
	}

	// Drop the red piece in the board.
	public static void dropRed(int row, int col) {
		int xOffset = 90 * col;
		int yOffset = 80 * (6 - row);

		URL redURL = cls.getResource("./Extras/red.png");
		ImageIcon redImg = new ImageIcon(redURL);
		JLabel redPiece = new JLabel(redImg);
		redPiece.setPreferredSize(new Dimension(75, 75));
		redPiece.setBounds(15 + xOffset, 60 - 75, redImg.getIconWidth(), redImg.getIconHeight());
		boardImg.add(redPiece);
	
		int delay = 17; // milliseconds
	
		Timer timer = new Timer(delay, new ActionListener() {
			int y = 60 - 75;
			int dy = 5;
	
			public void actionPerformed(ActionEvent e) {
				y += dy;
				dy += 2;
				redPiece.setLocation(15 + xOffset, y + 5);
				boardImg.repaint();
	
				if (y >= yOffset) {
					((Timer) e.getSource()).stop();
					redPiece.setLocation(15 + xOffset, yOffset + 5);
				}
			}
		});
	
		timer.start();
	}

	// Set dark mode for the app.
	public void setDarkMode() {
		// Set background color to dark gray
		frame.getContentPane().setBackground(new Color(128, 128, 128));
		
		// Set button colors to light gray and white
		for (Component c : frame.getComponents()) {
			if (c instanceof JButton) {
				c.setBackground(Color.LIGHT_GRAY);
				c.setForeground(Color.WHITE);
			}
		}
		
		// Set label and text field colors to white
		for (Component c : boardImg.getComponents()) {
			if (c instanceof JLabel || c instanceof JTextField) {
				c.setBackground(Color.WHITE);
				c.setForeground(Color.BLACK);
			}
		}
		
		bar.setBackground(Color.lightGray);
		bar.setForeground(Color.WHITE);
	}

	// Set light mode for the app.
	public void setLightMode() {
		// Set background color to light gray
		frame.getContentPane().setBackground(new Color(210, 210, 210));
		
		// Set button colors to dark gray and black
		for (Component c : frame.getComponents()) {
			if (c instanceof JButton) {
				c.setBackground(Color.WHITE);
				c.setForeground(Color.BLACK);
			}
		}
		
		// Set label and text field colors to black
		for (Component c : boardImg.getComponents()) {
			if (c instanceof JLabel || c instanceof JTextField) {
				c.setBackground(Color.WHITE);
				c.setForeground(Color.BLACK);
			}
		}
		
		bar.setBackground(Color.WHITE);
		bar.setForeground(Color.BLACK);
	}

	// Throw a message in a new window.
	public static void gameOverDialog() {
		JDialog dialog = new JDialog(frame, "Game Over", true);
		JLabel messageLabel;
		String message;

		if (board.AIOpp == false) {
			if (board.player == 1)
				message = "Player 1 Won!";
			else
				message = "Player 2 Won!";
		}
		else {
			if (board.player == 1)
				message = "You Won!";
			else
				message = "You Lost!";
		}

		messageLabel = new JLabel(message);
		messageLabel.setHorizontalAlignment(JLabel.CENTER);
		dialog.add(messageLabel, BorderLayout.CENTER);
	
		JButton okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dialog.dispose();
			}
		});

		JPanel buttonPanel = new JPanel();
		buttonPanel.add(okButton);
		dialog.add(buttonPanel, BorderLayout.SOUTH);
	
		if (darkMode.isSelected()) {
			dialog.setBackground(Color.DARK_GRAY);
			dialog.setForeground(Color.WHITE);
		}

		dialog.pack();
		dialog.setLocationRelativeTo(frame);
		dialog.setVisible(true);
	}

	// Make a window for multiple purposes with the wanted message.
	public static void makeWindow(String message) {
		JDialog dialog = new JDialog(frame, "", true);
		JLabel messageLabel;

		messageLabel = new JLabel(message);
		messageLabel.setHorizontalAlignment(JLabel.CENTER);
		dialog.add(messageLabel, BorderLayout.CENTER);
	
		JButton okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dialog.dispose();
			}
		});

		JPanel buttonPanel = new JPanel();
		buttonPanel.add(okButton);
		dialog.add(buttonPanel, BorderLayout.SOUTH);
	
		if (darkMode.isSelected()) {
			dialog.setBackground(Color.DARK_GRAY);
			dialog.setForeground(Color.WHITE);
		}

		dialog.pack();
		dialog.setLocationRelativeTo(frame);
		dialog.setVisible(true);
	}

	// Numbus feel for the app.
	private static void makeModern () {
		try {
			UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (Exception e) {
			e.printStackTrace();
		}
		UIManager.put("control", new Color(128, 128, 128));
		UIManager.put("info", new Color(128, 128, 128));
		UIManager.put("nimbusBase", new Color(18, 30, 49));
		UIManager.put("nimbusAlertYellow", new Color(248, 187, 0));
		UIManager.put("nimbusDisabledText", new Color(128, 128, 128));
		UIManager.put("nimbusFocus", new Color(115, 164, 209));
		UIManager.put("nimbusGreen", new Color(176, 179, 50));
		UIManager.put("nimbusInfoBlue", new Color(66, 139, 221));
		UIManager.put("nimbusLightBackground", new Color(18, 30, 49));
		UIManager.put("nimbusOrange", new Color(191, 98, 4));
		UIManager.put("nimbusRed", new Color(169, 46, 34));
		UIManager.put("nimbusSelectedText", new Color(255, 255, 255));
		UIManager.put("nimbusSelectionBackground", new Color(104, 93, 156));
		UIManager.put("text", new Color(18, 30, 49));
	}
}