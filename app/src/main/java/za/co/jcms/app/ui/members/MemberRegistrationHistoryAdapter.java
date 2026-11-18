package za.co.jcms.app.ui.members;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import za.co.jcms.app.R;
import za.co.jcms.app.data.model.RegistrationDto;
import za.co.jcms.app.util.DateUtils;
import za.co.jcms.app.util.ViewUtils;

public class MemberRegistrationHistoryAdapter extends RecyclerView.Adapter<MemberRegistrationHistoryAdapter.ViewHolder> {

    private final List<RegistrationDto> items = new ArrayList<>();
    private final OnHistoryClickListener listener;

    public interface OnHistoryClickListener {
        void onHistoryClick(RegistrationDto item);
    }

    public MemberRegistrationHistoryAdapter(OnHistoryClickListener listener) {
        this.listener = listener;
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
                .inflate(R.layout.item_registration_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RegistrationDto reg = items.get(position);

        holder.tvHistorySessionName.setText(reg.getSessionName());
        holder.tvHistoryCategory.setText(reg.getSessionCategory() != null ? reg.getSessionCategory() : "Jogging");
        holder.tvHistoryDate.setText(DateUtils.formatDisplayDate(reg.getSessionDate()) + " • " + DateUtils.formatDisplayTime(reg.getStartTime()));
        ViewUtils.applyStatusBadge(holder.tvHistoryStatus, reg.getStatus());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onHistoryClick(reg);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvHistoryCategory;
        TextView tvHistorySessionName;
        TextView tvHistoryDate;
        TextView tvHistoryStatus;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvHistoryCategory = itemView.findViewById(R.id.tvHistoryCategory);
            tvHistorySessionName = itemView.findViewById(R.id.tvHistorySessionName);
            tvHistoryDate = itemView.findViewById(R.id.tvHistoryDate);
            tvHistoryStatus = itemView.findViewById(R.id.tvHistoryStatus);
        }
    }
}
