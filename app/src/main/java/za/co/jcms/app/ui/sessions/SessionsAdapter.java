package za.co.jcms.app.ui.sessions;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import za.co.jcms.app.R;
import za.co.jcms.app.data.model.SessionDto;
import za.co.jcms.app.util.DateUtils;
import za.co.jcms.app.util.ViewUtils;

public class SessionsAdapter extends RecyclerView.Adapter<SessionsAdapter.ViewHolder> {

    private final List<SessionDto> items = new ArrayList<>();
    private final OnSessionClickListener listener;

    public interface OnSessionClickListener {
        void onSessionClick(SessionDto session);
    }

    public SessionsAdapter(OnSessionClickListener listener) {
        this.listener = listener;
    }

    public void submitList(List<SessionDto> list) {
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
                .inflate(R.layout.item_session_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SessionDto session = items.get(position);

        holder.tvSessionName.setText(session.getSessionName());
        holder.tvCategory.setText(session.getCategory());
        ViewUtils.applyStatusBadge(holder.tvStatus, session.getStatus());

        if (session.getDescription() != null && !session.getDescription().trim().isEmpty()) {
            holder.tvDescription.setText(session.getDescription());
            holder.tvDescription.setVisibility(View.VISIBLE);
        } else {
            holder.tvDescription.setVisibility(View.GONE);
        }

        holder.tvDate.setText(DateUtils.formatDisplayDate(session.getSessionDate()));
        holder.tvTime.setText(DateUtils.formatTimeRange(session.getStartTime(), session.getEndTime()));
        holder.tvLocation.setText(session.getLocation());

        holder.tvCapacity.setText(session.getConfirmedParticipants() + " / " + session.getCapacity() + " confirmed");

        if (session.isFull()) {
            holder.tvRemaining.setText("Session full");
            holder.tvRemaining.setTextColor(holder.itemView.getContext().getColor(R.color.status_full));
        } else {
            holder.tvRemaining.setText(session.getSpotsRemaining() + " spots left");
            holder.tvRemaining.setTextColor(holder.itemView.getContext().getColor(R.color.text_muted));
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onSessionClick(session);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategory;
        TextView tvStatus;
        TextView tvSessionName;
        TextView tvDescription;
        TextView tvDate;
        TextView tvTime;
        TextView tvLocation;
        TextView tvCapacity;
        TextView tvRemaining;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvSessionName = itemView.findViewById(R.id.tvSessionName);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvCapacity = itemView.findViewById(R.id.tvCapacity);
            tvRemaining = itemView.findViewById(R.id.tvRemaining);
        }
    }
}
