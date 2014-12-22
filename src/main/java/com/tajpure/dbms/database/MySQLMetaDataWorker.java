package com.tajpure.dbms.database;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
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


public class MySQLMetaDataWorker extends DatabaseMetaDataWorker {

	public MySQLMetaDataWorker(Connection con, User user) {
		super(con, user);
	}

	@Override
	public Schema getSchema(String schemaName) {
		Schema schema = null;
		List<Schema> schemas = getSchemas();
		for (Schema tmp : schemas) {
			if (tmp.getName().equals(schemaName)) {
				schema = tmp;
				break;
			}
		}
		return schema;
	}

	@Override
	public List<Schema> getSchemas() {
		List<Schema> schemas = new ArrayList<Schema>();
		
		try {
			ResultSet rs = metaData.getCatalogs();
			while (rs.next()) {
				if (isSysSchema(rs.getString(1))) {
					continue;
				}
				Schema schema = new Schema();
				schema.setName(rs.getString(1));
				schema.setTables(getTables(schema.getName()));
				schemas.add(schema);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return schemas;
	}

	@Override
	public Table getTable(String schemaName, String tableName) {
		Table table = null;
		table = getTables(schemaName, "%", tableName, new String[] {"TABLE"}, true).get(0);
		return table;
	}

	@Override
	public List<Table> getTables(String schemaName) {
		List<Table> tables = null;
		tables = getTables(schemaName, "%", "%", new String[] {"TABLE"}, false);
		return tables;
	}
	
	public List<Table> getTables(String catalog, String schemaPattern, String tableNamePattern, String[] types, boolean needColumns) {
		List<Table> tables = new ArrayList<Table>();
		
		try {
			ResultSet rs = metaData.getTables(catalog, schemaPattern, tableNamePattern, types);
			while (rs.next()) {
				Table table = new Table();
				table.setName(rs.getString("TABLE_NAME"));
				table.setItsSchema(rs.getString("TABLE_CAT"));
				table.setTableType(rs.getString("TABLE_TYPE"));
				table.setRemarks(rs.getString("REMARKS"));
				// For loading the table list faster
				if (needColumns) {
					table.setColumns(getColumns(catalog, tableNamePattern));
				}
				tables.add(table);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return tables;
	}

	@Override
	public Column getColumn(String schemaName, String tableName, String columnName) {
		Column column = null;
		column = getColumns(schemaName, "%", tableName, columnName).get(0);
		return column;
	}

	@Override
	public List<Column> getColumns(String schemaName, String tableName) {
		List<Column> columns = null;
		columns = getColumns(schemaName, "%", tableName, "%");
		return columns;
	}
	
	public List<Column> getColumns(String catalog, String schemaPattern, String tableNamePattern, String columnNamePattern) {
		List<Column> columns = new ArrayList<Column>();
		try {
			ResultSet rs = metaData.getColumns(catalog, schemaPattern, tableNamePattern, columnNamePattern);
			while (rs.next()) {
				Column column = new Column();
				column.setName(rs.getString("COLUMN_NAME"));
				column.setDataType(rs.getInt("DATA_TYPE"));
				column.setColumnSize(rs.getInt("COLUMN_SIZE"));
				column.setItsTable(rs.getString("TABLE_NAME"));
				column.setItsSchema(rs.getString("TABLE_SCHEM"));
				column.setItsCatalog(rs.getString("TABLE_CAT"));
				columns.add(column);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return columns;
	}
	
	public List<List<Object>> getValues(Table table) {
		String tableName = table.getName();
		String schemaName = table.getItsSchema();
		List<List<Object>> lists = new ArrayList<List<Object>>();
        PreparedStatement stmt = null;
        Connection con = ConnectionPool.getNewConnection(user, "/" + schemaName);
        int sizeOfColumns = getColumns(schemaName, tableName).size();
		try {
				stmt = con.prepareStatement("select * from ?;");
				stmt.setString(1, tableName);
				ResultSet rs = stmt.executeQuery();
				while (rs.next()) {
					List<Object> objs = new ArrayList<Object>();
					for (int i=1; i <= sizeOfColumns; i++) {
						objs.add(rs.getObject(i));
					}
					lists.add(objs);
				}
				con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return lists;
	}

	@Override
	public List<List<Object>> getValuesByPage(Table table, int page, int rowsPerPage) {
		String tableName = table.getName();
		String schemaName = table.getItsSchema();
		List<List<Object>> lists = new ArrayList<List<Object>>();
        PreparedStatement stmt = null;
        Connection con = ConnectionPool.getNewConnection(user, "/" + schemaName);
        List<Column> columns = getColumns(schemaName, tableName);
        int sizeOfColumns = columns.size();
		try {
				stmt = con.prepareStatement("select * from " + tableName + " order by ? desc limit ?,?;");
				stmt.setString(1, columns.get(0).getName());
				stmt.setInt(2, (page-1)*rowsPerPage);
				stmt.setInt(3, rowsPerPage);
				ResultSet rs = stmt.executeQuery();
				while (rs.next()) {
					List<Object> objs = new ArrayList<Object>();
					for (int i=1; i <= sizeOfColumns; i++) {
						objs.add(rs.getObject(i));
					}
					lists.add(objs);
				}
				con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return lists;
	}

	@Override
	public boolean isSysSchema(String schemaName) {
		String[] sysSchemas = {"information_schema", "mysql", "performance_schema"};
		for (int i=0; i < sysSchemas.length; i++) {
			if (sysSchemas[i].equals(schemaName)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int getValuesTotalPages(Table table, int rowsPerPage) {
		int totalRows = 0;
		int totalPages = 0;
		String tableName = table.getName();
		String schemaName = table.getItsSchema();
        PreparedStatement stmt = null;
        Connection con = ConnectionPool.getNewConnection(user, "/" + schemaName);
		try {
				stmt = con.prepareStatement("select count(*) from " + tableName + ";");
				ResultSet rs = stmt.executeQuery();
				rs.next();
				totalRows = rs.getInt(1);
				con.close();
				if (totalRows % rowsPerPage == 0) {
					totalPages = totalRows / rowsPerPage;
				} else {
					totalPages = totalRows / rowsPerPage + 1;
				}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return totalPages;
	}

	@Override
	public <T> int insertValue(Table table, List<T> obj) {
		String tableName = table.getName();
		String schemaName = table.getItsSchema();
        PreparedStatement stmt = null;
        Connection con = ConnectionPool.getNewConnection(user, "/" + schemaName);
        List<Column> columns = getColumns(schemaName, tableName);
        int rs = 0;
        String questionMarks = "";
        for (int i=0; i < columns.size()-1; i++) {
        	questionMarks += "?, ";
        }
        questionMarks = questionMarks + "?";
		try {
				stmt = con.prepareStatement("insert into " + tableName + " values(" + questionMarks + ");");
				for (int i=1; i <= columns.size(); i++) {
					stmt.setObject(i, mapDataType(columns.get(i-1), obj.get(i-1).toString()));
		        }
				rs = stmt.executeUpdate();
				con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return rs;
	}

	@Override
	public <T> int insertValues(Table table, List<List<T>> list) {
		String tableName = table.getName();
		String schemaName = table.getItsSchema();
        PreparedStatement stmt = null;
        Connection con = ConnectionPool.getNewConnection(user, "/" + schemaName);
        List<Column> columns = getColumns(schemaName, tableName);
        int rs = 0;
        String questionMarks = "";
        for (int i=0; i < columns.size()-1; i++) {
        	questionMarks += "?, ";
        }
        questionMarks = questionMarks + "?";
		try {
				for (List<T> obj : list) {
					stmt = con.prepareStatement("insert into " + tableName + " values(" + questionMarks + ");");
					for (int i=1; i <= columns.size(); i++) {
						stmt.setObject(i, mapDataType(columns.get(i-1), obj.get(i-1).toString()));
					}
					rs = stmt.executeUpdate();
				}
				con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return rs;
	}
	
	@SuppressWarnings("deprecation")
	public Object mapDataType(Column column, String val) {
		Object obj = null;
		switch (column.getDataType()) {
		case Types.BIGINT : obj = Long.parseLong(val); break;
		case Types.BOOLEAN : obj = Boolean.parseBoolean(val); break;
		case Types.CHAR : obj = val; break;
		case Types.DATE : obj = Date.parse(val); break;
		case Types.DOUBLE : obj = Double.parseDouble(val); break;
		case Types.FLOAT : obj = Float.parseFloat(val); break;
		case Types.INTEGER : obj = Integer.parseInt(val); break;
		case Types.VARCHAR :
		case Types.LONGNVARCHAR : obj = val; break;
		default : Assert.error("This data type isn't supported now.(column -type -size: " + column + ")");
		}
		return obj;
	}

	@Override
	public <T> int updateValues(Table table, List<List<T>> oldList, List<List<T>> newList) {
		String tableName = table.getName();
		String schemaName = table.getItsSchema();
        PreparedStatement stmt = null;
        Connection con = ConnectionPool.getNewConnection(user, "/" + schemaName);
        List<Column> columns = getColumns(schemaName, tableName);
        String SQL = getUpdateSQL(columns, tableName);
        int columnsSize = columns.size();
        int rs = 0;
		try {
				for (int i = 0 ; i < newList.size(); i++) {
					List<T> oldObj = oldList.get(i);
					List<T> newObj = newList.get(i);
					stmt = con.prepareStatement(SQL);
					for (int j = 1; j <= columnsSize; j++) {
						stmt.setObject(j, mapDataType(columns.get(j-1), newObj.get(j-1).toString()));
					}
					for (int j = 1; j <= columnsSize; j++) {
						stmt.setObject(j + columnsSize, mapDataType(columns.get(j-1), oldObj.get(j-1).toString()));
					}
					rs = stmt.executeUpdate();
				}
				con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return rs;
	}
	
	public String getUpdateSQL(List<Column> columns, String tableName) {
		StringBuilder SQL = new StringBuilder("update ").append(tableName).append(" set ");
		int i = 0;
		Column column = null;
		for (i = 0; i < columns.size()-1; i++) {
			column = columns.get(i);
			SQL.append(column.getName()).append(" = ?, ");
		}
		column = columns.get(i);
		SQL.append(column.getName()).append(" = ?");
		SQL.append(" where ");
		for (i = 0; i < columns.size()-1; i++) {
			column = columns.get(i);
			SQL.append(column.getName()).append(" = ? and ");
		}
		column = columns.get(i);
		SQL.append(column.getName()).append(" = ?;");
		return SQL.toString();
	}

	@Override
	public <T> int deleteValue(Table table, List<List<T>> list) {
		String tableName = table.getName();
		String schemaName = table.getItsSchema();
        PreparedStatement stmt = null;
        Connection con = ConnectionPool.getNewConnection(user, "/" + schemaName);
        List<Column> columns = getColumns(schemaName, tableName);
        String SQL = getDeleteSQL(columns, tableName);
        int columnsSize = columns.size();
        int rs = 0;
		try {
				for (int i = 0 ; i < list.size(); i++) {
					List<T> delObj = list.get(i);
					stmt = con.prepareStatement(SQL);
					for (int j = 1; j <= columnsSize; j++) {
						stmt.setObject(j, mapDataType(columns.get(j-1), delObj.get(j-1).toString()));
					}
					rs = stmt.executeUpdate();
				}
				con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return rs;
	}
	
	public String getDeleteSQL(List<Column> columns, String tableName) {
		StringBuilder SQL = new StringBuilder("delete from ").append(tableName).append(" where ");
		int i = 0;
		Column column = null;
		for (i = 0; i < columns.size()-1; i++) {
			column = columns.get(i);
			SQL.append(column.getName()).append(" = ? and ");
		}
		column = columns.get(i);
		SQL.append(column.getName()).append(" = ?;");
		return SQL.toString();
	}

	@Override
	public List<View> getViews(String schemaName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<StoredProcedure> getStoredProcedures() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Function> getFunctions() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public int createSchema(String schemaName) {
		int rs = 0;
//		rs = metaData.
		return rs;
	}
}
