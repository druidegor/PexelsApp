package com.mleval.pexelsapp.di

import com.mleval.pexelsapp.BuildConfig
import com.mleval.pexelsapp.data.remote.PexelsApiService
import com.mleval.pexelsapp.data.repository.PexelsRepositoryImpl
import com.mleval.pexelsapp.domain.repository.PexelsRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {

    @Binds
    @Singleton
    fun providePexelsRepository(
        impl: PexelsRepositoryImpl
    ): PexelsRepository

    companion object {

        @Provides
        @Singleton
        fun provideJson(): Json {
            return Json {
                ignoreUnknownKeys = true
                coerceInputValues = true
            }
        }

        @Provides
        @Singleton
        fun provideConverterFactory(
            json: Json
        ): Converter.Factory {
            return json.asConverterFactory("application/json".toMediaType())
        }

        @Provides
        @Singleton
        fun provideOkHttpClient(): OkHttpClient {
            return OkHttpClient.Builder()
                .addInterceptor { chain ->
                    val request = chain.request()
                        .newBuilder()
                        .addHeader("Authorization", BuildConfig.PEXELS_API_KEY)
                        .build()

                    chain.proceed(request)
                }
                .build()
        }

        @Provides
        @Singleton
        fun provideRetrofit(
            converter: Converter.Factory,
            client: OkHttpClient
        ): Retrofit {
            return Retrofit.Builder()
                .baseUrl("https://api.pexels.com/v1/")
                .client(client)
                .addConverterFactory(converter)
                .build()
        }

        @Provides
        @Singleton
        fun provideApiService(
            retrofit: Retrofit
        ): PexelsApiService {
            return retrofit.create()
        }
    }
}