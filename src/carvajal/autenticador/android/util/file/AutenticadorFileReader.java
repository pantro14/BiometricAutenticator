package carvajal.autenticador.android.util.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.util.Log;
import carvajal.autenticador.android.util.file.exception.AutenticadorFileReaderException;

public class AutenticadorFileReader {
	
	public static ArrayList<String> readFile(String path, String nameFile) throws AutenticadorFileReaderException {
		File file = null;
		FileInputStream fileInputStream = null;
		InputStreamReader inputStreamReader = null;
		BufferedReader bufferedReader = null;
		String line = null;
		ArrayList<String> listLines = null;
        try {
        	listLines = new ArrayList<String>();
        	file = new File(path, nameFile);
        	fileInputStream = new FileInputStream(file);
        	inputStreamReader = new InputStreamReader(fileInputStream);
        	bufferedReader = new BufferedReader(inputStreamReader);
            line = bufferedReader.readLine();
            while (line != null) {
            	Log.d("FileReader:", "readFile: " + line);
            	listLines.add(line);
                line = bufferedReader.readLine();
            }

        }
        catch (IOException e) {
        	throw new AutenticadorFileReaderException(e.getLocalizedMessage().toString(), e);
        }
        catch (Exception e) {
        	throw new AutenticadorFileReaderException(e.getLocalizedMessage().toString(), e);
        }
        finally {
        	try {
        		if (bufferedReader != null) {
            		bufferedReader.close();
            	}
            	if (inputStreamReader != null) {
            		inputStreamReader.close();
            	}
        	}
        	catch (Exception e) {
        		throw new AutenticadorFileReaderException(e.getLocalizedMessage().toString(), e);
        	}
        }
        return listLines;
	}
}