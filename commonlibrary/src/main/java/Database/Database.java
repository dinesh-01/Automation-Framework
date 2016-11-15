package Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import util.ListUtility;

import common.Log;

/**
 * Exposes methods for database operation.
 * 
 * @author Administrator
 */
public class Database {
	static Connection connection = null;
	private static String sqlQuery;
	static Statement statement = null;

	/**
	 * This used in conjunction with FitNesse Query fixture.
	 * 
	 * @param sqlQuery
	 *            - query to run, query has to be specified with field name &
	 *            not select (*).
	 */
	public Database(String query) {
		sqlQuery = query;
	}

	/**
	 * This would be used when no query parameter is specified.
	 */
	public Database() {

	}

	/**
	 * Initializes the connection to database
	 * 
	 * @param databaseUrl
	 *            e.g. MySQL: //10.211.64.231/ci Postgres:
	 *            //10.211.181.112:5432/dcsc_so
	 * @param databaseName
	 *            - name of the database
	 * @param userName
	 *            - user name
	 * @param password
	 *            - password
	 * @throws SQLException 
	 * @throws ClassNotFoundException 
	 */
	public static void initialize(String databaseType, String databaseUrl,String userName,String password) throws SQLException, ClassNotFoundException
	{
		String driverName = null;
		String driverUrl = null;
		switch(databaseType.toLowerCase())
		{
			case "mysql":
				driverName = "com.mysql.jdbc.Driver";
				driverUrl = "jdbc:mysql:" + databaseUrl;
				break;
			case "mssql":
				driverName = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
				driverUrl = "jdbc:sqlserver:" + databaseUrl;
				break;
			case "vertica":
				driverName = "com.vertica.jdbc.Driver";
				driverUrl = "jdbc:vertica:" + databaseUrl;
				break;
			case "postgres":
				driverName = "org.postgresql.Driver";
				driverUrl = "jdbc:postgresql:" + databaseUrl;
			break;
		}
		Log.info("Database initialization is done");
		Log.info("driverName: " + driverName);
		Log.info("driverUrl: " + driverUrl);
		Log.info("dbUserName: " + userName);
		Log.info("dbPassword: " + password);
		// Load the driver class.
		Class.forName(driverName);
		if (null == connection || connection.isClosed()) {
			connection = DriverManager.getConnection(driverUrl, userName,
					password);
		}
	}

	/**
	 * This method executes select query on database and returns ArrayList of
	 * hash map. each hash map represents a row.
	 * 
	 * @param selectQuery
	 * @return
	 * @throws SQLException
	 */
	@SuppressWarnings("rawtypes")
	public static ArrayList<HashMap> executeQueryAndGetMapArrayList(
			String selectQuery) throws Exception {
		ResultSet resultSet = executeQuery(selectQuery);
		ArrayList<HashMap> resultMap = new ArrayList<HashMap>();
		ResultSetMetaData metaData = resultSet.getMetaData();
		int columnCount = metaData.getColumnCount();
		while (resultSet.next()) {
			HashMap<String, String> row = new HashMap<String, String>();
			for (int columnNo = 1; columnNo <= columnCount; columnNo++) {
				String cellValue = resultSet.getString(columnNo);
				String columnName = metaData.getColumnName(columnNo);
				row.put(columnName, cellValue);
			}
			resultMap.add(row);
		}
		return resultMap;
	}

	/**
	 * This method executes select query on database and returns ArrayList of
	 * ArrayList of Strings
	 * 
	 * @param selectQuery
	 * @return
	 * @throws Exception
	 */
	public ArrayList<ArrayList<String>> executeQueryAndGetArrayList(
			String selectQuery) throws Exception {

		ResultSet resultSet = executeQuery(selectQuery);
		ArrayList<ArrayList<String>> resultList = new ArrayList<ArrayList<String>>();
		ResultSetMetaData metaData = resultSet.getMetaData();
		int columnCount = metaData.getColumnCount();
		while (resultSet.next()) {
			ArrayList<String> row = new ArrayList<String>();
			for (int columnNo = 1; columnNo <= columnCount; columnNo++) {
				String cellValue = resultSet.getString(columnNo);
				row.add(cellValue);
			}
			resultList.add(row);
		}
		return resultList;
	}

	/**
	 * This method executes select query on database and returns a string
	 * containing 0th row of 0th column. This will be used to get a single value
	 * from a data table. example: Getting encrypted password for a user
	 * specified in the SQL query.
	 * 
	 * @param selectQuery
	 * @return
	 * @throws Exception
	 */
	public static String executeQueryAndGetValueInFirstColumnFirstRow(
			String selectQuery) throws Exception {
		ResultSet resultSet = executeQuery(selectQuery);
		String cellValue = null;
		while (resultSet.next()) {

			cellValue = resultSet.getString(1);

		}
		return cellValue;
	}

	/**
	 * Execute the sql query and return the count of updated rows. This method
	 * returns 0 for SQL statements, that returns nothing.
	 * 
	 * @param sqlQuery
	 * @return
	 * @throws SQLException
	 */
	public static int executeUpdate(String sqlQuery) throws Exception {
		Log.info("SQL query to be executed:" + sqlQuery);
		statement = connection.createStatement();
		int rowCount = statement.executeUpdate(sqlQuery);

		return rowCount;
	}

	/**
	 * Execute query and returns a result set.
	 * 
	 * @param sqlQuery
	 * @return
	 * @throws SQLException
	 */
	private static ResultSet executeQuery(String sqlQuery) throws Exception {
		Log.info("SQL query to be executed:" + sqlQuery);
		statement = connection.createStatement();
		ResultSet resultSet = statement.executeQuery(sqlQuery);
		return resultSet;
	}

	/**
	 * Close the database connection utility.
	 * 
	 * @throws SQLException
	 */
	public static void close() throws Exception {
		statement.close();
		connection.close();
	}

	/**
	 * Execute the sql query and return the count of updated rows. This method
	 * returns 0 for SQL statements, that returns nothing.
	 * 
	 * @param sqlQuery
	 * @return
	 * @throws SQLException
	 */
	private String SqlQuery;

	public void setSqlQuery(String SqlQuery) {
		this.SqlQuery = SqlQuery;
	}

	/**
	 * This method takes query from fitness page and executes it. This method
	 * can be used to delete records from table
	 * 
	 * @return
	 * @throws Exception
	 */
	public String executeSqlQuery() throws Exception {
		String result;
		int rowCount = Database.executeUpdate(SqlQuery);
		if (rowCount >= 0) {
			result = "pass";
		} else {
			result = "fail";
		}
		return result;
	}

	/**
	 * This method takes select query from fitness page and returns count of
	 * records for it. This method can be used for select query
	 * 
	 * @return
	 * @throws Exception
	 */
	public int getRowCountForSqlQuery() throws Exception {
		ResultSet resultSet = Database.executeQuery(SqlQuery);
		int rowCount = 0;
		while (resultSet.next()) {
			// Process the row.
			rowCount++;
		}
		Log.info("Query Result count is" + rowCount);
		return rowCount;
	}

	/**
	 * This is a default method required for SLIM query table fixture. This
	 * method generates a required list of data extracted from database table
	 * and send it to fitNesse Wiki page for verification.
	 * 
	 * @return List of data extracted from DB.
	 * @throws Exception
	 */
	public List<Object> query() throws Exception {

		@SuppressWarnings("rawtypes")
		ArrayList<HashMap> resultDb = Database
				.executeQueryAndGetMapArrayList(sqlQuery);
		sqlQuery = sqlQuery.toLowerCase();
		int indexToTruncateLaterPartOfQuery = sqlQuery.indexOf(" from ");

		// Get a substring from select query by removing "select " & string
		// after " from "
		String commaSeparatedfieldsFromSqlQuery = sqlQuery.substring(7,
				indexToTruncateLaterPartOfQuery).toLowerCase();

		// Split this string on ',' and get a array of fields.
		String[] fieldsFromSqlQuery = commaSeparatedfieldsFromSqlQuery
				.split(",");

		int i = 0;
		for (String SqlField : fieldsFromSqlQuery) {
			SqlField = SqlField.trim();

			// if the field has alias eg: question.id as QuestionID
			// then only alias will be chosen else, id will be choosed out of
			// question.id
			boolean aliasPresentFlag = SqlField.contains(" as ");
			boolean dotPresentFlag = SqlField.contains(".");

			if (aliasPresentFlag) {
				int index = SqlField.indexOf(" as ");
				SqlField = SqlField.substring((index + 4)).trim();
				fieldsFromSqlQuery[i] = SqlField;
			} else if (dotPresentFlag) {
				int dotIndex = SqlField.indexOf(".");
				if (!(dotIndex == -1)) {
					SqlField = SqlField.substring(dotIndex + 1).trim();
					fieldsFromSqlQuery[i] = SqlField;
				}
			} else {
				fieldsFromSqlQuery[i] = SqlField.trim();
			}
			Log.info("Field : " + fieldsFromSqlQuery[i]);
			i++;
		}

		List<Object> returnTableList = new ArrayList<Object>();
		List<Object> singleRowForTable = new ArrayList<Object>();
		Object[] tableRows = new Object[resultDb.size()];

		int rowCount = 0;
		Log.info("Total Records fetched from Datbase; " + resultDb.size());

		for (@SuppressWarnings("rawtypes")
		HashMap dbMap : resultDb) {

			Object[] fieldsDataToBeFetched = new Object[fieldsFromSqlQuery.length];
			Object[] rowFieldsValues = new Object[fieldsFromSqlQuery.length];
			List<String> singleFieldValue = new ArrayList<String>();

			i = 0;
			for (String sqlField : fieldsFromSqlQuery) {
				fieldsDataToBeFetched[i] = dbMap.get(sqlField);
				if (fieldsDataToBeFetched[i] == null) {
					fieldsDataToBeFetched[i] = "";
				}
				singleFieldValue = ListUtility.list(sqlField,
						fieldsDataToBeFetched[i].toString());
				rowFieldsValues[i] = singleFieldValue;
				i++;
			}

			singleRowForTable = ListUtility.list(rowFieldsValues);
			Log.info("Retrieved row for result is : "
					+ singleRowForTable.toString());
			tableRows[rowCount] = singleRowForTable;
			rowCount++;
		}

		returnTableList = ListUtility.list(tableRows);
		Log.info("Returning result as : " + returnTableList.toString());
		return returnTableList;
	}

	/**
	 * Execute select query and returns the result
	 * 
	 * @param selectQuery
	 * @param seperator
	 * @return
	 * @throws Exception
	 */
	public static String executeSelectQuery(String selectQuery, String seperator)
			throws Exception {
		ResultSet resultSet = executeQuery(selectQuery);
		String result = "";
		int itr = 0;
		while (resultSet.next()) {
			if (itr == 0) {
				result = result + resultSet.getString(1);
			} else {
				result = result + seperator + resultSet.getString(1);
			}
			itr++;
		}
		return result;
	}

}
