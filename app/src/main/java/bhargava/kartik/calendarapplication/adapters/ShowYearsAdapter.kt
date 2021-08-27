package bhargava.kartik.calendarapplication.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import bhargava.kartik.calendarapplication.databinding.SingleItemMonthBinding
import bhargava.kartik.calendarapplication.databinding.SingleItemYearBinding

class ShowYearsAdapter(
    private var yearList: ArrayList<String>,
    var listener: (String, Int) -> Unit
) : RecyclerView.Adapter<ShowYearsAdapter.ShowYearsViewHolder>() {

    inner class ShowYearsViewHolder(
        val binding: SingleItemYearBinding
    ) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        fun bind(monthText: String, position: Int) {
            val monthName = binding.tvYearName
            monthName.text = monthText
        }

        override fun onClick(p0: View?) {
            listener(yearList[layoutPosition], layoutPosition)
        }

        init {
            binding.yearLayout.setOnClickListener(this)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShowYearsViewHolder {
        val binding =
            SingleItemYearBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return ShowYearsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ShowYearsViewHolder, position: Int) {
        holder.bind(yearList[position], position)
    }

    override fun getItemCount(): Int {
        return yearList.size
    }

}