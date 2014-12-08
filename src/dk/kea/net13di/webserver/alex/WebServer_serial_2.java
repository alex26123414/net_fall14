package dk.kea.net13di.webserver.alex;

import java.io.*;
import java.net.*;
import java.util.*;

public class WebServer_serial_2
{
        
    public static void main(String[] args)
    {
        System.out.println("OK, we are starting the WebServer.");

        try
        {
            ServerSocket listnerSocket = new ServerSocket(Integer.parseInt(args[0]));
            System.out.println("OK, we have a listening socket.");
        
            while(true)
            {
                Socket newsocket = listnerSocket.accept();
                System.out.println("OK, we got a clinet connection!");
                ServiceTheClient(newsocket);
            }

        }
        catch (IOException e)
        {
            System.out.println("Webserver IO exception");
        }               

    }


    public static void ServiceTheClient(Socket con)
    {
        Socket socket;
        socket = con;

        try
        {
            System.out.println("****************************************************************************");
            System.out.println("OK, we are starting to service the client.");
            String path = "C:\\Users\\alex\\Desktop\\www\\";
            String requestMessageLine;
            String fileName;
       
            BufferedReader inFromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            DataOutputStream outToClient = new DataOutputStream(socket.getOutputStream());                                          
            requestMessageLine = inFromClient.readLine();
            System.out.println("From Client:   " + requestMessageLine);
        
            StringTokenizer tokenizedLine = new StringTokenizer(requestMessageLine);
        
            if(tokenizedLine.nextToken().equals("GET"))
            {
                fileName = tokenizedLine.nextToken();
                
                if(fileName.startsWith("/") == true)
                {
                    fileName = path + fileName.substring(1);
                }
            
                if(fileName.endsWith("/") == true)
                {
                    fileName = fileName + "index.html";
                }

                File file = new File(fileName);
                if (!file.isFile() == true)
                {
                    fileName = path + "error404.html";
                    file = new File(fileName);
                }
            
            	FileInputStream inFile = null;
                System.out.println("Trying to find file: " + fileName);
				try
                {
                	inFile = new FileInputStream(fileName);
                }
                catch(IOException e)
                {
	                System.out.println("Error: Did not find file: " + fileName);
	                System.exit(0);
                }

                
                outToClient.writeBytes("HTTP/1.0 200 Her kommer skidtet\r\n");
            
                if(fileName.endsWith(".jpg"))
                {
                    outToClient.writeBytes("Content-Type:image/jpeg\r\n");
                }
            
                if(fileName.endsWith(".gif"))
                {
                    outToClient.writeBytes("Content-Type:image/gif\r\n");
                }
            
                
                byte[] fileInBytes = new byte[1024];
                int fileSize = (int)file.length();
                outToClient.writeBytes("Content-Length: " + fileSize + "\r\n");
                outToClient.writeBytes("\r\n");
                int howmany = 0;
                int total = 0;
                boolean allSent = false;
                if (fileSize <= 0) allSent = true;
                while (allSent == false)
                {
                	for (int i=0; i<1024; i++) fileInBytes[i] = 'x';
                	howmany = inFile.read(fileInBytes, 0, 1024);
					//System.out.println("DEBUG:  howmany = " + howmany);
                	outToClient.write(fileInBytes, 0, howmany);
                	total = total + howmany;
                	if ((total%1048576) == 0) System.out.println("Sent so far: " + (int)(total/1048576) + " MB");
                	fileSize = fileSize - howmany;
					//System.out.println("DEBUG:  fileSize = " + fileSize);
                	if (fileSize <= 0) allSent = true;
                }

                outToClient.writeBytes("\r\n");
                
                System.out.println("OK, the file is sent to Client.");
                System.out.println("****************************************************************************");

                socket.close();
            }
            else // no "GET"
            {
                System.out.println("Bad request Message");
                outToClient.writeBytes("HTTP/1.0 400  I do not understand. I am from Barcelona.\r\n");
                outToClient.writeBytes("\r\n");
                socket.close();
            }
        }
     
        catch(IOException e)
        {
            System.out.println("IO Exception");
        }        

    }  // end of 


}