package com.artincodes.miniera.utils.widgetutils;

/**
 * Created by jayadeep on 3/8/15.
 */
import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Context;


/**
 * Specific {@link AppWidgetHost} that creates our {@link LauncherAppWidgetHostView}
 * which correctly captures all long-press events. This ensures that users can
 * always pick up and move widgets.
 */
public class LauncherAppWidgetHost extends AppWidgetHost {
    public LauncherAppWidgetHost(Context context, int hostId) {
        super(context, hostId);
    }


    @Override
    protected AppWidgetHostView onCreateView(Context context, int appWidgetId,
                                             AppWidgetProviderInfo appWidget) {
        return new LauncherAppWidgetHostView(context);
    }


    @Override
    public void stopListening() {
        super.stopListening();
        clearViews();
    }
}