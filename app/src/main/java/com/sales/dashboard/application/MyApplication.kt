package com.sales.dashboard.application

import android.app.Application
import com.sales.dashboard.database.ProfileDataBase
import com.sales.dashboard.repository.ProfileRepository
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

@HiltAndroidApp
class MyApplication : Application()