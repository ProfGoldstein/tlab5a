/*
 * Copyright (c) 1995, 2014, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *	 notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *	 notice, this list of conditions and the following disclaimer in the
 *	 documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle or the names of its
 *	 contributors may be used to endorse or promote products derived
 *	 from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * @Version Spring 2023
 * @Author Oracle
 * @Author Ira Goldstein - Added comments and cleaned up program termination
 */ 

import java.net.*;
import java.io.*;

public class KnockKnockServer {
	public static void main(String[] args) throws IOException {
		
		// get port number from the command line
		if (args.length != 1) {
			System.err.println("Usage: java KnockKnockServer <port number>");
			System.exit(1);
		}

		int portNumber = Integer.parseInt(args[0]);
		
		System.out.println("Starting the server");
		
		try ( 
			// instantiate the serversocket object
			ServerSocket serverSocket = new ServerSocket(portNumber);
			
			//  the accept() method is used to wait for a request from the client 
			Socket clientSocket = serverSocket.accept();
			
			// to instantiate "out", we use the constructor of PrintWriter class, 
			// this constructor takes one parameter which is an output stream for the socket, 
			// notice that the socket used here is clientSocket , because it is responsible for sending data to the client.
			PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
			
			// instantiate the "in" object that we will use to read from clientSocket.
			BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		) {
			System.out.println("Starting the client connection");

			String inputLine, outputLine;
			
			// initiate conversation with client using the KnockKnockProtocol (kkp)
			KnockKnockProtocol kkp = new KnockKnockProtocol();
			
			// first prompt sent to the client should be "Knock! Knock!"
			outputLine = kkp.processInput(null);
			out.println(outputLine);
			
			// process input from the client.  If the client reposonds "n" to "Want another? (y/n)"
			// the server will send the text "Bye." to indicate that we are done.
			while ((inputLine = in.readLine()) != null) {
				outputLine = kkp.processInput(inputLine);
				out.println(outputLine);
				if (outputLine.equals("Bye.")) {
					// close the client connection
					System.out.println("Closing the client connection");
					clientSocket.close();
					
					// shut down the server
					System.out.println("Shutting down the server");
					serverSocket.close();
	
					break;
				}
			}
		} catch (IOException e) {
			System.out.println("Exception caught when trying to listen on port "
				+ portNumber + " or listening for a connection");
			System.out.println(e.getMessage());
		}
	}
}
