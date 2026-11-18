package za.co.jcms.app.ui.sessions;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import za.co.jcms.app.R;
import za.co.jcms.app.data.model.MemberDto;
import za.co.jcms.app.data.model.RegistrationDto;
import za.co.jcms.app.data.model.RegistrationRequestDto;
import za.co.jcms.app.data.model.SessionDto;
import za.co.jcms.app.data.model.SessionRequestDto;
import za.co.jcms.app.util.DateUtils;
import za.co.jcms.app.util.ViewUtils;

public class SessionDetailFragment extends Fragment {

    private SessionDetailViewModel viewModel;
    private int sessionId;
    private SessionDto currentSession;

    private SwipeRefreshLayout swipeRefresh;
    private ProgressBar progressBar;
    private TextView tvCategory;
    private TextView tvStatus;
    private TextView tvSessionName;
    private TextView tvDescription;
    private TextView tvDate;
    private TextView tvTime;
    private TextView tvLocation;
    private TextView tvCapacityStats;
    private TextView tvSpotsRemaining;
    private MaterialButton btnAttendance;
    private MaterialButton btnEditSession;
    private MaterialButton btnDeleteSession;
    private MaterialButton btnRegisterMember;
    private RecyclerView rvRegistrations;
    private View layoutEmptyRegistrations;
    private RegistrationsAdapter registrationsAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_session_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            sessionId = getArguments().getInt("sessionId", -1);
        }

        viewModel = new ViewModelProvider(this).get(SessionDetailViewModel.class);

        initViews(view);
        setupRecyclerView();
        observeViewModel();

        loadData();
    }

    private void initViews(View view) {
        swipeRefresh = view.findViewById(R.id.swipeRefresh);
        progressBar = view.findViewById(R.id.progressBar);

        tvCategory = view.findViewById(R.id.tvCategory);
        tvStatus = view.findViewById(R.id.tvStatus);
        tvSessionName = view.findViewById(R.id.tvSessionName);
        tvDescription = view.findViewById(R.id.tvDescription);
        tvDate = view.findViewById(R.id.tvDate);
        tvTime = view.findViewById(R.id.tvTime);
        tvLocation = view.findViewById(R.id.tvLocation);
        tvCapacityStats = view.findViewById(R.id.tvCapacityStats);
        tvSpotsRemaining = view.findViewById(R.id.tvSpotsRemaining);

        btnAttendance = view.findViewById(R.id.btnAttendance);
        btnEditSession = view.findViewById(R.id.btnEditSession);
        btnDeleteSession = view.findViewById(R.id.btnDeleteSession);
        btnRegisterMember = view.findViewById(R.id.btnRegisterMember);

        rvRegistrations = view.findViewById(R.id.rvRegistrations);
        layoutEmptyRegistrations = view.findViewById(R.id.layoutEmptyRegistrations);

        swipeRefresh.setColorSchemeResources(R.color.primary);
        swipeRefresh.setOnRefreshListener(this::loadData);

        btnAttendance.setOnClickListener(v -> {
            if (currentSession != null) {
                Bundle args = new Bundle();
                args.putInt("sessionId", sessionId);
                args.putString("sessionName", currentSession.getSessionName());
                Navigation.findNavController(v).navigate(R.id.action_sessionDetail_to_attendance, args);
            }
        });

        btnEditSession.setOnClickListener(v -> showEditSessionDialog());
        btnDeleteSession.setOnClickListener(v -> confirmDeleteSession());
        btnRegisterMember.setOnClickListener(v -> showRegisterMemberDialog());
    }

    private void setupRecyclerView() {
        registrationsAdapter = new RegistrationsAdapter(this::confirmCancelRegistration);
        rvRegistrations.setLayoutManager(new LinearLayoutManager(getContext()));
        rvRegistrations.setAdapter(registrationsAdapter);
    }

    private void loadData() {
        if (sessionId != -1) {
            viewModel.loadSession(sessionId);
            viewModel.loadRegistrations(sessionId);
        }
    }

    private void observeViewModel() {
        viewModel.getSessionDetail().observe(getViewLifecycleOwner(), resource -> {
            if (resource == null) return;
            swipeRefresh.setRefreshing(false);

            if (resource.isLoading()) {
                progressBar.setVisibility(View.VISIBLE);
            } else if (resource.isSuccess() && resource.getData() != null) {
                progressBar.setVisibility(View.GONE);
                currentSession = resource.getData();
                bindSessionDetails(currentSession);
            } else if (resource.isError()) {
                progressBar.setVisibility(View.GONE);
                ViewUtils.showSnackbar(getView(), resource.getMessage(), true);
            }
        });

        viewModel.getRegistrations().observe(getViewLifecycleOwner(), resource -> {
            if (resource == null) return;

            if (resource.isSuccess() && resource.getData() != null) {
                List<RegistrationDto> list = resource.getData();
                registrationsAdapter.submitList(list);
                if (list.isEmpty()) {
                    layoutEmptyRegistrations.setVisibility(View.VISIBLE);
                    rvRegistrations.setVisibility(View.GONE);
                } else {
                    layoutEmptyRegistrations.setVisibility(View.GONE);
                    rvRegistrations.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void bindSessionDetails(SessionDto session) {
        tvSessionName.setText(session.getSessionName());
        tvCategory.setText(session.getCategory());
        ViewUtils.applyStatusBadge(tvStatus, session.getStatus());

        if (session.getDescription() != null && !session.getDescription().trim().isEmpty()) {
            tvDescription.setText(session.getDescription());
            tvDescription.setVisibility(View.VISIBLE);
        } else {
            tvDescription.setVisibility(View.GONE);
        }

        tvDate.setText(DateUtils.formatDisplayDate(session.getSessionDate()));
        tvTime.setText(DateUtils.formatTimeRange(session.getStartTime(), session.getEndTime()));
        tvLocation.setText(session.getLocation());

        tvCapacityStats.setText(session.getConfirmedParticipants() + " / " + session.getCapacity() + " confirmed");

        if (session.isFull()) {
            tvSpotsRemaining.setText("Session full");
            tvSpotsRemaining.setTextColor(requireContext().getColor(R.color.status_full));
        } else {
            tvSpotsRemaining.setText(session.getSpotsRemaining() + " spots remaining");
            tvSpotsRemaining.setTextColor(requireContext().getColor(R.color.text_primary));
        }
    }

    private void showRegisterMemberDialog() {
        if (currentSession != null && currentSession.isFull()) {
            ViewUtils.showSnackbar(getView(), "Cannot register: This session has reached full capacity", true);
            return;
        }

        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_register_member, null);
        TextView tvSessionSummary = dialogView.findViewById(R.id.tvSessionSummary);
        Spinner spinnerMembers = dialogView.findViewById(R.id.spinnerMembers);
        TextView tvBookingNotice = dialogView.findViewById(R.id.tvBookingNotice);

        if (currentSession != null) {
            tvSessionSummary.setText("Session: " + currentSession.getSessionName() + " (" + currentSession.getSpotsRemaining() + " spots left)");
        }

        final List<MemberDto> memberList = new ArrayList<>();
        final List<String> memberNames = new ArrayList<>();

        viewModel.getAllMembers().observe(getViewLifecycleOwner(), resource -> {
            if (resource != null && resource.isSuccess() && resource.getData() != null) {
                memberList.clear();
                memberNames.clear();
                memberList.addAll(resource.getData());
                for (MemberDto m : memberList) {
                    memberNames.add(m.getFullName() + " (" + m.getEmail() + ")");
                }
                ArrayAdapter<String> memAdapter = new ArrayAdapter<>(requireContext(),
                        android.R.layout.simple_spinner_item, memberNames);
                memAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerMembers.setAdapter(memAdapter);
            }
        });

        AlertDialog dialog = new MaterialAlertDialogBuilder(requireContext())
                .setView(dialogView)
                .setCancelable(true)
                .create();

        dialogView.findViewById(R.id.btnCancel).setOnClickListener(v -> dialog.dismiss());

        dialogView.findViewById(R.id.btnConfirmRegister).setOnClickListener(v -> {
            int selectedIndex = spinnerMembers.getSelectedItemPosition();
            if (selectedIndex < 0 || selectedIndex >= memberList.size()) {
                tvBookingNotice.setText("Please select a member");
                tvBookingNotice.setVisibility(View.VISIBLE);
                return;
            }

            MemberDto selectedMember = memberList.get(selectedIndex);
            RegistrationRequestDto req = new RegistrationRequestDto(selectedMember.getMemberId(), sessionId);

            progressBar.setVisibility(View.VISIBLE);
            dialog.dismiss();

            viewModel.registerMember(req).observe(getViewLifecycleOwner(), resource -> {
                if (resource == null) return;
                if (resource.isSuccess()) {
                    progressBar.setVisibility(View.GONE);
                    ViewUtils.showSnackbar(getView(), "Member booked successfully!", false);
                    loadData();
                } else if (resource.isError()) {
                    progressBar.setVisibility(View.GONE);
                    ViewUtils.showSnackbar(getView(), resource.getMessage(), true);
                }
            });
        });

        dialog.show();
    }

    private void confirmCancelRegistration(RegistrationDto reg) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Cancel Booking")
                .setMessage("Are you sure you want to cancel the booking for " + reg.getMemberName() + "? The registration history will be preserved.")
                .setPositiveButton("Yes, Cancel", (dialog, which) -> {
                    progressBar.setVisibility(View.VISIBLE);
                    viewModel.cancelRegistration(reg.getRegistrationId()).observe(getViewLifecycleOwner(), resource -> {
                        if (resource == null) return;
                        if (resource.isSuccess()) {
                            progressBar.setVisibility(View.GONE);
                            ViewUtils.showSnackbar(getView(), "Booking cancelled", false);
                            loadData();
                        } else if (resource.isError()) {
                            progressBar.setVisibility(View.GONE);
                            ViewUtils.showSnackbar(getView(), resource.getMessage(), true);
                        }
                    });
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void showEditSessionDialog() {
        if (currentSession == null) return;

        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_session_form, null);

        TextView tvDialogTitle = dialogView.findViewById(R.id.tvDialogTitle);
        tvDialogTitle.setText("Edit Jogging Session");

        EditText etSessionName = dialogView.findViewById(R.id.etSessionName);
        Spinner spinnerFormCategory = dialogView.findViewById(R.id.spinnerFormCategory);
        EditText etDescription = dialogView.findViewById(R.id.etDescription);
        EditText etSessionDate = dialogView.findViewById(R.id.etSessionDate);
        EditText etStartTime = dialogView.findViewById(R.id.etStartTime);
        EditText etEndTime = dialogView.findViewById(R.id.etEndTime);
        EditText etLocation = dialogView.findViewById(R.id.etLocation);
        EditText etCapacity = dialogView.findViewById(R.id.etCapacity);
        Spinner spinnerFormStatus = dialogView.findViewById(R.id.spinnerFormStatus);

        // Pre-fill values
        etSessionName.setText(currentSession.getSessionName());
        etDescription.setText(currentSession.getDescription());
        etSessionDate.setText(currentSession.getSessionDate());
        etStartTime.setText(currentSession.getStartTime());
        etEndTime.setText(currentSession.getEndTime());
        etLocation.setText(currentSession.getLocation());
        etCapacity.setText(String.valueOf(currentSession.getCapacity()));

        // Setup Category spinner
        String[] categories = {
                "Beginner Jogging",
                "Intermediate Running",
                "Advanced Running",
                "Family Fun Runs",
                "Marathon Training"
        };
        ArrayAdapter<String> catAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, categories);
        catAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFormCategory.setAdapter(catAdapter);
        for (int i = 0; i < categories.length; i++) {
            if (categories[i].equalsIgnoreCase(currentSession.getCategory())) {
                spinnerFormCategory.setSelection(i);
                break;
            }
        }

        // Setup Status spinner
        String[] statuses = {"OPEN", "FULL", "CANCELLED", "COMPLETED"};
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, statuses);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFormStatus.setAdapter(statusAdapter);
        for (int i = 0; i < statuses.length; i++) {
            if (statuses[i].equalsIgnoreCase(currentSession.getStatus())) {
                spinnerFormStatus.setSelection(i);
                break;
            }
        }

        // Date & Time pickers
        Calendar calendar = Calendar.getInstance();
        etSessionDate.setOnClickListener(v -> {
            new DatePickerDialog(requireContext(), (view, year, month, dayOfMonth) -> {
                String dateStr = String.format(Locale.US, "%04d-%02d-%02d", year, month + 1, dayOfMonth);
                etSessionDate.setText(dateStr);
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        etStartTime.setOnClickListener(v -> {
            new TimePickerDialog(requireContext(), (view, hourOfDay, minute) -> {
                etStartTime.setText(String.format(Locale.US, "%02d:%02d", hourOfDay, minute));
            }, 7, 0, true).show();
        });

        etEndTime.setOnClickListener(v -> {
            new TimePickerDialog(requireContext(), (view, hourOfDay, minute) -> {
                etEndTime.setText(String.format(Locale.US, "%02d:%02d", hourOfDay, minute));
            }, 8, 30, true).show();
        });

        AlertDialog dialog = new MaterialAlertDialogBuilder(requireContext())
                .setView(dialogView)
                .setCancelable(true)
                .create();

        dialogView.findViewById(R.id.btnCancel).setOnClickListener(v -> dialog.dismiss());

        dialogView.findViewById(R.id.btnSave).setOnClickListener(v -> {
            String name = etSessionName.getText().toString().trim();
            String cat = (String) spinnerFormCategory.getSelectedItem();
            String desc = etDescription.getText().toString().trim();
            String date = etSessionDate.getText().toString().trim();
            String start = etStartTime.getText().toString().trim();
            String end = etEndTime.getText().toString().trim();
            String loc = etLocation.getText().toString().trim();
            String capStr = etCapacity.getText().toString().trim();
            String status = (String) spinnerFormStatus.getSelectedItem();

            if (name.isEmpty() || date.isEmpty() || start.isEmpty() || end.isEmpty() || loc.isEmpty() || capStr.isEmpty()) {
                ViewUtils.showSnackbar(dialogView, "Please fill in all required fields", true);
                return;
            }

            int cap;
            try {
                cap = Integer.parseInt(capStr);
            } catch (NumberFormatException e) {
                ViewUtils.showSnackbar(dialogView, "Invalid capacity number", true);
                return;
            }

            SessionRequestDto req = new SessionRequestDto(name, desc, cat, date, start, end, loc, cap, status);

            dialog.dismiss();
            progressBar.setVisibility(View.VISIBLE);

            viewModel.updateSession(sessionId, req).observe(getViewLifecycleOwner(), resource -> {
                if (resource == null) return;
                if (resource.isSuccess()) {
                    progressBar.setVisibility(View.GONE);
                    ViewUtils.showSnackbar(getView(), "Session updated successfully", false);
                    loadData();
                } else if (resource.isError()) {
                    progressBar.setVisibility(View.GONE);
                    ViewUtils.showSnackbar(getView(), resource.getMessage(), true);
                }
            });
        });

        dialog.show();
    }

    private void confirmDeleteSession() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Delete Session")
                .setMessage("Are you sure you want to delete this session? Sessions with active confirmed registrations cannot be deleted.")
                .setPositiveButton("Delete", (dialog, which) -> {
                    progressBar.setVisibility(View.VISIBLE);
                    viewModel.deleteSession(sessionId).observe(getViewLifecycleOwner(), resource -> {
                        if (resource == null) return;
                        if (resource.isSuccess()) {
                            progressBar.setVisibility(View.GONE);
                            ViewUtils.showSnackbar(getView(), "Session deleted", false);
                            Navigation.findNavController(requireView()).popBackStack();
                        } else if (resource.isError()) {
                            progressBar.setVisibility(View.GONE);
                            ViewUtils.showSnackbar(getView(), resource.getMessage(), true);
                        }
                    });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
