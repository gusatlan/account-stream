package br.com.oneguy.accountstream.utils

import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*

fun storeJson(
    json: String? = null,
    directory: Path = Path.of("build", "json_test"),
    prefixName: String = ""
) {
    directory.toFile().mkdirs()
    Files.write(
        Paths.get(directory.toString(), "${prefixName}_${UUID.randomUUID().toString().lowercase()}.json"),
        json?.toByteArray(Charset.defaultCharset()) ?: "".toByteArray(Charset.defaultCharset())
    )
}