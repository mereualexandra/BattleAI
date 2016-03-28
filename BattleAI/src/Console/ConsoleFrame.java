package Console;

import javax.swing.JFrame;

/**
 *  Clasa singleton prin care se vor afisa toate mesajele importante ale serverului sau
 * clientului pentru debug
 * !OBS: Se poate afisa fie print instanta sau prin metoda statica
 * @author Dragos-Alexandru
 */
public final class ConsoleFrame extends JFrame {

    private static ConsoleFrame instance;
    public boolean ready = false;
    
    /**
     * Creates new form ConsoleFrame
     * @param isServer
     */
    private ConsoleFrame() {
        initComponents();
        printMessage("Console", "***Welcome to BattleAI Console***");
    }
    
    public static ConsoleFrame getInstance(){
        if(instance == null){
            instance = new ConsoleFrame();
        }
        return instance;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        outputScroll = new javax.swing.JScrollPane();
        outputArea = new javax.swing.JTextArea();
        inputField = new javax.swing.JTextField();
        sendButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        outputArea.setColumns(20);
        outputArea.setRows(5);
        outputArea.setEditable(false);
        outputScroll.setViewportView(outputArea);

        sendButton.setText("Send");
        sendButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sendButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(inputField, javax.swing.GroupLayout.PREFERRED_SIZE, 303, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(sendButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(outputScroll))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(outputScroll, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(inputField)
                    .addComponent(sendButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void sendButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sendButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_sendButtonActionPerformed
    
    /**
     *  Metoda sincronizata ce afiseaza un mesaj pe consola (Nestatica)
     * @param className
     * @param message
     */
    
    public synchronized void printMessage(String className, String message){
        outputArea.append(" "+className+": "+message+"\n");
    }
    
    /**
     *  Metoda sincronizata ce afiseaza un mesaj pe consola (Statica)
     * @param className
     * @param message
     * @param isMasterServer
     */
    public synchronized static void sendMessage(String className, String message, boolean isMasterServer){
        if(!isMasterServer){
            ConsoleFrame consola = getInstance();
            consola.printMessage(className, message);
        }
        sendMessageStandardOutput(className, message);
    }
    
    public synchronized static void sendMessageStandardOutput(String className, String message){
        System.out.println(" "+className+": "+message+"\n");
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField inputField;
    private javax.swing.JTextArea outputArea;
    private javax.swing.JScrollPane outputScroll;
    private javax.swing.JButton sendButton;
    // End of variables declaration//GEN-END:variables
}
