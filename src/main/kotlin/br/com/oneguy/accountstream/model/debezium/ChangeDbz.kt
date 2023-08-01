package br.com.oneguy.accountstream.model.debezium

import br.com.oneguy.accountstream.util.cleanCodeTextWithoutUnderscore
import com.fasterxml.jackson.annotation.JsonIgnore

data class ChangeDbz(
    val changes: Map<String, String> = emptyMap()
) {

    @JsonIgnore
    fun getValue(id: String): String? {
        return changes.keys
            .filter { k ->
                cleanCodeTextWithoutUnderscore(k).trim() == cleanCodeTextWithoutUnderscore(id)
            }
            .map { k ->
                changes[k]
            }.firstOrNull()
    }
}