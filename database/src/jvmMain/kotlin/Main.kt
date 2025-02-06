import com.charleskorn.kaml.YamlNode
import me.dvyy.tasks.database.helpers.DocumentHelpers
import me.dvyy.tasks.database.helpers.DocumentHelpers.toYaml
import me.dvyy.tasks.database.vaultModule
import org.koin.dsl.koinApplication
import kotlin.io.path.Path

fun main() {
    val application = koinApplication {
        modules(vaultModule(Path("vault")))
    }

//    val indexer = application.koin.get<VaultIndexer>()
//    indexer.indexRoot()

//    println(
//        documentOf(
//            "test" to listOf(1, 2, 3),
//            "hello" to documentOf("world" to "asdf")
//        )
//    )
}
