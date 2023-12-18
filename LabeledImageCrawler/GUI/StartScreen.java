package GUI;

import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.Canvas;

public class StartScreen extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					StartScreen frame = new StartScreen();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 * @throws IOException
	 */
	public StartScreen() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 400, 420);
		try {
			contentPane = new JPanel() {
				private BufferedImage backgroundImage = ImageIO.read(new File("Image/startscreen.png"));
				public void paint( Graphics g ) {
					super.paint(g);
				    g.drawImage(backgroundImage, 0, 0, null);
				}
			};
		} catch (IOException e) {
			e.printStackTrace();
		}
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
	}

}
