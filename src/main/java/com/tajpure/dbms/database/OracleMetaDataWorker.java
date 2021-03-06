package com.tajpure.dbms.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.tajpure.dbms.entity.Column;
import com.tajpure.dbms.entity.Function;
import com.tajpure.dbms.entity.Schema;
import com.tajpure.dbms.entity.StoredProcedure;
import com.tajpure.dbms.entity.Table;
import com.tajpure.dbms.entity.User;
import com.tajpure.dbms.entity.View;

public class OracleMetaDataWorker extends DatabaseMetaDataWorker {
	
	public OracleMetaDataWorker(Connection con, User user) {
		super(con, user);
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<Schema> getSchemas() {
		List<Schema> schemas = new ArrayList<Schema>();
		
		try {
			ResultSet rs = metaData.getSchemas();
			while (rs.next()) {
				Schema schema = new Schema();
				schema.setName(rs.getString(1));
				schemas.add(schema);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println(schemas);
		return schemas;
	}

	@Override
	public List<Table> getTables(String schemaName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Schema getSchema(String schemaName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Table getTable(String schemaName, String tableName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Column getColumn(String schemaName, String tableName,
			String columnName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Column> getColumns(String schemaName, String tableName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<List<Object>> getValues(Table table) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isSysSchema(String schemaName) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getValuesTotalPages(Table table, int rowsPerPage) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public <T> int insertValue(Table table, List<T> list) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public <T> int insertValues(Table table, List<List<T>> list) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public <T> int updateValues(Table table, List<List<T>> oldList,
			List<List<T>> newlist) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public <T> List<List<T>> getValuesByPage(Table table, int page,
			int rowsPerPage) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> int deleteValue(Table table, List<List<T>> list) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<View> getViews(String schemaName) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public int updateColumns(Table table, List<Column> oldColumns,
			List<Column> newColumns) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int deleteColumns(Table table, List<Column> columns) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int insertColumn(Table table, Column column) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<StoredProcedure> getStoredProcedures(String schemaName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Function> getFunctions(String schemaName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int createTable(Table table) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int dropTable(Table table) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int dropTables(List<Table> tables) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public int updateTables(List<Table> oldTables, List<Table> newTables) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int executeCommond(String sql, String curSchema) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<List<Object>> executeQueryCommond(String sql, String curSchema) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int createSchema(Schema schema) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int dorpSchema(Schema schema) {
		// TODO Auto-generated method stub
		return 0;
	}
	
}

