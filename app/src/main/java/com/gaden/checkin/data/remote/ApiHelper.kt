package com.gaden.checkin.data.remote

import android.util.Log
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import retrofit2.HttpException

fun HttpException.parseErrorMessage(): String {
    return try {
        val errorBody = this.response()?.errorBody()?.string() ?: ""
        val json = Json.parseToJsonElement(errorBody)
        json.jsonObject["message"]?.jsonPrimitive?.content!!
    }
    catch (e: Exception) {
        "Something went wrong" + e.message
    }
}

suspend fun <T> safeApiCall(key: String? = null, apiCall: suspend () -> T): Result<T> {
    return try {
        Result.success(apiCall.invoke())
    }
    catch (e: HttpException) {
        val message = e.parseErrorMessage()
        Log.e("ApiException${key?.let { "- ${key}" }}", message)
        Result.failure(Exception(message))
    }
    catch (e: Exception) {
        Log.e("ApiException", e.message ?: "Unknown error")
        Result.failure(e)
    }
}
