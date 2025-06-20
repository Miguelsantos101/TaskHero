package com.example.taskhero.ui.tasks;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

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
import com.example.taskhero.util.NotificationScheduler;
import com.example.taskhero.util.UIUtils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Calendar;

public class TaskListFragment extends Fragment implements TaskAdapter.OnTaskInteractionListener {

    private FragmentTaskListBinding binding;
    private TaskViewModel taskViewModel;
    private TaskAdapter adapter;
    private User currentUser;
    private SoundPool soundPool;
    private int taskCompleteSoundId;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentTaskListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupRecyclerView();
        setupSoundPool();

        taskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);

        SharedPreferences prefs = requireActivity().getSharedPreferences("TaskHeroPrefs", Context.MODE_PRIVATE);
        int userId = prefs.getInt("LOGGED_IN_USER_ID", -1);

        if (userId != -1) {
            taskViewModel.getTasksForUser(userId).observe(getViewLifecycleOwner(), tasks -> {
                if (tasks == null) return;

                binding.textViewEmpty.setVisibility(tasks.isEmpty() ? View.VISIBLE : View.GONE);

                if (adapter.getItemCount() == 0 && !tasks.isEmpty()) {
                    final LayoutAnimationController controller =
                            AnimationUtils.loadLayoutAnimation(requireContext(), R.anim.layout_animation);

                    binding.recyclerViewTasks.setLayoutAnimation(controller);
                    adapter.submitList(tasks);
                    binding.recyclerViewTasks.scheduleLayoutAnimation();
                } else {
                    adapter.submitList(tasks);
                }
            });

            taskViewModel.getUserById(userId).observe(getViewLifecycleOwner(), user -> this.currentUser = user);
        }

        binding.fabAddTask.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).loadFragment(new AddTaskFragment(), true);
            }
        });
    }

    private void setupRecyclerView() {
        binding.recyclerViewTasks.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new TaskAdapter(this);
        binding.recyclerViewTasks.setAdapter(adapter);
    }

    private void setupSoundPool() {
        SoundPool.Builder builder = new SoundPool.Builder();
        builder.setMaxStreams(1);
        soundPool = builder.build();

        taskCompleteSoundId = soundPool.load(getContext(), R.raw.task_complete_sound, 1);
    }

    @Override
    public void onTaskClicked(Task task) {
        EditTaskFragment editTaskFragment = new EditTaskFragment();

        Bundle args = new Bundle();
        args.putInt("TASK_ID", task.getId());
        editTaskFragment.setArguments(args);

        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).loadFragment(editTaskFragment, true);
        }
    }

    @Override
    public void onTaskCompleted(Task task, boolean isCompleted, int position) {
        task.setCompleted(isCompleted);
        taskViewModel.updateTask(task);

        if (isCompleted && currentUser != null) {
            int basePoints;
            switch (task.getDifficulty()) {
                case 0: basePoints = 5; break;
                case 2: basePoints = 20; break;
                default: basePoints = 10; break;
            }

            int punctualityBonus = (task.getDueDate() > 0 && System.currentTimeMillis() <= task.getDueDate()) ? 5 : 0;

            long today = getStartOfDay(System.currentTimeMillis());
            long lastDay = getStartOfDay(currentUser.getLastCompletionDate());

            if (today > lastDay) {
                if (today - lastDay == (24 * 60 * 60 * 1000)) {
                    currentUser.dailyStreak++;
                } else {
                    currentUser.dailyStreak = 1;
                }
                currentUser.setLastCompletionDate(System.currentTimeMillis());
            }

            double streakMultiplier = 1.0 + (Math.min(currentUser.dailyStreak - 1, 10) * 0.1);
            int finalPoints = (int) ((basePoints + punctualityBonus) * streakMultiplier);
            currentUser.setScore(currentUser.getScore() + finalPoints);
            taskViewModel.updateUser(currentUser);

            StringBuilder feedbackMessageBuilder = new StringBuilder();
            String finalPointsMessage = getResources().getQuantityString(
                    R.plurals.task_list_snackbar_points_gain,
                    finalPoints,
                    finalPoints
            );

            feedbackMessageBuilder.append(finalPointsMessage);

            if (currentUser.dailyStreak > 1) {
                String streakMessage = getResources().getQuantityString(
                        R.plurals.task_list_snackbar_streak_bonus_message,
                        currentUser.dailyStreak,
                        currentUser.dailyStreak
                );
                feedbackMessageBuilder.append(" ").append(streakMessage);
            }

            String feedbackMessage = feedbackMessageBuilder.toString();
            UIUtils.showSuccessSnackbar(requireView(), feedbackMessage);

            NotificationScheduler.cancelTaskReminder(requireContext(), task);
            if (soundPool != null) {
                soundPool.play(taskCompleteSoundId, 1.0f, 1.0f, 1, 0, 1.0f);
            }

        } else {
            if (task.getDueDate() > System.currentTimeMillis()) {
                NotificationScheduler.scheduleTaskReminder(requireContext(), task);
            }
        }

        adapter.notifyItemChanged(position);
    }

    @Override
    public void onTaskDeleted(Task task) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext()).
                setTitle(R.string.dialog_confirm_delete_title).
                setMessage(R.string.dialog_confirm_delete_message)
                .setNegativeButton(R.string.common_action_cancel, (dialog, which) -> dialog.dismiss()).
                setPositiveButton(R.string.common_action_delete, (dialog, which) -> {
                    taskViewModel.deleteTask(task);
                    NotificationScheduler.cancelTaskReminder(requireContext(), task);
                    UIUtils.showSuccessSnackbar(requireView(), getString(R.string.task_list_snackbar_task_deleted));
                });

        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(requireContext(), R.color.status_error));
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(requireContext(), R.color.status_neutral));
    }

    private long getStartOfDay(long timestamp) {
        if (timestamp == 0) return 0;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;

        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
        }
    }
}