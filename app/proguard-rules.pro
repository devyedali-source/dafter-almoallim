# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.

# 1. Attributes preservation
-keepattributes Signature, *Annotation*, InnerClasses, EnclosingMethod
-keepattributes SourceFile, LineNumberTable

# 2. Kotlin Metadata, Enum, Parcelable, Serializable
-keep class kotlin.Metadata { *; }

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keepclassmembers class * implements android.os.Parcelable {
    static ** CREATOR;
}

-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# 3. Room Database & DAOs & Entities
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao interface * { *; }
-keep @androidx.room.Database class * { *; }
-keep class * extends androidx.room.RoomDatabase { *; }
-keep class com.example.data.db.** { *; }
-keep class com.example.data.dao.** { *; }
-keep class com.example.data.models.** { *; }
-keepclassmembers class com.example.data.models.** { *; }

# 4. Firestore & Firebase
-keepclassmembers class * {
    @com.google.firebase.firestore.PropertyName <fields>;
    @com.google.firebase.firestore.PropertyName <methods>;
}
-keepclassmembers class com.example.data.models.** {
    <init>();
    <fields>;
    <methods>;
}
-keep class com.google.firebase.** { *; }

# 5. JSON Deserialization & Moshi Data Classes (Assets & Backup)
-keep @com.squareup.moshi.JsonClass class * { *; }
-keep class com.squareup.moshi.** { *; }
-keep interface com.squareup.moshi.** { *; }
-keep class *JsonAdapter { *; }
-keep class **.*JsonAdapter { *; }

# Canonical Planning & Preparation models (assets)
-keep class com.example.data.planning.canonical.** { *; }
-keepclassmembers class com.example.data.planning.canonical.** { *; }

# Annual planning models
-keep class com.example.data.planning.** { *; }
-keepclassmembers class com.example.data.planning.** { *; }

# Backup models
-keep class com.example.data.backup.** { *; }
-keepclassmembers class com.example.data.backup.** { *; }

# School legislation model (assets JSON)
-keep class com.example.ui.screens.LegislationArticle { *; }
-keepclassmembers class com.example.ui.screens.LegislationArticle { *; }

# 6. Google Sign-In, Google Drive API & WorkManager
-keep class com.google.android.gms.auth.api.signin.** { *; }
-keep class com.google.android.gms.common.** { *; }
-keep class com.example.data.backup.GoogleDriveBackupManager { *; }
-keep class com.example.data.backup.DriveBackupWorker { *; }
-keep class * extends androidx.work.Worker { *; }
-keep class * extends androidx.work.ListenableWorker { *; }
-keep class androidx.work.** { *; }

# Course / Concours / Exams content models (assets JSON)
-keep class com.example.data.course.** { *; }
-keepclassmembers class com.example.data.course.** { *; }
-keep class com.example.data.concours.** { *; }
-keepclassmembers class com.example.data.concours.** { *; }
-keep class com.example.data.exams.** { *; }
-keepclassmembers class com.example.data.exams.** { *; }

# قراءة PDF (pdfbox-android)
-keep class com.tom_roush.pdfbox.** { *; }
-keep class com.tom_roush.fontbox.** { *; }
-dontwarn com.tom_roush.pdfbox.**
-dontwarn com.gemalto.jp2.**
-dontwarn org.bouncycastle.**
