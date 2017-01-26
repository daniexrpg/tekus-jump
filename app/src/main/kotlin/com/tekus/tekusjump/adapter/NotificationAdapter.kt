package com.tekus.tekusjump.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.afollestad.materialdialogs.MaterialDialog
import com.tekus.tekusjump.R
import com.tekus.tekusjump.model.Notification
import java.util.*

/**
 * Created by Jose Daniel on 21/01/2017.
 */
class NotificationAdapter(context: Context, items: ArrayList<Notification>) : BaseAdapter() {

    private val context: Context = context
    private var inflater: LayoutInflater
    private var items: ArrayList<Notification>

    init {
        this.inflater = LayoutInflater.from(context)
        this.items = items
    }

    fun setItems(items: ArrayList<Notification>) {
        this.items = items
        notifyDataSetChanged()
    }

    override fun getCount(): Int {
        return items.size
    }

    override fun getItem(position: Int): Any {
        return items[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View

        if (convertView != null) {
            view = convertView
        } else {
            view = this.inflater.inflate(R.layout.item_notification, parent, false)
        }

        val id = view.findViewById(R.id.notification_id) as TextView
        val date = view.findViewById(R.id.notification_date) as TextView
        val duration = view.findViewById(R.id.notification_duration) as TextView

        val buttonEdit = view.findViewById(R.id.button_edit)
        val buttonDelete = view.findViewById(R.id.button_delete)

        id.text = items[position].id.toString()
        date.text = items[position].date
        duration.text = items[position].duration.toString() + " seg"

        buttonEdit.tag = position
        buttonEdit.setOnClickListener { view ->
            if (onItemClickOption != null)
                onItemClickOption!!.OnClickEdit(items[view.tag as Int])
        }

        buttonDelete.tag = position
        buttonDelete.setOnClickListener { view ->
            if (onItemClickOption != null)
                onItemClickOption!!.OnClickDelete(items[view.tag as Int])
        }

        return view
    }

    var onItemClickOption: OnItemClickOption? = null

    interface OnItemClickOption {
        fun OnClickEdit(item: Notification)
        fun OnClickDelete(item: Notification)
    }
}