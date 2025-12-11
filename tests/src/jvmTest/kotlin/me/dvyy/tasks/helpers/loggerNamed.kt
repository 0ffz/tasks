package me.dvyy.tasks.helpers

import co.touchlab.kermit.CommonWriter
import co.touchlab.kermit.Logger
import co.touchlab.kermit.Severity
import co.touchlab.kermit.loggerConfigInit

fun loggerNamed(name: String) = Logger(
    loggerConfigInit(CommonWriter(), minSeverity = Severity.Verbose),
    name
)