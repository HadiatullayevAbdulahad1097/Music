package developer.abdulahad.mymusic

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import developer.abdulahad.mymusic.Adapters.RvAdapter
import developer.abdulahad.mymusic.Models.MusicObj
import developer.abdulahad.mymusic.database.AppDataBase
import developer.abdulahad.mymusic.Models.User
import developer.abdulahad.mymusic.databinding.FragmentArtistBinding

class ArtistFragment : Fragment() {
    lateinit var binding: FragmentArtistBinding
    lateinit var list: ArrayList<User>
    lateinit var appDataBase: AppDataBase
    lateinit var rvAdapter: RvAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentArtistBinding.inflate(layoutInflater)

        appDataBase = AppDataBase.getInstance(binding.root.context)

        list = ArrayList()
        for (i in appDataBase.biletDuo().getAllUser()) {
            if (MusicObj.list2[MusicObj.position].artist == i.artist) {
                list.add(i)
            }
        }

        rvAdapter = RvAdapter(binding.root.context,list,object : RvAdapter.ClickInterFace{
            override fun clickItem(user: User, position: Int) {
                MusicObj.user = user
                MusicObj.position = position
                MusicObj.list = list
                MusicObj.mediaPlayer.stop()
                findNavController().navigate(R.id.musicFragment)
            }
        })

        binding.rv.adapter = rvAdapter

        return binding.root
    }
}