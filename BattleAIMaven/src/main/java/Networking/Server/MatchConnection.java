package Networking.Server;

import Constants.MasterServerConstants;
import Networking.Requests.AddPlayer;
import Networking.Requests.RemovePlayer;
import Networking.Requests.Request;
import Networking.Requests.RequestType;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * MatchConnection handles the continuous connection between a
 * hosted match and the master server. The master server requires the host of 
 * the match to send requests each PACKET_DELAY milliseconds in order
 * to check if the connection is still active. This class starts a thread 
 * the constructor running its own run method in order to read and
 * handle requests.
 * The connection is deemed inactive if a period of PACKET_DELAY * 2 
 * milliseconds have passed and no request has been received!
 */
public class MatchConnection extends Connection {
    
    private final Match activeMatch;
    
    public MatchConnection(Socket clientSocket, 
            ObjectInputStream inputStream,
            ObjectOutputStream outputStream,
            Match activeMatch)  {
        super(clientSocket, inputStream, outputStream);
        this.activeMatch = activeMatch;
    }
    
     /**
     * @return Returns the match associated with this connection.
     */
    public Match getActiveMatch() {
        return activeMatch;
    }
    
     /**
     * Starts a handler which takes care of connection activity, marking it
     * active or inactive.
     */
    private void startConnectionHandler() {
        Timer connectionHandler = new Timer();
        
        /* This task runs every PACKET_DELAY * 2 milliseconds. If 
        activeConnection is true, then a request has been received in the last 
        PACKET_DELAY * 2 milliseconds and we set activeConnection to false. 
        activeConnections becomes true after a request has been received. If 
        activeConnection is false when this task is executed, it means a request
        has not been received in the last PACKET_DELAY * 2 milliseconds and 
        the connection is inactive for good.
        */
        TimerTask handleConnections = new TimerTask() {
            @Override
            public void run() {
                if (!threadRunning) {
                    connectionHandler.cancel();
                    try {
                        clientSocket.close();
                    } catch (IOException ex) {
                        Logger.getLogger(MatchConnection.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    return;
                }
                
                int level = inactivityLevel.incrementAndGet();
                
                if (level == MAX_INACTIVITY_LEVEL) {
                    // Shut down the thread
                    System.out.println("closing");
                    activeConnection = false;
                    threadRunning = false;
                    try {
                        /* Close the input stream of the socket. This also 
                        forces readObject() to exit if it's still waiting for 
                        an object to be read from the stream.
                        */
                        clientSocket.close();
                        System.out.println("Closed input");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        
        connectionHandler.scheduleAtFixedRate(handleConnections, MasterServerConstants.PACKET_DELAY * 2, 
                MasterServerConstants.PACKET_DELAY * 2);
    }
    
    @Override
    public void run() {
        threadRunning = true;
        startConnectionHandler();
        
        Object object = null;

        while (threadRunning) {
            try {
                if (!clientSocket.isInputShutdown()) {
                    object = inputStream.readObject();
                   
                    // decrease level by 1 but remain non-negative
                    inactivityLevel.updateAndGet(i -> i > 0 ? i - 1 : i);
                    
                    Request request = (Request)object;
                    request.execute(outputStream);
                    
                    if (request.getType() == RequestType.ADD_PLAYER) {
                        AddPlayer player = (AddPlayer)request;
                        activeMatch.addPlayer(player.getUsername());
                    } else if (request.getType() == RequestType.REMOVE_PLAYER) {
                        RemovePlayer player = (RemovePlayer)request;
                        activeMatch.removePlayer(player.getUsername());
                    }
                }

            } catch (IOException | ClassNotFoundException ex) {
                Logger.getLogger(MatchConnection.class.getName()).log(Level.SEVERE, null, ex);
                threadRunning = false;
                activeConnection = false;
            }
        }
    }
}
