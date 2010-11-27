package cornell.cs2046.tasks;

import android.net.Uri;
import android.provider.BaseColumns;

public class Tasks implements BaseColumns {
    // This class cannot be instantiated.
    private Tasks() {}

    /**
     * Intent action specifying that tasks should be backed up by TaskBackupService.
     */
    public static final String ACTION_BACKUP_TASKS =
        "cornell.cs2046.taskbackup.ACTION_BACKUP_TASKS";

    /**
     * Intent action specifying that tasks should be restored by TaskBackupService.
     */
    public static final String ACTION_RESTORE_TASKS =
        "cornell.cs2046.taskbackup.ACTION_RESTORE_TASKS";

    public static final String AUTHORITY = "cornell.cs2046.tasks";
    public static final String TASK_URI = "tasks";

    /**
     * The URI used to access TaskProvider.
     */
    public static final Uri CONTENT_URI =
        Uri.parse("content://" + AUTHORITY + "/" + TASK_URI);

    /**
     * The title of the task.
     *
     * Type: TEXT
     */
    public static final String TITLE = "title";

    /**
     * Additional details about the task.
     *
     * Type: TEXT
     */
    public static final String DETAILS = "details";

    /**
     * Whether or not the task has been completed.
     * Should be 0 if not completed, or 1 if completed.
     *
     * Type: INTEGER
     */
    public static final String COMPLETED = "completed";

    /**
     * The time in milliseconds when this task was last modified, as
     * obtained by System.currentTimeMillis().
     *
     * Type: INTEGER
     */
    public static final String DATE_MODIFIED = "date_modified";

    /**
     * Intent action specifying that old tasks should be deleted by TaskService.
     */
    public static final String ACTION_DELETE_OLD = "cornell.cs2046.tasks.ACTION_DELETE_OLD";
}
