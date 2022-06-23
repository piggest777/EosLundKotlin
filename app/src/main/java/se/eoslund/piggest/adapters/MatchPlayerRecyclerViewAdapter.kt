package se.eoslund.piggest.adapters

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView

import se.eoslund.piggest.controller.placeholder.PlaceholderContent.PlaceholderItem
import se.eoslund.piggest.databinding.FragmentPlayerBinding
import se.eoslund.piggest.model.PlayerRO
import se.eoslund.piggest.services.DateFormatter

/**
 * [RecyclerView.Adapter] that can display a [PlaceholderItem].
 * TODO: Replace the implementation with code for your data type.
 */
class MatchPlayerRecyclerViewAdapter(
    private val values: MutableList<PlayerRO>
) : RecyclerView.Adapter<MatchPlayerRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            FragmentPlayerBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.idView.text = item.number.toString()
        holder.contentView.text = item.name

        val sublineString = StringBuilder()
        if (item.dayOfBirth != null) {
            val age = DateFormatter.getAge(item.dayOfBirth!!)
            sublineString.append("Age: $age")
        }
        if (item.height != null) {
            sublineString.append(" Height: ${item.height} cm")
        }
        holder.subLineView.text = sublineString.toString()
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FragmentPlayerBinding) : RecyclerView.ViewHolder(binding.root) {
        val idView: TextView = binding.itemNumber
        val contentView: TextView = binding.content
        val subLineView: TextView = binding.playerSubline

        override fun toString(): String {
            return super.toString() + " '" + contentView.text + "'"
        }
    }

}