package ch.bbbaden.choreapp.widgets

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import ch.bbbaden.choreapp.R
import ch.bbbaden.choreapp.UserManager
import ch.bbbaden.choreapp.models.Child
import ch.bbbaden.choreapp.models.Chore
import ch.bbbaden.choreapp.models.Parent

class ChoresWidgetService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory? {
        return ChoresRemoteViewsFactory(applicationContext, intent)
    }
}

class ChoresRemoteViewsFactory(private val context: Context, intent: Intent) :
    RemoteViewsService.RemoteViewsFactory {

    private var appWidgetId = 0
    private lateinit var chores: ArrayList<Chore>

    init {
        appWidgetId = intent.getIntExtra(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        )
    }

    override fun onCreate() {
        loadData()
    }

    private fun loadData() {
        chores.add(Chore("asdfjkl", "asdfjl", "asdfjkl"))
        UserManager.getUser {
            if (it is Child) {
                it.fetchChores { chores ->
                    this.chores = chores
                }
            } else if (it is Parent) {
                it.fetchChores { chores ->
                    this.chores = chores
                }
            }
        }
    }

    override fun getLoadingView(): RemoteViews? {
        return null
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onDataSetChanged() {
        loadData()
    }

    override fun hasStableIds(): Boolean {
        return true
    }

    override fun getViewAt(position: Int): RemoteViews {
        return RemoteViews(context.packageName, R.layout.card_chore_widget).apply {
            setTextViewText(R.id.choreName, chores[position].name)
            setTextViewText(R.id.choreDescription, chores[position].description)
        }
    }

    override fun getCount(): Int {
        return chores.size
    }

    override fun getViewTypeCount(): Int {
        return 1
    }

    override fun onDestroy() {
        chores.clear()
    }

}