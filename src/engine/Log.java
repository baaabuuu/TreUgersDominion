package engine;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A simple little logger, if debug is set to true the code will start printing information about the game in the console.
 * @author s164166
 */
public class Log {

	public static final boolean debug = true;
	
	public static void log(String message)
	{
		if (debug)
		{
			//Not sure we need year and day for this
			//System.out.println("[" + new SimpleDateFormat("yyyy:MM:dd:HH:mm:ss").format(new Date()) + "]: " + message);
			System.out.println("[" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "]: " + message);
		}
	}
	
	public static void important(String message)
	{
		if (debug)
		{
			System.err.println("[" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "]: " + message);
		}
	}

}
