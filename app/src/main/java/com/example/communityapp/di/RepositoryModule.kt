package com.example.communityapp.di

import com.example.communityapp.data.repository.FeedsRepo
import com.example.communityapp.data.repository.JobsRepo
import com.example.communityapp.data.retrofit.CustomAPI
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object RepositoryModule {

    @Provides
    @Singleton
    fun provideJobsRepository(customAPI: CustomAPI): JobsRepo {
        return JobsRepo(FirebaseFirestore.getInstance(), customAPI)
    }

    @Provides
    @Singleton
    fun provideFeedsRepository(customAPI: CustomAPI): FeedsRepo {
        return FeedsRepo(FirebaseFirestore.getInstance(), FirebaseStorage.getInstance(), customAPI)
    }

}