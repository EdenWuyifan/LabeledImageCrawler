package GUI;

import static org.hamcrest.Matchers.stringContainsInOrder;

import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.PrintStream;
import java.sql.Connection;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;

import DB.SqlCRUD;
import Server.FinishCrawlException;
import Server.Server;
import Server.Utils;

public class LICApp implements ActionListener {

	JFrame frame, dbFrame;
	JButton btnStartCrawl, btnStopCrawl, btnViewDB;
	JLabel lblRootUrl, lblKeywords, lblMaxDepth;
	JTextField rootUrl, keywords;
	JSpinner maxDepth;
	JTextArea textArea;
	public static PrintStream out;
	public static SqlCRUD sql;

	Server server;

	public LICApp() {
		sql = new SqlCRUD("test.db");
	}

	public static void main(String[] args) {
		StartScreen startScreen = new StartScreen();
		LICApp lic = new LICApp();
		JFrame licUI = lic.createUI();

		startScreen.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				startScreen.setVisible(false);
				licUI.setVisible(true);
			}
			@Override
			public void mousePressed(MouseEvent e) {}
			@Override
			public void mouseReleased(MouseEvent e) {}
			@Override
			public void mouseEntered(MouseEvent e) {}
			@Override
			public void mouseExited(MouseEvent e) {}
		});
		startScreen.setVisible(true);
		licUI.setVisible(false);
	}

	private JFrame createUI() {
		frame = new JFrame("LIC");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		Container c = frame.getContentPane();
		c.setLayout(new BoxLayout(c,BoxLayout.Y_AXIS));

		// Labels
		lblRootUrl = new JLabel("Root URL :");
		lblKeywords = new JLabel("Keywords :");
		lblMaxDepth = new JLabel("Max depth :");

		// TextFields
		rootUrl = new JTextField("", 30);
		keywords = new JTextField();

		// DepthFields
		int currentDepth = 5;
		maxDepth = new JSpinner(new SpinnerNumberModel(currentDepth,
				1, 100, 1));


		// Buttons
		btnStartCrawl = new JButton("Start Crawl");
		btnStartCrawl.addActionListener(this);

		btnStopCrawl = new JButton("Stop Crawl");
		btnStopCrawl.addActionListener(this);

		btnViewDB = new JButton("View DB");
		btnViewDB.addActionListener(this);

		// Text Output
		textArea = new JTextArea(10, 30);
	    textArea.setEditable(false);
	    JScrollPane scrollPane = new JScrollPane(textArea);

		out = new PrintStream(new CustomOutputStream(textArea));


		// Panels
		JPanel pnlInput = new JPanel(new GridLayout(1, 2));
		JPanel subPnlInput = new JPanel(new GridLayout(1, 4));

		pnlInput.add(lblRootUrl);
		pnlInput.add(rootUrl);

		subPnlInput.add(lblKeywords);
		subPnlInput.add(keywords);
		subPnlInput.add(lblMaxDepth);
		subPnlInput.add(maxDepth);

		JPanel pnlButton = new JPanel(new GridLayout(1, 3));
		pnlButton.add(btnStartCrawl);
		pnlButton.add(btnStopCrawl);
		pnlButton.add(btnViewDB);


		JPanel pnlOutput = new JPanel(new GridLayout(1, 1));
		pnlOutput.add(scrollPane);

		frame.add(pnlInput);
		frame.add(subPnlInput);
		frame.add(pnlButton);
		frame.add(pnlOutput);

		frame.pack();
		return frame;

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();

		switch (cmd) {
		case "Start Crawl": {
			out.println("[SUCCESS] Crawler job start!");
			String url = rootUrl.getText();
			if (!Utils.checkUrlValid(url)) {
				out.println("[ERROR] Url is invalid!!!");
				return;
			}
			String keyword = keywords.getText().trim();
			int depth = (Integer)maxDepth.getValue();
			server = new Server(url, keyword, depth);
			try {
				server.start();
			} catch (FinishCrawlException err) {
				out.println("[SUCCESS] Crawler job finish!");
				LICApp.out.println("Successfully crawled " + Server.resultCount + " images with keyword " + keyword + "~");
			} catch (Exception err) {
				err.printStackTrace();
				out.println("[ERROR] Start server: " + err.getMessage());
			}
			break;
		}
		case "Stop Crawl": {
			if (server != null) {
				out.println("[SUCCESS] Current Crawler stopped!");
				server.interrupt();
				server = null;
			}
			break;
		}
		case "View DB": {
			DBView dbView = new DBView(sql);
			dbFrame = new JFrame("LIC");
			dbFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			dbFrame.add(dbView);
			dbFrame.setSize(600, 400);
			dbFrame.setVisible(true);
		}
		}
	}

}
