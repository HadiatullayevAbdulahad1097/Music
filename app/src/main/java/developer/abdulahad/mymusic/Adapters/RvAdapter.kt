package developer.abdulahad.mymusic.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import developer.abdulahad.mymusic.Models.MusicObj
import developer.abdulahad.mymusic.Models.User
import developer.abdulahad.mymusic.databinding.ItemRvVerticalBinding


class RvAdapter(var context: Context, var list: List<User>, var clickInterFace: ClickInterFace) : RecyclerView.Adapter<RvAdapter.Vh>() {
    inner class Vh(var itemRv: ItemRvVerticalBinding) : RecyclerView.ViewHolder(itemRv.root){
        fun onBind(user : User, position: Int) {
            itemRv.nameMusic.text = list[position].title
            itemRv.artistMusic.text = list[position].artist
            itemRv.card.setOnClickListener {
                clickInterFace.clickItem(user,position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
        return Vh(ItemRvVerticalBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: Vh, position: Int) {
        holder.onBind(list[position] , position)
    }

    override fun getItemCount(): Int = list.size

    interface ClickInterFace{
        fun clickItem(user: User, position: Int)
    }
}