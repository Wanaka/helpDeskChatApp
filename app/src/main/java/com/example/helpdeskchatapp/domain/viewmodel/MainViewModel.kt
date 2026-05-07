package com.example.helpdeskchatapp.domain.viewmodel

import androidx.lifecycle.ViewModel
import com.example.helpdeskchatapp.data.interfaces.UserRepository
import com.example.helpdeskchatapp.navigation.AdminRouteKey
import com.example.helpdeskchatapp.navigation.LoginRouteKey
import androidx.navigation3.runtime.NavKey
import com.example.helpdeskchatapp.util.CurrentUserId
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    fun getInitialRoute(): NavKey {
        val userId = userRepository.getCurrentUser()
        return if (userId != null) {
            CurrentUserId.CURRENT_USER_ID = userId
            AdminRouteKey
        } else {
            LoginRouteKey
        }
    }
}
