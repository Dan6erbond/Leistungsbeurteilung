package ch.bbbaden.choreapp.parent.child

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Point
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import ch.bbbaden.choreapp.R
import ch.bbbaden.choreapp.dialogs.QRDialogFragment
import ch.bbbaden.choreapp.inflate
import ch.bbbaden.choreapp.models.Child
import kotlinx.android.synthetic.main.card_view_parent_child.view.*


class ChildRecyclerAdapter(private val children: List<Child>) :
    RecyclerView.Adapter<ChildRecyclerAdapter.ChildHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ChildHolder {
        val inflatedView = parent.inflate(R.layout.card_view_parent_child, false)
        return ChildHolder(
            inflatedView
        )
    }

    override fun getItemCount() = children.size

    override fun onBindViewHolder(holder: ChildHolder, position: Int) {
        val itemChore = children[position]
        holder.bindChore(itemChore)
    }

    class ChildHolder(private val view: View) : RecyclerView.ViewHolder(view),
        View.OnClickListener {

        private var child: Child? = null

        private val smallerDimension: Int
            get() {
                val manager = view.context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
                val display = manager.defaultDisplay
                val point = Point()
                display.getSize(point)
                val width = point.x
                val height = point.y
                Pair(width, height)
                val smallerDimension = if (width < height) width else height
                return smallerDimension * 3 / 4
            }

        init {
            view.setOnClickListener(this)
        }

        @SuppressLint("SetTextI18n")
        fun bindChore(child: Child) {
            this.child = child
            // view.childImage.setImageResource(R.drawable.ic_menu_camera)
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
            Toast.makeText(itemView.context, "${child?.userId} clicked.", Toast.LENGTH_SHORT).show()
        }
    }
}