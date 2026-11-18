package za.co.jcms.app.ui.attendance;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import za.co.jcms.app.R;
import za.co.jcms.app.data.model.AttendanceRecordDto;
import za.co.jcms.app.data.model.RegistrationDto;
import za.co.jcms.app.util.DateUtils;
import za.co.jcms.app.util.ViewUtils;

public class AttendanceAdapter extends RecyclerView.Adapter<AttendanceAdapter.ViewHolder> {

    public interface OnAttendanceActionListener {
        void onMark(RegistrationDto registration, String status);
    }

    private final List<RegistrationDto> registrations = new ArrayList<>();
    private final Map<Integer, AttendanceRecordDto> attendanceMap = new HashMap<>();
    private final OnAttendanceActionListener listener;

    public AttendanceAdapter(OnAttendanceActionListener listener) {
        this.listener = listener;
    }

    public void setData(List<RegistrationDto> regList, List<AttendanceRecordDto> attList) {
        registrations.clear();
        attendanceMap.clear();

        if (regList != null) {
            for (RegistrationDto reg : regList) {
                if (reg.isConfirmed()) {
                    registrations.add(reg);
                }
            }
        }

        if (attList != null) {
            for (AttendanceRecordDto att : attList) {
                if (att.getMemberId() != null) {
                    attendanceMap.put(att.getMemberId(), att);
                }
            }
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_attendance_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RegistrationDto reg = registrations.get(position);
        holder.tvMemberName.setText(reg.getMemberName());

        // Initials
        String initials = "M";
        if (reg.getMemberName() != null && !reg.getMemberName().trim().isEmpty()) {
            String[] parts = reg.getMemberName().trim().split("\\s+");
            if (parts.length == 1) {
                initials = parts[0].substring(0, Math.min(2, parts[0].length())).toUpperCase();
            } else {
                initials = ("" + parts[0].charAt(0) + parts[parts.length - 1].charAt(0)).toUpperCase();
            }
        }
        holder.tvInitials.setText(initials);

        // Check if attendance record exists for this member
        AttendanceRecordDto record = attendanceMap.get(reg.getMemberId());
        if (record != null) {
            ViewUtils.applyAttendanceBadge(holder.tvAttendanceStatus, record.getStatus());
            holder.tvMarkedAt.setText("Marked: " + DateUtils.formatDisplayDateTime(record.getMarkedAt()));
            holder.tvMarkedAt.setVisibility(View.VISIBLE);

            if (record.getNotes() != null && !record.getNotes().trim().isEmpty()) {
                holder.tvNotes.setText("Note: " + record.getNotes());
                holder.tvNotes.setVisibility(View.VISIBLE);
            } else {
                holder.tvNotes.setVisibility(View.GONE);
            }
        } else {
            ViewUtils.applyAttendanceBadge(holder.tvAttendanceStatus, "NOT MARKED");
            holder.tvMarkedAt.setVisibility(View.GONE);
            holder.tvNotes.setVisibility(View.GONE);
        }

        holder.btnMarkPresent.setOnClickListener(v -> {
            if (listener != null) listener.onMark(reg, "PRESENT");
        });

        holder.btnMarkAbsent.setOnClickListener(v -> {
            if (listener != null) listener.onMark(reg, "ABSENT");
        });

        holder.btnMarkLate.setOnClickListener(v -> {
            if (listener != null) listener.onMark(reg, "LATE");
        });
    }

    @Override
    public int getItemCount() {
        return registrations.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvInitials;
        TextView tvMemberName;
        TextView tvMarkedAt;
        TextView tvAttendanceStatus;
        TextView tvNotes;
        Button btnMarkPresent;
        Button btnMarkAbsent;
        Button btnMarkLate;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvInitials = itemView.findViewById(R.id.tvInitials);
            tvMemberName = itemView.findViewById(R.id.tvMemberName);
            tvMarkedAt = itemView.findViewById(R.id.tvMarkedAt);
            tvAttendanceStatus = itemView.findViewById(R.id.tvAttendanceStatus);
            tvNotes = itemView.findViewById(R.id.tvNotes);
            btnMarkPresent = itemView.findViewById(R.id.btnMarkPresent);
            btnMarkAbsent = itemView.findViewById(R.id.btnMarkAbsent);
            btnMarkLate = itemView.findViewById(R.id.btnMarkLate);
        }
    }
}
