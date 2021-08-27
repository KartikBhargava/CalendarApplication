package bhargava.kartik.calendarapplication.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import bhargava.kartik.calendarapplication.databinding.SingleItemMonthBinding

class ShowMonthsAdapter(
    private var monthList: ArrayList<String>,
    var listener: (String, Int) -> Unit
) : RecyclerView.Adapter<ShowMonthsAdapter.ShowMonthsViewHolder>() {
    inner class ShowMonthsViewHolder(
        val binding: SingleItemMonthBinding
    ) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        fun bind(monthText: String, position: Int) {
            val monthName = binding.tvMonthName
            monthName.text = monthText
        }

        override fun onClick(p0: View?) {
            listener(monthList[layoutPosition], layoutPosition)

        }

        init {
            binding.monthLayout.setOnClickListener(this)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShowMonthsViewHolder {
        val binding =
            SingleItemMonthBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return ShowMonthsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ShowMonthsViewHolder, position: Int) {
        holder.bind(monthList[position], position)
    }

    override fun getItemCount(): Int {
        return monthList.size
    }

}