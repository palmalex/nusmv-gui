/********************************************************************************
*                                                                               *
*   Module      :   ProcessWriter.java                                          *
*   Author      :   Silvia Lorenzini		       		     		            *
*   Tools       :   Eclipse                                                     *
********************************************************************************/
package apps;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;

import com.trolltech.qt.QSignalEmitter;

public class ProcessWriter extends QSignalEmitter implements Runnable 
{
	private InputStream is;
	private final String EXIT_STRING;
	private PrintStream pw;
	private boolean run;
	private int stop_count;
    
    public QSignalEmitter.Signal0 interrupted;
    
    /********************************************************************************
    *                                                                               *
    *  							PUBLIC FUNCTIONS DEFINITION	                        *
    *                                                                               *
    ********************************************************************************/

    /**
     * Costruttore.
     */
    ProcessWriter(InputStream is, File file)
    {
        this.is = is;
        this.EXIT_STRING = "NuSMV >";
        this.run = true;
        this.stop_count = 1000;
        
        try
		{
			pw = new PrintStream(file);
		} 
        catch (FileNotFoundException e){}
        
        this.interrupted = new Signal0();
    }
    
    /**
     * Scrive l'output di NuSMV su file.
     */
    public void run()
    {
        try
        {        	
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line = "";
            char c = '\0';
            
            while (true)
            {
            	if (br.ready())
            	{
	            	c = (char)br.read();
	 				if (line.length() < EXIT_STRING.length())
	     			{
	     				line += c;
	     			}
	     			else
	     			{
	     				line = line.substring(1) + c;
	     			}
	 				if (line.contains(EXIT_STRING))
	 	 			{
	 	 				interrupted.emit();
	 	 				return;
	 	 			}
	 				pw.print(c);
            	}        
            	else if (!run)
            	{
            		if (stop_count-- <= 0)
            			return;
            	}
            }
        } 
        catch (IOException ioe)
        {
            ioe.printStackTrace();  
        }
    }
    
    /**
     * Interrompe il processo settando la variabile di controllo.
     */
    public void stopProcess()
    {
    	run = false;
    	return;
    }
}
