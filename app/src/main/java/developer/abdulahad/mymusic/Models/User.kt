package developer.abdulahad.mymusic.Models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class User {
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
    var title: String? = null
    var music: String? = null
    var image: String? = null
    var artist: String? = null
    var like: Boolean? = null

    constructor()
    constructor(
        id: Int?,
        title: String?,
        music: String?,
        image: String?,
        artist: String?,
        like: Boolean?,
    ) {
        this.id = id
        this.title = title
        this.music = music
        this.image = image
        this.artist = artist
        this.like = like
    }

    constructor(
        title: String?,
        music: String?,
        image: String?,
        artist: String?,
        like: Boolean?,
    ) {
        this.title = title
        this.music = music
        this.image = image
        this.artist = artist
        this.like = like
    }

}