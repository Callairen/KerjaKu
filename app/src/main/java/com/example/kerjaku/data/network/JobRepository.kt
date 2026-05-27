package com.example.kerjaku.data.network

import com.example.kerjaku.data.model.Job
import com.example.kerjaku.data.model.JobApplication
import com.example.kerjaku.data.network.SupabaseApi
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class JobRepository {
    private val client = SupabaseApi.client

    // Mengambil semua pekerjaan yang statusnya masih OPEN
    suspend fun getOpenJobs(): List<Job> {
        return withContext(Dispatchers.IO) {
            client.postgrest["jobs"]
                .select {
                    filter {
                        eq("status", "OPEN")
                        // Nanti bisa ditambahkan filter() lokasi di sini jika profile lengkap
                    }
                }
                .decodeList<Job>()
        }
    }
    // --- TAMBAHAN BARU ---

    // Mengambil detail satu pekerjaan spesifik
    suspend fun getJobById(jobId: String): Job {
        return withContext(Dispatchers.IO) {
            client.postgrest["jobs"]
                .select {
                    filter { eq("id", jobId) }
                }
                .decodeSingle<Job>()
        }
    }

    // Menyimpan pekerjaan baru ke database
    suspend fun createJob(job: Job) {
        withContext(Dispatchers.IO) {
            client.postgrest["jobs"].insert(job)
        }
    }

    // Menyimpan lamaran kerja baru
    suspend fun applyForJob(application: JobApplication) {
        withContext(Dispatchers.IO) {
            client.postgrest["job_applications"].insert(application)
        }
    }
}