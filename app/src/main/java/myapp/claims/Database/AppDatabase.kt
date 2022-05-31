package myapp.claims.Database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [ClaimType::class, ClaimDetail::class, ClaimField::class, ClaimFieldOption::class,Expense::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun claimTypeDao(): ClaimTypeDao
    abstract fun claimDetailDao(): ClaimDetailDao
    abstract fun claimFieldDao(): ClaimFieldDao
    abstract fun claimFieldOptionDao(): ClaimFieldOptionDao
    abstract fun expenseDao():ExpenseDao

    companion object {
        private var instance: AppDatabase? = null

        @Synchronized
        fun getInstance(context: Context): AppDatabase {
            if (instance == null) {
                instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "claims_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
            }
            return instance!!
        }
    }
}