package za.co.jcms.app.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.google.android.material.button.MaterialButton;
import za.co.jcms.app.R;
import za.co.jcms.app.data.model.DashboardDto;
import za.co.jcms.app.ui.sessions.SessionsAdapter;
import za.co.jcms.app.util.ViewUtils;

public class DashboardFragment extends Fragment {

    private DashboardViewModel viewModel;
    private SwipeRefreshLayout swipeRefresh;
    private ProgressBar progressBar;
    private View layoutContent;
    private View layoutError;
    private TextView tvErrorMessage;
    private MaterialButton btnRetry;

    private TextView tvTotalMembersCount;
    private TextView tvUpcomingSessionsCount;
    private TextView tvActiveBookingsCount;
    private TextView tvEmptyUpcoming;

    private RecyclerView rvCategorySummaries;
    private RecyclerView rvUpcomingSessions;
    private CategorySummaryAdapter categoryAdapter;
    private SessionsAdapter sessionsAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(DashboardViewModel.class);

        initViews(view);
        setupRecyclerViews();
        setupClickListeners(view);
        observeViewModel();
    }

    private void initViews(View view) {
        swipeRefresh = view.findViewById(R.id.swipeRefresh);
        progressBar = view.findViewById(R.id.progressBar);
        layoutContent = view.findViewById(R.id.layoutContent);
        layoutError = view.findViewById(R.id.layoutError);
        tvErrorMessage = view.findViewById(R.id.tvErrorMessage);
        btnRetry = view.findViewById(R.id.btnRetry);

        tvTotalMembersCount = view.findViewById(R.id.tvTotalMembersCount);
        tvUpcomingSessionsCount = view.findViewById(R.id.tvUpcomingSessionsCount);
        tvActiveBookingsCount = view.findViewById(R.id.tvActiveBookingsCount);
        tvEmptyUpcoming = view.findViewById(R.id.tvEmptyUpcoming);

        rvCategorySummaries = view.findViewById(R.id.rvCategorySummaries);
        rvUpcomingSessions = view.findViewById(R.id.rvUpcomingSessions);

        swipeRefresh.setColorSchemeResources(R.color.primary);
        swipeRefresh.setOnRefreshListener(() -> viewModel.loadDashboard());
        btnRetry.setOnClickListener(v -> viewModel.loadDashboard());
    }

    private void setupRecyclerViews() {
        categoryAdapter = new CategorySummaryAdapter(category -> {
            Navigation.findNavController(requireView()).navigate(R.id.action_dashboard_to_sessions);
        });
        rvCategorySummaries.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvCategorySummaries.setAdapter(categoryAdapter);

        sessionsAdapter = new SessionsAdapter(session -> {
            if (session.getSessionId() != null) {
                Bundle args = new Bundle();
                args.putInt("sessionId", session.getSessionId());
                Navigation.findNavController(requireView()).navigate(R.id.action_dashboard_to_sessionDetail, args);
            }
        });
        rvUpcomingSessions.setLayoutManager(new LinearLayoutManager(getContext()));
        rvUpcomingSessions.setAdapter(sessionsAdapter);
    }

    private void setupClickListeners(View view) {
        view.findViewById(R.id.cardTotalMembers).setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_dashboard_to_members));

        view.findViewById(R.id.cardUpcomingSessions).setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_dashboard_to_sessions));

        view.findViewById(R.id.tvViewAllSessions).setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_dashboard_to_sessions));
    }

    private void observeViewModel() {
        viewModel.getDashboardData().observe(getViewLifecycleOwner(), resource -> {
            if (resource == null) return;

            swipeRefresh.setRefreshing(false);

            if (resource.isLoading()) {
                if (sessionsAdapter.getItemCount() == 0) {
                    progressBar.setVisibility(View.VISIBLE);
                    layoutContent.setVisibility(View.GONE);
                    layoutError.setVisibility(View.GONE);
                }
            } else if (resource.isSuccess() && resource.getData() != null) {
                progressBar.setVisibility(View.GONE);
                layoutError.setVisibility(View.GONE);
                layoutContent.setVisibility(View.VISIBLE);
                bindDashboardData(resource.getData());
            } else if (resource.isError()) {
                progressBar.setVisibility(View.GONE);
                if (sessionsAdapter.getItemCount() == 0) {
                    layoutContent.setVisibility(View.GONE);
                    layoutError.setVisibility(View.VISIBLE);
                    tvErrorMessage.setText(resource.getMessage() != null ? resource.getMessage() : getString(R.string.error_loading));
                } else {
                    ViewUtils.showSnackbar(getView(), resource.getMessage(), true);
                }
            }
        });
    }

    private void bindDashboardData(DashboardDto data) {
        tvTotalMembersCount.setText(String.valueOf(data.getTotalMembers()));
        tvUpcomingSessionsCount.setText(String.valueOf(data.getUpcomingSessionsCount()));
        tvActiveBookingsCount.setText(String.valueOf(data.getTotalActiveBookings()));

        categoryAdapter.submitList(data.getCategorySummaries());

        if (data.getUpcomingSessions() != null && !data.getUpcomingSessions().isEmpty()) {
            sessionsAdapter.submitList(data.getUpcomingSessions());
            rvUpcomingSessions.setVisibility(View.VISIBLE);
            tvEmptyUpcoming.setVisibility(View.GONE);
        } else {
            sessionsAdapter.submitList(null);
            rvUpcomingSessions.setVisibility(View.GONE);
            tvEmptyUpcoming.setVisibility(View.VISIBLE);
        }
    }
}
