package com.example.communityapp.di

import android.content.Context
import com.example.communityapp.data.PreferencesHelper
import com.example.communityapp.data.retrofit.AuthInterceptor
import com.example.communityapp.data.retrofit.CustomAPI
import com.example.communityapp.utils.Constants
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.Date
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object NetworksModule {

    @Provides
    @Singleton
    fun provideAuthInterceptor(@ApplicationContext context: Context,preferencesHelper: PreferencesHelper): AuthInterceptor {
        return AuthInterceptor(context,preferencesHelper)
    }

    @Provides
    @Singleton
    fun providePreferenceHelper(@ApplicationContext context: Context): PreferencesHelper {
        return PreferencesHelper(context)
    }

    @Provides
    @Singleton
    fun provideCustomApi(retrofit: Retrofit): CustomAPI {
        return retrofit.create(CustomAPI::class.java)
    }
    @Provides
    @Singleton
    fun provideRetrofit(authInterceptor: AuthInterceptor): Retrofit {
        val gson = GsonBuilder().registerTypeAdapter(Date::class.java, DateTypeDeserializer()).create()
        return Retrofit.Builder()
            .baseUrl(Constants.CUSTOM_BASE_URL)
            .client(provideOkHttpClient(authInterceptor))
            .addConverterFactory(GsonConverterFactory.create(gson)).build()
    }

    private fun provideOkHttpClient(authInterceptor: AuthInterceptor): OkHttpClient {
        val builder = OkHttpClient()
            .newBuilder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .addNetworkInterceptor(authInterceptor)


        val requestInterceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
        builder.addNetworkInterceptor(requestInterceptor)

        return builder.build()
    }

    class DateTypeDeserializer : JsonDeserializer<Date> {
        private val DATE_FORMATS = arrayOf("dd/MM/yyyy HH:mm:ss", "HH:mm:ss")

        override fun deserialize(
            json: JsonElement?,
            typeOfT: Type?,
            context: JsonDeserializationContext?
        ): Date {
            for (format in DATE_FORMATS) {
                try {
                    return SimpleDateFormat(format).parse(json?.asString)
                } catch (e: Exception) {
                }
            }
            throw JsonParseException("Unparseable date: " + json?.asString)
        }
    }
}