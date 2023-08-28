package com.example.wealthbuilder.ui.company_info

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wealthbuilder.domain.repository.StockRepository
import com.example.wealthbuilder.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CompanyInfoViewModel @Inject constructor(
    private val repository: StockRepository,
    private val savedStateHandle: SavedStateHandle
): ViewModel() {

    var state = MutableStateFlow(CompanyInfoState())

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val symbol = savedStateHandle.get<String>("symbol") ?: return@launch
            state.value = state.value.copy(isLoading = true)
            val companyInfoResult = async { repository.getCompanyInfo(symbol) }
            val intradayInfoResult = async { repository.getIntradayInfo(symbol) }

            when(val result = companyInfoResult.await()) {
                is Resource.Success -> {
                    state.value = state.value.copy(
                        company = result.data,
                        isLoading = false,
                        error = null
                    )
                }
                is Resource.Error -> {
                    state.value = state.value.copy(
                        company = null,
                        isLoading = false,
                        error = result.message
                    )
                }
                else -> Unit
            }
            when(val result = intradayInfoResult.await()) {
                is Resource.Success -> {
                    state.value = state.value.copy(
                        stockTrades = result.data ?: emptyList(),
                        isLoading = false,
                        error = null
                    )
                }
                is Resource.Error -> {
                    state.value = state.value.copy(
                        company = null,
                        stockTrades = emptyList(),
                        isLoading = false,
                        error = result.message
                    )
                }
                else -> Unit
            }  // end WHEN
        }  // end COROUTINE
    }  // end INIT
}