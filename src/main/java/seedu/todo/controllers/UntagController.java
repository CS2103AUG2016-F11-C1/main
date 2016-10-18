package seedu.todo.controllers;

import seedu.todo.commons.EphemeralDB;
import seedu.todo.models.CalendarItem;
import seedu.todo.models.Event;
import seedu.todo.models.Task;
import seedu.todo.models.TodoListDB;
import seedu.todo.ui.UiManager;
import seedu.todo.ui.views.IndexView;

public class UntagController implements Controller {
    
    private static String NAME = "Untag";
    private static String DESCRIPTION = "Untag a task/event by listed index";
    private static String COMMAND_SYNTAX = "untag <index>";
    
    private static String MESSAGE_UNTAG_SUCCESS = "Tag has been removed!\n" + "To undo, type \"undo\".";
    private static String MESSAGE_INVALID_CALENDARITEM = "Could not untag task/event: invalid index provided!";
    
    private static CommandDefinition commandDefinition =
            new CommandDefinition(NAME, DESCRIPTION, COMMAND_SYNTAX); 

    public static CommandDefinition getCommandDefinition() {
        return commandDefinition;
    }

    @Override
    public float inputConfidence(String input) {
        // TODO
        return (input.startsWith("untag")) ? 1 : 0;
    }

    @Override
    public void process(String args) {
        // TODO: Example of last minute work
        
        // Get index.
        int index = Integer.decode(args.replaceFirst("untag", "").trim());
        
        // Get record
        EphemeralDB edb = EphemeralDB.getInstance();
        CalendarItem calendarItem = edb.getCalendarItemsByDisplayedId(index);
        TodoListDB db = TodoListDB.getInstance();
        
        if (calendarItem == null) {
            UiManager.updateConsoleMessage(MESSAGE_INVALID_CALENDARITEM);
            return;
        }
        
        assert calendarItem != null;
        
        calendarItem.setTag(null);
        
        // Re-render
        IndexView view = UiManager.loadView(IndexView.class);
        view.tasks = db.getAllTasks();
        view.events = db.getAllEvents();
        UiManager.renderView(view);
        
        // Show console message
        UiManager.updateConsoleMessage(MESSAGE_UNTAG_SUCCESS);
    }

}