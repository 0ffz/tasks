package me.dvyy.tasks.model

import com.benasher44.uuid.uuid4

object AppUuids {
    fun forTask() = uuid4()

    fun forDate(date: String) = uuid4()
}
