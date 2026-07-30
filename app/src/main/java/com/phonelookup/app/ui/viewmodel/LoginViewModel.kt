package com.phonelookup.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.phonelookup.app.data.local.SessionManager
import com.phonelookup.app.data.repository.PhoneRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LoginUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false,
    val isAdmin: Boolean = false,
    val message: String? = null
)

class LoginViewModel(
    private val sessionManager: SessionManager
) : ViewModel() {

    private val repository = PhoneRepository()

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun login(licenseKey: String) {
        if (licenseKey.isBlank()) {
            _uiState.value = LoginUiState(error = "LICENSE KEY REQUIRED")
            return
        }

        viewModelScope.launch {
            // Start simulation of secure authentication
            _uiState.value = LoginUiState(isLoading = true, message = "AUTHENTICATING...")
            
            // Adding a small delay for "AI Processing" feel
            delay(1200)

            // Special Admin Key Check
            if (licenseKey == "MANI-ADMIN-786") {
                _uiState.value = _uiState.value.copy(
                    message = "ADMIN ACCESS GRANTED",
                    isLoading = false
                )
                delay(600)
                sessionManager.saveSession("ADMIN_TOKEN", "Admin")
                _uiState.value = _uiState.value.copy(isSuccess = true, isAdmin = true)
                return@launch
            }

            val result = repository.validateLicenseKey(licenseKey)
            
            result.fold(
                onSuccess = { isValid ->
                    if (isValid) {
                        _uiState.value = _uiState.value.copy(
                            message = "ACCESS GRANTED",
                            isLoading = false
                        )
                        delay(600)
                        
                        // Save session securely
                        sessionManager.saveSession(
                            token = "TOKEN_${licenseKey.hashCode()}",
                            username = "System User"
                        )
                        
                        _uiState.value = _uiState.value.copy(isSuccess = true)
                    } else {
                        _uiState.value = LoginUiState(
                            error = "INVALID OR EXPIRED LICENSE KEY"
                        )
                    }
                },
                onFailure = { e ->
                    _uiState.value = LoginUiState(
                        error = "CONNECTION ERROR: ${e.message?.uppercase()}"
                    )
                }
            )
        }
    }

    fun clearError() {
        if (_uiState.value.error != null) {
            _uiState.value = _uiState.value.copy(error = null, message = null)
        }
    }
}
