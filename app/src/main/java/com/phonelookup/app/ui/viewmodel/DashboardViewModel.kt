package com.phonelookup.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.phonelookup.app.data.model.PhoneResult
import com.phonelookup.app.data.repository.PhoneRepository
import com.phonelookup.app.native_bridge.NativeBridge
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class DashboardUiState(
    val isLoading: Boolean = false,
    val result: PhoneResult? = null,
    val formattedResult: String = "",
    val error: String? = null,
    val showCopied: Boolean = false,
    val selectedTab: Int = 0 // 0: Mobile, 1: Aadhar/Family
)

class DashboardViewModel : ViewModel() {

    private val repository = PhoneRepository()

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    fun setTab(index: Int) {
        _uiState.value = _uiState.value.copy(selectedTab = index, result = null, error = null)
    }

    /** Lookup a phone number with native validation */
    fun lookupPhone(phoneNumber: String) {
        if (_uiState.value.selectedTab == 1) {
            lookupFamily(phoneNumber)
            return
        }

        // Validate using native C++ (faster than Kotlin regex)
        if (!NativeBridge.validatePhoneNumber(phoneNumber)) {
            _uiState.value = _uiState.value.copy(error = "Invalid phone number format")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            val result = repository.lookupPhone(phoneNumber)
            result.fold(
                onSuccess = { phoneResult ->
                    // Format result using native C++ string processing
                    val formatted = repository.formatResultForCopy(phoneResult)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        result = phoneResult,
                        formattedResult = formatted
                    )
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message ?: "Lookup failed. Please try again."
                    )
                }
            )
        }
    }

    private fun lookupFamily(aadharNumber: String) {
        if (aadharNumber.length < 12) {
            _uiState.value = _uiState.value.copy(error = "Enter a valid 12-digit Aadhar number")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            val result = repository.lookupFamily(aadharNumber)
            result.fold(
                onSuccess = { phoneResult ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        result = phoneResult,
                        formattedResult = phoneResult.lineType // Members list
                    )
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message ?: "Aadhar lookup failed."
                    )
                }
            )
        }
    }

    /** Mark copy toast as shown, then auto-hide after delay */
    fun onCopied() {
        _uiState.value = _uiState.value.copy(showCopied = true)
        viewModelScope.launch {
            kotlinx.coroutines.delay(2000)
            _uiState.value = _uiState.value.copy(showCopied = false)
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun clearResult() {
        _uiState.value = DashboardUiState()
    }
}
