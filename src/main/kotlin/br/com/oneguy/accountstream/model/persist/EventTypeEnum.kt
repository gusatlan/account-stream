package br.com.oneguy.accountstream.model.persist

import com.fasterxml.jackson.annotation.JsonIgnore

enum class EventTypeEnum {
    INSERT, UPDATE, DELETE;

    @JsonIgnore
    fun isUpsert() : Boolean {
        return when(this) {
            INSERT, UPDATE -> true
            DELETE -> false
        }
    }
}