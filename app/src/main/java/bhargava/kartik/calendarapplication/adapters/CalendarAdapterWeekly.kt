package bhargava.kartik.calendarapplication.adapters

import android.os.Build
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import bhargava.kartik.calendarapplication.R
import android.widget.TextView
import androidx.annotation.RequiresApi
import java.time.LocalDate

class CalendarAdapterWeekly(
    private val daysOfMonth: ArrayList<LocalDate>,
    private val onItemListenerWeekly: OnItemListenerWeekly
) :
    RecyclerView.Adapter<CalendarAdapterWeekly.CalendarViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view: View = inflater.inflate(R.layout.calendar_cell, parent, false)
        return CalendarViewHolder(view, onItemListenerWeekly)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        holder.dayOfMonth.text = daysOfMonth[position].dayOfMonth.toString()
    }

    override fun getItemCount(): Int {
        return daysOfMonth.size
    }

    interface OnItemListenerWeekly {
        fun onItemClickWeekly(position: Int, dayText: String?)
    }

    inner class CalendarViewHolder(
        itemView: View,
        private val onItemListener: OnItemListenerWeekly
    ) :
        RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val dayOfMonth: TextView = itemView.findViewById(
            R.id.cellDayText
        )

        override fun onClick(view: View?) {
            onItemListener.onItemClickWeekly(adapterPosition, dayOfMonth.text as String)
        }

        init {
            itemView.setOnClickListener(this)
        }
    }
}
