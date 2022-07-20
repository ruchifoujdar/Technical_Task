package com.registration.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.registration.data.model.User
import com.registration.R
import kotlinx.android.synthetic.main.custom_adapter_list_item.view.*

class CustomAdapter(
    private val users: ArrayList<User.Details>
) : RecyclerView.Adapter<CustomAdapter.DataViewHolder>() {
    private var clickListener: ClickListener? = null

    inner class DataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnLongClickListener {
        fun bind(user: User.Details) {
            itemView.textViewUserName.text = user.name
            itemView.textViewUserEmail.text = user.email
            /*  for (value in user.data!!.iterator()){
                println("the element at ${value.name} is ${value.email}")
                itemView.textViewUserName.text = value.name
                itemView.textViewUserEmail.text = value.email
            }*/
        }
        init {
            if (clickListener != null) {
                itemView.setOnLongClickListener(this)
            }
        }
      /*  override fun onClick(v: View?) {
            if (v != null) {
                clickListener?.onItemClick(v,adapterPosition)
            }
        }*/

        override fun onLongClick(v: View?): Boolean {
            if (v != null) {
                clickListener?.onItemClick(v,adapterPosition)
            }
            return true
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        DataViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.custom_adapter_list_item, parent,
                false
            )
        )

    override fun getItemCount(): Int = users.size

    fun setOnItemClickListener(clickListener: ClickListener) {
        this.clickListener = clickListener
    }

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) =
        holder.bind(users[position])

    fun addData(list: List<User>) {
        var list_new: List<User.Details>? = null
        for ((index, value) in list!!.withIndex()) {
            println("the name at ${value.data?.get(index)?.name} is ${value.data?.get(index)?.email}")
            list_new = value.data!!
        }
        if (list_new != null) {
            users.addAll(list_new)
        }
    }
    interface ClickListener {
        fun onItemClick(v: View,position: Int)
    }
}