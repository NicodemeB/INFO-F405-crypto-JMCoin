package com.jmcoin.io;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Class IOFileHandler
 * @author enzo
 *
 */
public abstract class IOFileHandler {
	
	/**
	 * Checks if a file exists, and is not a directory
	 * @param fileName
	 * @return the file
	 */
	public static File doesFileExist(String fileName) {
		if(fileName == null) return null;
		File file = new File(fileName);
		return file.exists() && !file.isDirectory() ? file : null;
	}
	
	/**
	 * Reads a file
	 * @param fileName
	 * @return file content if the file exists | null otherwise
	 * @throws IOException
	 */
	public static String readFile(String fileName) throws IOException {		
		File file = IOFileHandler.doesFileExist(fileName);
		if(file == null) return null;
		String result = null;
		try (BufferedReader reader = new BufferedReader(new FileReader(file))){
			String line;
			StringBuilder builder = new StringBuilder();
			while((line = reader.readLine()) != null) {
				builder.append(line);
			}
			result = builder.toString();
		} catch (IOException e) {
			throw e;
		}
		return result;
	}
	
	/**
	 * Writes to file
	 * @param fileName
	 * @param content
	 * @param append writing mode
	 * @return true in case of success, false otherwise
	 * @throws IOException
	 */
	public static boolean writeFile(String fileName, String content, boolean append) throws IOException {
		if(content == null) return false;
		File file = IOFileHandler.doesFileExist(fileName);
		if(file == null) return false;
		try (BufferedWriter br = new BufferedWriter(new FileWriter(file, append))){
			br.write(content);
		}
		catch (IOException e) {
			throw e;
		}
		return true;
	}

	public static <T> T getFromJsonFile(String fileName, Class<T> type) throws FileNotFoundException {
		return new Gson().fromJson(new BufferedReader(new FileReader(fileName)), type);
	}
}
