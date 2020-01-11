package ch.bbbaden.choreapp.widgets

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import ch.bbbaden.choreapp.R
import ch.bbbaden.choreapp.UserManager
import ch.bbbaden.choreapp.child.ChildActivity
import ch.bbbaden.choreapp.models.Child
import ch.bbbaden.choreapp.models.Parent
import ch.bbbaden.choreapp.parent.ParentActivity


class ChoresWidgetProvider : AppWidgetProvider() {

    companion object {
        internal fun updateAppWidget(
            context: Context?,
            appWidgetManager: AppWidgetManager?,
            appWidgetId: Int
        ) {
            val views = RemoteViews(context?.packageName, R.layout.chores_app_widget)

            val widgetServiceIntent = Intent(context, ChoresWidgetService::class.java).apply {
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetId)
            }

            UserManager.getUser {
                if (it is Child) {
                    it.fetchChores { chores ->
                        val intent = Intent(context, ChildActivity::class.java)
                        val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)

                        views.apply {
                            setTextViewText(R.id.title, "${chores.size} Chores")
                            setOnClickPendingIntent(R.id.title, pendingIntent)
                            setRemoteAdapter(R.id.chores, widgetServiceIntent)
                            setEmptyView(R.id.chores, R.id.emptyView)
                        }

                        appWidgetManager?.updateAppWidget(appWidgetId, views)
                    }
                } else if (it is Parent) {
                    it.fetchChores { chores ->
                        val intent = Intent(context, ParentActivity::class.java)
                        val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)

                        views.apply {
                            setTextViewText(R.id.title, "${chores.size} Chores")
                            setOnClickPendingIntent(R.id.title, pendingIntent)
                            setRemoteAdapter(R.id.chores, widgetServiceIntent)
                            setEmptyView(R.id.chores, R.id.emptyView)
                        }

                        appWidgetManager?.updateAppWidget(appWidgetId, views)
                    }
                }
            }
        }
    }

    override fun onUpdate(
        context: Context?,
        appWidgetManager: AppWidgetManager?,
        appWidgetIds: IntArray?
    ) {
        val intent = Intent(context?.applicationContext, ChoresService::class.java)
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds)
        context?.startService(intent)
    }
}