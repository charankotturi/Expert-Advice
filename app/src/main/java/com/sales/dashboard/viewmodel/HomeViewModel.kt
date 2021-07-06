package com.sales.dashboard.viewmodel

import androidx.lifecycle.*
import com.sales.dashboard.database.Profile
import com.sales.dashboard.repository.ProfileRepository
import dagger.Component
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: ProfileRepository
): ViewModel(){

    val allProfiles: LiveData<List<Profile>> = repository.allProfile.asLiveData()

    fun inset(profile: Profile) =
        viewModelScope.launch {
            repository.insert(profile)
        }

    val admin = MutableLiveData<Boolean>()
}