package za.co.jcms.app.ui.sessions;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import java.util.Calendar;
import java.util.Locale;
import za.co.jcms.app.R;
import za.co.jcms.app.data.model.SessionRequestDto;
import za.co.jcms.app.util.ViewUtils;

public class SessionsFragment extends Fragment {

    private SessionsViewModel viewModel;
    private SwipeRefreshLayout swipeRefresh;
    private ProgressBar progressBar;
    private View layoutEmpty;
    private TextView tvSessionsCount;
    private Spinner spinnerCategory;
    private Spinner spinnerStatus;
    private RecyclerView rvSessions;
    private ExtendedFloatingActionButton fabAddSession;
    private SessionsAdapter adapter;

    private static final String[] CATEGORIES = {
            "All",
            "Beginner Jogging",
            "Intermediate Running",
            "Advanced Running",
            "Family Fun Runs",
            "Marathon Training"
    };

    private static final String[] STATUSES = {
            "All",
            "OPEN",
            "FULL",
            "CANCELLED",
            "COMPLETED"
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sessions, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(SessionsViewModel.class);

        initViews(view);
        setupSpinners();
        setupRecyclerView();
        observeViewModel();
    }

    private void initViews(View view) {
        swipeRefresh = view.findViewById(R.id.swipeRefresh);
        progressBar = view.findViewById(R.id.progressBar);
        layoutEmpty = view.findViewById(R.id.layoutEmpty);
        tvSessionsCount = view.findViewById(R.id.tvSessionsCount);
        spinnerCategory = view.findViewById(R.id.spinnerCategory);
        spinnerStatus = view.findViewById(R.id.spinnerStatus);
        rvSessions = view.findViewById(R.id.rvSessions);
        fabAddSession = view.findViewById(R.id.fabAddSession);

        swipeRefresh.setColorSchemeResources(R.color.primary);
        swipeRefresh.setOnRefreshListener(() -> viewModel.loadSessions());

        fabAddSession.setOnClickListener(v -> showCreateSessionDialog());
    }

    private void setupSpinners() {
        ArrayAdapter<String> catAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, CATEGORIES);
        catAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(catAdapter);

        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
                viewModel.setCategoryFilter(CATEGORIES[position]);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, STATUSES);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(statusAdapter);

        spinnerStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
                viewModel.setStatusFilter(STATUSES[position]);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setupRecyclerView() {
        adapter = new SessionsAdapter(session -> {
            if (session.getSessionId() != null) {
                Bundle args = new Bundle();
                args.putInt("sessionId", session.getSessionId());
                Navigation.findNavController(requireView()).navigate(R.id.action_sessions_to_sessionDetail, args);
            }
        });
        rvSessions.setLayoutManager(new LinearLayoutManager(getContext()));
        rvSessions.setAdapter(adapter);
    }

    private void observeViewModel() {
        viewModel.getRawSessions().observe(getViewLifecycleOwner(), resource -> {
            if (resource == null) return;
            swipeRefresh.setRefreshing(false);

            if (resource.isLoading()) {
                if (adapter.getItemCount() == 0) progressBar.setVisibility(View.VISIBLE);
            } else if (resource.isSuccess()) {
                progressBar.setVisibility(View.GONE);
            } else if (resource.isError()) {
                progressBar.setVisibility(View.GONE);
                ViewUtils.showSnackbar(getView(), resource.getMessage(), true);
            }
        });

        viewModel.getFilteredSessions().observe(getViewLifecycleOwner(), list -> {
            adapter.submitList(list);
            if (list == null || list.isEmpty()) {
                layoutEmpty.setVisibility(View.VISIBLE);
                tvSessionsCount.setText("No matching sessions");
            } else {
                layoutEmpty.setVisibility(View.GONE);
                tvSessionsCount.setText("Showing " + list.size() + " sessions");
            }
        });
    }

    private void showCreateSessionDialog() {
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_session_form, null);

        TextView tvDialogTitle = dialogView.findViewById(R.id.tvDialogTitle);
        tvDialogTitle.setText("New Jogging Session");

        EditText etSessionName = dialogView.findViewById(R.id.etSessionName);
        Spinner spinnerFormCategory = dialogView.findViewById(R.id.spinnerFormCategory);
        EditText etDescription = dialogView.findViewById(R.id.etDescription);
        EditText etSessionDate = dialogView.findViewById(R.id.etSessionDate);
        EditText etStartTime = dialogView.findViewById(R.id.etStartTime);
        EditText etEndTime = dialogView.findViewById(R.id.etEndTime);
        EditText etLocation = dialogView.findViewById(R.id.etLocation);
        EditText etCapacity = dialogView.findViewById(R.id.etCapacity);
        View layoutStatusContainer = dialogView.findViewById(R.id.layoutStatusContainer);

        // Status is not needed for creation (defaults to OPEN in backend)
        layoutStatusContainer.setVisibility(View.GONE);

        // Categories list without "All"
        String[] formCategories = {
                "Beginner Jogging",
                "Intermediate Running",
                "Advanced Running",
                "Family Fun Runs",
                "Marathon Training"
        };
        ArrayAdapter<String> catAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, formCategories);
        catAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFormCategory.setAdapter(catAdapter);

        // Setup Date Picker
        Calendar calendar = Calendar.getInstance();
        etSessionDate.setOnClickListener(v -> {
            new DatePickerDialog(requireContext(), (view, year, month, dayOfMonth) -> {
                String dateStr = String.format(Locale.US, "%04d-%02d-%02d", year, month + 1, dayOfMonth);
                etSessionDate.setText(dateStr);
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        // Setup Time Pickers
        etStartTime.setOnClickListener(v -> {
            new TimePickerDialog(requireContext(), (view, hourOfDay, minute) -> {
                String timeStr = String.format(Locale.US, "%02d:%02d", hourOfDay, minute);
                etStartTime.setText(timeStr);
            }, 7, 0, true).show();
        });

        etEndTime.setOnClickListener(v -> {
            new TimePickerDialog(requireContext(), (view, hourOfDay, minute) -> {
                String timeStr = String.format(Locale.US, "%02d:%02d", hourOfDay, minute);
                etEndTime.setText(timeStr);
            }, 8, 30, true).show();
        });

        AlertDialog dialog = new MaterialAlertDialogBuilder(requireContext())
                .setView(dialogView)
                .setCancelable(true)
                .create();

        dialogView.findViewById(R.id.btnCancel).setOnClickListener(v -> dialog.dismiss());

        dialogView.findViewById(R.id.btnSave).setOnClickListener(v -> {
            String name = etSessionName.getText().toString().trim();
            String category = (String) spinnerFormCategory.getSelectedItem();
            String desc = etDescription.getText().toString().trim();
            String date = etSessionDate.getText().toString().trim();
            String start = etStartTime.getText().toString().trim();
            String end = etEndTime.getText().toString().trim();
            String loc = etLocation.getText().toString().trim();
            String capStr = etCapacity.getText().toString().trim();

            if (name.isEmpty() || date.isEmpty() || start.isEmpty() || end.isEmpty() || loc.isEmpty() || capStr.isEmpty()) {
                ViewUtils.showSnackbar(dialogView, "Please fill in all required fields", true);
                return;
            }

            int capacity;
            try {
                capacity = Integer.parseInt(capStr);
                if (capacity <= 0) throw new NumberFormatException();
            } catch (NumberFormatException e) {
                ViewUtils.showSnackbar(dialogView, "Capacity must be a positive number", true);
                return;
            }

            SessionRequestDto request = new SessionRequestDto(name, desc, category, date, start, end, loc, capacity, "OPEN");

            dialog.dismiss();
            progressBar.setVisibility(View.VISIBLE);

            viewModel.createSession(request).observe(getViewLifecycleOwner(), resource -> {
                if (resource == null) return;
                if (resource.isSuccess()) {
                    progressBar.setVisibility(View.GONE);
                    ViewUtils.showSnackbar(getView(), "Session created successfully!", false);
                    viewModel.loadSessions();
                } else if (resource.isError()) {
                    progressBar.setVisibility(View.GONE);
                    ViewUtils.showSnackbar(getView(), resource.getMessage(), true);
                }
            });
        });

        dialog.show();
    }
}
