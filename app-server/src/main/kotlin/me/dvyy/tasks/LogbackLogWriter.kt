package me.dvyy.tasks

import co.touchlab.kermit.LogWriter
import co.touchlab.kermit.Severity
import org.slf4j.LoggerFactory

internal object LogbackLogWriter : LogWriter() {
    val logger = LoggerFactory.getLogger("Application")
    override fun log(
        severity: Severity,
        message: String,
        tag: String,
        throwable: Throwable?,
    ) {
        when (severity) {
            Severity.Verbose -> logger.debug(message)
            Severity.Debug -> logger.debug(message)
            Severity.Info -> logger.info(message)
            Severity.Warn -> logger.warn(message)
            Severity.Error -> logger.error(message, throwable)
            Severity.Assert -> logger.debug(message)
        }
    }
}