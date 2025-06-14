package com.example.taskhero.ui.main;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.taskhero.R;
import com.example.taskhero.data.model.Task;
import com.example.taskhero.databinding.FragmentEditTaskBinding;
import com.example.taskhero.util.UIUtils;
import com.example.taskhero.viewmodel.TaskViewModel;

import java.util.Calendar;
import java.util.Objects;

@SuppressWarnings("unused")
public class EditTaskFragment extends Fragment {

    private FragmentEditTaskBinding binding;
    private TaskViewModel taskViewModel;
    private Task currentTask;
    private final Calendar calendar = Calendar.getInstance();

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

    private void populateUI() {
        binding.editTextTaskTitle.setText(currentTask.getTitle());
        binding.editTextTaskDescription.setText(currentTask.getDescription());

        if (currentTask.getDueDate() > 0) {
            calendar.setTimeInMillis(currentTask.getDueDate());
            updateDateLabel();
            updateTimeLabel();
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
                binding.textInputLayoutTitle.setError(getString(R.string.error_task_title_required));
                return;
            } else {
                binding.textInputLayoutTitle.setError(null);
            }

            String newDescription = Objects.requireNonNull(binding.editTextTaskDescription.getText()).toString().trim();

            currentTask.setTitle(newTitle);
            currentTask.setDescription(newDescription);
            currentTask.setDueDate(calendar.getTimeInMillis());

            taskViewModel.updateTask(currentTask);

            UIUtils.showSuccessSnackbar(requireView(), getString(R.string.success_task_updated));

            requireActivity().getSupportFragmentManager().popBackStack();
        }
    }

    private void showDatePickerDialog() {
        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateDateLabel();
        };

        new DatePickerDialog(requireContext(), dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void showTimePickerDialog() {
        TimePickerDialog.OnTimeSetListener timeSetListener = (view, hourOfDay, minute) -> {
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set(Calendar.MINUTE, minute);
            updateTimeLabel();
        };

        new TimePickerDialog(getContext(), timeSetListener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
    }

    private void updateDateLabel() {
        String dateString = android.text.format.DateFormat.getMediumDateFormat(getContext()).format(calendar.getTime());
        binding.textViewSelectDate.setText(dateString);
    }

    private void updateTimeLabel() {
        String timeString = android.text.format.DateFormat.getTimeFormat(getContext()).format(calendar.getTime());
        binding.textViewSelectTime.setText(timeString);
    }
}