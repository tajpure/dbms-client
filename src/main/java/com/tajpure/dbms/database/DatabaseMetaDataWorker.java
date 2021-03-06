package com.tajpure.dbms.database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.List;

import com.tajpure.dbms.entity.Column;
import com.tajpure.dbms.entity.Function;
import com.tajpure.dbms.entity.Schema;
import com.tajpure.dbms.entity.StoredProcedure;
import com.tajpure.dbms.entity.Table;
import com.tajpure.dbms.entity.User;
import com.tajpure.dbms.entity.View;
import com.tajpure.dbms.util.Assert;
import com.tajpure.dbms.util.ConnectionPool;


/**
 * 
 * @author tajpure
 *
 */
public abstract class DatabaseMetaDataWorker {
	
	protected Connection con = null;
	
	protected DatabaseMetaData metaData = null;
	
	protected User user = null;
	
	public DatabaseMetaDataWorker(Connection con, User user) {
		this.con = con;
		this.user = user;
		init();
	}
	
	private void init() {
		if (con == null) {
			Assert.error("Connection is null!");
			return;
		}
		try {
			this.metaData = con.getMetaData();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method need to be called after the use of the worker finished.
	 */
	public void drop() {
		ConnectionPool.pushConnectionBackToPool(con);
		con = null;
		user = null;
		metaData = null;
	}
	
	@Override
	protected void finalize() throws Throwable {
		drop();
		super.finalize();
	}

	public String getDriverName() {
		String driverName = null;
		try {
			driverName = metaData.getDriverName();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return driverName;
	}
	
	public abstract int createSchema(Schema schema);
	
	public abstract int dorpSchema(Schema schema);
	
	public abstract List<Schema> getSchemas();
	
	public abstract Schema getSchema(String schemaName);

	public abstract Table getTable(String schemaName, String tableName);
	
	public abstract List<Table> getTables(String schemaName);
	
	public abstract int createTable(Table table);
	
	public abstract int dropTable(Table table);
	
	public abstract int dropTables(List<Table> tables);
	
	public abstract int updateTables(List<Table> oldTables, List<Table> newTables);

	public abstract Column getColumn(String schemaName, String tableName, String columnName);
	
	public abstract List<Column> getColumns(String schemaName, String tableName);
	
	public abstract int updateColumns(Table table, List<Column> oldColumns, List<Column> newColumns);
	
	public abstract int deleteColumns(Table table, List<Column> columns);
	
	public abstract int insertColumn(Table table, Column column);
	
	public abstract List<List<Object>> getValues(Table table);
	
	public abstract <T> int insertValue(Table table, List<T> list);
	
	public abstract <T> int insertValues(Table table, List<List<T>> list);
	
	public abstract <T> int updateValues(Table table, List<List<T>> oldList, List<List<T>> newlist);
	
	public abstract <T> int deleteValue(Table table, List<List<T>> list);
	
	public abstract <T> List<List<T>> getValuesByPage(Table table, int page, int rowsPerPage);
	
	public abstract int getValuesTotalPages(Table table, int rowsPerPage);
	
	public abstract boolean isSysSchema(String schemaName);
	
	public abstract List<View> getViews(String schemaName);
	
	public abstract List<StoredProcedure> getStoredProcedures(String schemaName);
	
	public abstract List<Function> getFunctions(String schemaName);
	
	public abstract int executeCommond(String sql, String schemaName);
	
	public abstract List<List<Object>> executeQueryCommond(String sql, String schemaName);
}
