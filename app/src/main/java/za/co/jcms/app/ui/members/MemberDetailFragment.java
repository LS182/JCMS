package za.co.jcms.app.ui.members;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
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
import java.util.List;
import za.co.jcms.app.R;
import za.co.jcms.app.data.model.MemberDto;
import za.co.jcms.app.data.model.MemberRequestDto;
import za.co.jcms.app.data.model.RegistrationDto;
import za.co.jcms.app.util.DateUtils;
import za.co.jcms.app.util.ViewUtils;

public class MemberDetailFragment extends Fragment {

    private MemberDetailViewModel viewModel;
    private int memberId;
    private MemberDto currentMember;

    private SwipeRefreshLayout swipeRefresh;
    private ProgressBar progressBar;
    private TextView tvMemberInitials;
    private TextView tvMemberName;
    private TextView tvMemberId;
    private TextView tvMemberEmail;
    private TextView tvMemberContact;
    private TextView tvMemberJoined;
    private TextView tvEmergencyName;
    private TextView tvEmergencyContact;
    private TextView tvHistoryCount;
    private MaterialButton btnEditMember;
    private RecyclerView rvMemberHistory;
    private View layoutEmptyHistory;
    private MemberRegistrationHistoryAdapter historyAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_member_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            memberId = getArguments().getInt("memberId", -1);
        }

        viewModel = new ViewModelProvider(this).get(MemberDetailViewModel.class);

        initViews(view);
        setupRecyclerView();
        observeViewModel();

        loadData();
    }

    private void initViews(View view) {
        swipeRefresh = view.findViewById(R.id.swipeRefresh);
        progressBar = view.findViewById(R.id.progressBar);

        tvMemberInitials = view.findViewById(R.id.tvMemberInitials);
        tvMemberName = view.findViewById(R.id.tvMemberName);
        tvMemberId = view.findViewById(R.id.tvMemberId);
        tvMemberEmail = view.findViewById(R.id.tvMemberEmail);
        tvMemberContact = view.findViewById(R.id.tvMemberContact);
        tvMemberJoined = view.findViewById(R.id.tvMemberJoined);
        tvEmergencyName = view.findViewById(R.id.tvEmergencyName);
        tvEmergencyContact = view.findViewById(R.id.tvEmergencyContact);
        tvHistoryCount = view.findViewById(R.id.tvHistoryCount);
        btnEditMember = view.findViewById(R.id.btnEditMember);

        rvMemberHistory = view.findViewById(R.id.rvMemberHistory);
        layoutEmptyHistory = view.findViewById(R.id.layoutEmptyHistory);

        swipeRefresh.setColorSchemeResources(R.color.primary);
        swipeRefresh.setOnRefreshListener(this::loadData);

        btnEditMember.setOnClickListener(v -> showEditMemberDialog());
    }

    private void setupRecyclerView() {
        historyAdapter = new MemberRegistrationHistoryAdapter(item -> {
            if (item.getSessionId() != null) {
                Bundle args = new Bundle();
                args.putInt("sessionId", item.getSessionId());
                Navigation.findNavController(requireView()).navigate(R.id.action_memberDetail_to_sessionDetail, args);
            }
        });
        rvMemberHistory.setLayoutManager(new LinearLayoutManager(getContext()));
        rvMemberHistory.setAdapter(historyAdapter);
    }

    private void loadData() {
        if (memberId != -1) {
            viewModel.loadMember(memberId);
            viewModel.loadHistory(memberId);
        }
    }

    private void observeViewModel() {
        viewModel.getMemberDetail().observe(getViewLifecycleOwner(), resource -> {
            if (resource == null) return;
            swipeRefresh.setRefreshing(false);

            if (resource.isLoading()) {
                progressBar.setVisibility(View.VISIBLE);
            } else if (resource.isSuccess() && resource.getData() != null) {
                progressBar.setVisibility(View.GONE);
                currentMember = resource.getData();
                bindMemberData(currentMember);
            } else if (resource.isError()) {
                progressBar.setVisibility(View.GONE);
                ViewUtils.showSnackbar(getView(), resource.getMessage(), true);
            }
        });

        viewModel.getMemberHistory().observe(getViewLifecycleOwner(), resource -> {
            if (resource == null) return;

            if (resource.isSuccess() && resource.getData() != null) {
                List<RegistrationDto> history = resource.getData();
                historyAdapter.submitList(history);
                tvHistoryCount.setText(history.size() + " registrations");
                if (history.isEmpty()) {
                    layoutEmptyHistory.setVisibility(View.VISIBLE);
                    rvMemberHistory.setVisibility(View.GONE);
                } else {
                    layoutEmptyHistory.setVisibility(View.GONE);
                    rvMemberHistory.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void bindMemberData(MemberDto member) {
        tvMemberInitials.setText(member.getInitials());
        tvMemberName.setText(member.getFullName());
        tvMemberId.setText("Member #" + member.getMemberId());
        tvMemberEmail.setText(member.getEmail());
        tvMemberContact.setText(member.getContactNumber());
        tvMemberJoined.setText("Member since " + DateUtils.formatDisplayDateTime(member.getCreatedAt()));

        tvEmergencyName.setText(member.getEmergencyContactName());
        tvEmergencyContact.setText(member.getEmergencyContactNumber());
    }

    private void showEditMemberDialog() {
        if (currentMember == null) return;

        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_member_form, null);

        TextView tvTitle = dialogView.findViewById(R.id.tvMemberDialogTitle);
        tvTitle.setText("Edit Member Details");

        EditText etFullName = dialogView.findViewById(R.id.etFullName);
        EditText etEmail = dialogView.findViewById(R.id.etEmail);
        EditText etContact = dialogView.findViewById(R.id.etContactNumber);
        EditText etEmergName = dialogView.findViewById(R.id.etEmergencyContactName);
        EditText etEmergPhone = dialogView.findViewById(R.id.etEmergencyContactNumber);

        // Pre-fill
        etFullName.setText(currentMember.getFullName());
        etEmail.setText(currentMember.getEmail());
        etContact.setText(currentMember.getContactNumber());
        etEmergName.setText(currentMember.getEmergencyContactName());
        etEmergPhone.setText(currentMember.getEmergencyContactNumber());

        AlertDialog dialog = new MaterialAlertDialogBuilder(requireContext())
                .setView(dialogView)
                .setCancelable(true)
                .create();

        dialogView.findViewById(R.id.btnCancelMember).setOnClickListener(v -> dialog.dismiss());

        dialogView.findViewById(R.id.btnSaveMember).setOnClickListener(v -> {
            String fullName = etFullName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String contact = etContact.getText().toString().trim();
            String emergName = etEmergName.getText().toString().trim();
            String emergPhone = etEmergPhone.getText().toString().trim();

            if (fullName.isEmpty() || email.isEmpty() || contact.isEmpty() || emergName.isEmpty() || emergPhone.isEmpty()) {
                ViewUtils.showSnackbar(dialogView, "Please fill in all fields", true);
                return;
            }

            MemberRequestDto request = new MemberRequestDto(fullName, email, contact, emergName, emergPhone);

            dialog.dismiss();
            progressBar.setVisibility(View.VISIBLE);

            viewModel.updateMember(memberId, request).observe(getViewLifecycleOwner(), resource -> {
                if (resource == null) return;
                if (resource.isSuccess()) {
                    progressBar.setVisibility(View.GONE);
                    ViewUtils.showSnackbar(getView(), "Member updated successfully", false);
                    loadData();
                } else if (resource.isError()) {
                    progressBar.setVisibility(View.GONE);
                    ViewUtils.showSnackbar(getView(), resource.getMessage(), true);
                }
            });
        });

        dialog.show();
    }
}
