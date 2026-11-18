package za.co.jcms.app.ui.sessions;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import za.co.jcms.app.R;
import za.co.jcms.app.data.model.RegistrationDto;
import za.co.jcms.app.util.DateUtils;
import za.co.jcms.app.util.ViewUtils;

public class RegistrationsAdapter extends RecyclerView.Adapter<RegistrationsAdapter.ViewHolder> {

    private final List<RegistrationDto> items = new ArrayList<>();
    private final OnCancelRegistrationListener cancelListener;

    public interface OnCancelRegistrationListener {
        void onCancelRegistration(RegistrationDto registration);
    }

    public RegistrationsAdapter(OnCancelRegistrationListener cancelListener) {
        this.cancelListener = cancelListener;
    }

    public void submitList(List<RegistrationDto> list) {
        items.clear();
        if (list != null) {
            items.addAll(list);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_registration_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RegistrationDto reg = items.get(position);

        holder.tvMemberName.setText(reg.getMemberName());
        holder.tvMemberEmail.setText(reg.getMemberEmail());
        holder.tvRegisteredDate.setText("Registered: " + DateUtils.formatDisplayDateTime(reg.getRegisteredAt()));

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

        // Status badge
        ViewUtils.applyStatusBadge(holder.tvRegStatus, reg.getStatus());

        // Cancel button: only show if CONFIRMED
        if (reg.isConfirmed()) {
            holder.btnCancelRegistration.setVisibility(View.VISIBLE);
            holder.btnCancelRegistration.setOnClickListener(v -> {
                if (cancelListener != null) {
                    cancelListener.onCancelRegistration(reg);
                }
            });
        } else {
            holder.btnCancelRegistration.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvInitials;
        TextView tvMemberName;
        TextView tvMemberEmail;
        TextView tvRegisteredDate;
        TextView tvRegStatus;
        Button btnCancelRegistration;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvInitials = itemView.findViewById(R.id.tvInitials);
            tvMemberName = itemView.findViewById(R.id.tvMemberName);
            tvMemberEmail = itemView.findViewById(R.id.tvMemberEmail);
            tvRegisteredDate = itemView.findViewById(R.id.tvRegisteredDate);
            tvRegStatus = itemView.findViewById(R.id.tvRegStatus);
            btnCancelRegistration = itemView.findViewById(R.id.btnCancelRegistration);
        }
    }
}
