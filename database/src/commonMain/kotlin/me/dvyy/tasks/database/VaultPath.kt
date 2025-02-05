package me.dvyy.tasks.database

import kotlin.io.path.Path

class VaultPath(desiredPath: String) {
    val pathString: String = desiredPath.removePrefix("/")
    init {
        val asPath = Path(pathString)
        require(!asPath.isAbsolute) { "Path must be relative" }
        // TODO make can't go up from Path(""), .. internally is okay
        require(!pathString.contains("..")) { "Path must not contain .." }
    }
    companion object{
        val root = VaultPath("/")
    }
}
