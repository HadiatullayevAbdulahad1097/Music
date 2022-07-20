package developer.abdulahad.mymusic.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import developer.abdulahad.mymusic.databinding.ItemRvMusicBinding

class RvAdapterCard(var list: List<Int>,var clickItem: ClickItem) : RecyclerView.Adapter<RvAdapterCard.Vh>() {
    inner class Vh(var itemRv: ItemRvMusicBinding) : RecyclerView.ViewHolder(itemRv.root){
        fun onBind(position: Int) {
            itemRv.image.setImageResource(list[position])
            itemRv.card.setOnClickListener {
                clickItem.itemClick(position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
        return Vh(ItemRvMusicBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: Vh, position: Int) {
        holder.onBind(position)
    }

    override fun getItemCount(): Int = list.size
    interface ClickItem{
        fun itemClick(position: Int)
    }
}