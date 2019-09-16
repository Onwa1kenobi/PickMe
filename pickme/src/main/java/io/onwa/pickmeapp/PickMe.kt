package io.onwa.pickme

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.*
import io.onwa.pickme.adapter.PickCallback
import io.onwa.pickme.adapter.PickMeAdapter

class PickMe : FrameLayout, PickCallback {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PickMeAdapter
    private val picks = mutableListOf<Any>()

    private enum class Justification { START, CENTER, END }

    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, attributeSetId: Int) : super(context, attrs, attributeSetId) {
        init(context, attrs)
    }


    private fun init(context: Context, attrs: AttributeSet?) {

        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.PickMe, 0, 0)
        val positiveColor = a.getColor(
            R.styleable.PickMe_positiveColor, ContextCompat.getColor(
                context,
                R.color.colorAccent
            )
        )
        val negativeColor = a.getColor(
            R.styleable.PickMe_negativeColor, ContextCompat.getColor(
                context,
                android.R.color.white
            )
        )

        val textSize = getPixelValue(a.getDimension(R.styleable.PickMe_textSize, convertFloatToPX(16f)))
        val cornerRadius = a.getDimension(R.styleable.PickMe_textCornerRadius, convertFloatToPX(0f))
        Log.e("PICK", "TextSize: $textSize - CornerRadius: $cornerRadius")

        val justification = Justification.values()[a.getInt(R.styleable.PickMe_justification, 0)]

        a.recycle()

        val states = arrayOf(
            intArrayOf(-android.R.attr.state_checked), // unchecked
            intArrayOf(android.R.attr.state_checked), // checked
            intArrayOf(android.R.attr.state_focused), // focused
            intArrayOf(android.R.attr.state_pressed),  // pressed
            intArrayOf(android.R.attr.state_enabled) // enabled
        )

        val colors = intArrayOf(
            positiveColor,
            negativeColor,
            negativeColor,
            negativeColor,
            positiveColor
        )

        val colorStateList = ColorStateList(states, colors)

        val layoutManager = FlexboxLayoutManager(context)
        layoutManager.flexWrap = FlexWrap.WRAP
        layoutManager.flexDirection = FlexDirection.ROW
        layoutManager.alignItems = AlignItems.BASELINE
        layoutManager.justifyContent = when (justification) {
            Justification.START -> JustifyContent.FLEX_START
            Justification.CENTER -> JustifyContent.CENTER
            Justification.END -> JustifyContent.FLEX_END
        }

        adapter = PickMeAdapter(emptyList(), this)
        adapter.colorStateList = colorStateList
        adapter.textSize = textSize
        adapter.cornerRadius = cornerRadius

        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.layout_pick_me, this, true)

        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
    }

    override fun onPickMade(pick: Any) {
        picks.add(pick)
    }

    override fun onDeselectPick(pick: Any) {
        picks.remove(pick)
    }

    fun setData(data: List<Any>) {
        adapter.setData(data)
    }

    fun getPicks() = picks

    private fun convertFloatToPX(dp: Float) = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, dp,
        this.context.resources.displayMetrics
    )

    private fun getPixelValue(value: Float) = (value / this.context.resources.displayMetrics.density)
}