package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.dao.TeacherDao
import com.example.data.models.ClassSection
import com.example.data.models.Grade
import com.example.data.models.Student
import com.example.data.models.Subject
import com.example.data.models.ClassSubjectCustomization
import com.example.data.models.TeacherExchangePost
import com.example.data.models.StudentManualTermAverage
import com.example.data.models.LocalNotification
import com.example.data.models.AttendanceRecord
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import java.util.concurrent.ConcurrentHashMap

@Database(
    entities = [
        ClassSection::class,
        Student::class,
        Subject::class,
        Grade::class,
        ClassSubjectCustomization::class,
        TeacherExchangePost::class,
        StudentManualTermAverage::class,
        LocalNotification::class,
        AttendanceRecord::class
    ],
    version = 19,
    exportSchema = false
)
abstract class TeacherDatabase : RoomDatabase() {

    abstract fun teacherDao(): TeacherDao

    companion object {
        /** إضافة جدول الغياب دون المساس بأي بيانات موجودة */
        private val MIGRATION_18_19 = object : Migration(18, 19) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `attendance_records` (`studentId` INTEGER NOT NULL, `date` TEXT NOT NULL, `classId` INTEGER NOT NULL, `morningAbsent` INTEGER NOT NULL, `afternoonAbsent` INTEGER NOT NULL, PRIMARY KEY(`studentId`, `date`))"
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_attendance_records_classId_date` ON `attendance_records` (`classId`, `date`)")
            }
        }

        private val INSTANCES = ConcurrentHashMap<String, TeacherDatabase>()

        fun getDatabase(context: Context, userId: String? = null): TeacherDatabase {
            val dbName = if (!userId.isNullOrEmpty()) {
                "teacher_database_${userId.lowercase().replace(Regex("[^a-z0-9_]"), "_")}"
            } else {
                "teacher_database_guest"
            }

            return INSTANCES.getOrPut(dbName) {
                synchronized(this) {
                    INSTANCES[dbName] ?: Room.databaseBuilder(
                        context.applicationContext,
                        TeacherDatabase::class.java,
                        dbName
                    )
                    .addMigrations(MIGRATION_18_19)
                    .fallbackToDestructiveMigration()
                    .build().also { INSTANCES[dbName] = it }
                }
            }
        }
    }
}
