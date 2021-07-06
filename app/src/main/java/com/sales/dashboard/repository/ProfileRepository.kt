package com.sales.dashboard.repository

import com.sales.dashboard.database.Profile
import com.sales.dashboard.database.ProfileDao
import kotlinx.coroutines.flow.Flow

class ProfileRepository(private val dao: ProfileDao) {

    val allProfile: Flow<List<Profile>> = dao.getUser()

    suspend fun insert(profile: Profile){
        dao.insert(profile)
    }
}