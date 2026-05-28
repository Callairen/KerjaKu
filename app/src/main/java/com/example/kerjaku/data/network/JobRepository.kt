package com.example.kerjaku.data.network

import com.example.kerjaku.data.model.Job
import com.example.kerjaku.data.model.JobApplication
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

    // Mengambil semua pekerjaan OPEN
    suspend fun getOpenJobs(): List<Job> {
        return withContext(Dispatchers.IO) {
            client.postgrest["jobs"]
                .select {
                    filter {
                        eq("status", "OPEN")
                    }
                }
                .decodeList<Job>()
        }
    }

    // Mengambil detail job
    suspend fun getJobById(jobId: String): Job {
        return withContext(Dispatchers.IO) {
            client.postgrest["jobs"]
                .select {
                    filter { eq("id", jobId) }
                }
                .decodeSingle<Job>()
        }
    }

    // Membuat pekerjaan baru
    suspend fun createJob(
        job: Job,
        imageBytes: ByteArray?
    ) {
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

            client.postgrest.rpc(
                "create_job_with_balance_check",
                request
            )
        }
    }

    // Melamar pekerjaan
    suspend fun applyForJob(application: JobApplication) {
        withContext(Dispatchers.IO) {
            client.postgrest["job_applications"]
                .insert(application)
        }
    }

    // Mengambil job milik employer
    suspend fun getMyPostedJobs(employerId: String): List<Job> {
        return withContext(Dispatchers.IO) {
            client.postgrest["jobs"]
                .select {
                    filter { eq("employer_id", employerId) }
                }
                .decodeList<Job>()
        }
    }

    // Mengambil pelamar job
    suspend fun getApplicantsForJob(jobId: String): List<JobApplication> {
        return withContext(Dispatchers.IO) {
            client.postgrest["job_applications"]
                .select(columns = Columns.raw("*, profiles(*)")) {
                    filter { eq("job_id", jobId) }
                }
                .decodeList<JobApplication>()
        }
    }

    // Update status lamaran
    suspend fun updateApplicationStatus(
        applicationId: String,
        newStatus: String
    ) {
        withContext(Dispatchers.IO) {
            client.postgrest["job_applications"]
                .update(
                    {
                        set("status", newStatus)
                    }
                ) {
                    filter { eq("id", applicationId) }
                }
        }
    }

    // Update status job
    suspend fun updateJobStatus(
        jobId: String,
        newStatus: String
    ) {
        withContext(Dispatchers.IO) {
            client.postgrest["jobs"]
                .update(
                    {
                        set("status", newStatus)
                    }
                ) {
                    filter { eq("id", jobId) }
                }
        }
    }

    // Riwayat lamaran pekerja
    suspend fun getMyApplications(workerId: String): List<JobApplication> {
        return withContext(Dispatchers.IO) {
            client.postgrest["job_applications"]
                .select(columns = Columns.raw("*, jobs(*)")) {
                    filter { eq("worker_id", workerId) }
                }
                .decodeList<JobApplication>()
        }
    }

    // Menyelesaikan pekerjaan oleh worker
    suspend fun finishJobApplication(
        applicationId: String,
        notes: String,
        proofUrl: String
    ) {
        withContext(Dispatchers.IO) {
            client.postgrest["job_applications"]
                .update(
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

    // VERIFIKASI & TRANSFER PEMBAYARAN
    suspend fun verifyAndCompleteJob(
        applicationId: String,
        jobId: String
    ) {
        withContext(Dispatchers.IO) {

            val request = VerifyPaymentRequest(
                p_application_id = applicationId,
                p_job_id = jobId
            )

            // Semua proses dilakukan di RPC:
            // - update status
            // - transfer saldo
            // - proteksi exploit
            client.postgrest.rpc(
                "finalize_payment_and_status",
                request
            )
        }
    }

    // Upload gambar pekerjaan
    suspend fun uploadJobImage(
        userId: String,
        imageBytes: ByteArray
    ): String {

        return withContext(Dispatchers.IO) {

            val path = "$userId/job_${System.currentTimeMillis()}.jpg"

            client.storage["job-images"]
                .upload(path, imageBytes) {
                    upsert = true
                }

            client.storage["job-images"]
                .publicUrl(path)
        }
    }
}