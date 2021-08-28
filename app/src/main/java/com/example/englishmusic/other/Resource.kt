package com.example.englishmusic.other

data class Resource<out T>(val status: Status, val data: T?, val message: String?) {

    companion object {
        fun <T> success(data: T?,message: String?) = Resource(Status.SUCCESS, data, message)

        fun <T> error(message: String, data: T?) = Resource(Status.ERROR, data, message)

        fun <T> loading(data: T?) = Resource(Status.LOADING, data, null)

        fun  clean() = Resource(Status.ClEAN,null,null)
    }
}

enum class Status {
    SUCCESS,
    ERROR,
    LOADING,
    ClEAN
}