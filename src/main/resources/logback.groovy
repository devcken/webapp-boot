import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.core.ConsoleAppender

import static ch.qos.logback.classic.Level.DEBUG
import static ch.qos.logback.classic.Level.ERROR
import static ch.qos.logback.classic.Level.INFO

scan("1000")

appender("CONSOLE", ConsoleAppender) {
    encoder(PatternLayoutEncoder) {
        pattern = "[%d{HH:mm:ss.SSS}] %white([%t]) %highlight(%-5level) %white(@) %black(%logger{20}) %boldGreen(>) %msg %n"
    }
}

root(DEBUG, ["CONSOLE"])

logger("path.to.pkg", DEBUG)