package com.mbweskley.mobileproject.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.mbweskley.mobileproject.databinding.ItemAdapterBinding
import com.mbweskley.mobileproject.model.Task

class TaskAdapter(private val taskList: List<Task>, val taskSelected: (Task, Int) -> Unit) :
    RecyclerView.Adapter<TaskAdapter.MyViewHolder>() {

    companion object {
        const val SELECT_BACK = 1
        const val SELECT_REMOVE = 2
        const val SELECT_EDIT = 3
        const val SELECT_NEXT = 4
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            ItemAdapterBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val task = taskList[position]
        holder.binding.tvCardTitle.text = task.titulo
        holder.binding.tvDescTitle.text = task.descricao
        holder.binding.tvEndTitle.text = task.endereco
        holder.binding.tvDataTitle.text = task.data

        holder.binding.btDeletar.setOnClickListener { taskSelected(task, SELECT_REMOVE) }
        holder.binding.btEditar.setOnClickListener { taskSelected(task, SELECT_EDIT) }

        when (task.status) {
            0 -> {
                holder.binding.btVoltar.isVisible = false
                holder.binding.btProximo.setOnClickListener { taskSelected(task, SELECT_NEXT) }
            }
            1 -> {
                holder.binding.btVoltar.setOnClickListener { taskSelected(task, SELECT_BACK) }
                holder.binding.btProximo.setOnClickListener { taskSelected(task, SELECT_NEXT) }
            }
            else -> {
                holder.binding.btProximo.isVisible = false
                holder.binding.btVoltar.setOnClickListener { taskSelected(task, SELECT_BACK) }
            }
        }
    }

    override fun getItemCount() = taskList.size

    inner class MyViewHolder(val binding: ItemAdapterBinding) :
        RecyclerView.ViewHolder(binding.root)
}