package me.dvyy.tasks.app.logging

import co.touchlab.kermit.Message
import co.touchlab.kermit.MessageStringFormatter
import co.touchlab.kermit.Severity
import co.touchlab.kermit.Tag

object ColoredFormatter : MessageStringFormatter {
    override fun formatSeverity(severity: Severity): String {
        return when (severity) {
            Severity.Verbose -> "\u001B[37mTRACE"  // White
            Severity.Debug -> "\u001B[36mDEBUG"    // Cyan
            Severity.Info -> "\u001B[32mINFO "     // Green
            Severity.Warn -> "\u001B[33mWARN "     // Yellow
            Severity.Error -> "\u001B[31mERROR"    // Red
            Severity.Assert -> "\u001B[35mASSRT"   // Magenta
        }
    }

    override fun formatMessage(severity: Severity?, tag: Tag?, message: Message): String {
        return buildString {
            if (severity != null) append(formatSeverity(severity))
            if (tag != null && tag.tag.isNotEmpty()) {
                append("\u001B[33m ")
                append(tag.tag)
            }
            append(' ')
            append("\u001B[36m[${Thread.currentThread().name}]")
            append("\u001B[0m ")
            append(message.message)
        }
    }
}