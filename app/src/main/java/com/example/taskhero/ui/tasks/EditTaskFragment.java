package com.example.taskhero.ui.tasks;

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
import com.example.taskhero.databinding.FragmentEditTaskBinding;
import com.example.taskhero.ui.base.BaseTaskFormFragment;
import com.example.taskhero.util.NotificationScheduler;
import com.example.taskhero.util.UIUtils;

import java.util.Objects;

public class EditTaskFragment extends BaseTaskFormFragment {

    private FragmentEditTaskBinding binding;
    private TaskViewModel taskViewModel;
    private Task currentTask;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentEditTaskBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        taskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);

        if (getArguments() != null) {
            int taskId = getArguments().getInt("TASK_ID", -1);
            if (taskId != -1) {
                taskViewModel.getTaskById(taskId).observe(getViewLifecycleOwner(), task -> {
                    if (task != null) {
                        currentTask = task;
                        populateUI();
                    }
                });
            }
        }

        setupClickListeners();
    }

    @Override
    protected TextView getDateTextView() {
        return binding.textViewSelectDate;
    }

    @Override
    protected TextView getTimeTextView() {
        return binding.textViewSelectTime;
    }

    private void populateUI() {
        binding.editTextTaskTitle.setText(currentTask.getTitle());
        binding.editTextTaskDescription.setText(currentTask.getDescription());

        if (currentTask.getDueDate() > 0) {
            calendar.setTimeInMillis(currentTask.getDueDate());
            updateDateLabel();
            updateTimeLabel();
        }

        switch (currentTask.getDifficulty()) {
            case 0:
                binding.radioGroupDifficulty.check(R.id.radio_button_easy);
                break;
            case 2:
                binding.radioGroupDifficulty.check(R.id.radio_button_hard);
                break;
            default:
                binding.radioGroupDifficulty.check(R.id.radio_button_medium);
                break;
        }
    }

    private void setupClickListeners() {
        binding.textViewSelectDate.setOnClickListener(v -> showDatePickerDialog());
        binding.textViewSelectTime.setOnClickListener(v -> showTimePickerDialog());
        binding.buttonSaveTask.setOnClickListener(v -> saveTaskChanges());
    }

    private void saveTaskChanges() {
        if (currentTask != null) {
            String newTitle = Objects.requireNonNull(binding.editTextTaskTitle.getText()).toString().trim();

            if (newTitle.isEmpty()) {
                binding.textInputLayoutTitle.setError(getString(R.string.task_form_error_title_required));
                return;
            } else {
                binding.textInputLayoutTitle.setError(null);
            }

            String newDescription = Objects.requireNonNull(binding.editTextTaskDescription.getText()).toString().trim();

            currentTask.setTitle(newTitle);
            currentTask.setDescription(newDescription);
            currentTask.setDueDate(calendar.getTimeInMillis());

            int checkedRadioButtonId = binding.radioGroupDifficulty.getCheckedRadioButtonId();
            int selectedDifficulty = (checkedRadioButtonId == R.id.radio_button_easy) ? 0 :
                                     (checkedRadioButtonId == R.id.radio_button_hard) ? 2 : 1;
            currentTask.setDifficulty(selectedDifficulty);

            NotificationScheduler.cancelTaskReminder(requireContext(), currentTask);
            NotificationScheduler.scheduleTaskReminder(requireContext(), currentTask);

            taskViewModel.updateTask(currentTask);

            UIUtils.showSuccessSnackbar(requireView(), getString(R.string.task_form_snackbar_edit_success));

            requireActivity().getSupportFragmentManager().popBackStack();
        }
    }

}