import me.dvyy.tasks.database.VaultIndexer
import me.dvyy.tasks.database.vaultModule
import org.koin.dsl.koinApplication
import kotlin.io.path.Path

fun main() {
    val application = koinApplication {
        modules(vaultModule(Path("vault")))
    }

//    indexer.indexRoot()

//    println(
//        documentOf(
//            "test" to listOf(1, 2, 3),
//            "hello" to documentOf("world" to "asdf")
//        )
//    )
}
