// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

package client;

import ocsf.client.*;
import common.*;
import java.io.*;

/**
 * This class overrides some of the methods defined in the abstract
 * superclass in order to give more functionality to the client.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;
 * @author Fran&ccedil;ois B&eacute;langer
 * @version July 2000
 */
public class ChatClient extends AbstractClient
{
  //Instance variables **********************************************
  
  /**
   * The interface type variable.  It allows the implementation of 
   * the display method in the client.
   */
  ChatIF clientUI; 

  private boolean loggedOut;

  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the chat client.
   *
   * @param host The server to connect to.
   * @param port The port number to connect on.
   * @param clientUI The interface type variable.
   */
  
  public ChatClient(String host, int port, ChatIF clientUI) 
    throws IOException 
  {
    super(host, port); //Call the superclass constructor
    this.clientUI = clientUI;
    openConnection();
  }

  
  //Instance methods ************************************************
    
  /**
   * This method handles all data that comes in from the server.
   *
   * @param msg The message from the server.
   */
  public void handleMessageFromServer(Object msg) 
  {
    clientUI.display(msg.toString());
  }

  /**
   * This method handles all data coming from the UI            
   *
   * @param message The message from the UI.    
   */
  public void handleMessageFromClientUI(String message)
  {

    // **** Changed for E50, Daniel R
    // If else statement to handle command conditions
    if(message.equals("#quit")){
      quit();
    } else if (message.equals("#logoff")){
      try {
        this.closeConnection();
      } catch (IOException e) {}
      loggedOut = true;
      clientUI.display("Logged off");
    } else if (message.split(" ")[0].equals("#sethost")){
      if (loggedOut){
        this.setHost(message.split(" ")[1]);
        clientUI.display("Host set to " + this.getHost());
      } else {
        clientUI.display("Error: Please log off first to change host");
      }
    } else if (message.split(" ")[0].equals("#setport")){
      if (loggedOut){
        this.setPort(Integer.parseInt(message.split(" ")[1]));
        clientUI.display("Port set to " + this.getPort());
      } else {
        clientUI.display("Error: Please log off first to change port");
      }
    } else if (message.equals("#login")){
      if (loggedOut){
        try {
          this.openConnection();
        } catch (IOException e) {
          clientUI.display("Error connecting " + e.toString());
        }
        loggedOut = false;
      } else {
        clientUI.display("Error: Please log out first before logging in");
      }
    } else if (message.equals("#gethost")){
      clientUI.display("Host is " + this.getHost());
    } else if (message.equals("#getport")){
      clientUI.display("Port is " + this.getPort());
    } else {
      try
      {
        sendToServer(message);
      }
      catch(IOException e)
      {
        clientUI.display
          ("Could not send message to server.  Terminating client.");
        quit();
      }
    }
  }

  // **** Changed for E49, Daniel R
  /**
	 * Hook method called after the connection has been closed. The default
	 * implementation does nothing. The method may be overriden by subclasses to
	 * perform special processing such as cleaning up and terminating, or
	 * attempting to reconnect.
	 */
	protected void connectionClosed() {
          clientUI.display("Connection has been closed");
	}

  // **** Changed for E49, Daniel R
	/**
	 * Hook method called each time an exception is thrown by the client's
	 * thread that is waiting for messages from the server. The method may be
	 * overridden by subclasses.
	 * 
	 * @param exception
	 *            the exception raised.
	 */
	protected void connectionException(Exception exception) {
	      clientUI.display("The server has shut down.\n> I am quitting.");
        quit();
    }

  
  /**
   * This method terminates the client.
   */
  public void quit()
  {
    try
    {
      closeConnection();
    }
    catch(IOException e) {}
    System.exit(0);
  }
}
//End of ChatClient class
