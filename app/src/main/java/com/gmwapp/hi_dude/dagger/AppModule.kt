package com.gmwapp.hi_dude.dagger

import com.gmwapp.hi_dude.BaseApplication
import com.gmwapp.hi_dude.BuildConfig
import com.gmwapp.hi_dude.retrofit.ApiInterface
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.Interceptor.*
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import org.greenrobot.eventbus.EventBus
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Singleton
    @Provides
    fun providesHttpLoggingInterceptor() = HttpLoggingInterceptor()
        .apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

    @Singleton
    @Provides
    fun providesOkHttpClient(httpLoggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
        val okClientBuilder = OkHttpClient.Builder().connectTimeout(100, TimeUnit.SECONDS)
            .readTimeout(100, TimeUnit.SECONDS)

        okClientBuilder.addInterceptor(object : Interceptor {
            @Throws(IOException::class)
            override fun intercept(chain: Chain): Response {
                val request: Request.Builder = chain.request().newBuilder()
                request.header("Authorization", "Bearer "+BaseApplication.getInstance()?.getPrefs()?.getAuthenticationToken())
                val response = chain.proceed(request.build())
                if(response.code == 401){
                    EventBus.getDefault().post(UnauthorizedEvent());
                }
                return response
            }
        })
        if (BuildConfig.DEBUG) {
            okClientBuilder.addInterceptor(httpLoggingInterceptor)
        }
        return okClientBuilder.build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {

        val gson = GsonBuilder()
            .setLenient()
            .create()


        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL) // Replace with your base URL
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(okHttpClient)
            .build()
    }

    @Provides
    @Singleton
    fun provideApiInterface(retrofit: Retrofit): ApiInterface {
        return retrofit.create(ApiInterface::class.java)
    }
}

