package za.co.jcms.app.ui.attendance;

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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import java.util.ArrayList;
import java.util.List;
import za.co.jcms.app.R;
import za.co.jcms.app.data.model.AttendanceRecordDto;
import za.co.jcms.app.data.model.AttendanceRequestDto;
import za.co.jcms.app.data.model.RegistrationDto;
import za.co.jcms.app.util.ViewUtils;

public class AttendanceFragment extends Fragment {

    private AttendanceViewModel viewModel;
    private int sessionId;
    private String sessionName = "";

    private SwipeRefreshLayout swipeRefresh;
    private ProgressBar progressBar;
    private TextView tvAttendanceSessionTitle;
    private TextView tvCountPresent;
    private TextView tvCountAbsent;
    private TextView tvCountLate;
    private RecyclerView rvAttendance;
    private View layoutEmptyAttendance;
    private AttendanceAdapter adapter;

    private List<RegistrationDto> cachedRegistrations = new ArrayList<>();
    private List<AttendanceRecordDto> cachedAttendance = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_attendance, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            sessionId = getArguments().getInt("sessionId", -1);
            sessionName = getArguments().getString("sessionName", "");
        }

        viewModel = new ViewModelProvider(this).get(AttendanceViewModel.class);

        initViews(view);
        setupRecyclerView();
        observeViewModel();

        loadData();
    }

    private void initViews(View view) {
        swipeRefresh = view.findViewById(R.id.swipeRefresh);
        progressBar = view.findViewById(R.id.progressBar);

        tvAttendanceSessionTitle = view.findViewById(R.id.tvAttendanceSessionTitle);
        tvCountPresent = view.findViewById(R.id.tvCountPresent);
        tvCountAbsent = view.findViewById(R.id.tvCountAbsent);
        tvCountLate = view.findViewById(R.id.tvCountLate);

        rvAttendance = view.findViewById(R.id.rvAttendance);
        layoutEmptyAttendance = view.findViewById(R.id.layoutEmptyAttendance);

        if (!sessionName.isEmpty()) {
            tvAttendanceSessionTitle.setText(sessionName);
        }

        swipeRefresh.setColorSchemeResources(R.color.primary);
        swipeRefresh.setOnRefreshListener(this::loadData);
    }

    private void setupRecyclerView() {
        adapter = new AttendanceAdapter(this::onMarkAttendance);
        rvAttendance.setLayoutManager(new LinearLayoutManager(getContext()));
        rvAttendance.setAdapter(adapter);
    }

    private void loadData() {
        if (sessionId != -1) {
            viewModel.loadData(sessionId);
        }
    }

    private void observeViewModel() {
        viewModel.getRegistrations().observe(getViewLifecycleOwner(), resource -> {
            if (resource == null) return;
            swipeRefresh.setRefreshing(false);

            if (resource.isLoading()) {
                if (adapter.getItemCount() == 0) progressBar.setVisibility(View.VISIBLE);
            } else if (resource.isSuccess() && resource.getData() != null) {
                progressBar.setVisibility(View.GONE);
                cachedRegistrations = resource.getData();
                adapter.setData(cachedRegistrations, cachedAttendance);
                updateStats();
            } else if (resource.isError()) {
                progressBar.setVisibility(View.GONE);
                ViewUtils.showSnackbar(getView(), resource.getMessage(), true);
            }
        });

        viewModel.getAttendanceRecords().observe(getViewLifecycleOwner(), resource -> {
            if (resource == null) return;

            if (resource.isSuccess() && resource.getData() != null) {
                cachedAttendance = resource.getData();
                adapter.setData(cachedRegistrations, cachedAttendance);
                updateStats();
            }
        });
    }

    private void updateStats() {
        int present = 0;
        int absent = 0;
        int late = 0;

        for (AttendanceRecordDto record : cachedAttendance) {
            if (record.isPresent()) present++;
            else if (record.isAbsent()) absent++;
            else if (record.isLate()) late++;
        }

        tvCountPresent.setText(String.valueOf(present));
        tvCountAbsent.setText(String.valueOf(absent));
        tvCountLate.setText(String.valueOf(late));

        if (adapter.getItemCount() == 0) {
            layoutEmptyAttendance.setVisibility(View.VISIBLE);
            rvAttendance.setVisibility(View.GONE);
        } else {
            layoutEmptyAttendance.setVisibility(View.GONE);
            rvAttendance.setVisibility(View.VISIBLE);
        }
    }

    private void onMarkAttendance(RegistrationDto registration, String status) {
        if (registration == null || registration.getMemberId() == null) return;

        AttendanceRequestDto req = new AttendanceRequestDto(
                registration.getRegistrationId(),
                status,
                "Marked via Android App"
        );

        progressBar.setVisibility(View.VISIBLE);
        viewModel.recordAttendance(req).observe(getViewLifecycleOwner(), resource -> {
            if (resource == null) return;
            if (resource.isSuccess()) {
                progressBar.setVisibility(View.GONE);
                ViewUtils.showSnackbar(getView(), "Marked " + registration.getMemberName() + " as " + status, false);
                loadData();
            } else if (resource.isError()) {
                progressBar.setVisibility(View.GONE);
                ViewUtils.showSnackbar(getView(), resource.getMessage(), true);
            }
        });
    }
}
