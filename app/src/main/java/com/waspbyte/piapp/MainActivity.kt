package com.waspbyte.piapp

import PiManager
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlin.math.min

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val piManager = PiManager(this)

        val sharedPref = getSharedPreferences(getString(R.string.prefs), MODE_PRIVATE)
        val index = sharedPref.getInt(getString(R.string.index), 0)

        val typedValue = TypedValue()
        theme.resolveAttribute(
            com.google.android.material.R.attr.colorOnSurfaceVariant, typedValue, true
        )
        val colorLearned = typedValue.data
        theme.resolveAttribute(
            com.google.android.material.R.attr.colorTertiary, typedValue, true
        )
        val colorNew = typedValue.data
        val piItem = layoutInflater.inflate(R.layout.pi_item, null);
        val paint = piItem.findViewById<TextView>(R.id.pi_tv).paint
        val charWidth = paint.measureText("1")
        val width = Resources.getSystem().displayMetrics.widthPixels
        // TODO - width of textview
        val piAdapter =
            PiAdapter(piManager.PI, index, colorLearned, colorNew, (width / charWidth).toInt() - 4)
        val recyclerView: RecyclerView = findViewById(R.id.pi_rv)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = piAdapter
        val todayTv = findViewById<TextView>(R.id.today_tv)
        todayTv.text = index.toString()

        findViewById<MaterialButton>(R.id.to_game_btn).setOnClickListener {
            startActivity(Intent(this, GameActivity::class.java))
        }

        val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val today = sdf.format(Date())
        if (TimeUnit.DAYS.convert(
                sdf.parse(today)!!.time - sdf.parse(
                    sharedPref.getString(
                        getString(
                            R.string.previous_date
                        ), today
                    )!!
                )!!.time, TimeUnit.MILLISECONDS
            ) > 1
        ) {
            with(sharedPref.edit()) {
                putInt(getString(R.string.streak), 0)
                apply()
            }
        }
        val streakBtn = findViewById<MaterialButton>(R.id.streak_btn)
        val streak = sharedPref.getInt(getString(R.string.streak), 0)
        if (streak > 0) {
            streakBtn.setIconTintResource(R.color.theme_secondary)
        }
        streakBtn.text = streak.toString()
    }

    public override fun onRestart() {
        super.onRestart()
        recreate()
    }

    class PiAdapter(
        private val text: String,
        private val index: Int,
        private val colorLearned: Int,
        private val colorNew: Int,
        private val maxWidth: Int
    ) :
        RecyclerView.Adapter<PiAdapter.ViewHolder>() {
        private var dataSet: Array<SpannableString>

        init {
            dataSet = Array(text.length / maxWidth) { i ->
                val span = SpannableString(text.slice(i * maxWidth..<i * maxWidth + maxWidth))
                if (i * maxWidth <= index) {
                    span.setSpan(
                        ForegroundColorSpan(colorLearned),
                        0,
                        min(index - i * maxWidth, if (i != 0) i * maxWidth else maxWidth),
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    if (index + 1 <= (i + 1) * maxWidth) {
                        span.setSpan(
                            ForegroundColorSpan(colorNew),
                            index - i * maxWidth,
                            index - i * maxWidth + 1,
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                    }
                }
                span
            }
        }

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val textView: TextView

            init {
                textView = view.findViewById(R.id.pi_tv)
            }
        }

        override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.pi_item, viewGroup, false)

            return ViewHolder(view)
        }

        override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
            viewHolder.textView.text = dataSet[position]
        }

        override fun getItemCount() = dataSet.size

    }
}