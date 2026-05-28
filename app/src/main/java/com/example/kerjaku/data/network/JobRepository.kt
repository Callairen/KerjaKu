package com.example.kerjaku.data.network

import com.example.kerjaku.data.model.Job
import com.example.kerjaku.data.model.JobApplication
import com.example.kerjaku.data.network.SupabaseApi
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.rpc
import io.github.jan.supabase.storage.storage
import kotlinx.serialization.Serializable

@Serializable
data class CreateJobRequest(
    val p_employer_id: String,
    val p_category_id: Long?,
    val p_title: String,
    val p_description: String?,
    val p_wage: Double,
    val p_duration_days: Int,
    val p_city: String,
    val p_district: String?,
    val p_image_url: String?,
    val p_village: String?
)

@Serializable
data class VerifyPaymentRequest(
    val p_application_id: String,
    val p_job_id: String
)
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
    suspend fun createJob(
        job: Job,
        imageBytes: ByteArray?
    ){
        withContext(Dispatchers.IO) {

            val imageUrl = if (imageBytes != null) {
                uploadJobImage(
                    userId = job.employer_id,
                    imageBytes = imageBytes
                )
            } else {
                null
            }
            val request = CreateJobRequest(
                p_employer_id = job.employer_id,
                p_category_id = job.category_id,
                p_title = job.title,
                p_description = job.description,
                p_wage = job.wage,
                p_duration_days = job.duration_days,
                p_city = job.city,
                p_district = job.district,
                p_village = job.village,
                p_image_url = imageUrl
            )
            // Memanggil RPC yang mengecek saldo
            client.postgrest.rpc("create_job_with_balance_check", request)
        }
    }

    // Menyimpan lamaran kerja baru
    suspend fun applyForJob(application: JobApplication) {
        withContext(Dispatchers.IO) {
            client.postgrest["job_applications"].insert(application)
        }
    }
    suspend fun getMyPostedJobs(employerId: String): List<Job> {
        return withContext(Dispatchers.IO) {
            client.postgrest["jobs"]
                .select {
                    filter { eq("employer_id", employerId) }
                }
                .decodeList<Job>()
        }
    }
    suspend fun getApplicantsForJob(jobId: String): List<JobApplication> {
        return withContext(Dispatchers.IO) {
            client.postgrest["job_applications"]
                // Sintaks ini akan mengambil data pelamar sekaligus profilnya (JOIN)
                .select(columns = Columns.raw("*, profiles(*)")) {
                    filter { eq("job_id", jobId) }
                }
                .decodeList<JobApplication>()
        }
    }

    // Mengubah status lamaran (misal: APPLIED -> ACCEPTED)
    suspend fun updateApplicationStatus(applicationId: String, newStatus: String) {
        withContext(Dispatchers.IO) {
            client.postgrest["job_applications"].update(
                { set("status", newStatus) }
            ) {
                filter { eq("id", applicationId) }
            }
        }
    }

    // Mengubah status pekerjaan (misal: OPEN -> ON_GOING)
    suspend fun updateJobStatus(jobId: String, newStatus: String) {
        withContext(Dispatchers.IO) {
            client.postgrest["jobs"].update(
                { set("status", newStatus) }
            ) {
                filter { eq("id", jobId) }
            }
        }
    }
    // Menarik riwayat lamaran dan pekerjaan yang sedang berjalan milik pekerja
    suspend fun getMyApplications(workerId: String): List<JobApplication> {
        return withContext(Dispatchers.IO) {
            client.postgrest["job_applications"]
                // Menggunakan JOIN untuk mengambil detail pekerjaan sekaligus
                .select(columns = Columns.raw("*, jobs(*)")) {
                    filter { eq("worker_id", workerId) }
                }
                .decodeList<JobApplication>()
        }
    }

    // Memperbarui status lamaran menjadi FINISHED dengan menyertakan bukti
    suspend fun finishJobApplication(applicationId: String, notes: String, proofUrl: String) {
        withContext(Dispatchers.IO) {
            client.postgrest["job_applications"].update(
                {
                    set("status", "FINISHED")
                    set("completion_notes", notes)
                    set("completion_proof_url", proofUrl)
                }
            ) {
                filter { eq("id", applicationId) }
            }
        }
    }
    suspend fun verifyAndCompleteJob(applicationId: String, jobId: String) {
        withContext(Dispatchers.IO) {
            val request = VerifyPaymentRequest(applicationId, jobId)
            // Memanggil RPC untuk transfer saldo
            client.postgrest.rpc("finalize_payment_and_status", request)
            client.postgrest["job_applications"].update(
                { set("status", "APPROVED_AND_PAID") }
            ) {
                filter { eq("id", applicationId) }
            }

            client.postgrest["jobs"].update(
                { set("status", "COMPLETED") }
            ) {
                filter { eq("id", jobId) }
            }
        }
    }
    suspend fun uploadJobImage(userId: String, imageBytes: ByteArray): String {
        return withContext(Dispatchers.IO) {
            val path = "$userId/job_${System.currentTimeMillis()}.jpg"
            client.storage["job-images"].upload(path, imageBytes) {
                upsert = true
            }
            client.storage["job-images"].publicUrl(path)
        }
    }
}