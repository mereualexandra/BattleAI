package Engine;

import Client.ConnectionHandler;
import Compiler.SourceCompiler;
import Console.ConsoleFrame;
import Constants.VisualConstants;
import Editor.Source;
import Intelligence.IntelligenceTemplate;
import Intelligence.Semaphore;
import Intelligence.TankThread;
import Networking.Requests.RequestType;
import Visual.VisualEngine;
import java.io.IOException;
import java.util.ArrayList;

public class IntelligenceControlThread extends Thread{
    private static IntelligenceControlThread instance;
    private BulletUpdater bulletUpdater;
    private ArrayList<TankThread> tankThreads;
    private ArrayList<Semaphore> semaphores;
    private boolean running;
    
    public IntelligenceControlThread(ArrayList<Source> surse){
        IntelligenceTemplate playerCode;
        tankThreads = new ArrayList<>();
        semaphores = new ArrayList<>();
        
        running = true;
        
        for(int i = 0; i<surse.size(); i++){
            
            synchronized(GameEntity.entityList){
                new Tank(); //adds it to entityList
            }
            
            playerCode = (IntelligenceTemplate)SourceCompiler.getInstanceOfSource(surse.get(i));
            semaphores.add(new Semaphore());
            tankThreads.add(new TankThread(playerCode, semaphores.get(i)));
        }
        
        bulletUpdater = new BulletUpdater();
    }
    
    //Testing
    //Couldn't figure out the compiler so I'm using this just for testing purposes
    //This constructor will be removed at a later date
    public IntelligenceControlThread(int numberOfTanks){
        GameEntity.entityList.clear();
        GameEntity.currentIndex = 0;
        
        IntelligenceTemplate playerCode;// = new IntelligenceTemplate();
        tankThreads = new ArrayList<>();
        semaphores = new ArrayList<>();
        
        running = true;
        
        for(int i = 0; i<numberOfTanks; i++){
            
            synchronized(GameEntity.entityList){
                new Tank(); //adds it to entityList
            }
            if(i == 0)
                playerCode = new Intelligence.TestTank1();
            else if(i==1)
                playerCode = new Intelligence.TestTank2();
            else if(i == 2)
                playerCode = new Intelligence.TestTank3();
            else
                playerCode = new IntelligenceTemplate();
            
            semaphores.add(new Semaphore());
            tankThreads.add(new TankThread(playerCode, semaphores.get(i)));
        }
        
        if(VisualEngine.getInstance().getMatchMode() == VisualConstants.SINGLEPLAYER)
            VisualEngine.getInstance().updateEntityList(GameEntity.entityList);
        
        bulletUpdater = new BulletUpdater();
        
        ConsoleFrame.sendMessage("IntelligenceControlThread","size = "+GameEntity.entityList.size());
        
    }
    //END testing
    
    @Override
    public void run(){
        
        for(int i = 0; i<tankThreads.size(); i++) {
            tankThreads.get(i).start();
        }
        
        bulletUpdater.start();
        
        while(running) {
            //OFF FOR NOW
            /*
            synchronized (GameEntity.entityList) {
                for (int i = 0; i < tankThreads.size(); i++) {
                    if (semaphores.get(i).isGreen()) {
                        EntityUpdateRequest eur = new EntityUpdateRequest(RequestType.ENTITIY_UPDATE, GameEntity.entityList);
                        try {
                            ConnectionHandler.getInstance().sendToMatch(eur);
                        } catch (IOException ex) {
                            Console.ConsoleFrame.sendMessage("IntelligenceControlThread", ex.getMessage());
                        }
                    }
                }
            }
            */
            for(int i = 0; i < tankThreads.size(); i++){
                synchronized (semaphores.get(i)) {
                    if (semaphores.get(i).isGreen()) {
                        semaphores.get(i).goRed();
                        semaphores.get(i).notify();
                    }
                }
            }
            
            try {
                this.sleep(1000/60);
            } catch (InterruptedException ex) {
                Console.ConsoleFrame.sendMessage("IntelligenceControlThread", ex.getMessage());
            }
        }
    }
    
    public void stopNicely(){
        running = false;
        
        bulletUpdater.stopNicely();
        for(int i = 0; i < tankThreads.size(); i++)
            tankThreads.get(i).stopNicely();
    }
}
