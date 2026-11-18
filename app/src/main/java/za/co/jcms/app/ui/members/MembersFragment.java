package za.co.jcms.app.ui.members;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import za.co.jcms.app.R;
import za.co.jcms.app.data.model.MemberRequestDto;
import za.co.jcms.app.util.ViewUtils;

public class MembersFragment extends Fragment {

    private MembersViewModel viewModel;
    private SwipeRefreshLayout swipeRefresh;
    private ProgressBar progressBar;
    private View layoutEmpty;
    private TextView tvMembersCount;
    private EditText etSearchMembers;
    private RecyclerView rvMembers;
    private ExtendedFloatingActionButton fabAddMember;
    private MembersAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_members, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(MembersViewModel.class);

        initViews(view);
        setupRecyclerView();
        setupSearch();
        observeViewModel();
    }

    private void initViews(View view) {
        swipeRefresh = view.findViewById(R.id.swipeRefresh);
        progressBar = view.findViewById(R.id.progressBar);
        layoutEmpty = view.findViewById(R.id.layoutEmpty);
        tvMembersCount = view.findViewById(R.id.tvMembersCount);
        etSearchMembers = view.findViewById(R.id.etSearchMembers);
        rvMembers = view.findViewById(R.id.rvMembers);
        fabAddMember = view.findViewById(R.id.fabAddMember);

        swipeRefresh.setColorSchemeResources(R.color.primary);
        swipeRefresh.setOnRefreshListener(() -> viewModel.loadMembers());

        fabAddMember.setOnClickListener(v -> showAddMemberDialog());
    }

    private void setupRecyclerView() {
        adapter = new MembersAdapter(member -> {
            if (member.getMemberId() != null) {
                Bundle args = new Bundle();
                args.putInt("memberId", member.getMemberId());
                Navigation.findNavController(requireView()).navigate(R.id.action_members_to_memberDetail, args);
            }
        });
        rvMembers.setLayoutManager(new LinearLayoutManager(getContext()));
        rvMembers.setAdapter(adapter);
    }

    private void setupSearch() {
        etSearchMembers.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.setSearchQuery(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void observeViewModel() {
        viewModel.getRawMembers().observe(getViewLifecycleOwner(), resource -> {
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

        viewModel.getFilteredMembers().observe(getViewLifecycleOwner(), list -> {
            adapter.submitList(list);
            if (list == null || list.isEmpty()) {
                layoutEmpty.setVisibility(View.VISIBLE);
                tvMembersCount.setText("No club members found");
            } else {
                layoutEmpty.setVisibility(View.GONE);
                tvMembersCount.setText("Showing " + list.size() + " members");
            }
        });
    }

    private void showAddMemberDialog() {
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_member_form, null);

        TextView tvTitle = dialogView.findViewById(R.id.tvMemberDialogTitle);
        tvTitle.setText("Register New Member");

        EditText etFullName = dialogView.findViewById(R.id.etFullName);
        EditText etEmail = dialogView.findViewById(R.id.etEmail);
        EditText etContact = dialogView.findViewById(R.id.etContactNumber);
        EditText etEmergName = dialogView.findViewById(R.id.etEmergencyContactName);
        EditText etEmergPhone = dialogView.findViewById(R.id.etEmergencyContactNumber);

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
                ViewUtils.showSnackbar(dialogView, "Please fill in all member details", true);
                return;
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                ViewUtils.showSnackbar(dialogView, "Please enter a valid email address", true);
                return;
            }

            MemberRequestDto request = new MemberRequestDto(fullName, email, contact, emergName, emergPhone);

            dialog.dismiss();
            progressBar.setVisibility(View.VISIBLE);

            viewModel.registerMember(request).observe(getViewLifecycleOwner(), resource -> {
                if (resource == null) return;
                if (resource.isSuccess()) {
                    progressBar.setVisibility(View.GONE);
                    ViewUtils.showSnackbar(getView(), "Member registered successfully!", false);
                    viewModel.loadMembers();
                } else if (resource.isError()) {
                    progressBar.setVisibility(View.GONE);
                    ViewUtils.showSnackbar(getView(), resource.getMessage(), true);
                }
            });
        });

        dialog.show();
    }
}
