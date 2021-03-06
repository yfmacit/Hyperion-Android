package com.willowtreeapps.hyperion.core.internal;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;

import javax.inject.Inject;

@AppScope
class HyperionServiceLifecycleDelegate extends LifecycleDelegate {

    private final CoreComponentContainer container;
    private Activity foregroundActivity;

    @Inject
    HyperionServiceLifecycleDelegate(CoreComponentContainer container) {
        this.container = container;
    }

    @Override
    public void onActivityStarted(Activity activity) {
        foregroundActivity = activity;
        CoreComponent component = container.getComponent(activity);
        if (component == null) {
            return;
        }
        final ServiceConnection connection = component.getServiceConnection();
        foregroundActivity.bindService(
                new Intent(activity, HyperionService.class),
                connection,
                Context.BIND_AUTO_CREATE);
        component.getMenuController().onStart();
    }

    @Override
    public void onActivityStopped(Activity activity) {
        if (foregroundActivity == activity) {
            CoreComponent component = container.getComponent(activity);
            if (component == null) {
                return;
            }
            final ServiceConnection connection = component.getServiceConnection();
            foregroundActivity.unbindService(connection);
            component.getMenuController().onStop();
            foregroundActivity = null;
        }
    }
}