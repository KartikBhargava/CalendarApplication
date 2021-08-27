package bhargava.kartik.calendarapplication.ui

import android.app.Dialog
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.Window
import android.widget.EditText
import bhargava.kartik.calendarapplication.R
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import bhargava.kartik.calendarapplication.adapters.CalendarAdapter
import android.widget.TextView
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import bhargava.kartik.calendarapplication.adapters.CalendarAdapterWeekly
import bhargava.kartik.calendarapplication.databinding.ActivityCalendarBinding
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import bhargava.kartik.calendarapplication.adapters.ShowMonthsAdapter
import bhargava.kartik.calendarapplication.adapters.ShowYearsAdapter
import com.google.android.material.bottomsheet.BottomSheetDialog
import bhargava.kartik.calendarapplication.extras.ItemOffsetDecoration
import bhargava.kartik.calendarapplication.viewmodels.CalendarActivityViewModel
import com.google.android.material.button.MaterialButton
import java.time.DayOfWeek


class CalendarActivity : AppCompatActivity(), CalendarAdapter.OnItemListener,
    CalendarAdapterWeekly.OnItemListenerWeekly {
    private var monthYearText: TextView? = null
    private var calendarRecyclerView: RecyclerView? = null
    private var monthsRecyclerView: RecyclerView? = null
    private var yearsRecyclerView: RecyclerView? = null
    private var selectedDate: LocalDate? = null
    private var _binding: ActivityCalendarBinding? = null
    private val binding get() = _binding!!
    private var monthList: ArrayList<String> = ArrayList()
    private var yearsList: ArrayList<String> = ArrayList()
    private var bottomSheetDialog: BottomSheetDialog? = null
    private var selectedMonth: Int = 0
    private var weeklyRecyclerView: RecyclerView? = null
    private var weekList: ArrayList<LocalDate> = ArrayList()
    private val viewModel: CalendarActivityViewModel by viewModels()
    var weeklySwitch = false

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityCalendarBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.monthlyLayout.isVisible = true
        binding.weeklyLayout.isVisible = false
        calendarRecyclerView = binding.calendarRecyclerView
        monthYearText = binding.monthYearTV
        weeklyRecyclerView = binding.calendarRecyclerViewWeekly
        selectedDate = LocalDate.now()
        prepareMonthArray()
        prepareYearsArray()
        prepareWeekList()
        setMonthView()
        setWeekView()
        binding.backBtn.setOnClickListener {
            selectedDate = selectedDate?.minusMonths(1)
            setMonthView()
        }
        binding.nextBtn.setOnClickListener {
            selectedDate = selectedDate?.plusMonths(1)
            setMonthView()
        }
        binding.monthYearTV.setOnClickListener {
            showBottomSheetDialog()
        }
        binding.btnGoToParticularDate.setOnClickListener {
            showParticularDateDialogBox()
        }
        binding.btnCalendarLayout.setOnClickListener {
            viewModel.switchView.value = !weeklySwitch
            if (weeklySwitch) {
                viewModel.switchView.value = false
                weeklySwitch = false
            } else {
                viewModel.switchView.value = true
                weeklySwitch = true
            }
        }
        binding.btnBack.setOnClickListener {
            selectedDate = selectedDate?.minusWeeks(1)
            prepareWeekList()
        }
        binding.btnNext.setOnClickListener {
            selectedDate = selectedDate?.plusWeeks(1)
            prepareWeekList()
        }
        observeChanges()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun prepareWeekList() {
        weekList = daysInWeekArray(selectedDate)!!
        setWeekView()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setWeekView() {
        binding.tvMonthYearWeekly.text = monthYearFromDate(selectedDate)
        weeklyRecyclerView!!.adapter = CalendarAdapterWeekly(weekList, this)
        weeklyRecyclerView!!.layoutManager = GridLayoutManager(this, 7)
    }

    private fun observeChanges() {
        viewModel.switchView.observe(this, {
            when (it) {
                true -> {
                    binding.monthlyLayout.isVisible = false
                    binding.weeklyLayout.isVisible = true
                }
                false -> {
                    binding.monthlyLayout.isVisible = true
                    binding.weeklyLayout.isVisible = false
                }
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun daysInWeekArray(selectedDate: LocalDate?): ArrayList<LocalDate>? {
        val days: ArrayList<LocalDate> = ArrayList()
        var current: LocalDate = sundayForDate(selectedDate!!)!!
        val endDate = current.plusWeeks(1)
        while (current.isBefore(endDate)) {
            days.add(current)
            current = current.plusDays(1)
        }
        return days
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun sundayForDate(Current: LocalDate): LocalDate? {
        var current = Current
        val oneWeekAgo = current.minusWeeks(1)
        while (current.isAfter(oneWeekAgo)) {
            if (current.dayOfWeek == DayOfWeek.SUNDAY) return current
            current = current.minusDays(1)
        }
        return null
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showParticularDateDialogBox() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.particular_date_dialog)
        val window = dialog.window ?: return
        val windowAttributes = window.attributes
        windowAttributes.gravity = Gravity.CENTER
        window.attributes = windowAttributes
        val etDayOfMonth =
            dialog.findViewById<EditText>(R.id.etDayOfMonth)
        val etMonth = dialog.findViewById<EditText>(R.id.etMonth)
        val etYear = dialog.findViewById<EditText>(R.id.etYear)
        val btnOk = dialog.findViewById<MaterialButton>(R.id.btnOk)
        val btnCancel = dialog.findViewById<MaterialButton>(R.id.btnCancel)
        btnOk.setOnClickListener {
            if (etYear.text.isNotEmpty() || etMonth.text.isNotEmpty() || etDayOfMonth.text.isNotEmpty()) {
                var year = Integer.parseInt(etYear.text.toString())
                val month = Integer.parseInt(etMonth.text.toString())
                val day = Integer.parseInt(etDayOfMonth.text.toString())
                if (month in 1..12) {
                    if (day in 1..31) {
                        val date = LocalDate.of(
                            Integer.parseInt(etYear.text.toString()),
                            Integer.parseInt(etMonth.text.toString()),
                            Integer.parseInt(etDayOfMonth.text.toString())
                        )
                        selectedDate = date
                        setMonthView()
                        dialog.dismiss()
                    } else {
                        Toast.makeText(this, "Invalid Date!", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    }
                } else {
                    Toast.makeText(this, "Invalid Date!", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }
            } else {
                Toast.makeText(this, "Invalid Date!", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }

        }
        btnCancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun prepareYearsArray() {
        val startYear = selectedDate!!.year - 6
        val endYear = selectedDate!!.year + 6
        for (i in startYear until endYear) {
            yearsList.add(i.toString())
        }
    }

    private fun prepareMonthArray() {
        monthList.add("January")
        monthList.add("February")
        monthList.add("March")
        monthList.add("April")
        monthList.add("May")
        monthList.add("June")
        monthList.add("July")
        monthList.add("August")
        monthList.add("September")
        monthList.add("October")
        monthList.add("November")
        monthList.add("December")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setMonthView() {
        monthYearText!!.text = monthYearFromDate(selectedDate)
        binding.tvMonthYearWeekly.text = monthYearFromDate(selectedDate)
        val daysInMonth = daysInMonthArray(selectedDate)
        val calendarAdapter = CalendarAdapter(daysInMonth, this)
        val layoutManager: RecyclerView.LayoutManager = GridLayoutManager(applicationContext, 7)
        calendarRecyclerView!!.layoutManager = layoutManager
        calendarRecyclerView!!.adapter = calendarAdapter
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun daysInMonthArray(date: LocalDate?): ArrayList<String> {
        val daysInMonthArray: ArrayList<String> = ArrayList()
        val yearMonth: YearMonth = YearMonth.from(date)
        val daysInMonth: Int = yearMonth.lengthOfMonth()
        val firstOfMonth: LocalDate = selectedDate?.withDayOfMonth(1)!!
        val dayOfWeek: Int = firstOfMonth.dayOfWeek.value
        for (i in 1..42) {
            if (i <= dayOfWeek || i > daysInMonth + dayOfWeek) {
                daysInMonthArray.add("")
            } else {
                daysInMonthArray.add((i - dayOfWeek).toString())
            }
        }
        return daysInMonthArray
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun monthYearFromDate(date: LocalDate?): String {
        val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MMMM yyyy")
        return date?.format(formatter).toString()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun monthFromDate(date: LocalDate?): String {
        val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MMMM")
        return date?.format(formatter).toString()
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onItemClick(position: Int, dayText: String?) {
        if (dayText != "") {
            val message = "Selected Date " + dayText + " " + monthYearFromDate(selectedDate)
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showBottomSheetDialog() {
        bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog!!.setContentView(R.layout.bottom_sheet_dialog)
        monthsRecyclerView = bottomSheetDialog!!.findViewById(R.id.monthsRecyclerView)
        yearsRecyclerView = bottomSheetDialog!!.findViewById(R.id.yearRecyclerView)
        val monthTextView = bottomSheetDialog!!.findViewById<TextView>(R.id.tvMonthName)
        monthTextView!!.text = monthFromDate(selectedDate)
        setUpBottomSheetRecyclerViewMonths()
        bottomSheetDialog!!.show()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setUpBottomSheetRecyclerViewMonths() {
        monthsRecyclerView!!.adapter = ShowMonthsAdapter(monthList) { s: String, i: Int ->
            onItemClickMonths(i, s)
        }
        val itemDecoration = ItemOffsetDecoration(this, R.dimen.cardview_default_radius)
        monthsRecyclerView!!.clipToPadding = false
        monthsRecyclerView!!.addItemDecoration(itemDecoration)
        monthsRecyclerView!!.layoutManager = GridLayoutManager(this, 4)
        yearsRecyclerView!!.adapter = ShowYearsAdapter(yearsList) { s: String, i: Int ->
            onItemClickYears(s, i)
        }
        yearsRecyclerView!!.clipToPadding = false
        yearsRecyclerView!!.addItemDecoration(itemDecoration)
        yearsRecyclerView!!.layoutManager = GridLayoutManager(this, 4)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun onItemClickYears(s: String, i: Int) {
        val year = Integer.parseInt(yearsList[i])
        val date = LocalDate.of(year, selectedMonth, 1)
        selectedDate = date
        bottomSheetDialog!!.dismiss()
        setMonthView()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun onItemClickMonths(position: Int, monthText: String?) {
        selectedMonth = position + 1
        val date = LocalDate.of(selectedDate!!.year, position + 1, 1)
        Toast.makeText(this, date.toString(), Toast.LENGTH_SHORT).show()
        selectedDate = date
        monthsRecyclerView!!.isVisible = false
        yearsRecyclerView!!.isVisible = true
        val monthTextView = bottomSheetDialog!!.findViewById<TextView>(R.id.tvMonthName)
        monthTextView!!.text = selectedDate!!.year.toString()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onItemClickWeekly(position: Int, dayText: String?) {
        val date = LocalDate.of(
            selectedDate!!.year,
            weekList[position].month,
            weekList[position].dayOfMonth
        )
        Toast.makeText(this, date.toString(), Toast.LENGTH_SHORT).show()
    }
}