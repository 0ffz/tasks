package me.dvyy.tasks.app.data

object Environment {
    val customAppDir = env("TASKS_APP_DIR")
    private fun env(name: String): String? = System.getenv(name)
}
