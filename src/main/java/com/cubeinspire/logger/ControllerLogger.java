package com.cubeinspire.logger;

import com.cubeinspire.logger.model.Logger;
import com.cubeinspire.logger.view.LogView;
import javafx.scene.Node;

public class ControllerLogger {

    private static Logger logger;
    private static LogView logView;

    private static final org.slf4j.Logger LOG  = org.slf4j.LoggerFactory.getLogger(ControllerLogger.class);

    public ControllerLogger(){
        logger = new Logger();
        logView = new LogView();
    }

    public static void warning(String text) { logView.addLog(logger.warning(text)); LOG.error(text); System.out.println(text);}
    public static void error(String text) { logView.addLog(logger.error(text)); LOG.error(text); System.out.println(text);}
  /*  public static void debug(String text) { logView.addLog(logger.debug(text)); LOG.debug(text); System.out.println(text);}*/
    public static void info(String text) { logView.addLog(logger.info(text)); LOG.info(text); System.out.println(text);}
    public static void postPendLog(String text) { logView.postPendLog(text); System.out.print(text); }

    public Node getViewOutput() {
        return logView.getPane();
    }
}
