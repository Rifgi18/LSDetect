package com.tugasakhirrifgi.lsdetect.ui.newHistory

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tugasakhirrifgi.lsdetect.databinding.ItemDataBinding
import com.tugasakhirrifgi.lsdetect.ui.newHistory.detail.DetailHistoryActivity

class NewHistoryAdapter (private val historyList: MutableList<NewHistoryDetection>) :
    RecyclerView.Adapter<NewHistoryAdapter.NewHistoryViewHolder>(){



    private var itemClickCallbackVariable : ItemClickCallback? = null

    fun setOnItemListener(listener: ItemClickCallback) {
        this.itemClickCallbackVariable = listener
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NewHistoryAdapter.NewHistoryViewHolder {
        val binding = ItemDataBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NewHistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NewHistoryAdapter.NewHistoryViewHolder, position: Int) {
        val data: NewHistoryDetection = historyList[position]
        holder.bind(data)

        holder.binding.imgBtnDelete.setOnClickListener {
            itemClickCallbackVariable?.onDelete(data)
            //removeItem(position)
        }
    }

    override fun getItemCount(): Int =historyList.size

    inner class NewHistoryViewHolder(val binding: ItemDataBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind (newData: NewHistoryDetection){
            with(binding) {
                tvItemDate.text = newData.timestamp
                tvStatus.text = newData.hasil
                Glide.with(itemView.context)
                    .load(newData.image)
                    //.apply(new RequestOptions().override(55, 55))
                    .into(binding.ivImg)

                itemView.setOnClickListener {
                    val intentToDetail = Intent(itemView.context, DetailHistoryActivity::class.java)
                    intentToDetail.putExtra(DetailHistoryActivity.EXTRA_DATA, newData)
                    it.context.startActivity(intentToDetail)
                }
            }
        }
    }

    fun removeItem(data: NewHistoryDetection) {
        val position = historyList.indexOf(data)
        if (position != RecyclerView.NO_POSITION) {
            historyList.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    interface ItemClickCallback{
        fun onDelete(data: NewHistoryDetection)
    }

}