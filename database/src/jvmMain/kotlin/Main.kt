import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.cbor.CborArray
import kotlinx.serialization.cbor.CborTag
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.decodeFromHexString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.encodeToHexString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import me.dvyy.tasks.database.VaultDataSource
import me.dvyy.tasks.database.helpers.DocumentYamlHelpers.toYaml
import me.dvyy.tasks.database.helpers.applyTimestamps
import me.dvyy.tasks.database.helpers.conflictFreeMerge
import me.dvyy.tasks.database.vaultModule
import org.dizitart.kno2.documentOf
import org.dizitart.no2.collection.Document
import org.koin.dsl.koinApplication
import kotlin.io.path.Path
import kotlin.math.max

@OptIn(ExperimentalSerializationApi::class, ExperimentalStdlibApi::class)
fun main() {
    val application = koinApplication {
        modules(vaultModule(Path("vault")))
    }

    val vault = application.koin.get<VaultDataSource>()
    val docA = documentOf("address" to documentOf("street" to "hello", "postal" to "world"), "something" to 1)
        .applyTimestamps(100)
        .merge(documentOf("address.street" to null).applyTimestamps(200))

    val docB = documentOf("something" to listOf(1, 2, 3)).applyTimestamps(300)

    println(docA.conflictFreeMerge(docB))
    println(docB.conflictFreeMerge(docA))
    println(Json.encodeToString(docB.conflictFreeMerge(docA).toJsonElement()))
}
