package org.shanoir.ng.importer.model;

import java.util.ArrayList;
import java.util.List;

public class ExpressionFormat {

	private String type;
	
	private List<DatasetFile> datasetFiles = new ArrayList<DatasetFile>();

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<DatasetFile> getDatasetFiles() {
		return datasetFiles;
	}

	public void setDatasetFiles(List<DatasetFile> datasetFiles) {
		this.datasetFiles = datasetFiles;
	}
	
}
