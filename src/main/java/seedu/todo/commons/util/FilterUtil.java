package seedu.todo.commons.util;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import seedu.todo.models.Event;
import seedu.todo.models.Task;

public class FilterUtil {
    
    /*==================== Filtering Methods for Tasks ======================*/
    
    public static List<Task> filterTaskByNames(List<Task> tasks, HashSet<String> nameList) {
        if (tasks.size() == 0 || nameList.size() == 0) {
            return tasks;
        }
        
        List<Task> filteredTasks = new ArrayList<Task>();
        for (int i = 0; i < tasks.size(); i ++) {
            Task task = tasks.get(i);
            Iterator<String> nameListIterator = nameList.iterator();
            while (nameListIterator.hasNext()) {
                String currentMatchingName = nameListIterator.next().toLowerCase();
                if (task.getName().startsWith(currentMatchingName)) {
                    filteredTasks.add(task);
                    break;
                }
            }
            nameListIterator = nameList.iterator();
        }
        return filteredTasks;
    }
    
    public static List<Task> filterTaskByTags(List<Task> tasks, HashSet<String> nameList) {
        if (tasks.size() == 0 || nameList.size() == 0) {
            return tasks;
        }
        
        List<Task> filteredTasks = new ArrayList<Task>();
        for (int i = 0; i < tasks.size(); i ++) {
            Task task = tasks.get(i);
            ArrayList<String> taskTagList = task.getTagList();
            Iterator<String> nameListIterator = nameList.iterator();
            while (nameListIterator.hasNext()) {
                String currentMatchingName = nameListIterator.next();
                if (taskTagList.contains(currentMatchingName)) {
                    filteredTasks.add(task);
                    break;
                }
            }
            nameListIterator = nameList.iterator();
        }
        return filteredTasks;
    }
    
    public static List<Task> filterCompletedTaskList(List<Task> tasks) {
        if (tasks.size() == 0) {
            return tasks;
        }
        
        List<Task> filteredTasks = new ArrayList<Task>();
        for (int i = 0; i < tasks.size(); i ++) {
            Task task = tasks.get(i);
            if (task.isCompleted()) {
                filteredTasks.add(task);
            }
        }
        return filteredTasks;
    }
    
    public static List<Task> filterIncompletedTaskList(List<Task> tasks) {
        if (tasks.size() == 0) {
            return tasks;
        }
        
        List<Task> filteredTasks = new ArrayList<Task>();
        for (int i = 0; i < tasks.size(); i ++) {
            Task task = tasks.get(i);
            if (!task.isCompleted()) {
                filteredTasks.add(task);
            }
        }
        return filteredTasks;
    }
    
    
    public static List<Task> filterTaskBySingleDate (List<Task> tasks, LocalDateTime date) {
        if (tasks.size() == 0) {
            return tasks;
        }
        
        ArrayList<Task> filteredTasks = new ArrayList<Task>();
        Iterator<Task> iterator = tasks.iterator();
        while (iterator.hasNext()) {
            Task task = iterator.next();
            LocalDateTime taskDate = DateUtil.floorDate(task.getCalendarDT());
            
            //May have floating tasks
            if (taskDate != null && taskDate.equals(date)) {
                filteredTasks.add(task);
            }
        }
        return filteredTasks;
    }
    
    public static List<Task> filterTaskWithDateRange (List<Task> tasks, LocalDateTime startDate, LocalDateTime endDate) {
        if (tasks.size() == 0) {
            return tasks;
        }
    
        if (startDate == null) {
            startDate = LocalDateTime.MIN;
        }
        
        if (endDate == null) {
            endDate = LocalDateTime.MAX;
        }
        
        ArrayList<Task> filteredTasks = new ArrayList<Task>();
        Iterator<Task> iterator = tasks.iterator();
        while (iterator.hasNext()) {
            Task task = iterator.next();
            LocalDateTime taskDate = DateUtil.floorDate(task.getDueDate());
            if (taskDate == null) {
                taskDate = DateUtil.floorDate(LocalDateTime.MIN);
            }
            
            if (taskDate.compareTo(startDate) >= 0 && taskDate.compareTo(endDate) <= 0) {
                filteredTasks.add(task);
            }
        }
        return filteredTasks;
    }
    
    /*==================== Filtering Methods for Events ======================*/
    
    public static List<Event> filterEventByNames (List<Event> events, HashSet<String> nameList) {
        if (events.size() == 0 || nameList.size() == 0) {
            return events;
        }
        
        List<Event> filteredEvents = new ArrayList<Event>();
        for (int i = 0; i < events.size(); i ++) {
            Event event = events.get(i);
            Iterator<String> nameListIterator = nameList.iterator();
            while (nameListIterator.hasNext()) {
                String currentMatchingName = nameListIterator.next().toLowerCase();
                if (event.getName().startsWith(currentMatchingName)) {
                    filteredEvents.add(event);
                    break;
                }
            }
            nameListIterator = nameList.iterator();
        }
        return filteredEvents;
    }
    
    public static List<Event> filterEventByTags (List<Event> events, HashSet<String> nameList) {
        if (events.size() == 0 || nameList.size() == 0) {
            return events;
        }
        
        List<Event> filteredEvents = new ArrayList<Event>();
        for (int i = 0; i < events.size(); i ++) {
            Event event = events.get(i);
            ArrayList<String> taskTagList = event.getTagList();
            Iterator<String> nameListIterator = nameList.iterator();
            while (nameListIterator.hasNext()) {
                String currentMatchingName = nameListIterator.next();
                if (taskTagList.contains(currentMatchingName)) {
                    filteredEvents.add(event);
                    break;
                }
            }
            nameListIterator = nameList.iterator();
        }
        return filteredEvents;
    }
    
    
    public static List<Event> filterIsOverEventList(List<Event> events) {
        if (events.size() == 0) {
            return events;
        }
        
        List<Event> filteredEvent = new ArrayList<Event>();
        for (int i = 0; i < events.size(); i ++) {
            Event event = events.get(i);
            if (event.isOver()) {
                filteredEvent.add(event);
            }
        }
        return filteredEvent;
    }
    
    public static List<Event> filterCurrentEventList(List<Event> events) {
        if (events.size() == 0) {
            return events;
        }
        
        List<Event> filteredEvent = new ArrayList<Event>();
        for (int i = 0; i < events.size(); i ++) {
            Event event = events.get(i);
            if (!event.isOver()) {
                filteredEvent.add(event);
            }
        }
        return filteredEvent;
    }
    
    public static List<Event> filterEventBySingleDate(List<Event> events, LocalDateTime date) {
        if (events.size() == 0) {
            return events;
        }
        
        ArrayList<Event> filteredEvents = new ArrayList<Event>();
        Iterator<Event> iterator = events.iterator();
        while (iterator.hasNext()) {
            Event event = iterator.next();
            LocalDateTime eventDate = DateUtil.floorDate(event.getStartDate());
            //May have floating tasks
            if (eventDate != null && eventDate.equals(date)) {
                filteredEvents.add(event);
            }
        }
        return filteredEvents;
    }
    
    public static List<Event> filterEventWithDateRange(List<Event> events, LocalDateTime startDate, LocalDateTime endDate) {
        if (events.size() == 0) {
            return events;
        }
    
        if (startDate == null) {
            startDate = LocalDateTime.MIN;
        }
        
        if (endDate == null) {
            endDate = LocalDateTime.MAX;
        }
        
        ArrayList<Event> filteredEvents = new ArrayList<Event>();
        Iterator<Event> iterator = events.iterator();
        while (iterator.hasNext()) {
            Event event = iterator.next();
            LocalDateTime eventStartDate = DateUtil.floorDate(event.getStartDate());
            LocalDateTime eventEndDate = DateUtil.floorDate(event.getEndDate());
            
            if (eventStartDate == null) {
                eventStartDate = DateUtil.floorDate(LocalDateTime.MIN);
            }
            if (eventEndDate == null) {
                eventEndDate = DateUtil.floorDate(LocalDateTime.MAX);
            }
            System.out.println(eventStartDate);
            System.out.println(startDate);
            if (eventStartDate.compareTo(startDate) >= 0 && eventEndDate.compareTo(endDate) <= 0) {
                filteredEvents.add(event);
            }
        }
        return filteredEvents;
    }

}