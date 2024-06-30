package main;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javax.imageio.ImageIO;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;

public class Main extends Application {

    public static Stage stage;
    // a timer allowing the tray icon to provide a periodic notification event.
    private final Timer notificationTimer = new Timer();
    // format used to display the current time in a tray icon notification.
    private final DateFormat timeFormat = SimpleDateFormat.getTimeInstance();
    private static final String iconImageLoc =
            "https://icons.iconarchive.com/icons/fatcow/farm-fresh/16/temperature-cold-icon.png";


    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("resources/fxml/display.fxml"));
        primaryStage.setTitle("Riedel Chiller Monitor");
        primaryStage.setScene(new Scene(root, 600, 400));
        Platform.setImplicitExit(false);
        javax.swing.SwingUtilities.invokeLater(this::addAppToTray);
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.show();
        stage = primaryStage;
    }

    private void addAppToTray() {
        try {
            // ensure awt toolkit is initialized.
            java.awt.Toolkit.getDefaultToolkit();

            // app requires system tray support, just exit if there is no support.
            if (!java.awt.SystemTray.isSupported()) {
                System.out.println("No system tray support, application exiting.");
                Platform.exit();
            }

            // set up a system tray icon.
            java.awt.SystemTray tray = java.awt.SystemTray.getSystemTray();
            URL imageLoc = new URL(
                    iconImageLoc
            );
            java.awt.Image image = ImageIO.read(imageLoc);
            java.awt.TrayIcon trayIcon = new java.awt.TrayIcon(image);

            // if the user double-clicks on the tray icon, show the main app stage.
            trayIcon.addActionListener(event -> Platform.runLater(this::showStage));

            // if the user selects the default menu item (which includes the app name),
            // show the main app stage.
            java.awt.MenuItem openItem = new java.awt.MenuItem("RCM show");
            openItem.addActionListener(event -> Platform.runLater(this::showStage));

            // the convention for tray icons seems to be to set the default icon for opening
            // the application stage in a bold font.
            java.awt.Font defaultFont = java.awt.Font.decode(null);
            java.awt.Font boldFont = defaultFont.deriveFont(java.awt.Font.BOLD);
            openItem.setFont(boldFont);

            // to really exit the application, the user must go to the system tray icon
            // and select the exit option, this will shutdown JavaFX and remove the
            // tray icon (removing the tray icon will also shut down AWT).
            java.awt.MenuItem exitItem = new java.awt.MenuItem("Exit");
            exitItem.addActionListener(event -> {
                notificationTimer.cancel();
                Platform.exit();
                tray.remove(trayIcon);
            });

            // setup the popup menu for the application.
            final java.awt.PopupMenu popup = new java.awt.PopupMenu();
            popup.add(openItem);
            popup.addSeparator();
            popup.add(exitItem);
            trayIcon.setPopupMenu(popup);

            // create a timer which periodically displays a notification message.
//            notificationTimer.schedule(
//                    new TimerTask() {
//                        @Override
//                        public void run() {
//                          //  javax.swing.SwingUtilities.invokeLater(() ->
////                                    trayIcon.displayMessage(
////                                            "hello",
////                                            "The time is now " + timeFormat.format(new Date()),
////                                            java.awt.TrayIcon.MessageType.INFO
//                                    //)
//                  //          );
//                        }
//                    },
//                    5_000,
//                    60_000
//            );
            // add the application tray icon to the system tray.
            tray.add(trayIcon);
        } catch (java.awt.AWTException | IOException e) {
            System.out.println("Unable to init system tray");
            e.printStackTrace();
        }
    }

    private void showStage() {
        if (stage != null) {
            stage.show();
            stage.toFront();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static void logging(String buf){
        try {
            FileWriter writer = new FileWriter("log.txt", true);
            writer.append(String.valueOf(new Date())).append(": ").append(buf).append("\n");
            writer.close();
        } catch (IOException e) {
            System.out.println("Ошибка при записи в файл");
            e.printStackTrace();
        }
    }
}
