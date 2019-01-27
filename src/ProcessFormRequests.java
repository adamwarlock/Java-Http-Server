import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;


public class ProcessFormRequests {
	
	private static final String PATH = "src/HTML_FILES";

	public File findResource(String resource, String formData)
	{
		HashMap<String,String> formDataMap= new HashMap<String,String>();
		
		File file=null;
		if(formData!=null)
		{			
			String dataPair[]=null;
			String str[]=formData.split("&");
			for(int i=0;i<str.length;i++)
			{
				dataPair=str[i].split("=");
				
				formDataMap.put(dataPair[0], dataPair[1]);
			}
			
			file=processFormData(resource, formDataMap);
			
		}
		return file;
	}
	
	public File processFormData(String resource,HashMap<String,String> formDataMap)
	{
		resource= resource.replace("/", "");
		File file=null;
		File tempFile=null;
		if("studentinfo".equalsIgnoreCase(resource))
		{
			try {
			StudentData data= new StudentData();			
			
			data.setFirstName(formDataMap.get("firstname"));
			data.setLastName(formDataMap.get("lastname"));
			
			if( checkAndSaveStudentData(data) ) {
				
				file = new File(PATH+"\\InstructorTemplate.html");
				tempFile= new File(PATH+"\\TempFile.html");
				
				file_copy(file, tempFile);
				
				dynamic_html_writer(PATH+"\\TempFile.html",data);
				
				
			}
			else {
				
				tempFile= new File(PATH+"\\studentinfo.html");
				
			}
			
			
			
			}
			catch (Exception e) {
				e.printStackTrace();
			}					
			 
			
			return tempFile;
			
		}
		else if("studentinfo".equalsIgnoreCase(resource))
		{
			
		}
		return file;
	}
	public boolean checkAndSaveStudentData(StudentData data)
	{
		String studentInfoFile = "src/DatabaseFiles/studentlist.txt";
		boolean entryFound=false;
		try {
		    BufferedReader in = new BufferedReader(new FileReader(studentInfoFile));
		    BufferedWriter out = new BufferedWriter(new FileWriter(studentInfoFile,true));
		    String str;
		    String studentName[];	    
		    while ((str = in.readLine()) != null)		    	
		    {		    	
		    	studentName=str.split(" ");
		    	if(studentName[0].equalsIgnoreCase(data.getFirstName()) 
		    			&& studentName[1].equalsIgnoreCase(data.getLastName()))
		    	{
		    		entryFound= true;
		    		break;
		    	}
		    }
		    in.close();
		    
		    if(!entryFound)
		    {
		    	out.write("\n"+data.getFirstName()+" "+data.getLastName());
		    	
		    }
		    out.close();

		} catch (IOException e) {
		}
		return entryFound;
	}
	
	
	public void file_copy(File src, File dest) throws Exception {
		FileChannel source = new FileInputStream(src).getChannel();
		FileChannel destination = new FileOutputStream(dest).getChannel();
		destination.transferFrom(source, 0, source.size());
		source.close();
		destination.close();
	}
	
	public void dynamic_html_writer(String filePath,StudentData data) throws Exception {
		Path path = Paths.get(filePath);
		Charset charset = StandardCharsets.UTF_8;

		String content = new String(Files.readAllBytes(path), charset);
		content = content.replaceAll("#StudentName#", data.getFirstName()+" "+data.getLastName());
		content = content.replaceAll("#StudentResource#", data.getFirstName()+"_"+data.getLastName()+".txt");
		Files.write(path, content.getBytes(charset));
	}
}
