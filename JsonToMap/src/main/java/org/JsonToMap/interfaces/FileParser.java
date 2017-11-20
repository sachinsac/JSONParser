package org.JsonToMap.interfaces;

import java.io.File;
import java.util.Map;

public interface FileParser {

	public void getFile(String fileName);
	
	public void readFileContent(File file);
	
	public Map <String,String> parseFileContent(String fileContent);
}
