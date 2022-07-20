package developer.abdulahad.mymusic

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import developer.abdulahad.mymusic.Adapters.RvAdapter
import developer.abdulahad.mymusic.Models.MusicObj
import developer.abdulahad.mymusic.database.AppDataBase
import developer.abdulahad.mymusic.Models.User
import developer.abdulahad.mymusic.databinding.FragmentLikedBinding

class LikedFragment : Fragment() {
    lateinit var binding: FragmentLikedBinding
    lateinit var likedAdapter: RvAdapter
    lateinit var appDataBase: AppDataBase
    lateinit var list: ArrayList<User>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLikedBinding.inflate(layoutInflater)

        list = ArrayList()

        appDataBase = AppDataBase.getInstance(binding.root.context)

        for (i in appDataBase.biletDuo().getAllUser()){
            if (i.like!!){
                list.add(i)
            }
        }

        likedAdapter = RvAdapter(binding.root.context,list,object : RvAdapter.ClickInterFace{
            override fun clickItem(user: User, position: Int) {
                MusicObj.user = user
                MusicObj.position = position
                MusicObj.list = list
                MusicObj.mediaPlayer.stop()
                findNavController().navigate(R.id.musicFragment)
            }})

        binding.rvLiked.adapter = likedAdapter

        return binding.root
    }
}