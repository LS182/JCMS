package za.co.jcms.app.ui.dashboard;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import za.co.jcms.app.R;
import za.co.jcms.app.data.model.CategorySummaryDto;
import za.co.jcms.app.util.ViewUtils;

public class CategorySummaryAdapter extends RecyclerView.Adapter<CategorySummaryAdapter.ViewHolder> {

    private final List<CategorySummaryDto> items = new ArrayList<>();
    private final OnCategoryClickListener listener;

    public interface OnCategoryClickListener {
        void onCategoryClick(CategorySummaryDto category);
    }

    public CategorySummaryAdapter(OnCategoryClickListener listener) {
        this.listener = listener;
    }

    public void submitList(List<CategorySummaryDto> list) {
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
                .inflate(R.layout.item_category_summary, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CategorySummaryDto item = items.get(position);
        holder.tvCategoryName.setText(item.getCategory());
        holder.tvUpcomingCount.setText(String.valueOf(item.getUpcomingSessions()));
        holder.tvActiveBookings.setText(item.getTotalConfirmedBookings() + " booked");

        int color = ViewUtils.getCategoryAccentColor(holder.itemView.getContext(), item.getCategory());
        holder.categoryIndicator.setBackgroundColor(color);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCategoryClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        View categoryIndicator;
        TextView tvCategoryName;
        TextView tvUpcomingCount;
        TextView tvActiveBookings;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryIndicator = itemView.findViewById(R.id.categoryIndicator);
            tvCategoryName = itemView.findViewById(R.id.tvCategoryName);
            tvUpcomingCount = itemView.findViewById(R.id.tvUpcomingCount);
            tvActiveBookings = itemView.findViewById(R.id.tvActiveBookings);
        }
    }
}
