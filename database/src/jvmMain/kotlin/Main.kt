import me.dvyy.tasks.database.VaultIndexer
import me.dvyy.tasks.database.vaultModule
import org.koin.dsl.koinApplication
import kotlin.io.path.Path

fun main() {
    val application = koinApplication {
        modules(vaultModule(Path("vault")))
    }

    val indexer = application.koin.get<VaultIndexer>()
    indexer.indexRoot()

}
