package com.example.kerjaku.ui.job

import com.example.kerjaku.data.network.JobRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kerjaku.data.model.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class JobViewModel : ViewModel() {
    private val repository = JobRepository()

    private val _jobs = MutableStateFlow<List<Job>>(emptyList())
    val jobs: StateFlow<List<Job>> = _jobs

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun fetchOpenJobs() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = repository.getOpenJobs()
                _jobs.value = result
            } catch (e: Exception) {
                e.printStackTrace()
                // TODO: Lempar error ke UI menggunakan State khusus jika perlu
            } finally {
                _isLoading.value = false
            }
        }
    }
}