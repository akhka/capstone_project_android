package com.example.chatapp.ui.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.example.chatapp.R;
import com.example.chatapp.ui.activities.HomeActivity;

public class ContactWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds){
            Intent intent = new Intent(context, HomeActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.home_widget);
            views.setOnClickPendingIntent(R.id.display_chats_button, pendingIntent);

            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
}
