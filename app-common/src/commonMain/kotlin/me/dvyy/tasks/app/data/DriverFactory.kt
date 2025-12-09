package me.dvyy.tasks.app.data

import me.dvyy.sqlite.Database

expect fun createDatabase(): Database

//fun createClientDatabase(db: Database): ClientSchema = runBlocking {
//    val schema = AppSchema.asClientSchema(db)
//    schema.initialize()
//    schema
//}
