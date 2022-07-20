package developer.abdulahad.mymusic.Models

import android.content.Context
import android.content.SharedPreferences

object MyData {
    private const val NAME = "my_cash_file"
    private const val MODE = Context.MODE_PRIVATE

    private lateinit var sharedPreferences: SharedPreferences

    fun init(context: Context){
        sharedPreferences = context.getSharedPreferences(NAME, MODE)
    }

    private inline fun SharedPreferences.edit(operation : (SharedPreferences.Editor)-> Unit){
        val editor = edit()
        operation(editor)
        editor.apply()
    }

    var name : String?
    get() = sharedPreferences.getString("name","")
    set(value) = sharedPreferences.edit {
        it.putString("name",value)
    }
}