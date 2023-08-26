package com.example.wealthbuilder.ui.company_listings

import android.util.Log
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wealthbuilder.domain.repository.StockRepository
import com.example.wealthbuilder.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class CompanyListingsViewModel @Inject constructor(
    private val repository: StockRepository
): ViewModel() {

    var state = MutableStateFlow(CompanyListingsState())

    var searchJob: Job? = null

    init {
        getCompanyListings(true, "")
    }
    fun onEvent(event: CompanyListingsEvent) {
        when(event) {
            is CompanyListingsEvent.Refresh -> {
                getCompanyListings(fetchFromRemote = true)
            }
            is CompanyListingsEvent.OnSearchQueryChanged -> {
                state.value = state.value.copy(searchQuery = event.query)
                searchJob?.cancel()
                searchJob = viewModelScope.launch(Dispatchers.IO) {
                    delay(500L)
                    getCompanyListings(fetchFromRemote = false)
                }
            }
        }
    }

    fun getCompanyListings(
        fetchFromRemote: Boolean = false,
        query: String = state.value.searchQuery.lowercase()
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            repository
                .getCompanyListings(fetchFromRemote, query)
                .collect() { result ->
                    when(result) {
                        is Resource.Success -> {
                            result.data?.let { companyListings ->
                                Log.d("DEBUG_UI", "Collected resource type success with size: ${companyListings.size.toString()} in view model!")
                                state.value = state.value.copy(
                                    companies = companyListings
                                )
                            }
                        }
                        is Resource.Error -> Unit
                        is Resource.Loading -> {
                            state.value = state.value.copy(
                                isLoading = result.isLoading
                            )
                        }
                    }  // end WHEN
                }  // end COLLECT
        }  // end COROUTINE
    }  // end FUN
}