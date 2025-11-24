package com.example.taskhero.ui.tasks;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.example.taskhero.R;
import com.example.taskhero.data.model.Task;
import com.example.taskhero.databinding.FragmentAddTaskBinding;
import com.example.taskhero.ui.base.BaseTaskFormFragment;
import com.example.taskhero.util.NotificationScheduler;
import com.example.taskhero.util.UIUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class AddTaskFragment extends BaseTaskFormFragment {

    private FragmentAddTaskBinding binding;
    private TaskViewModel taskViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAddTaskBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        taskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);
        setupClickListeners();
        setupObservers();
    }

    @Override
    protected TextView getDateTextView() {
        return binding.textViewSelectDate;
    }

    @Override
    protected TextView getTimeTextView() {
        return binding.textViewSelectTime;
    }

    private void setupClickListeners() {
        binding.textViewSelectDate.setOnClickListener(v -> showDatePickerDialog());
        binding.textViewSelectTime.setOnClickListener(v -> showTimePickerDialog());
        binding.buttonSaveTask.setOnClickListener(v -> saveTask());
    }

    private void setupObservers() {
        taskViewModel.getNewlyInsertedTaskId().observe(getViewLifecycleOwner(), newTaskId -> {
            if (newTaskId != null && newTaskId > 0) {
                taskViewModel.getTaskById(newTaskId.intValue()).observe(getViewLifecycleOwner(), task -> {
                    if (task != null) {
                        NotificationScheduler.scheduleTaskReminder(requireContext(), task);
                        UIUtils.showSuccessSnackbar(requireView(), getString(R.string.task_form_snackbar_add_success));
                        requireActivity().getSupportFragmentManager().popBackStack();
                        taskViewModel.onNewTaskHandled();
                        taskViewModel.getTaskById(newTaskId.intValue()).removeObservers(getViewLifecycleOwner());
                    }
                });
            } else if (newTaskId != null) {
                UIUtils.showErrorSnackbar(requireView(), getString(R.string.task_form_snackbar_add_error));
            }
        });
    }

    private void saveTask() {
        if (!validateInput()) {
            return;
        }

        int userId = validateUser();
        if (userId == -1) return;

        createAndPersistTask(userId);
    }

    private boolean validateInput() {
        String title = Objects.requireNonNull(binding.editTextTaskTitle.getText()).toString().trim();
        if (title.isEmpty()) {
            binding.textInputLayoutTitle.setError(getString(R.string.task_form_error_title_required));
            return false;
        }
        binding.textInputLayoutTitle.setError(null);
        return true;
    }

    private int validateUser() {
        int userId = getCurrentUserId();
        if (userId == -1) {
            UIUtils.showErrorSnackbar(requireView(), getString(R.string.common_error_user_not_found));
            return -1;
        }
        return userId;
    }

    private int getCurrentUserId() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("TaskHeroPrefs", Context.MODE_PRIVATE);
        return prefs.getInt("LOGGED_IN_USER_ID", -1);
    }

    private void createAndPersistTask(int userId) {
        String title = Objects.requireNonNull(binding.editTextTaskTitle.getText()).toString().trim();
        String description = Objects.requireNonNull(binding.editTextTaskDescription.getText()).toString().trim();

        setDefaultDateIfNotSet();

        int checkedRadioButtonId = binding.radioGroupDifficulty.getCheckedRadioButtonId();
        int selectedDifficulty = (checkedRadioButtonId == R.id.radio_button_easy) ? 0 :
                (checkedRadioButtonId == R.id.radio_button_hard) ? 2 : 1;

        Task task = new Task(userId, title, description, calendar.getTimeInMillis(), selectedDifficulty);
        taskViewModel.insertTask(task);
    }

    private void setDefaultDateIfNotSet() {
        boolean isDateSet = !binding.textViewSelectDate.getText().toString().equals(getString(R.string.task_form_label_due_date));
        if (!isDateSet) {
            calendar.setTime(new Date());
            calendar.add(Calendar.MINUTE, 1);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}