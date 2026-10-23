package com.example.data.backup

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.data.db.TeacherDatabase
import com.example.data.repository.TeacherRepository
import com.google.firebase.auth.FirebaseAuth

class DriveBackupWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val user = FirebaseAuth.getInstance().currentUser ?: return Result.success()

            val prefs = applicationContext.getSharedPreferences("teacher_settings_prefs", Context.MODE_PRIVATE)
            val lastBackupTime = prefs.getLong("last_google_drive_backup_time", 0L)
            val now = System.currentTimeMillis()
            if (now - lastBackupTime < 3600000L) {
                return Result.success()
            }

            val database = TeacherDatabase.getDatabase(applicationContext, user.uid)
            val repository = TeacherRepository(database.teacherDao())
            val backupManager = GoogleDriveBackupManager(applicationContext, repository)

            val backupResult = backupManager.createAndUploadBackup(
                currentUserId = user.uid,
                currentUserEmail = user.email,
                onProgress = { _, _ -> }
            )

            when (backupResult) {
                is BackupResult.Success -> {
                    if (backupResult.uploaded) prefs.edit().remove("drive_fail_since").remove("drive_fail_msg").apply()
                    Result.success()
                }
                is BackupResult.Error -> {
                    if (!backupResult.errorMessage.contains("لا يوجد اتصال")) {
                        if (prefs.getLong("drive_fail_since", 0L) == 0L) prefs.edit().putLong("drive_fail_since", now).apply()
                        prefs.edit().putString("drive_fail_msg", backupResult.errorMessage.take(200)).apply()
                    }
                    Result.retry()
                }
            }
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
