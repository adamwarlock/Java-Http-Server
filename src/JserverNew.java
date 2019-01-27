import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.CharBuffer;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Scanner;

public class JserverNew {

	private static final String PATH = "src/HTML_FILES";  
	
	private static final String CONTENT_TYPE = "text/html"; 
														
	private static final String STATUS_OK = "HTTP/1.1 200 OK";
	private static final String SPACE = " ";
	
	private static BufferedReader req_reader=null;
	private static HashMap<String,String> headerDataMap=null;
	
	public static void main(String[] args) throws Exception {
	
		ServerSocket myserver = new ServerSocket(8081);

		System.out.println("Listening on port 8081");

		PrintWriter response = null;

		while (true) {
			Socket inbound = myserver.accept();

			req_reader = new BufferedReader(new InputStreamReader(inbound.getInputStream()));
			if (req_reader != null) {

				response = new PrintWriter(new OutputStreamWriter(inbound.getOutputStream()));

				BufferedOutputStream response_stream = new BufferedOutputStream(inbound.getOutputStream());

				if (response != null) {
					
					headerDataMap = new HashMap<String,String>();
					
					String header_data = req_reader.readLine();
					if (header_data != null) {

						String[] req_line = header_data.split(SPACE);
						String [] headerContent=null;
						System.out.println("-----------------PRINTING REQUEST HEADER----------------------");
						while (!header_data.isEmpty()) {
							System.out.println(header_data);
							header_data = req_reader.readLine();
							
							headerContent= header_data.split(":");
							if(headerContent!=null && headerContent.length>1)
							{
								headerDataMap.put(headerContent[0].trim(), headerContent[1].trim());
							}
							

						}

						System.out.println("Responding to Client!");
						request_process_handler(req_line, response, response_stream);
						
						req_reader.close();
						response.close();
						inbound.close();

					}
				}

			}

		}

	}

	// RESPONSE HEADER WRITER

	private static void response_header_writer(String status, String content_type, int content_length,
			PrintWriter response) {
		if ((status != null) && (content_length != 0) && (content_type != null)) {
			response.println("HTTP/1.1 200 OK");
			response.println("Content-type: " + content_type);
			response.println("Content-length: " + content_length);
			response.println();
			response.flush();

		} else {
			System.out.println("Check parameters for null!");
		}
	}

	// RESPONSE FILE WRITER
	// WRITE FILE IN FORM OF CHARACTERS
	private static void response_web_page_writer(File webpage_file, PrintWriter response) {
		try {

			Scanner sc = new Scanner(webpage_file);
			while (sc.hasNextLine()) {
				response.println(sc.nextLine());
			}

		} catch (FileNotFoundException e) {

			e.printStackTrace();

		}
	}

	// RESPONSE FILE WRITER
	// WRITE FILES IN FORM OF BYTES.
	// USED FOR SENDING HTML FILES OR IMAGE FILES.
	private static void response_file_writer(File file, BufferedOutputStream filewriter) {
		try {

			int filelength = (int) file.length();

			FileInputStream fileInputStream = new FileInputStream(file);

			byte[] fileData = new byte[filelength];

			fileInputStream.read(fileData);
			filewriter.write(fileData, 0, filelength); // WRITING FILE IN FORM OF BYTES IN RESPONSE

			filewriter.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Return Requested file
	private static File getFileForRequest(String resource) {
		
		System.out.println(resource);
		resource = resource.replace("/", "");
		if( resource.contains(".") )
			resource = resource.substring(0, resource.lastIndexOf('.'));
		//System.out.println(resource);
		File folder = new File(PATH);
		File files[] = folder.listFiles();
		for (File file : files) {

			//System.out.println(file.getName());
			String filename = file.getName().substring(0, file.getName().lastIndexOf('.'));

			

			if (resource.equalsIgnoreCase(filename)) {
				System.out.println("Found File: " + file.getName());
				return file;
			}
			
		}
		return new File(PATH + "\\404.html");
	}

	private static void request_process_handler(String[] req_line, PrintWriter response,
			BufferedOutputStream response_stream) throws IOException {

		String method = req_line[0];
		String requested_resource = req_line[1];
		if (method.equals("GET")) {
			if (requested_resource.equals("/")) {

				File file = new File(PATH + "\\index.html");
				int fileLength = (int) file.length();
				response_header_writer(STATUS_OK, CONTENT_TYPE, fileLength, response);
				response_file_writer(file, response_stream);

			} else {

				File file = getFileForRequest(requested_resource);
				System.out.println(file.getName());
				int fileLength = (int) file.length();
				response_header_writer(STATUS_OK, CONTENT_TYPE, fileLength, response);
				response_file_writer(file, response_stream);
			}
		}else if (method.equals("POST")) {
			char[] c = null;
			String contentLength = headerDataMap.get("Content-Length");
			
			if(contentLength != null) {
				c = new char[new Integer(contentLength).intValue()];
			}
			else {
				c = new char[1024];
			}
			
			req_reader.read(c);
			String formData = new String(c);
			formData = formData.trim();
			
			ProcessFormRequests formRequest = new ProcessFormRequests();
			
			File file  = formRequest.findResource(requested_resource, formData);
			
			System.out.println(file.getName());
			
			response_header_writer(STATUS_OK, CONTENT_TYPE, (int) file.length(), response);
			response_file_writer(file, response_stream);
		
		}
		else {
			
			File file = new File(PATH + "\\404.html");
			int fileLength = (int) file.length();
			response_header_writer(STATUS_OK, CONTENT_TYPE, fileLength, response);
			response_file_writer(file, response_stream);
			
		}
	}
	
	
}
