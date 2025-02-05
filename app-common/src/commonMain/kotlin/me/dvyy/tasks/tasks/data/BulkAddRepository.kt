//package me.dvyy.tasks.tasks.data
//
//import kotlinx.datetime.Clock
//import kotlinx.datetime.LocalDate
//import kotlinx.datetime.TimeZone
//import kotlinx.datetime.toLocalDateTime
//import me.dvyy.tasks.db.client.Task
//import me.dvyy.tasks.model.Highlight
//import me.dvyy.tasks.model.ListId
//import me.dvyy.tasks.model.TaskId
//
//class BulkAddRepository {
//    fun String.prop(vararg delimiters: String): String? {
//        val regex = Regex("(?<=${delimiters.joinToString("|") { Regex.escape(it) }})\\S+")
//        return regex.find(this)?.value//?.groupValues?.get(1) ?: ""
//    }
//
//    fun parseLine(text: String): Task {
//        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
//        val date = text.prop("^")?.let { LocalDate.parse(it) } ?: today
//
//        val highlight = Highlight(Highlight.Type.entries[text.prop("!")?.toIntOrNull() ?: 0], isLight = true)
//        val taskText = text.substringAfter(":")
//        return Task(
//            uuid = TaskId.new(),
//            text = taskText,
//            highlight = highlight,
//            completed = false,
//            list = ListId.forDate(date),
//        )
//    }
//}
