package developer.abdulahad.mymusic

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import developer.abdulahad.mymusic.Models.MusicObj
import developer.abdulahad.mymusic.database.AppDataBase
import developer.abdulahad.mymusic.databinding.FragmentMusicBinding
import java.io.IOException

class MusicFragment : Fragment() {
    lateinit var binding: FragmentMusicBinding
    lateinit var mediaPlayer: MediaPlayer
    lateinit var appDataBase: AppDataBase
    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMusicBinding.inflate(layoutInflater)

        binding.nameMusic.text = MusicObj.user.title!!.trim()
        binding.artistMusic.text = MusicObj.user.artist!!.trim()

        MusicObj.nameMusic = binding.nameMusic.text.toString()
        MusicObj.artistMusic = binding.artistMusic.text.toString()

        appDataBase = AppDataBase.getInstance(binding.root.context)

        binding.one.visibility = View.INVISIBLE
        mediaPlayer = MediaPlayer()
        try {
            mediaPlayer.setDataSource(MusicObj.user.music)
            mediaPlayer.prepare()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        mediaPlayer.isLooping = true
        mediaPlayer.seekTo(0)
        mediaPlayer.setVolume(2f, 2f)

        mediaPlayer.start()

        var duration = milliSecondsToString(mediaPlayer.duration)

        binding.tvDuration.text = duration

        binding.reverse.setOnClickListener {
            if (binding.one.visibility == View.INVISIBLE) {
                binding.one.visibility = View.VISIBLE
            } else {
                binding.one.visibility = View.INVISIBLE
            }
        }

        binding.speed.setOnClickListener {
            when (binding.speed.text) {
                "0.5X" -> {
                    mediaPlayer.playbackParams = mediaPlayer.playbackParams.setSpeed(1.0f)
                    binding.speed.text = "1.0X"
                }
                "1.0X" -> {
                    mediaPlayer.playbackParams = mediaPlayer.playbackParams.setSpeed(1.5f)
                    binding.speed.text = "1.5X"
                }
                "1.5X" -> {
                    mediaPlayer.playbackParams = mediaPlayer.playbackParams.setSpeed(2.0f)
                    binding.speed.text = "2.0X"
                }
                else -> {
                    mediaPlayer.playbackParams = mediaPlayer.playbackParams.setSpeed(0.5f)
                    binding.speed.text = "0.5X"
                }
            }
        }

        binding.temp.setOnClickListener {
            if (MusicObj.temp == 0){
                mediaPlayer.playbackParams = mediaPlayer.playbackParams.setPitch(1.0f)
                binding.tempText.text = "1.0X"
                MusicObj.temp = 1
            }else if (MusicObj.temp == 1){
                mediaPlayer.playbackParams = mediaPlayer.playbackParams.setPitch(1.5f)
                binding.tempText.text = "1.5X"
                MusicObj.temp = 2
            }else if (MusicObj.temp == 2){
                mediaPlayer.playbackParams = mediaPlayer.playbackParams.setPitch(2.0f)
                binding.tempText.text = "2.0X"
                MusicObj.temp = 3
            }else{
                mediaPlayer.playbackParams = mediaPlayer.playbackParams.setPitch(0.5f)
                binding.tempText.text = "0.5X"
                MusicObj.temp = 0
            }
        }

        if (MusicObj.list[MusicObj.position].like!!){
            binding.like.setImageResource(R.drawable.selectable)
        }else{
            binding.like.setImageResource(R.drawable.unselectable)
        }

        binding.like.setOnClickListener {
            if (MusicObj.list[MusicObj.position].like!!){
                binding.like.setImageResource(R.drawable.unselectable)
                MusicObj.list[MusicObj.position].like = false
            }else{
                binding.like.setImageResource(R.drawable.selectable)
                MusicObj.list[MusicObj.position].like = true
            }
            appDataBase.biletDuo().updateUser(MusicObj.list[MusicObj.position])
        }

        binding.seekbar.max = mediaPlayer.duration
        binding.seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress)
                    seekBar!!.progress = progress
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }
        })
        class SimpleThread : Thread() {
            override fun run() {
                while (mediaPlayer != null) {
                    try {
                        if (mediaPlayer.isPlaying) {
                            try {
                                requireActivity().runOnUiThread {
                                    val current = mediaPlayer.currentPosition
                                    val elapsedTime = milliSecondsToString(current)
                                    binding.tvTime.text = elapsedTime
                                    binding.seekbar.progress = current
                                    if (binding.one.visibility == View.INVISIBLE) {
                                        if (binding.tvTime.text == milliSecondsToString(mediaPlayer.duration)) {
                                            if (MusicObj.position < MusicObj.list.size-1) {
                                                mediaPlayer.stop()
                                                binding.pausePlay.setImageResource(R.drawable.pause)
                                                mediaPlayer.release()
                                                MusicObj.position = ++MusicObj.position
                                            } else {
                                                binding.pausePlay.setImageResource(R.drawable.pause)
                                            }
                                            var uri =
                                                Uri.parse(MusicObj.list[MusicObj.position].music.toString())
                                            mediaPlayer =
                                                MediaPlayer.create(binding.root.context, uri)
                                            val durationUnit =
                                                milliSecondsToString(mediaPlayer.duration)
                                            binding.seekbar.max = mediaPlayer.duration
                                            binding.seekbar.setOnSeekBarChangeListener(object :
                                                SeekBar.OnSeekBarChangeListener {
                                                override fun onProgressChanged(
                                                    seekBar: SeekBar?,
                                                    progress: Int,
                                                    fromUser: Boolean
                                                ) {
                                                    if (fromUser) mediaPlayer.seekTo(progress)
                                                    seekBar!!.progress = progress
                                                }

                                                override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                                                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
                                            })
                                            binding.tvDuration.text = durationUnit
                                            binding.nameMusic.text =
                                                MusicObj.list[MusicObj.position].title
                                            binding.speed.text = "1.0X"
                                            MusicObj.temp = 0
                                            binding.artistMusic.text =
                                                MusicObj.list[MusicObj.position].artist
                                            binding.pausePlay.setImageResource(R.drawable.play)
                                            mediaPlayer.start()
                                            startAnimation(binding.imageMusic, 360f)
                                        }else if (binding.tvTime.text == milliSecondsToString(mediaPlayer.duration-1000)){
                                            if (MusicObj.position < MusicObj.list.size-1) {
                                                mediaPlayer.stop()
                                                binding.pausePlay.setImageResource(R.drawable.pause)
                                                mediaPlayer.release()
                                                MusicObj.position = ++MusicObj.position
                                            } else {
                                                binding.pausePlay.setImageResource(R.drawable.pause)
                                            }
                                            var uri =
                                                Uri.parse(MusicObj.list[MusicObj.position].music.toString())
                                            mediaPlayer =
                                                MediaPlayer.create(binding.root.context, uri)
                                            val durationUnit =
                                                milliSecondsToString(mediaPlayer.duration)
                                            binding.seekbar.max = mediaPlayer.duration
                                            binding.seekbar.setOnSeekBarChangeListener(object :
                                                SeekBar.OnSeekBarChangeListener {
                                                override fun onProgressChanged(
                                                    seekBar: SeekBar?,
                                                    progress: Int,
                                                    fromUser: Boolean
                                                ) {
                                                    if (fromUser) mediaPlayer.seekTo(progress)
                                                    seekBar!!.progress = progress
                                                }

                                                override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                                                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
                                            })
                                            binding.tvDuration.text = durationUnit
                                            binding.nameMusic.text =
                                                MusicObj.list[MusicObj.position].title
                                            binding.speed.text = "1.0X"
                                            MusicObj.temp = 0
                                            binding.artistMusic.text =
                                                MusicObj.list[MusicObj.position].artist
                                            binding.pausePlay.setImageResource(R.drawable.play)
                                            mediaPlayer.start()
                                            startAnimation(binding.imageMusic, 360f)
                                        }
                                    } else {
                                        if (binding.tvTime.text == milliSecondsToString(mediaPlayer.duration)) {
                                            mediaPlayer.stop()
                                            mediaPlayer.release()
                                            var uri =
                                                Uri.parse(MusicObj.list[MusicObj.position].music.toString())
                                            mediaPlayer =
                                                MediaPlayer.create(binding.root.context, uri)
                                            binding.tvDuration.text =
                                                milliSecondsToString(mediaPlayer.duration)
                                            binding.nameMusic.text =
                                                MusicObj.list[MusicObj.position].title
                                            binding.speed.text = "1.0X"
                                            MusicObj.temp = 0
                                            binding.artistMusic.text =
                                                MusicObj.list[MusicObj.position].artist
                                            binding.pausePlay.setImageResource(R.drawable.play)
                                            mediaPlayer.start()
                                        }
                                    }
                                }
                                sleep(500)
                            } catch (e: IOException) {
                            }
                        }
                    } catch (e: IllegalStateException) {
                    }
                }
            }
        }
        SimpleThread().start()

        binding.pausePlay.setOnClickListener {
            if (mediaPlayer.isPlaying) {
                mediaPlayer.pause()
                binding.pausePlay.setImageResource(R.drawable.pause)
            } else {
                mediaPlayer.start()
                binding.pausePlay.setImageResource(R.drawable.play)
            }
        }

        binding.seekSound.progress = 50

        binding.seekSound.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                var volume = progress / 100f
                mediaPlayer.setVolume(volume, volume)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }
        })

        binding.next.setOnClickListener {
            mediaPlayer.stop()
            mediaPlayer.release()
            MusicObj.position = ((MusicObj.position + 1) % MusicObj.list.size)
            var uri = Uri.parse(MusicObj.list[MusicObj.position].music.toString())
            mediaPlayer = MediaPlayer.create(binding.root.context, uri)
            var durationUnit = milliSecondsToString(mediaPlayer.duration)
            MusicObj.mediaPlayer = mediaPlayer
            MusicObj.nameMusic = MusicObj.list[MusicObj.position].title.toString()
            MusicObj.artistMusic = MusicObj.list[MusicObj.position].artist.toString()
            binding.seekbar.max = mediaPlayer.duration
            binding.seekbar.setOnSeekBarChangeListener(object :
                SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    if (fromUser) {
                        mediaPlayer.seekTo(progress)
                        seekBar!!.progress = progress
                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {

                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {

                }
            })
            binding.tvDuration.text = durationUnit
            binding.speed.text = "1.0X"
            MusicObj.temp = 0
            binding.nameMusic.text = MusicObj.list[MusicObj.position].title
            binding.artistMusic.text = MusicObj.list[MusicObj.position].artist
            binding.pausePlay.setImageResource(R.drawable.play)
            mediaPlayer.start()
            startAnimation(binding.imageMusic, 360f)
        }

        binding.nextSec.setOnClickListener {
            if ((mediaPlayer.duration.durationInt() - mediaPlayer.currentPosition) > 15000) {
                binding.seekbar.progress += 15000
                mediaPlayer.seekTo(binding.seekbar.progress)
                binding.tvTime.text = milliSecondsToString(mediaPlayer.currentPosition)
            }
        }
        binding.previousSec.setOnClickListener {
            if (mediaPlayer.currentPosition > 15000) {
                binding.seekbar.progress -= 15000
                mediaPlayer.seekTo(binding.seekbar.progress)
                binding.tvTime.text = milliSecondsToString(mediaPlayer.currentPosition)
            }
        }
        binding.previous.setOnClickListener {
            mediaPlayer.stop()
            mediaPlayer.release()
            if (MusicObj.position > 0) {
                MusicObj.position = --MusicObj.position
            }
            var uri = Uri.parse(MusicObj.list[MusicObj.position].music.toString())
            mediaPlayer = MediaPlayer.create(binding.root.context, uri)
            binding.seekbar.max = mediaPlayer.duration
            MusicObj.mediaPlayer = mediaPlayer
            MusicObj.nameMusic = MusicObj.list[MusicObj.position].title.toString()
            MusicObj.artistMusic = MusicObj.list[MusicObj.position].artist.toString()
            binding.seekbar.setOnSeekBarChangeListener(object :
                SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    if (fromUser) {
                        mediaPlayer.seekTo(progress)
                        seekBar!!.progress = progress
                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {

                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {

                }
            })
            var durationUnit = milliSecondsToString(mediaPlayer.duration)
            binding.tvDuration.text = durationUnit
            binding.speed.text = "1.0X"
            MusicObj.temp = 0
            binding.nameMusic.text = MusicObj.list[MusicObj.position].title
            binding.artistMusic.text = MusicObj.list[MusicObj.position].artist
            binding.pausePlay.setImageResource(R.drawable.play)
            mediaPlayer.start()
            startAnimation(binding.imageMusic, -360f)

        }
        MusicObj.mediaPlayer = mediaPlayer
        return binding.root
    }

    private fun Int.durationInt(): Long =
        ((((this / 1000 / 60) * 60) + (this / 1000 % 60)) * 1000).toLong()

    fun milliSecondsToString(time: Int): String {
        var elapsedTime = ""
        var minutes = time / 1000 / 60
        var second = time / 1000 % 60

        elapsedTime = "$minutes : "
        if (second < 10) {
            elapsedTime += "0"
        }
        elapsedTime += second

        return elapsedTime
    }

    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            if (mediaPlayer.isPlaying) {
                mediaPlayer.stop()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    @SuppressLint("Recycle")
    fun startAnimation(view: View, degree: Float) {
        var objectAnimator = ObjectAnimator.ofFloat(binding.imageMusic, "rotation", 0f, degree)
        objectAnimator.duration = 1000
        var setAnimator = AnimatorSet()
        setAnimator.playTogether(objectAnimator)
        setAnimator.start()
    }
}
