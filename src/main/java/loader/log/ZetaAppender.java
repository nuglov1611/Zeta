package loader.log;

import loader.ZetaProperties;
import org.apache.log4j.AsyncAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import java.io.IOException;


public class ZetaAppender extends AsyncAppender {
    private static final Logger log = Logger.getLogger(ZetaAppender.class);

    public ZetaAppender() {
        try {
            FileAppender file = new FileAppender(new PatternLayout(), ZetaProperties.HOME_PATH + "zeta.log", false);
            this.addAppender(file);
        } catch (IOException e) {
            log.error("", e);
        }
    }

}
