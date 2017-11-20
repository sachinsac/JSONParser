package org.JsonToMap;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.JsonToMap.interfaces.FileParser;
import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonFileParser implements FileParser{

	public void getFile(String fileName) {
		File file = new File(fileName);
		if(file.exists()){
			readFileContent(file);
		}
	}

	public void readFileContent(File file) {
		try{
			String jsonFileContent = FileUtils.readFileToString(file, Charset.defaultCharset());
			System.out.println(parseFileContent(jsonFileContent));
		}catch(IOException ioException){
			ioException.printStackTrace();
		}
	}

	public Map <String,String> parseFileContent(String fileContent) {
		Map <String,String> jsonMap = new HashMap <String,String> ();
		if(isJsonObject(fileContent)){
			JSONObject rootObject = new JSONObject (fileContent);
			traverseJsonKey(rootObject,jsonMap);
		}else if(isJsonArrayObject(fileContent)){
			JSONArray jsonArray = new JSONArray(fileContent);
			for(Object object:jsonArray){
				String eachObjectString = object.toString();
				JSONObject rootJsonObject = new JSONObject(eachObjectString);
				traverseJsonKey(rootJsonObject, jsonMap);
			}
		}else{
			//do nothing
		}
		
		return jsonMap;
	}
	
	
	void traverseJsonKey(JSONObject jsonObject,Map <String,String> jsonMap){
		Set <String> rootKeys = jsonObject.keySet();
		
		for(String rootKey:rootKeys){
			String rootKeyValue = jsonObject.get(rootKey).toString();
			if(isJsonObject(rootKeyValue)){
				traverseNestedJsonKey(rootKey, new JSONObject(rootKeyValue), jsonMap);
			}else if(isJsonArrayObject(rootKeyValue)){
				JSONArray jsonArray = new JSONArray(rootKeyValue);
				int depth = 0;
				for(Object object:jsonArray){
					String eachObjectString = object.toString();
					JSONObject rootJsonObject = new JSONObject(eachObjectString);
					traverseNestedJsonKey(rootKey+"["+depth+"]", rootJsonObject,jsonMap);
					depth++;
				}
			}else{
				if(!jsonMap.containsKey(rootKey))
					jsonMap.put(rootKey, rootKeyValue);
			}
		}
	}
	
	
	void traverseNestedJsonKey(String parentNodeName,JSONObject nestedJsonObject,Map <String,String> jsonMap){
		Set <String> rootKeys = nestedJsonObject.keySet();
		
		String completeKey = parentNodeName;		
		for(String rootKey:rootKeys){
			String rootKeyValue = nestedJsonObject.get(rootKey).toString();
			completeKey = parentNodeName+"."+rootKey;
			if(isJsonObject(rootKeyValue)){
				traverseNestedJsonKey(completeKey, new JSONObject(rootKeyValue), jsonMap);
			}else if(isJsonArrayObject(rootKeyValue)){
				JSONArray jsonArray = new JSONArray(rootKeyValue);
				int depth = 0;
				for(Object object:jsonArray){
					String eachObjectString = object.toString();
					JSONObject rootJsonObject = new JSONObject(eachObjectString);
					completeKey = completeKey+"["+depth+"]";
					traverseNestedJsonKey(completeKey, rootJsonObject,jsonMap);
					depth++;
				}
			}else{
				if(!jsonMap.containsKey(completeKey))
					jsonMap.put(completeKey, rootKeyValue);
			}
		}
	}
	
	
	boolean isJsonObject(String content){
		boolean isObject = false;
		try{
			new JSONObject(content);
			isObject = true;
		}catch(JSONException jsonException){
			isObject = false;
		}
		return isObject;
	}
	
	boolean isJsonArrayObject(String content){
		boolean isArrayObject = false;
		try{
			JSONArray Jsonarray = new JSONArray(content);
			for(Object array:Jsonarray){
				isArrayObject = isJsonObject(array.toString());
			}
			return isArrayObject;
		}catch(JSONException jsonException){
			isArrayObject = false;
		}
		return isArrayObject;
	}
	
	
	public static void main(String args[]){
		new JsonFileParser().getFile("Test.json");
	}

}
