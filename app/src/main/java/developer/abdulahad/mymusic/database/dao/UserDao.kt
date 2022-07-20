package developer.abdulahad.mymusic.database.dao

import androidx.room.*
import developer.abdulahad.mymusic.Models.MusicObj
import developer.abdulahad.mymusic.Models.User

@Dao
interface UserDao {

    @Insert
    fun addUser(user: User)

    @Query("select * from user")
    fun getAllUser():List<User>

    @Delete
    fun deleteUser(user: User)

    @Update
    fun updateUser(user: User)
}