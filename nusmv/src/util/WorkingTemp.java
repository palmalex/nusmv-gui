package util;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.security.MessageDigest;



/**
 * 
 * @author Alessio Palmieri
 *
 */
public class WorkingTemp {
	
	private static WorkingTemp instance = null;
	private String tempPath = null;
	private static final FilenameFilter CTLFilter 			= new PrefixFilenameFilter("check_ctlspec_");
	private static final FilenameFilter LTLFilter			= new PrefixFilenameFilter("check_ltlspec_");
	private static final FilenameFilter SimulationFilter 	= new PrefixFilenameFilter("simulation.xml");
	private static final FilenameFilter ShowVarFilter 		= new PrefixFilenameFilter("show_vars.txt");
	
	
	/**
	 * Private constructor
	 */
	private WorkingTemp() {
	}
	
	/**
	 * Singleton class
	 * @return instance
	 */
	public static WorkingTemp getInstance() {
		if (instance==null) {
			instance = new WorkingTemp();
		} 
		return instance;
	}
	
	private String createRandomDirname() {
		String seed = System.currentTimeMillis() + "_ramdom";
		System.out.println("seed " + seed);
		String randomString = "";
		try {
			byte[] seedByte = seed.getBytes();
			MessageDigest algorithm = MessageDigest.getInstance("MD5");
			algorithm.reset();
			algorithm.update(seedByte);
			byte messageDigest[] = algorithm.digest();
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < messageDigest.length; ++i) {
		          sb.append(Integer.toHexString((messageDigest[i] & 0xFF) | 0x100).substring(1,3));
		    }
			randomString = sb.toString();
		} catch (java.security.NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return randomString;
	}
	
	public String createNewWorkingTemp() {
		if (tempPath != null) {
			File tmp = new File(tempPath);
			if (tmp.isDirectory()){
				tmp.delete();
			}
		} 
		String tmpDirName = System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+ "nusmv_"+createRandomDirname();
		tempPath = tmpDirName; 
		System.out.println("tmpDirName : " + tmpDirName);
		File tmpFile = new File(tmpDirName);
		tmpFile.deleteOnExit();
		tmpFile.mkdir();
		
		return tempPath;
	}
	
	public String getCurrentPath(){
		if (tempPath==null) {
			createNewWorkingTemp();
		}
		return tempPath;
	}
	
	public String[] getCurrentContent() throws IOException {
		String[] content = null;
		System.out.println("get current directory");
		File currentPath = new File(tempPath);
		if (currentPath.isDirectory()) {
			content =  currentPath.list();
		}
		return content;
	}
	
	public String[] getCurrentContent(FilenameFilter filter) throws IOException {
		String[] content = null;
		System.out.println("get current directory");
		File currentPath = new File(tempPath);
		if (currentPath.isDirectory()) {
			content =  currentPath.list(filter);
		}
		return content;
	}

	public String[] getCurrentCTL() throws IOException{
		return getCurrentContent(CTLFilter);
	}
	
	public String[] getCurrentLTL() throws IOException{
		return getCurrentContent(LTLFilter);
	}
	
	public String[] getSimulation() throws IOException{
		return getCurrentContent(SimulationFilter);
	}
	
	public String[] getShowVars() throws IOException{
		return getCurrentContent(ShowVarFilter);
	}
	

}
