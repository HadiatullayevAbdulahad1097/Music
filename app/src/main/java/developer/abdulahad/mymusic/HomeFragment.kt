package developer.abdulahad.mymusic

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import developer.abdulahad.mymusic.Adapters.RvAdapter
import developer.abdulahad.mymusic.Adapters.RvAdapterCard
import developer.abdulahad.mymusic.Models.MusicObj
import developer.abdulahad.mymusic.Models.MyData
import developer.abdulahad.mymusic.database.AppDataBase
import developer.abdulahad.mymusic.Models.User
import developer.abdulahad.mymusic.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {
    lateinit var binding: FragmentHomeBinding
    lateinit var rvAdapter: RvAdapter
    lateinit var rvAdapterCard: RvAdapterCard
    lateinit var list2: ArrayList<Int>
    lateinit var appDataBase: AppDataBase
    lateinit var list: ArrayList<User>
    private var REQUEST_PERMISSSION: Int = 99

    @SuppressLint("ObsoleteSdkInt")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater)

        MyData.init(binding.root.context)

        appDataBase = AppDataBase.getInstance(binding.root.context)

        list = ArrayList()
        list2 = ArrayList()
        list2.add(R.drawable.heart)
        list2.add(R.drawable.microphone)
        list.addAll(appDataBase.biletDuo().getAllUser())

        binding.imagePausePlay.setImageResource(R.drawable.pause)

        rvAdapter = RvAdapter(binding.root.context, list, object : RvAdapter.ClickInterFace {
            override fun clickItem(user: User, position: Int) {
                MusicObj.user = user
                MusicObj.position = position
                MusicObj.list = list
                MusicObj.mediaPlayer.stop()
                findNavController().navigate(R.id.musicFragment)
            }
        })

        if (ActivityCompat.checkSelfPermission(
                binding.root.context,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                container!!.context as Activity,
                arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                REQUEST_PERMISSSION
            )
        } else {
            getSongs()
        }

        binding.nameMusic.text = MusicObj.nameMusic
        binding.artistMusic.text = MusicObj.artistMusic

        binding.next.setOnClickListener {
            if (MusicObj.mediaPlayer.isPlaying) {
                MusicObj.mediaPlayer.stop()
                MusicObj.position = ((MusicObj.position + 1) % MusicObj.list.size)
                var uri = Uri.parse(MusicObj.list[MusicObj.position].music.toString())
                MusicObj.mediaPlayer = MediaPlayer.create(binding.root.context, uri)
                startAnimation(binding.imageMusic, 360f)
                binding.nameMusic.text = MusicObj.list[MusicObj.position].title
                binding.artistMusic.text = MusicObj.list[MusicObj.position].artist
                MusicObj.mediaPlayer.start()
            } else {
                Toast.makeText(binding.root.context, "IsPause", Toast.LENGTH_SHORT).show()
            }
        }

        try {
            if (MusicObj.mediaPlayer.isPlaying) {
                binding.imagePausePlay.setImageResource(R.drawable.play)
            } else {
                binding.imagePausePlay.setImageResource(R.drawable.pause)
            }

            binding.imagePausePlay.setOnClickListener {
                if (MusicObj.mediaPlayer.isPlaying) {
                    MusicObj.mediaPlayer.pause()
                    binding.imagePausePlay.setImageResource(R.drawable.pause)
                } else {
                    if (binding.nameMusic.text != "") {
                        MusicObj.mediaPlayer.start()
                        binding.imagePausePlay.setImageResource(R.drawable.play)
                    }else{
                        Toast.makeText(binding.root.context, "IsPause", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } catch (e: IllegalStateException) {
        }
        rvAdapterCard = RvAdapterCard(list2, object : RvAdapterCard.ClickItem {
            override fun itemClick(position: Int) {
                if (list2[position] == R.drawable.heart) {
                    findNavController().navigate(R.id.likedFragment)
                } else {
                    MusicObj.position = position
                    MusicObj.list2 = list
                    findNavController().navigate(R.id.artistFragment)
                }
            }
        })

        binding.rvMusic2.adapter = rvAdapter
        binding.rvMusic.adapter = rvAdapterCard

        return binding.root
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_PERMISSSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getSongs()
            }
        }
    }

    @SuppressLint("Recycle", "NotifyDataSetChanged", "InlinedApi")
    private fun getSongs() {
        val contentResolver: ContentResolver = requireActivity().contentResolver
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val cursor = contentResolver.query(uri, null, null, null, null)
        if (cursor != null && cursor.moveToFirst()) {
            val indexTitle = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
            val indexData = cursor.getColumnIndex(MediaStore.Audio.Media.DATA)
            val indexImage = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ARTIST)
            val indexArtist = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
            if (MyData.name == "") {
                do {
                    val title = cursor.getString(indexTitle)
                    val music = cursor.getString(indexData)
                    val image = cursor.getString(indexImage)
                    val artist = cursor.getString(indexArtist)
                    val like = false
                    var user = User(title, music, image, artist, like)
                    list.add(user)
                    appDataBase.biletDuo().addUser(user)
                    MyData.name = "1"
                    var index = list.indexOf(user)
                    rvAdapter.notifyItemInserted(index)
                } while (cursor.moveToNext())
            }
            for (i in appDataBase.biletDuo().getAllUser()){
                appDataBase.biletDuo().updateUser(i)
            }
        }
    }

    fun startAnimation(view: View, degree: Float) {
        var objectAnimator = ObjectAnimator.ofFloat(view, "rotation", 0f, degree)
        objectAnimator.duration = 1000
        var setAnimator = AnimatorSet()
        setAnimator.playTogether(objectAnimator)
        setAnimator.start()
    }
}