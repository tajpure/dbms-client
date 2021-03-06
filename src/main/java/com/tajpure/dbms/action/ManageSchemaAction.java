package com.tajpure.dbms.action;

import java.util.List;

import javax.servlet.http.HttpServlet;

import com.tajpure.dbms.database.DatabaseMetaDataFactory;
import com.tajpure.dbms.database.DatabaseMetaDataWorker;
import com.tajpure.dbms.entity.Schema;

public class ManageSchemaAction extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private DatabaseMetaDataFactory factory = DatabaseMetaDataFactory.getInstance();
	
	public String execute() {
		DatabaseMetaDataWorker worker = factory.getWorker();
		
		if (curSchema == null || curSchema.getName() == null) {
			curSchema = worker.getSchema(schemaName);
		}
		
		schemas = worker.getSchemas();
		worker.drop();
		return "success";
	}
	
	public String createSchema() {
		DatabaseMetaDataWorker worker = factory.getWorker();
		worker.createSchema(newSchema);
		worker.drop();
		return "success";
	}
	
	public String dropSchema() {
		DatabaseMetaDataWorker worker = factory.getWorker();
		worker.dorpSchema(curSchema);
		worker.drop();
		curTab = 1;
		return "success";
	}
	
	private Schema newSchema = new Schema();
	
	private int curTab = 0;

	public int getCurTab() {
		return curTab;
	}

	public void setCurTab(int curTab) {
		this.curTab = curTab;
	}

	private List<Schema> schemas = null;
	
	private Schema curSchema = new Schema();
	
	private String schemaName;

	public List<Schema> getSchemas() {
		return schemas;
	}

	public void setSchemas(List<Schema> schemas) {
		this.schemas = schemas;
	}

	public Schema getCurSchema() {
		return curSchema;
	}

	public void setCurSchema(Schema curSchema) {
		this.curSchema = curSchema;
	}

	public String getSchemaName() {
		return schemaName;
	}

	public void setSchemaName(String schemaName) {
		this.schemaName = schemaName;
	}

	public Schema getNewSchema() {
		return newSchema;
	}

	public void setNewSchema(Schema newSchema) {
		this.newSchema = newSchema;
	}

}
