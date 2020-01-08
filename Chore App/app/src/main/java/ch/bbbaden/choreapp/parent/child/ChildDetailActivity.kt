package ch.bbbaden.choreapp.parent.child

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import ch.bbbaden.choreapp.R
import ch.bbbaden.choreapp.UserManager
import ch.bbbaden.choreapp.dialogs.ConfirmationDialogFragment
import ch.bbbaden.choreapp.dialogs.NameDialogFragment
import ch.bbbaden.choreapp.dialogs.ScanQRDialogFragment
import ch.bbbaden.choreapp.models.Child
import ch.bbbaden.choreapp.models.ChoreDAO
import ch.bbbaden.choreapp.models.CompletedChore
import ch.bbbaden.choreapp.smallerDimension
import com.google.firebase.Timestamp
import kotlinx.android.synthetic.main.activity_child_detail.*
import kotlinx.android.synthetic.main.activity_chore_detail.toolbar

private const val REQUEST_CODE_PERMISSIONS = 10
private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)

class ChildDetailActivity : AppCompatActivity(), ScanQRDialogFragment.ScanQRDialogListener,
    NameDialogFragment.NameDialogListener {

    private lateinit var child: Child
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: CompletedChoreRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_child_detail)
        setSupportActionBar(toolbar)

        linearLayoutManager = LinearLayoutManager(this)
        completedChores.layoutManager = linearLayoutManager

        val childId = intent.extras?.get("childId") as String
        UserManager.parent!!.getChild(childId) {
            child = it!!
            setupUI()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.chore_detail_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_save -> {
            save {
                finish()
            }
            true
        }

        R.id.action_delete -> {
            val dialog = ConfirmationDialogFragment(getString(R.string.remove_child_confirmation))
                .setPositiveButtonListener {
                    deleteChild()
                }
            dialog.show(supportFragmentManager, "ConfirmationDialogFragment")
            true
        }

        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    private fun deleteChild() {
        UserManager.parent!!.deleteChild(child) {
            if (it) {
                setResult(Activity.RESULT_OK)
                finish()
            } else {
                TODO("Show error")
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setupUI() {
        childImg.setImageBitmap(child.getQRCode(smallerDimension))
        nameTxt.text = "${child.first} ${child.last ?: ""}"

        fabScanQR.setOnClickListener {
            if (allPermissionsGranted()) {
                val dialog =
                    ScanQRDialogFragment(
                        this,
                        getString(R.string.scan_the_qr_code_showed_on_your_childs_phone)
                    )
                dialog.show(supportFragmentManager, "ScanQRDialogFragment")
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    REQUIRED_PERMISSIONS,
                    REQUEST_CODE_PERMISSIONS
                )
            }
        }

        adapter = CompletedChoreRecyclerAdapter(child.completedChores)
        completedChores.adapter = adapter

        val onClickListener = View.OnClickListener {
            val dialog = NameDialogFragment(
                this,
                resources.getString(R.string.change_name)
            )
            dialog.show(supportFragmentManager, "NameDialogFragment")
        }
        editName.setOnClickListener(onClickListener)
        nameTxt.setOnClickListener(onClickListener)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(this, "Permissions not granted by the user.", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onScanQRResult(dialog: DialogFragment, rawValue: String) {
        val regex = Regex("(?:choreid):(\\w+)")
        val matchResult = regex.find(rawValue)
        matchResult?.let { mr ->
            ChoreDAO().getChore(mr.groupValues[1]) { chore ->
                if (chore != null && chore.parent?.id == UserManager.parent!!.id) {
                    val completedChore = CompletedChore(chore.documentReference, Timestamp.now())
                    val d =
                        ConfirmationDialogFragment("Has your child really completed the chore \"${chore.name}\"?").setPositiveButtonListener {
                            completeChore(completedChore)
                        }
                    d.show(supportFragmentManager, "ConfirmationDialogFragment")
                } else {
                    Toast.makeText(this, "Chore scanned does not exist.", Toast.LENGTH_LONG)
                        .show()
                }
            }
        }
    }

    private fun save(callback: ((success: Boolean) -> Unit)? = null) {
        // TODO: Saving toast/card animation
        UserManager.parent!!.saveChild(child) {
            if (it) {
                setResult(Activity.RESULT_OK)
            }
            callback?.invoke(it)
        }
    }

    private fun completeChore(completedChore: CompletedChore) {
        child.completedChores.add(completedChore)
        save {
            if (it) adapter.notifyDataSetChanged()
        }
    }

    override fun setName(dialog: DialogFragment, first: String, last: String?) {
        child.first = first
        child.last = last
        save {
            if (it) setupUI()
        }
    }
}
