package DB;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.table.DefaultTableModel;

import GUI.LICApp;
import Server.Server;


public class SqlCRUD {

	public Connection conn;
	private String insertSQL = "INSERT INTO LabeledImage (Name,Keyword,Url,Address) " +
			"VALUES (?,?,?,?)";
	private String querySQL = "SELECT Name, Keyword, Url, Address FROM LabeledImage";
	private PreparedStatement insertStatement, queryStatement;
	public ResultSet rs;

	public SqlCRUD(String filename) {
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		String url = "jdbc:sqlite:" + filename;

        try {
        	conn = DriverManager.getConnection(url);
            if (conn != null) {
                DatabaseMetaData meta = conn.getMetaData();
                System.out.println("The driver name is " + meta.getDriverName());
                System.out.println("A new database has been created.");
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        try {
        	String sql = "CREATE TABLE IF NOT EXISTS LabeledImage (\n"
                    + "	Name text PRIMARY KEY,\n"
                    + "	Keyword text,\n"
                    + "	Url text,\n"
                    + " Address text\n"
                    + ");";
        	conn.createStatement().execute(sql);
			insertStatement = conn.prepareStatement(insertSQL);
			queryStatement = conn.prepareStatement(querySQL);
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}

	// Create
	public void Create(LabeledImage item) {
		try {
			insertStatement.setString(1, item.name);
			insertStatement.setString(2, item.keyword);
			insertStatement.setString(3, item.url);
			insertStatement.setString(4, item.address);
			insertStatement.execute();
		} catch(Exception e) {
			// createMessageBox(e.getMessage());
			// LICApp.out.println("[SQL-Create] Error: " + e.getMessage());
			return;
		}
		// createMessageBox("Inserted Successfully");
		Server.resultCount++;
	}

	// Read
	public void ReadFromDB() {
		try {
			rs = queryStatement.executeQuery();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public DefaultTableModel ReadAsTable() {
		DefaultTableModel table = null;
		ReadFromDB();
		try {
			table = buildTableModel(rs);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return table;
	}

	private void createMessageBox(String msg)
	{
		JFrame frame = new JFrame("Result");
		JLabel lbl = new JLabel(msg);
		frame.add(lbl);
		frame.setSize(200,200);
		frame.setVisible(true);
	}


	public static DefaultTableModel buildTableModel(ResultSet rs) throws SQLException {

		ResultSetMetaData metaData = rs.getMetaData();

		// names of columns
		Vector<String> columnNames = new Vector<String>();
		int columnCount = metaData.getColumnCount();
		for (int column = 1; column <= columnCount; column++) {
		    columnNames.add(metaData.getColumnName(column));
		}

		// data of the table
		Vector<Vector<Object>> data = new Vector<Vector<Object>>();
		while (rs.next()) {
		    Vector<Object> vector = new Vector<Object>();
		    for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
		        vector.add(rs.getObject(columnIndex));
		    }
		    data.add(vector);
		}

		return new DefaultTableModel(data, columnNames);

	}
}
