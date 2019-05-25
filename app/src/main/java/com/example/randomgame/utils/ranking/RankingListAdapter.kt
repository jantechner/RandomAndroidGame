package com.example.randomgame.utils.ranking

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.example.randomgame.R
import com.example.randomgame.RankingActivity
import com.example.randomgame.data.model.RankingEntry
import kotlinx.android.synthetic.main.list_item.view.*

class RankingListAdapter(private val activity: RankingActivity?, private val dataSource: ArrayList<RankingEntry>) : BaseAdapter() {

    private val inflater: LayoutInflater = activity?.applicationContext?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int { return dataSource.size }
    override fun getItem(position: Int): Any { return dataSource[position] }
    override fun getItemId(position: Int): Long { return position.toLong() }
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val rowView = inflater.inflate(R.layout.list_item, parent, false)
        val entry = getItem(position) as RankingEntry
        rowView.nr.text = entry.nr.toString()
        rowView.index.text = entry.index
        rowView.result.text = entry.result.toString()
        return rowView
    }
}




