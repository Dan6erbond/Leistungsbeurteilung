package ch.bbbaden.choreapp.parent

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import ch.bbbaden.choreapp.R
import ch.bbbaden.choreapp.models.Child
import kotlinx.android.synthetic.main.custom_spinner_dropdown_item.view.*

class ChildArrayAdapter(ctx: Context, children: List<Child>) : ArrayAdapter<Child>(ctx, 0, children) {

    override fun getView(position: Int, recycledView: View?, parent: ViewGroup): View {
        val child = getItem(position)
        val view = recycledView ?: LayoutInflater.from(context).inflate(
            R.layout.custom_spinner_item_selected,
            parent,
            false
        )
        view.itemText.text = child?.first
        return view
    }

    override fun getDropDownView(position: Int, recycledView: View?, parent: ViewGroup): View {
        val child = getItem(position)
        val view = recycledView ?: LayoutInflater.from(context).inflate(
            R.layout.custom_spinner_dropdown_item,
            parent,
            false
        )
        view.itemText.text = child?.first
        return view
    }

}