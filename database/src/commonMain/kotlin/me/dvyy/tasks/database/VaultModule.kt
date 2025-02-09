package me.dvyy.tasks.database

import kotlinx.coroutines.Dispatchers
import org.dizitart.kno2.nitrite
import org.dizitart.kno2.serialization.KotlinXSerializationMapper
import org.dizitart.no2.Nitrite
import org.dizitart.no2.mvstore.MVStoreModule
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import java.nio.file.Path
import kotlin.time.Duration.Companion.seconds

fun vaultModule(
    rootPath: Path,
) = module {
    single { VaultPaths(rootPath) }
    single<Nitrite> {
        val dbPath = get<VaultPaths>().dbPath
        nitrite {
            loadModule(
                MVStoreModule.withConfig()
                    .filePath(dbPath.toFile())
                    .build()
            )
            loadModule { setOf(KotlinXSerializationMapper()) }
        }
    }

    singleOf(::VaultDataSource)
    singleOf(::VaultIndexer)
    singleOf(::VaultFileWatcher)
    single { VaultFileSystemDataSource(rootPath) }
    single { Vault(get(), get(), get(), get(), Dispatchers.IO, queueSaveDelay = 2.seconds) }
}
