package com.example.cs528finalproject.fragment

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.cs528finalproject.R
import com.example.cs528finalproject.models.Meal

class MealListViewAdapter (private val dataset: ArrayList<*>, mContext: Context):
    ArrayAdapter<Any?>(mContext, R.layout.list_item_meal, dataset) {
    private class ViewHolder {
        lateinit var txtName: TextView
        lateinit var txtCalories: TextView
    }

    override fun getCount(): Int {
        return dataset.size
    }

    override fun getItem(position: Int): Meal {
        return dataset[position] as Meal
    }

    override fun getView(
        position: Int,
        convertView: View?,
        parent: ViewGroup
    ): View {
        var convertView = convertView
        val viewHolder: ViewHolder
        val result: View
        if (convertView == null) {
            viewHolder = ViewHolder()
            convertView =
                LayoutInflater.from(parent.context).inflate(R.layout.list_item_meal, parent, false)
            viewHolder.txtName =
                convertView.findViewById(R.id.list_item_meal_name)
            viewHolder.txtCalories =
                convertView.findViewById(R.id.list_item_meal_cal)
            result = convertView
            convertView.tag = viewHolder
        } else {
            viewHolder = convertView.tag as ViewHolder
            result = convertView
        }

        val item: Meal = getItem(position)
        viewHolder.txtName.text = item.foodName

        viewHolder.txtCalories.text = item.totalCalories.toInt().toString()
        return result
    }
}