package ch.bbbaden.choreapp.parent.child

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import ch.bbbaden.choreapp.R
import ch.bbbaden.choreapp.dialogs.QRDialogFragment
import ch.bbbaden.choreapp.inflate
import ch.bbbaden.choreapp.models.Child
import ch.bbbaden.choreapp.smallerDimension
import kotlinx.android.synthetic.main.card_view_parent_child.view.*


class ChildRecyclerAdapter(private val children: List<Child>, private val listener: ChildHolder.ChildHolderListener) :
    RecyclerView.Adapter<ChildRecyclerAdapter.ChildHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ChildHolder {
        val inflatedView = parent.inflate(R.layout.card_view_parent_child, false)
        return ChildHolder(inflatedView, listener)
    }

    override fun getItemCount() = children.size

    override fun onBindViewHolder(holder: ChildHolder, position: Int) {
        val itemChore = children[position]
        holder.bindChore(itemChore)
    }

    class ChildHolder(private val view: View, private val listener: ChildHolderListener) :
        RecyclerView.ViewHolder(view),
        View.OnClickListener {

        interface ChildHolderListener {
            fun openDetails(child: Child)
        }

        private var child: Child? = null

        init {
            view.setOnClickListener(this)
        }

        @SuppressLint("SetTextI18n")
        fun bindChore(child: Child) {
            this.child = child
            view.childName.text = child.first

            val qrCode = child.getQRCode(smallerDimension)
            view.qrCode.setImageBitmap(qrCode)
            view.qrCode.setOnClickListener {
                val dialog =
                    QRDialogFragment(
                        qrCode,
                        view.context.getString(R.string.scan_this_qr_code_on_your_childs_phone)
                    )
                dialog.show(
                    (view.context as AppCompatActivity).supportFragmentManager,
                    "QRDialogFragment"
                )
            }
        }

        override fun onClick(v: View) {
            listener.openDetails(child!!)
        }
    }
}