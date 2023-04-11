package com.example.cs528finalproject.fragment

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.cs528finalproject.R
import com.example.cs528finalproject.models.FoodLocation

class FoodLocationListViewAdapter (private val dataset: ArrayList<*>, mContext: Context):
    ArrayAdapter<Any?>(mContext, R.layout.list_item_food_location, dataset) {
    private class ViewHolder {
        lateinit var txtName: TextView
        lateinit var txtDistance: TextView
    }

    override fun getCount(): Int {
        return dataset.size
    }

    override fun getItem(position: Int): FoodLocation {
        return dataset[position] as FoodLocation
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
                convertView.findViewById(R.id.list_item_location_name)
            viewHolder.txtDistance =
                convertView.findViewById(R.id.list_item_location_distance)
            result = convertView
            convertView.tag = viewHolder
        } else {
            viewHolder = convertView.tag as ViewHolder
            result = convertView
        }

        val item: FoodLocation = getItem(position)
        viewHolder.txtName.text = item.name

        viewHolder.txtDistance.text = item.getDistance().toString()
        return result
    }
}