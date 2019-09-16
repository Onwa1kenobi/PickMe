package io.onwa.pickme.adapter

import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatCheckedTextView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import io.onwa.pickme.R

class PickMeAdapter(private var poll: List<Any>, private val pickCallback: PickCallback) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var layoutInflater: LayoutInflater? = null
    var colorStateList: ColorStateList? = null
    var textSize: Float? = null
    var cornerRadius: Float = 0f

    // Sparse boolean array for saving the check state of interest during recycle
    private val stateArray = SparseBooleanArray()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(parent.context)
        }

        val view = layoutInflater!!.inflate(R.layout.item_pick_me, parent, false)

        return SimpleTextViewHolder(view)
    }

    override fun getItemCount() = poll.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as SimpleTextViewHolder).bindView(position)
    }

    fun setData(poll: List<Any>) {
        this.poll = poll
        notifyDataSetChanged()
    }

    inner class SimpleTextViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val pickText: AppCompatCheckedTextView = itemView.findViewById(R.id.text_pick)
        private lateinit var pick: String

        init {
            colorStateList?.run {
                pickText.setTextColor(this)

                val stateListDrawable = StateListDrawable()
                stateListDrawable.addState(
                    intArrayOf(-android.R.attr.state_checked), customView(
                        this.getColorForState(
                            intArrayOf(android.R.attr.state_checked),
                            ContextCompat.getColor(itemView.context, R.color.colorAccent)
                        ), this.getColorForState(
                            intArrayOf(-android.R.attr.state_checked),
                            ContextCompat.getColor(itemView.context, android.R.color.transparent)
                        )
                    )
                )
                stateListDrawable.addState(
                    intArrayOf(android.R.attr.state_checked), customView(
                        this.getColorForState(
                            intArrayOf(-android.R.attr.state_checked),
                            ContextCompat.getColor(itemView.context, android.R.color.transparent)
                        ), this.getColorForState(
                            intArrayOf(android.R.attr.state_checked),
                            ContextCompat.getColor(itemView.context, R.color.colorAccent)
                        )
                    )
                )

                ViewCompat.setBackground(pickText, stateListDrawable)
            }

            textSize?.run {
                pickText.textSize = this
            }
        }

        fun bindView(position: Int) {
            pick = poll[position].toString()
            pickText.text = pick
            pickText.isChecked = stateArray.get(position, false)
            pickText.setOnClickListener {
                pickText.toggle()
                stateArray.put(position, pickText.isChecked)
                if (pickText.isChecked) {
                    pickCallback.onPickMade(pick)
                } else {
                    pickCallback.onDeselectPick(pick)
                }
            }
        }
    }

    private fun customView(backgroundColor: Int, borderColor: Int): Drawable {
        val shape = GradientDrawable()
        shape.shape = GradientDrawable.RECTANGLE
        shape.cornerRadius = cornerRadius
        shape.setColor(backgroundColor)
        shape.setStroke(3, borderColor)
        return shape
    }
}
