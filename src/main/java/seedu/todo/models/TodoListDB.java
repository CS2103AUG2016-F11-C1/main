package seedu.todo.models;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import seedu.todo.commons.exceptions.CannotRedoException;
import seedu.todo.commons.exceptions.CannotUndoException;
import seedu.todo.storage.JsonStorage;
import seedu.todo.storage.Storage;

/**
 * This class holds the entire persistent database for the TodoList app.
 * <ul>
 * <li>This is a singleton class. For obvious reasons, the TodoList app should
 * not be working with multiple DB instances simultaneously.</li>
 * <li>Object to object dynamic references should not be expected to survive
 * serialization.</li>
 * </ul>
 * 
 * @@author A0093907W
 *
 */
public class TodoListDB {

    private static TodoListDB instance = null;
    private static Storage storage = new JsonStorage();
    
    private Set<Task> tasks = new LinkedHashSet<Task>();
    private Set<Event> events = new LinkedHashSet<Event>();
    private Map<String, String> aliases = new HashMap<String, String>();
    private HashMap<String, Integer> tagList = new HashMap<String, Integer>();
    
    protected TodoListDB() {
        // Prevent instantiation.
    }
    
    public void setStorage(Storage storageToSet) {
        storage = storageToSet;
    }
    
    /**
     * add into the overall Tags in the DB.
     * @@author Tiong YaoCong A0139922Y
     */
    public void addIntoTagList(String[] parsedTagNames) {
        assert parsedTagNames != null;
        for (int i = 0; i < parsedTagNames.length; i ++) {
            String tagName = parsedTagNames[i].trim();
            if (tagList.get(tagName) != null) {
                int currentTagCount = tagList.get(tagName);
                tagList.put(tagName, currentTagCount + 1);
            } else {
                tagList.put(tagName, 1);
            }
        }
    }
    
    /**
     * Remove from the overall Tags with a single tagName that exist in the DB.
     * @@author Tiong YaoCong A0139922Y
     */
    public void updateTagList(String[] parsedTagNames) {
        assert parsedTagNames != null;
        for (int i = 0; i < parsedTagNames.length; i ++) {
            String tagName = parsedTagNames[i].trim();
            int currentTagCount = tagList.get(tagName);
            
            int newTagCount = currentTagCount - 1;
            if (newTagCount == 0) {
                tagList.remove(tagName);
            } else {
                tagList.put(tagName, newTagCount);
            }
        }
    }
    
    /**
     * Remove from the overall Tags with a given List of CalendarItem that exist in the DB.
     * @param <E>listOfItem of type CalendarItem
     * @@author Tiong YaoCong A0139922Y
     */
    public <E> void removeFromTagList(List<E> listOfCalendarItem) {
        assert listOfCalendarItem != null;
        
        ArrayList<String> selectedTagList = new ArrayList<String>();
        for (int i = 0; i < listOfCalendarItem.size(); i ++) {
            selectedTagList.addAll(((CalendarItem) listOfCalendarItem.get(i)).getTagList());
        }
        
        updateTagList(selectedTagList.toArray(new String[0]));
    }
    
    /**
     * Get a list of Tags in the DB.
     * 
     * @return tagList
     * @@author Tiong YaoCong A0139922Y
     */
    public HashMap<String, Integer> getTagList() {
        return tagList;
    }
    
    /**
     * Count tags which are already inserted into the db
     * 
     * @return Number of tags
     * @@author Tiong YaoCong A0139922Y
     */
    public int countTagList() {
        return tagList.size();
    }
    
    public Map<String, String> getAliases() {
        return aliases;
    }
    
    /**
     * Get a list of Tasks in the DB.
     * 
     * @return tasks
     */
    public List<Task> getAllTasks() {
        return new ArrayList<Task>(tasks);
    }

    
    /**
     * Count tasks which are not marked as complete, where {@code isComplete} is false.
     * 
     * @return Number of incomplete tasks
     */
    public int countIncompleteTasks() {
        int count = 0;
        for (Task task : tasks) {
            if (!task.isCompleted()) {
                count++;
            }
        }
        return count;
    }
    
    /**
     * Count tasks which are overdue, where {@code dueDate} is before the time now.
     * 
     * @return Number of overdue tasks
     */
    public int countOverdueTasks() {
        LocalDateTime now = LocalDateTime.now();
        int count = 0;
        for (Task task : tasks) {
            LocalDateTime dueDate = task.getDueDate();
            if (!task.isCompleted() && dueDate != null && dueDate.compareTo(now) < 0) {
                count++;
            }
        }
        return count;
    }
    
    /**
     * Get a list of Events in the DB.
     * 
     * @return events
     */
    public List<Event> getAllEvents() {
        return new ArrayList<Event>(events);
    }

    /**
     * Count events which are in the future, where {@code startDate} is after the time now.
     * 
     * @return Number of future events
     */
    public int countFutureEvents() {
        LocalDateTime now = LocalDateTime.now();
        int count = 0;
        for (Event event : events) {
            LocalDateTime startDate = event.getStartDate();
            if (startDate != null && startDate.compareTo(now) >= 0) {
                count++;
            }
        }
        return count;
    }
    
    /**
     * Create a new Task in the DB and return it.<br>
     * <i>The new record is not persisted until <code>save</code> is explicitly
     * called.</i>
     * 
     * @return task
     */
    public Task createTask() {
        Task task = new Task();
        tasks.add(task);
        return task;
    }
    
    /**
     * Destroys a Task in the DB and persists the commit.
     * 
     * @param task
     * @return true if the save was successful, false otherwise
     */
    public boolean destroyTask(Task task) {
        tasks.remove(task);
        ArrayList<Task> taskList = new ArrayList<Task>();
        taskList.add(task);
        removeFromTagList(taskList);
        return save();
    }
    
    /**
     * Destroys all Task and Events in the DB and persists the commit.
     * 
     * @return true if the save was successful, false otherwise
     * @@author Tiong YaoCong A0139922Y
     */
    public boolean destroyAllTaskAndEvents() {
        removeFromTagList(new ArrayList<Task>(tasks));
        removeFromTagList(new ArrayList<Event>(events));
        tasks = new LinkedHashSet<Task>();
        events = new LinkedHashSet<Event>();
        return save();
    }
    
    /**
     * Destroys Task and Events based on list in the DB and persists the commit.
     * @taskLists 
     *            List of tasks to be destroyed
     * @eventLists
     *            List of events to be destroyed           
     * 
     * @return true if the save was successful, false otherwise
     * @@author Tiong YaoCong A0139922Y
     */
    public boolean destroyAllTaskAndEventsByList(List<Task> tasksList , List<Event> eventsList) {
        removeFromTagList(new ArrayList<Task>(tasksList));
        removeFromTagList(new ArrayList<Event>(eventsList));
        
        //removing tasks and events
        tasks.removeAll(tasksList);
        events.removeAll(eventsList);
        return save();
    }
    
    /**
     * Destroys all Task in the DB and persists the commit.
     * 
     * @return true if the save was successful, false otherwise
     * @@author Tiong YaoCong A0139922Y
     */
    public void destroyAllTask() {
        removeFromTagList(new ArrayList<Task>(tasks));
        tasks = new LinkedHashSet<Task>();
    }
    
    /**
     * Create a new Event in the DB and return it.<br>
     * <i>The new record is not persisted until <code>save</code> is explicitly
     * called.</i>
     * 
     * @return event
     * 
     * @@author A0093907W
     */
    public Event createEvent() {
        Event event = new Event();
        events.add(event);
        return event;
    }
    
    /**
     * Destroys an Event in the DB and persists the commit.
     * 
     * @param event
     * @return true if the save was successful, false otherwise
     */
    public boolean destroyEvent(Event event) {
        events.remove(event);
        ArrayList<CalendarItem> listOfCalendarItem = new ArrayList<CalendarItem>();
        listOfCalendarItem.add(event);
        removeFromTagList(listOfCalendarItem);
        return save();
    }
    
    /**
     * Destroys all Event in the DB and persists the commit.
     * 
     * @@author Tiong YaoCong A0139922Y
     */
    public void destroyAllEvent() {
        removeFromTagList(new ArrayList<Event>(events));
        events = new LinkedHashSet<Event>();
    }
    
    /**
     * Gets the singleton instance of the TodoListDB.
     * 
     * @return TodoListDB
     * 
     * @@author A0093907W
     */
    public static TodoListDB getInstance() {
        if (instance == null) {
            instance = new TodoListDB();
        }
        return instance;
    }
    
    /**
     * Explicitly persists the database to disk.
     * 
     * @return true if the save was successful, false otherwise
     */
    public boolean save() {
        try {
            storage.save(this);
            return true;
        } catch (IOException e) {
            return false;
        }
    }
    
    /**
     * Explicitly reloads the database from disk.
     * 
     * @return true if the load was successful, false otherwise
     */
    public boolean load() {
        try {
            instance = storage.load();
            return true;
        } catch (IOException e) {
            return false;
        }
    }
    
    public void move(String newPath) throws IOException {
        storage.move(newPath);
    }
    
    /**
     * Returns the maximum possible number of undos.
     * 
     * @return undoSize
     */
    public int undoSize() {
        return storage.undoSize();
    }
    
    /**
     * Rolls back the DB by one commit.
     * 
     * @return true if the rollback was successful, false otherwise
     */
    public boolean undo() {
        try {
            instance = storage.undo();
            return true;
        } catch (CannotUndoException | IOException e) {
            return false;
        }
    }
    
    /**
     * Returns the maximum possible number of redos.
     * 
     * @return redoSize
     */
    public int redoSize() {
        return storage.redoSize();
    }
    
    /**
     * Rolls forward the DB by one undo commit.
     * 
     * @return true if the redo was successful, false otherwise
     */
    public boolean redo() {
        try {
            instance = storage.redo();
            return true;
        } catch (CannotRedoException | IOException e) {
            return false;
        }
    }
}
