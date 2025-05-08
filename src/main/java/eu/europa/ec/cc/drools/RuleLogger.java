package eu.europa.ec.cc.drools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RuleLogger {

    private static final Logger LOGGER = LoggerFactory.getLogger("eu.europa.ec.cc.rules");

    public static void info(String format, Object arg) {
        LOGGER.info(format, arg);
    }

}
