package com.example.taskhero.ui.tasks;

import android.content.Context;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskhero.R;
import com.example.taskhero.data.model.Task;
import com.example.taskhero.databinding.ItemTaskBinding;

import java.util.Date;

public class TaskAdapter extends ListAdapter<Task, TaskAdapter.TaskViewHolder> {

    private final OnTaskInteractionListener listener;

    public TaskAdapter(OnTaskInteractionListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    private static final DiffUtil.ItemCallback<Task> DIFF_CALLBACK = new DiffUtil.ItemCallback<>() {
        @Override
        public boolean areItemsTheSame(@NonNull Task oldItem, @NonNull Task newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Task oldItem, @NonNull Task newItem) {
            return oldItem.getTitle().equals(newItem.getTitle()) && oldItem.isCompleted() == newItem.isCompleted();
        }
    };

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTaskBinding binding = ItemTaskBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new TaskViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task currentTask = getItem(position);
        holder.bind(currentTask, listener);
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        private final ItemTaskBinding binding;

        public TaskViewHolder(ItemTaskBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(final Task task, final OnTaskInteractionListener listener) {
            binding.textViewTaskTitle.setText(task.getTitle());
            binding.checkboxTaskCompleted.setChecked(task.isCompleted());

            Context context = itemView.getContext();

            int nightModeFlags = context.getResources().getConfiguration().uiMode & android.content.res.Configuration.UI_MODE_NIGHT_MASK;

            if (!task.isCompleted() && task.getDueDate() > 0 && task.getDueDate() < System.currentTimeMillis()) {
                int errorColor = ContextCompat.getColor(context, R.color.status_error);
                binding.textViewTaskTitle.setTextColor(errorColor);
                binding.textViewDueDate.setTextColor(errorColor);
            } else {
                int defaultTextColor;
                if (nightModeFlags == android.content.res.Configuration.UI_MODE_NIGHT_YES) {
                    defaultTextColor = ContextCompat.getColor(context, R.color.text_primary_dark);
                } else {
                    defaultTextColor = ContextCompat.getColor(context, R.color.text_primary_light);
                }
                binding.textViewTaskTitle.setTextColor(defaultTextColor);
                binding.textViewDueDate.setTextColor(ContextCompat.getColor(context, android.R.color.darker_gray));
            }

            itemView.setOnClickListener(v -> listener.onTaskClicked(task));

            if (task.isCompleted()) {
                binding.textViewTaskTitle.setPaintFlags(binding.textViewTaskTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                binding.textViewTaskTitle.setPaintFlags(binding.textViewTaskTitle.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            }

            if (task.getDueDate() > 0) {
                Date date = new Date(task.getDueDate());

                String dateString = android.text.format.DateFormat.getMediumDateFormat(context).format(date);
                String timeString = android.text.format.DateFormat.getTimeFormat(context).format(date);

                String fullDateTimeString = context.getString(R.string.task_list_item_due_date_format, dateString, timeString);

                binding.textViewDueDate.setText(fullDateTimeString);
                binding.textViewDueDate.setVisibility(View.VISIBLE);
            } else {
                binding.textViewDueDate.setVisibility(View.GONE);
            }

            binding.checkboxTaskCompleted.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (buttonView.isPressed()) {
                    buttonView.setEnabled(false);
                    listener.onTaskCompleted(task, isChecked, getAdapterPosition());
                    new Handler(Looper.getMainLooper()).postDelayed(() -> buttonView.setEnabled(true), 1000);
                }
            });

            binding.buttonDeleteTask.setOnClickListener(v -> listener.onTaskDeleted(task));
        }
    }

    public interface OnTaskInteractionListener {
        void onTaskCompleted(Task task, boolean isCompleted, int position);

        void onTaskDeleted(Task task);

        void onTaskClicked(Task task);
    }
}