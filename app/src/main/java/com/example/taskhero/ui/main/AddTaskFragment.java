package com.example.taskhero.ui.main;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
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
import com.example.taskhero.databinding.FragmentAddTaskBinding;
import com.example.taskhero.util.NotificationScheduler;
import com.example.taskhero.util.UIUtils;
import com.example.taskhero.viewmodel.TaskViewModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

@SuppressWarnings("unused")
public class AddTaskFragment extends Fragment {

    private FragmentAddTaskBinding binding;
    private TaskViewModel taskViewModel;
    private final Calendar calendar = Calendar.getInstance();

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

    private void setupClickListeners() {
        binding.textViewSelectDate.setOnClickListener(v -> showDatePickerDialog());
        binding.textViewSelectTime.setOnClickListener(v -> showTimePickerDialog());
        binding.buttonSaveTask.setOnClickListener(v -> saveTask());
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
        String format = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        binding.textViewSelectDate.setText(sdf.format(calendar.getTime()));
    }

    private void updateTimeLabel() {
        String format = "HH:mm";
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        binding.textViewSelectTime.setText(sdf.format(calendar.getTime()));
    }

    private void setupObservers() {
        taskViewModel.getNewlyInsertedTaskId().observe(getViewLifecycleOwner(), newTaskId -> {
            if (newTaskId != null && newTaskId > 0) {
                taskViewModel.getTaskById(newTaskId.intValue()).observe(getViewLifecycleOwner(), task -> {
                    if (task != null) {
                        NotificationScheduler.scheduleTaskReminder(requireContext(), task);
                        UIUtils.showSuccessSnackbar(requireView(), getString(R.string.success_task_saved));
                        requireActivity().getSupportFragmentManager().popBackStack();
                        taskViewModel.onNewTaskHandled();
                        taskViewModel.getTaskById(newTaskId.intValue()).removeObservers(getViewLifecycleOwner());
                    }
                });
            } else if (newTaskId != null) {
                UIUtils.showErrorSnackbar(requireView(), getString(R.string.error_saving_task));
            }
        });
    }

    private void saveTask() {
        String title = Objects.requireNonNull(binding.editTextTaskTitle.getText()).toString().trim();
        String description = Objects.requireNonNull(binding.editTextTaskDescription.getText()).toString().trim();

        if (title.isEmpty()) {
            binding.textInputLayoutTitle.setError(getString(R.string.error_task_title_required));
            return;
        } else {
            binding.textInputLayoutTitle.setError(null);
        }

        SharedPreferences prefs = requireActivity().getSharedPreferences("TaskHeroPrefs", Context.MODE_PRIVATE);
        int userId = prefs.getInt("LOGGED_IN_USER_ID", -1);

        if (userId == -1) {
            UIUtils.showErrorSnackbar(requireView(), getString(R.string.error_user_not_found));
            return;
        }

        Task task = new Task(userId, title, description, calendar.getTimeInMillis());
        taskViewModel.insertTask(task);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}