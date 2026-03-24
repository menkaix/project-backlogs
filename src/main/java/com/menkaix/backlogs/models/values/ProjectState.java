package com.menkaix.backlogs.models.values;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import com.menkaix.backlogs.models.entities.Task;

public enum ProjectState {

    ACTIVE,
    STANDBY,
    CLOSED;

    private static final Set<TaskStatus> ACTIVE_STATUSES = EnumSet.of(
            TaskStatus.NEW,
            TaskStatus.TODO,
            TaskStatus.IN_PROGRESS,
            TaskStatus.TO_SPEC,
            TaskStatus.SPECIFYING,
            TaskStatus.RND,
            TaskStatus.TO_STUDY,
            TaskStatus.TO_TEST,
            TaskStatus.TESTING
    );

    private static final Set<TaskStatus> TERMINAL_STATUSES = EnumSet.of(
            TaskStatus.DONE,
            TaskStatus.CANCELED,
            TaskStatus.UNKNOWN
    );

    /**
     * Calcule le statut d'un projet à partir de ses tâches.
     * <ul>
     *   <li>ACTIVE  : au moins une tâche en cours ou à faire</li>
     *   <li>STANDBY : aucune tâche active, mais au moins une tâche PENDING</li>
     *   <li>CLOSED  : uniquement des tâches terminales (DONE, CANCELED, UNKNOWN) ou aucune tâche</li>
     * </ul>
     */
    public static ProjectState compute(List<Task> tasks) {
        if (tasks == null || tasks.isEmpty()) return CLOSED;

        boolean hasActive  = false;
        boolean hasPending = false;

        for (Task task : tasks) {
            TaskStatus ts = TaskStatus.normalize(task.getStatus());
            if (ts == null) continue;
            if (ACTIVE_STATUSES.contains(ts))  { hasActive  = true; break; }
            if (ts == TaskStatus.PENDING)        hasPending = true;
        }

        if (hasActive)  return ACTIVE;
        if (hasPending) return STANDBY;
        return CLOSED;
    }
}
