package cornell.cs2046.taskbackup;

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

    /**
     * Intent broadcast action specifying that backup should be called.
     */
    public static final String BROADCAST_PERIODIC_BACKUP =
        "cornell.cs2046.taskbackup.BROADCAST_PERIODIC_BACKUP";

    /**
     * URL for performing API requests.
     */
    public static final String BACKUP_API_URL = "http://cs2046tasks.appspot.com/tasksync";

    /**
     * Parameter to be passed in GET/POST requests containing the NetID.
     */
    public static final String REQUEST_NETID = "netid";

    /**
     * Parameter to be passed in POST requests containing the tasks JSON object.
     */
    public static final String REQUEST_TASKS = "taskdata";

    // Names of the various JSON fields used in sending/receiving tasks.
    public static final String JSON_TASK_ARRAY = "tasks";
    public static final String JSON_TITLE = "title";
    public static final String JSON_DETAILS = "details";
    public static final String JSON_COMPLETED = "completed";
    public static final String JSON_DATE_MODIFIED = "lastModified";

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
