package carvajal.autenticador.android.log4j;

import org.apache.log4j.Level;

import carvajal.autenticador.android.log4j.exception.AndroidLog4jException;
import de.mindpipe.android.logging.log4j.LogConfigurator;

public class AndroidLog4jConfig {

	public static void configure(String path, String fileName) throws AndroidLog4jException {
		try {
			final LogConfigurator logConfigurator = new LogConfigurator();                
	        logConfigurator.setFileName(path.concat(fileName));
	        logConfigurator.setRootLevel(Level.DEBUG);
	        // Set log level of a specific logger
	        logConfigurator.setLevel("org.apache", Level.ERROR);
	        logConfigurator.configure();
		}
		catch (Exception e) {
			throw new AndroidLog4jException(e.getLocalizedMessage().toString(), e);
		}
    }
}
