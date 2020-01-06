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
import kotlinx.android.synthetic.main.card_view_child.view.*


class ChildRecyclerAdapter(private val children: List<Child>) :
    RecyclerView.Adapter<ChildRecyclerAdapter.ChildHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ChildHolder {
        val inflatedView = parent.inflate(R.layout.card_view_child, false)
        return ChildHolder(
            inflatedView
        )
    }

    override fun getItemCount() = children.size

    override fun onBindViewHolder(holder: ChildHolder, position: Int) {
        val itemChore = children[position]
        holder.bindChore(itemChore)
    }

    class ChildHolder(v: View) : RecyclerView.ViewHolder(v), View.OnClickListener {

        private var view: View = v
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
            v.setOnClickListener(this)
        }

        @SuppressLint("SetTextI18n")
        fun bindChore(child: Child) {
            this.child = child
            // view.childImage.setImageResource(R.drawable.ic_menu_camera)
            view.childName.text = child.first

            view.qrCode.setImageBitmap(child.getQRCode(smallerDimension))
            view.qrCode.setOnClickListener {
                val dialog =
                    QRDialogFragment(
                        child.getQRCode(smallerDimension),
                        view.context.getString(R.string.scan_this_qr_code_on_your_child_s_phone)
                    )
                dialog.show(
                    (view.context as AppCompatActivity).supportFragmentManager,
                    "ChildQRDialogFragment"
                )
            }
        }

        override fun onClick(v: View) {
            Toast.makeText(itemView.context, "${child?.userId} clicked.", Toast.LENGTH_SHORT).show()
        }
    }
}