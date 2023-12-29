package com.example.communityapp.di

import com.example.communityapp.data.repository.JobsRepo
import com.google.firebase.firestore.FirebaseFirestore
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
    fun provideJobsRepository(): JobsRepo {
        return JobsRepo(FirebaseFirestore.getInstance())
    }

}