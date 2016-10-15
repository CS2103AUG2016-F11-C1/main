package seedu.todo.controllers;

import seedu.todo.commons.EphemeralDB;
import seedu.todo.models.CalendarItem;
import seedu.todo.models.Task;
import seedu.todo.models.TodoListDB;
import seedu.todo.ui.UiManager;
import seedu.todo.ui.views.IndexView;

public class UncompleteTaskController implements Controller {
    
    private static String NAME = "Uncomplete Task";
    private static String DESCRIPTION = "Marks a task as incomplete, by listed index";
    private static String COMMAND_SYNTAX = "uncomplete <index>";
    
    private static CommandDefinition commandDefinition =
            new CommandDefinition(NAME, DESCRIPTION, COMMAND_SYNTAX);

    public static CommandDefinition getCommandDefinition() {
        return commandDefinition;
    }

    @Override
    public float inputConfidence(String input) {
        // TODO
        return input.startsWith("uncomplete") ? 1 : 0;
    }

    @Override
    public void process(String args) {
        // TODO: Example of last minute work
        
        // Get index.
        int index = Integer.decode(args.replaceFirst("uncomplete", "").trim());
        
        // Get record
        EphemeralDB edb = EphemeralDB.getInstance();
        CalendarItem calendarItem = edb.getCalendarItemsByDisplayedId(index);
        TodoListDB db = TodoListDB.getInstance();
        
        if (calendarItem == null) {
            // TODO: Show error message in console.
            return;
        }
        
        if (!(calendarItem instanceof Task)) {
            // TODO: Show error message in console.
            return;
        }
        
        Task task = (Task) calendarItem;
        
        if (!task.isCompleted()) {
            // TODO: Show error message in console.
            return;
        }
        
        // Set task as completed
        task.setIncomplete();
        db.save();
        
        // Re-render
        IndexView view = UiManager.loadView(IndexView.class);
        view.tasks = db.getAllTasks();
        view.events = db.getAllEvents();
        view.render();
    }

}