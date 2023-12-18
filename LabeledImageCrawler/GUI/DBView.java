package GUI;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import DB.SqlCRUD;

public class DBView extends JPanel {

	private static final long serialVersionUID = 1L;
	BufferedImage image;
	JLabel lblImage, lblName, lblKeyword, lblUrl;
	JLabel lbln = new JLabel("Name :");
	JLabel lblk = new JLabel("Keyword :");
	JLabel lblu = new JLabel("URL :");
	JButton next = new JButton("Next");
	JTable table;

	/**
	 * Create the panel.
	 */
	public DBView(SqlCRUD sql) {
		// Preview Panel
		JPanel pnlPreview = new JPanel(new GridLayout(3, 2));
		pnlPreview.add(lbln);
		lblName = new JLabel("");
		pnlPreview.add(lblName);
		pnlPreview.add(lblk);
		lblKeyword = new JLabel("");
		pnlPreview.add(lblKeyword);
		pnlPreview.add(lblu);
		lblUrl = new JLabel("");
		pnlPreview.add(lblUrl);

		showSingleResult(sql);
		this.add(lblImage);

		// Scroll Panel
		table = new JTable(sql.ReadAsTable());
		table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
		    public void valueChanged(ListSelectionEvent lse) {
		        if (!lse.getValueIsAdjusting()) {
		        	int row = table.getSelectedRow();
		        	String imgPath = table.getModel().getValueAt(row, 3).toString();
		        	updateImage(imgPath.substring(imgPath.indexOf("/")+1));
		        	String name = table.getModel().getValueAt(row, 0).toString();
		        	lblName.setText(name.substring(0, Math.min(name.length(), 20)));
					lblKeyword.setText(table.getModel().getValueAt(row, 1).toString());
					String url = table.getModel().getValueAt(row, 2).toString();
					lblUrl.setText("<html><a href='" + url + "'>Image URL</a></html>");
		            System.out.println("Selection Changed");
		        }
		    }
		});
		JScrollPane scrollPane = new JScrollPane(table); // or can you other swing component

		this.add(pnlPreview);
		this.add(scrollPane);


		next.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

			}
		});
	}

	public void showSingleResult(SqlCRUD sql) {
		try
		{
			if(sql.rs == null)
			{
				sql.ReadFromDB();
			}

			if(sql.rs.next() && !sql.rs.isAfterLast())
			{
				parseResult(sql.rs);
			}

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}


	}

	public void parseResult(ResultSet rs) {
		try{
			String imgPath = rs.getString("Address");
			updateImage(imgPath.substring(imgPath.indexOf("/")+1));

			String name = rs.getString("Name");
			lblName.setText(name.substring(0, Math.min(name.length(), 20)));
			lblKeyword.setText(rs.getString("Keyword"));
			String url = rs.getString("Url");
			lblUrl.setText("<html><a href='" + url + "'>Image URL</a></html>");
            System.out.println("Selection Changed");
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
	}

	public void updateImage(String filePath) {
		try {
			image = ImageIO.read(new File(filePath));
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (lblImage == null) {
			lblImage = new JLabel();
		}
		int scaleX = 200;
		int scaleY = 120;
		Image newImage = image.getScaledInstance(200, 120,  java.awt.Image.SCALE_SMOOTH);
		int imageType = image.getType();
		if(imageType == 0) imageType = 5;
		BufferedImage buffered = new BufferedImage(scaleX, scaleY, imageType);
		buffered.getGraphics().drawImage(newImage, 0, 0 , null);
		ImageIcon icon = new ImageIcon(buffered);
		lblImage.setIcon(icon);
	}

}
