package com.example.cs528finalproject.fragment

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.cs528finalproject.R
import com.example.cs528finalproject.models.FoodMenu

class FoodMenuListViewAdapter (private val dataset: ArrayList<*>, mContext: Context):
    ArrayAdapter<Any?>(mContext, R.layout.list_item_food_location, dataset) {

    private class ViewHolder {
        lateinit var txtName: TextView
        lateinit var txtCalories: TextView
    }

    override fun getCount(): Int {
        return dataset.size
    }

    override fun getItem(position: Int): FoodMenu {
        return dataset[position] as FoodMenu
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
                LayoutInflater.from(parent.context).inflate(R.layout.list_item_food_location, parent, false)
            viewHolder.txtName =
                convertView.findViewById(R.id.list_item_menu_name)
            viewHolder.txtCalories =
                convertView.findViewById(R.id.list_item_menu_calories)
            result = convertView
            convertView.tag = viewHolder
        } else {
            viewHolder = convertView.tag as ViewHolder
            result = convertView
        }

        val item: FoodMenu = getItem(position)
        viewHolder.txtName.text = item.name

        viewHolder.txtCalories.text = item.calories.toString()
        return result
    }
}
