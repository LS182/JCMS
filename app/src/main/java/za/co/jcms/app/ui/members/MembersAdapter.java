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
import za.co.jcms.app.data.model.MemberDto;

public class MembersAdapter extends RecyclerView.Adapter<MembersAdapter.ViewHolder> {

    private final List<MemberDto> items = new ArrayList<>();
    private final OnMemberClickListener listener;

    public interface OnMemberClickListener {
        void onMemberClick(MemberDto member);
    }

    public MembersAdapter(OnMemberClickListener listener) {
        this.listener = listener;
    }

    public void submitList(List<MemberDto> list) {
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
                .inflate(R.layout.item_member_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MemberDto member = items.get(position);

        holder.tvFullName.setText(member.getFullName());
        holder.tvEmail.setText(member.getEmail());
        holder.tvContact.setText(member.getContactNumber());
        holder.tvInitials.setText(member.getInitials());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onMemberClick(member);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvInitials;
        TextView tvFullName;
        TextView tvEmail;
        TextView tvContact;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvInitials = itemView.findViewById(R.id.tvInitials);
            tvFullName = itemView.findViewById(R.id.tvFullName);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            tvContact = itemView.findViewById(R.id.tvContact);
        }
    }
}
