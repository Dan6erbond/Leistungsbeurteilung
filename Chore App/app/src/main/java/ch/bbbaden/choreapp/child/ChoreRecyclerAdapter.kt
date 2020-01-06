package ch.bbbaden.choreapp.child

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
import ch.bbbaden.choreapp.models.Chore
import kotlinx.android.synthetic.main.card_view_child_chore.view.*

class ChoreRecyclerAdapter(private val chores: List<Chore>) :
    RecyclerView.Adapter<ChoreRecyclerAdapter.ChoreHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ChoreHolder {
        val inflatedView = parent.inflate(R.layout.card_view_child_chore, false)
        return ChoreHolder(
            inflatedView
        )
    }

    override fun getItemCount() = chores.size

    override fun onBindViewHolder(holder: ChoreHolder, position: Int) {
        val itemChore = chores[position]
        holder.bindChore(itemChore)
    }

    class ChoreHolder(private val view: View) : RecyclerView.ViewHolder(view),
        View.OnClickListener {

        private var chore: Chore? = null

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
        fun bindChore(chore: Chore) {
            this.chore = chore
            // view.choreImage.setImageResource(R.drawable.ic_menu_camera)
            view.choreName.text = chore.name
            view.choreTime.text = chore.getDisplayTime()
            view.choreDescription.text = chore.description

            val qrCode = chore.getQRCode(smallerDimension)
            view.qrCode.setImageBitmap(qrCode)
            view.qrCode.setOnClickListener {
                val dialog =
                    QRDialogFragment(
                        qrCode,
                        view.context.getString(R.string.scan_this_qr_code_on_your_parents_phone)
                    )
                dialog.show(
                    (view.context as AppCompatActivity).supportFragmentManager,
                    "QRDialogFragment"
                )
            }
        }

        override fun onClick(v: View) {
            Toast.makeText(itemView.context, "${chore?.id} clicked.", Toast.LENGTH_SHORT).show()
        }
    }
}