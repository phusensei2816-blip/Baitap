package com.uth.uthauth.ui

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.uth.uthauth.UthAuthApp
import com.uth.uthauth.ui.auth.LoginViewModel
import com.uth.uthauth.ui.detail.DetailViewModel
import com.uth.uthauth.ui.home.HomeViewModel
import com.uth.uthauth.ui.list.ListViewModel
import com.uth.uthauth.ui.profile.ProfileViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            val c = uthApp().container
            LoginViewModel(c.authRepository, c.googleCredentialProvider, c.telemetry, c.preferences)
        }
        initializer {
            val c = uthApp().container
            HomeViewModel(
                c.authRepository, c.taskRepository, c.telemetry,
                c.remoteConfig, c.presence, c.pushRegistrar
            )
        }
        initializer {
            val c = uthApp().container
            ListViewModel(c.taskRepository, c.telemetry)
        }
        initializer {
            val c = uthApp().container
            // createSavedStateHandle() chứa tham số điều hướng (taskId)
            DetailViewModel(createSavedStateHandle(), c.taskRepository, c.telemetry)
        }
        initializer {
            val c = uthApp().container
            ProfileViewModel(c.authRepository, c.taskRepository, c.telemetry, c.presence)
        }
    }
}

private fun CreationExtras.uthApp(): UthAuthApp = this[APPLICATION_KEY] as UthAuthApp
