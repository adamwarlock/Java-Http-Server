import java.io.File;

public class Temp {

	public static void main(String args[])
	{
		
		File folder = new File("E:\\Uwindsor_Work\\Eclipse_WS\\My_Web_Server\\src\\");
    	File files[]=folder.listFiles();
    	for(File file: files)
    	{
    		System.out.println(file.getName());
    	}
    	
	}
}
