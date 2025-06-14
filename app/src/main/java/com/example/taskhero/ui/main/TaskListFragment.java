package com.example.taskhero.ui.main;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.taskhero.R;
import com.example.taskhero.data.model.Task;
import com.example.taskhero.data.model.User;
import com.example.taskhero.databinding.FragmentTaskListBinding;
import com.example.taskhero.util.UIUtils;
import com.example.taskhero.viewmodel.TaskViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

@SuppressWarnings("unused")
public class TaskListFragment extends Fragment implements TaskAdapter.OnTaskInteractionListener {

    private FragmentTaskListBinding binding;
    private TaskViewModel taskViewModel;
    private TaskAdapter adapter;
    private User currentUser;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentTaskListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupRecyclerView();

        taskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);

        SharedPreferences prefs = requireActivity().getSharedPreferences("TaskHeroPrefs", Context.MODE_PRIVATE);
        int userId = prefs.getInt("LOGGED_IN_USER_ID", -1);

        if (userId != -1) {
            taskViewModel.getTasksForUser(userId).observe(getViewLifecycleOwner(), tasks -> {
                adapter.submitList(tasks);
                binding.textViewEmpty.setVisibility(tasks.isEmpty() ? View.VISIBLE : View.GONE);
            });

            taskViewModel.getUserById(userId).observe(getViewLifecycleOwner(), user -> this.currentUser = user);
        }

        binding.fabAddTask.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).loadFragment(new AddTaskFragment());
            }
        });
    }

    private void setupRecyclerView() {
        adapter = new TaskAdapter(this);
        binding.recyclerViewTasks.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewTasks.setAdapter(adapter);
    }

    @Override
    public void onTaskClicked(Task task) {
        EditTaskFragment editTaskFragment = new EditTaskFragment();

        Bundle args = new Bundle();
        args.putInt("TASK_ID", task.getId());
        editTaskFragment.setArguments(args);

        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).loadFragment(editTaskFragment);
        }

    }

    @Override
    public void onTaskCompleted(Task task, boolean isCompleted, int position) {
        task.setCompleted(isCompleted);
        taskViewModel.updateTask(task);

        if (isCompleted && currentUser != null) {
            int pointsToAdd = 10;
            int newScore = currentUser.getScore() + pointsToAdd;
            currentUser.setScore(newScore);

            taskViewModel.updateUser(currentUser);

            UIUtils.showSuccessSnackbar(requireView(), "+" + pointsToAdd + " " + getString(R.string.info_points_gain));
        }

        adapter.notifyItemChanged(position);
    }

    @Override
    public void onTaskDeleted(Task task) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext()).
                setTitle(R.string.delete_dialog_title).
                setMessage(R.string.delete_dialog_message)
                .setNegativeButton(R.string.action_cancel, (dialog, which) -> dialog.dismiss()).
                setPositiveButton(R.string.action_delete, (dialog, which) -> {
            taskViewModel.deleteTask(task);
            UIUtils.showSuccessSnackbar(requireView(), getString(R.string.success_task_deleted));
        });

        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(requireContext(), R.color.status_error));
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(requireContext(), R.color.status_neutral));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}