package developer.abdulahad.mymusic.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import developer.abdulahad.mymusic.database.dao.UserDao
import developer.abdulahad.mymusic.Models.User

@Database(entities = [User::class], version = 1)
abstract  class AppDataBase : RoomDatabase() {
    abstract fun biletDuo() : UserDao
    companion object{
        var appDataBase: AppDataBase? = null

        @Synchronized
        fun getInstance(context: Context): AppDataBase {
            if (appDataBase ==null){
                appDataBase = Room.databaseBuilder(context, AppDataBase::class.java,"bilet_db")
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build()
            }
            return appDataBase!!
        }
    }
}