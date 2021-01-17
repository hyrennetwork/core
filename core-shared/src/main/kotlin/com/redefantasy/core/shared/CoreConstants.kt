package com.redefantasy.core.shared

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.guava.GuavaModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.google.gson.Gson
import okhttp3.OkHttpClient
import java.util.*

/**
 * @author SrGutyerrez
 **/
object CoreConstants {

    val HOME_FOLDER = "/home"

    val JACKSON = ObjectMapper()
    val GSON = Gson()
    val OK_HTTP = OkHttpClient()
    val RANDOM = Random()

    init {
        JACKSON.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        JACKSON.configure(DeserializationFeature.WRAP_EXCEPTIONS, true)
        JACKSON.registerModule(GuavaModule())
        JACKSON.registerModule(KotlinModule())
        JACKSON.setSerializationInclusion(JsonInclude.Include.NON_NULL)
        JACKSON.setVisibility(
                JACKSON.serializationConfig.defaultVisibilityChecker
                        .with(JsonAutoDetect.Visibility.NONE)
                        .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
        )
    }

}