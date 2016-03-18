package Main;

import Console.ConsoleFrame;
import Constants.MasterServerConstants;
import Interface.MainFrame;
import java.awt.EventQueue;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Main implements ApplicationState{
    
    public static ConsoleFrame console;
    static MainFrame mainFrame;
        
    public static void main(String[] args){
        
        final boolean showConsole;
        final boolean isServer;
        
        if(args.length > 0){
            switch (args[0]) {
                case MASTER_SERVER:
                {
                    isServer = true;
                    showConsole = true;
                }
                break;
                case CLIENT_CONSOLE:
                {
                    isServer = false;
                    showConsole = true;
                }
                break;
                default:{
                    isServer = false;
                    showConsole = false;
                }
            }
        }else{
            showConsole = false;
            isServer = false;
        }
        
        //Starting main interface if is not server
        if(!isServer){
            EventQueue.invokeLater(new Runnable() {

                @Override
                public void run() {
                    mainFrame = new MainFrame();
                    mainFrame.setLocationRelativeTo(null);
                    mainFrame.setVisible(true);
                }
            });
        }
        
        //Starting console if wanted
        if(showConsole){
            EventQueue.invokeLater(new Runnable() {

                @Override
                public void run() {
                    console = new ConsoleFrame(isServer);
                    console.setLocation(100, 100);
                    console.setVisible(true);
                }
            });
            while(console == null){
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        
        if(isServer){
            //Start server connection
        }
    }
}