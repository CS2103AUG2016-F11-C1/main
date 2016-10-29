package seedu.todo.controllers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


import seedu.todo.commons.exceptions.ParseException;
import seedu.todo.commons.util.FilterUtil;
import seedu.todo.commons.util.ParseUtil;
import seedu.todo.commons.util.StringUtil;
import seedu.todo.controllers.concerns.Tokenizer;
import seedu.todo.controllers.concerns.Renderer;
import seedu.todo.models.Event;
import seedu.todo.models.Task;
import seedu.todo.models.TodoListDB;

/**
 * Controller to find task/event by keyword
 * 
 * @@author Tiong YaoCong A0139922Y
 *
 */
public class FindController implements Controller {
    
    private static final String NAME = "Find";
    private static final String DESCRIPTION = "Find all tasks and events based on the provided keywords.\n" + 
    "This command will be search with non-case sensitive keywords.";
    private static final String COMMAND_SYNTAX = "find <name> [on <date>] [task/event]";
    private static final String TASK_SYNTAX = "find <name> task [complete/incomplete]";
    private static final String EVENT_SYNTAX = "find <name> event [over/ongoing]";
    private static final String COMMAND_WORD = "find";
    
    private static final String MESSAGE_LISTING_SUCCESS = "A total of %s found!";
    private static final String MESSAGE_LISTING_FAILURE = "No task or event found!";
    private static final String MESSAGE_NO_KEYWORD_FOUND = "No keyword found!";
    private static final String MESSAGE_DATE_CONFLICT = "Unable to find!\nMore than 1 date criteria is provided!";
    private static final String MESSAGE_ITEMTYPE_CONFLICT = "Unable to find!\nItem type conflict detected!";
    private static final String MESSAGE_NO_DATE_DETECTED = "Unable to find!\nThe natural date entered is not supported.";
    
    //use to access parsing of dates
    private static final int NUM_OF_DATES_FOUND_INDEX = 0;
    private static final int DATE_ON_INDEX = 1;
    private static final int DATE_FROM_INDEX = 2;
    private static final int DATE_TO_INDEX = 3;
    
    private static CommandDefinition commandDefinition =
            new CommandDefinition(NAME, DESCRIPTION, COMMAND_SYNTAX); 

    public static CommandDefinition getCommandDefinition() {
        return commandDefinition;
    }

    @Override
    public float inputConfidence(String input) {
        return (input.toLowerCase().startsWith(COMMAND_WORD)) ? 1 : 0;
    }
    
    private static Map<String, String[]> getTokenDefinitions() {
        Map<String, String[]> tokenDefinitions = new HashMap<String, String[]>();
        tokenDefinitions.put("default", new String[] {"find"});
        tokenDefinitions.put("eventType", new String[] { "event", "events", "task", "tasks"});
        tokenDefinitions.put("taskStatus", new String[] { "complete" , "completed", "incomplete", "incompleted"});
        tokenDefinitions.put("eventStatus", new String[] { "over" , "ongoing", "current", "schedule" , "scheduled"});
        tokenDefinitions.put("time", new String[] { "at", "by", "on", "time", "date" });
        tokenDefinitions.put("timeFrom", new String[] { "from" });
        tokenDefinitions.put("timeTo", new String[] { "to", "before" });
        tokenDefinitions.put("itemName", new String[] { "name" });
        tokenDefinitions.put("tagName", new String [] { "tag" }); 
        return tokenDefinitions;
    }

    @Override
    public void process(String input) throws ParseException {
        
        Map<String, String[]> parsedResult;
        parsedResult = Tokenizer.tokenize(getTokenDefinitions(), input);
        
        HashSet<String> itemNameList = new HashSet<String>();
        HashSet<String> tagNameList = new HashSet<String>();
        HashSet<String> keywordList = new HashSet<String>();
        
        //to be use to be either name or tag
        updateHashList(parsedResult, keywordList, "default");
        updateHashList(parsedResult, itemNameList, "itemName");
        updateHashList(parsedResult, tagNameList, "tagName");
        itemNameList.addAll(keywordList);
        tagNameList.addAll(keywordList);
        
        if (keywordList.size() == 0 && itemNameList.size() == 0 && tagNameList.size() == 0) {
            //No keyword provided, display error
            Renderer.renderDisambiguation(COMMAND_SYNTAX, MESSAGE_NO_KEYWORD_FOUND);
            return;
        }
        
        boolean isItemTypeProvided = !ParseUtil.isTokenNull(parsedResult, "eventType");
        boolean isTaskStatusProvided = !ParseUtil.isTokenNull(parsedResult, "taskStatus");
        boolean isEventStatusProvided = !ParseUtil.isTokenNull(parsedResult, "eventStatus");
        
        boolean isTask = true; //default
        if (isItemTypeProvided) {
            isTask = ParseUtil.doesTokenContainKeyword(parsedResult, "eventType", "task");
            
            if (isTask && isEventStatusProvided) {
                Renderer.renderDisambiguation(TASK_SYNTAX, MESSAGE_ITEMTYPE_CONFLICT);
                return;
            }
            
            if (!isTask && isTaskStatusProvided) {
                Renderer.renderDisambiguation(EVENT_SYNTAX, MESSAGE_ITEMTYPE_CONFLICT);
                return;
            }
        }
        
        boolean isCompleted = false; //default 
        boolean isOver = false; //default
        if (isTaskStatusProvided) {
            isCompleted = !ParseUtil.doesTokenContainKeyword(parsedResult, "taskStatus", "incomplete");
        }
        if (isEventStatusProvided) {
            isOver = ParseUtil.doesTokenContainKeyword(parsedResult, "eventStatus", "over");
        }
        
        String[] parsedDates = ParseUtil.parseDates(parsedResult);
        LocalDateTime dateOn = null;
        LocalDateTime dateFrom = null;
        LocalDateTime dateTo = null;
        
        if (parsedDates != null) {
            String naturalOn = parsedDates[DATE_ON_INDEX];
            String naturalFrom = parsedDates[DATE_FROM_INDEX];
            String naturalTo = parsedDates[DATE_TO_INDEX];
            
            if (naturalOn != null && Integer.parseInt(parsedDates[NUM_OF_DATES_FOUND_INDEX]) > 1) {
                //date conflict detected
                Renderer.renderDisambiguation(COMMAND_SYNTAX, MESSAGE_DATE_CONFLICT);
                return;
            }
    
            // Parse natural date using Natty.
            dateOn = naturalOn == null ? null : ParseUtil.parseNatural(naturalOn); 
            dateFrom = naturalFrom == null ? null : ParseUtil.parseNatural(naturalFrom); 
            dateTo = naturalTo == null ? null : ParseUtil.parseNatural(naturalTo);
        }
        
        if (parsedDates != null && dateOn == null && dateFrom == null && dateTo == null) {
            //Natty failed to parse date
            Renderer.renderDisambiguation(COMMAND_SYNTAX, MESSAGE_NO_DATE_DETECTED);
            return ;
        }
        
        if (parsedDates != null && isEventStatusProvided) {
            //detect date conflict
            Renderer.renderDisambiguation(COMMAND_SYNTAX, MESSAGE_DATE_CONFLICT);
            return;
        }
                
        //setting up view
        TodoListDB db = TodoListDB.getInstance();
        List<Task> tasks = db.getAllTasks();
        List<Event> events = db.getAllEvents();
        HashSet<Task> mergedTasks = new HashSet<Task>();
        HashSet<Event> mergedEvents = new HashSet<Event>();
        if (!isItemTypeProvided) {
            List<Task> tasksByNames = FilterUtil.filterTaskByNames(tasks, itemNameList);
            List<Task> tasksByTags = FilterUtil.filterTaskByTags(tasks, tagNameList);
            mergedTasks.addAll(tasksByNames);
            mergedTasks.addAll(tasksByTags);
            tasks = new ArrayList<Task>(mergedTasks);
            
            List<Event> eventsByNames = FilterUtil.filterEventByNames(events, itemNameList);
            List<Event> eventsByTags = FilterUtil.filterEventByTags(events, tagNameList);
            mergedEvents.addAll(eventsByNames);
            mergedEvents.addAll(eventsByTags);
            events = new ArrayList<Event>(mergedEvents);
        } else if (isTask) {
            tasks = db.getTaskByName(tasks, itemNameList, tagNameList);
            events = new ArrayList<Event>();
        } else if (!isTask) {
            tasks = new ArrayList<Task>();
            events = db.getEventByName(events, itemNameList, tagNameList);
        }
        
        if (isTaskStatusProvided) {
            if (isCompleted) {
                tasks = FilterUtil.filterCompletedTaskList(tasks);
            } 
            
            if (!isCompleted) {
                tasks = FilterUtil.filterIncompletedTaskList(tasks);
            }
        }
        
        if (isEventStatusProvided) {
            if (isOver) {
                events = FilterUtil.filterIsOverEventList(events);
            } 
            
            if (!isOver) {
                events = FilterUtil.filterCurrentEventList(events);
            }
        }
        
        if (dateOn != null) {
            //filter by single date
            tasks = FilterUtil.filterTaskBySingleDate(tasks, dateOn);
            events = FilterUtil.filterEventBySingleDate(events, dateOn);
        } else {
            //filter by range
            tasks = FilterUtil.filterTaskWithDateRange(tasks, dateFrom, dateTo);
            events = FilterUtil.filterEventWithDateRange(events, dateFrom, dateTo);
        }
        
        if (tasks.size() == 0 && events.size() == 0) {
            Renderer.renderIndex(db, MESSAGE_LISTING_FAILURE);
            return;
        }
        
        String consoleMessage = String.format(MESSAGE_LISTING_SUCCESS, 
                StringUtil.displayNumberOfTaskAndEventFoundWithPuralizer(tasks.size(), events.size()));
        Renderer.renderSelected(db, consoleMessage, tasks, events);
    }
    
    /**
     * Extract the parsed result and update the hash list
     * @param parsedResult
     */
    private void updateHashList(Map<String, String[]> parsedResult, HashSet<String> hashList, 
            String token) {
      
        String result = ParseUtil.getTokenResult(parsedResult, token);
        
        //if found any matching , update list
        if (result != null) {
            hashList.add(result);
            String[] resultArray = StringUtil.convertStringIntoArray(result);
            for (int i = 0; i < resultArray.length; i ++) {
                hashList.add(resultArray[i]);
            }
        }
    }

}
