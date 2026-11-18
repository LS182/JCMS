package za.co.jcms.app.ui.common;

import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

/**
 * A practical lifecycle-aware component demonstrating the use of LifecycleObserver
 * and LifecycleOwner. Automatically tracks screen lifecycle states and can trigger
 * refresh callbacks when the host UI resumes.
 */
public class NetworkLifecycleObserver implements DefaultLifecycleObserver {

    private static final String TAG = "LifecycleObserver";
    private final String screenName;
    private final Runnable onResumeCallback;

    public NetworkLifecycleObserver(String screenName) {
        this(screenName, null);
    }

    public NetworkLifecycleObserver(String screenName, Runnable onResumeCallback) {
        this.screenName = screenName;
        this.onResumeCallback = onResumeCallback;
    }

    @Override
    public void onCreate(@NonNull LifecycleOwner owner) {
        Log.d(TAG, screenName + " -> onCreate observed from LifecycleOwner: " + owner.getClass().getSimpleName());
    }

    @Override
    public void onStart(@NonNull LifecycleOwner owner) {
        Log.d(TAG, screenName + " -> onStart observed");
    }

    @Override
    public void onResume(@NonNull LifecycleOwner owner) {
        Log.d(TAG, screenName + " -> onResume: screen active");
        if (onResumeCallback != null) {
            onResumeCallback.run();
        }
    }

    @Override
    public void onPause(@NonNull LifecycleOwner owner) {
        Log.d(TAG, screenName + " -> onPause: screen backgrounded");
    }

    @Override
    public void onStop(@NonNull LifecycleOwner owner) {
        Log.d(TAG, screenName + " -> onStop observed");
    }

    @Override
    public void onDestroy(@NonNull LifecycleOwner owner) {
        Log.d(TAG, screenName + " -> onDestroy: cleaning up lifecycle observer");
        owner.getLifecycle().removeObserver(this);
    }
}
