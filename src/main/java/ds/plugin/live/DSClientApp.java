package ds.plugin.live;

import ddb.CheckThreadViolationRepaintManager;
import ddb.splash.SplashScreen3;
import ds.core.DSClient;
import ds.core.DSConstants;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.RepaintManager;

public class DSClientApp {
   private JFrame appWindow;
   private final DSClient client;
   CheckThreadViolationRepaintManager repaintManager = new CheckThreadViolationRepaintManager();
   SplashScreen3 splashScreen;

   public DSClientApp(String[] args) {
      RepaintManager.setCurrentManager(this.repaintManager);

      try {
         EventQueue.invokeAndWait(new Runnable() {
            @Override
            public void run() {
               DSConstants.InstallDefaults();
              splashScreen = new SplashScreen3("Loading Danderspritz");
              splashScreen.setVisible(true);
              appWindow = new JFrame(String.format("%s - %s", "DanderSpritz", "Starting..."));
            }
         });
      } catch (Exception e) {
         e.printStackTrace();
         System.exit(-1);
      }

      this.client = new DSClient(this.splashScreen, args);

      try {
         synchronized(this.client) {
            EventQueue.invokeLater(new Runnable() {
               @Override
               public void run() {
                 client.setOwningFrame(DSClientApp.this.appWindow);
                 client.initialize();
               }
            });
            this.client.wait();
            this.client.establishConnection();
         }

         EventQueue.invokeAndWait(new Runnable() {
            @Override
            public void run() {
              appWindow.setDefaultCloseOperation(0);
              appWindow.setJMenuBar(DSClientApp.this.client.getMenuBar());
               JComponent mainWidget =client.getMainWidget();
              appWindow.add(mainWidget);
               mainWidget.addComponentListener(new ComponentAdapter() {
                  @Override
                  public void componentHidden(ComponentEvent var1) {
                     System.exit(0);
                  }
               });
              appWindow.setSize(new Dimension(DSConstants.WINDOW_WIDTH, DSConstants.WINDOW_HEIGHT));
              appWindow.setVisible(true);
              splashScreen.setVisible(false);
              splashScreen.dispose();
            }
         });
      } catch (Exception e) {
         e.printStackTrace();
         System.exit(-1);
      }

   }

   public static void main(String[] args) {
      new DSClientApp(args);
   }
}
