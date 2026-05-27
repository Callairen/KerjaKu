package com.example.kerjaku.ui.job

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kerjaku.data.model.Job
import com.example.kerjaku.data.model.JobApplication
import com.example.kerjaku.data.network.JobRepository
import com.example.kerjaku.data.network.SupabaseApi
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class JobViewModel : ViewModel() {
    private val repository = JobRepository()

    private val _jobs = MutableStateFlow<List<Job>>(emptyList())
    val jobs: StateFlow<List<Job>> = _jobs

    private val _selectedJob = MutableStateFlow<Job?>(null)
    val selectedJob: StateFlow<Job?> = _selectedJob

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _actionSuccess = MutableStateFlow(false)
    val actionSuccess: StateFlow<Boolean> = _actionSuccess
    private val _myPostedJobs = MutableStateFlow<List<Job>>(emptyList())
    val myPostedJobs: StateFlow<List<Job>> = _myPostedJobs

    private val _applicants = MutableStateFlow<List<JobApplication>>(emptyList())
    val applicants: StateFlow<List<JobApplication>> = _applicants
    fun fetchOpenJobs() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _jobs.value = repository.getOpenJobs()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getJobDetail(jobId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _selectedJob.value = repository.getJobById(jobId)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun createNewJob(title: String, description: String, wage: Double, duration: Int, city: String, district: String, village: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Ambil UID user yang sedang login dari sesi Supabase
                val currentUser = SupabaseApi.client.auth.currentUserOrNull()
                currentUser?.let { user ->
                    val newJob = Job(
                        employer_id = user.id,
                        title = title,
                        description = description,
                        wage = wage,
                        duration_days = duration,
                        city = city,
                        district = district,
                        village = village
                    )
                    repository.createJob(newJob)
                    _actionSuccess.value = true
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun applyJob(jobId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val currentUser = SupabaseApi.client.auth.currentUserOrNull()
                currentUser?.let { user ->
                    val application = JobApplication(
                        job_id = jobId,
                        worker_id = user.id
                    )
                    repository.applyForJob(application)
                    _actionSuccess.value = true
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchMyPostedJobs() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val currentUser = SupabaseApi.client.auth.currentUserOrNull()
                currentUser?.let { user ->
                    _myPostedJobs.value = repository.getMyPostedJobs(user.id)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
    fun fetchApplicants(jobId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _applicants.value = repository.getApplicantsForJob(jobId)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun acceptApplicant(applicationId: String, jobId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.updateApplicationStatus(applicationId, "ACCEPTED")
                repository.updateJobStatus(jobId, "ON_GOING")

                fetchApplicants(jobId)
                fetchMyPostedJobs()
                _actionSuccess.value = true
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
    fun resetActionState() {
        _actionSuccess.value = false
    }
}