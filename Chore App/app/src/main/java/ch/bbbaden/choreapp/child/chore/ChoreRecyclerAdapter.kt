package ch.bbbaden.choreapp.child.chore

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import ch.bbbaden.choreapp.*
import ch.bbbaden.choreapp.dialogs.QRDialogFragment
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

        init {
            view.setOnClickListener(this)
        }

        @SuppressLint("SetTextI18n")
        fun bindChore(chore: Chore) {
            this.chore = chore
            view.choreName.text = chore.name

            val assignment = chore.getAssignment(UserManager.child!!)
            view.choreTime.text = assignment?.getNextDate()!!.displayTime

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