package edu.cnm.deepdive.teamassignmentsandroid.controller;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import edu.cnm.deepdive.teamassignmentsandroid.adapter.TaskAdapter;
import edu.cnm.deepdive.teamassignmentsandroid.databinding.FragmentTasksBinding;
import edu.cnm.deepdive.teamassignmentsandroid.model.pojo.Task;
import edu.cnm.deepdive.teamassignmentsandroid.viewmodel.MainViewModel;

/**
 * This fragment contains methods to get and set tasks to a group.  It extends Bottom sheet dialog fragment
 * to populate the bottom pop up.
 */
public class  TasksFragment extends BottomSheetDialogFragment {

  /**
   * Gets the id of a group from the database.
   */
  public static final String GROUP_ID_KEY = "group_id";
  private MainViewModel viewModel;
  private FragmentTasksBinding binding;
  private long groupId;
  private LiveData<Task> taskLiveData;

  /**
   * Task fragment is initialized.
   * @param savedInstanceState of Bundle
   */
  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    //TODO read any arguments passed into fragment.
    if (getArguments() != null) {
      groupId = getArguments().getLong(GROUP_ID_KEY);
      Log.d(getClass().getSimpleName(), String.valueOf(groupId));
    }
  }

//bottom fragment doesn't use all the stuff a popup dialog fragment needs, do not need onCreateDialog.

  /**
   * Instantiates an XML layout.
   * @param inflater to inflate the layout
   * @param container implements the view group
   * @param savedInstanceState extends the base bundle
   * @return a rectangular area on the screen and is responsible for drawing and event handling
   */
  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    binding = FragmentTasksBinding.inflate(inflater, container, false);
    return binding.getRoot();
  }

  /**
   * CAlled immediately after onViewCreate.
   * @param view expands the layout and widgets
   * @param savedInstanceState extends teh base bundle
   */
  @Override
  public void onViewCreated(@NonNull View view,
      @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    viewModel = new ViewModelProvider(this).get(MainViewModel.class);
    viewModel.getTasks().observe(getViewLifecycleOwner(), (tasks) -> {
      if (!tasks.isEmpty()) {
        binding.noTasks.setVisibility(View.GONE);
        binding.tasks.setVisibility(View.VISIBLE);
        TaskAdapter adapter = new TaskAdapter(tasks, getContext(), (v, taskId) -> {
          Log.d(getClass().getSimpleName(), String.valueOf(taskId));
        });
        binding.tasks.setAdapter(adapter);
      } else {
        binding.noTasks.setVisibility(View.VISIBLE);
        binding.tasks.setVisibility(View.GONE);
      }
    });
    viewModel.loadTasks(groupId);
  }
}
