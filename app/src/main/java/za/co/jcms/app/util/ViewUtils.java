package za.co.jcms.app.util;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import com.google.android.material.snackbar.Snackbar;
import za.co.jcms.app.R;

public class ViewUtils {

    public static void applyStatusBadge(TextView badgeView, String status) {
        if (badgeView == null) return;
        Context context = badgeView.getContext();
        String safeStatus = status != null ? status.toUpperCase() : "OPEN";

        badgeView.setText(safeStatus);
        switch (safeStatus) {
            case "OPEN":
                badgeView.setBackgroundResource(R.drawable.bg_status_open);
                badgeView.setTextColor(ContextCompat.getColor(context, R.color.status_open));
                break;
            case "FULL":
                badgeView.setBackgroundResource(R.drawable.bg_status_full);
                badgeView.setTextColor(ContextCompat.getColor(context, R.color.status_full));
                break;
            case "CANCELLED":
                badgeView.setBackgroundResource(R.drawable.bg_status_cancelled);
                badgeView.setTextColor(ContextCompat.getColor(context, R.color.status_cancelled));
                break;
            case "COMPLETED":
            default:
                badgeView.setBackgroundResource(R.drawable.bg_status_completed);
                badgeView.setTextColor(ContextCompat.getColor(context, R.color.status_completed));
                break;
        }
    }

    public static void applyAttendanceBadge(TextView badgeView, String status) {
        if (badgeView == null) return;
        Context context = badgeView.getContext();
        String safeStatus = status != null ? status.toUpperCase() : "NOT MARKED";

        badgeView.setText(safeStatus);
        switch (safeStatus) {
            case "PRESENT":
                badgeView.setBackgroundResource(R.drawable.bg_status_open);
                badgeView.setTextColor(ContextCompat.getColor(context, R.color.attendance_present));
                break;
            case "ABSENT":
                badgeView.setBackgroundResource(R.drawable.bg_status_cancelled);
                badgeView.setTextColor(ContextCompat.getColor(context, R.color.attendance_absent));
                break;
            case "LATE":
                badgeView.setBackgroundResource(R.drawable.bg_status_full);
                badgeView.setTextColor(ContextCompat.getColor(context, R.color.attendance_late));
                break;
            default:
                badgeView.setBackgroundResource(R.drawable.bg_status_completed);
                badgeView.setTextColor(ContextCompat.getColor(context, R.color.status_completed));
                break;
        }
    }

    public static int getCategoryAccentColor(Context context, String category) {
        if (category == null) return ContextCompat.getColor(context, R.color.primary);
        switch (category.toLowerCase()) {
            case "beginner jogging":
                return ContextCompat.getColor(context, R.color.primary);
            case "intermediate running":
                return ContextCompat.getColor(context, R.color.secondary);
            case "advanced running":
                return ContextCompat.getColor(context, R.color.accent_purple);
            case "family fun runs":
                return ContextCompat.getColor(context, R.color.accent_amber);
            case "marathon training":
                return ContextCompat.getColor(context, R.color.primary_darker);
            default:
                return ContextCompat.getColor(context, R.color.primary);
        }
    }

    public static void showSnackbar(View view, String message, boolean isError) {
        if (view == null) return;
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        if (isError) {
            snackbar.setBackgroundTint(Color.parseColor("#EF4444"));
            snackbar.setTextColor(Color.WHITE);
        } else {
            snackbar.setBackgroundTint(Color.parseColor("#10B981"));
            snackbar.setTextColor(Color.WHITE);
        }
        snackbar.show();
    }
}
