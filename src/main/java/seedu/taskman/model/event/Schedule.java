package seedu.taskman.model.event;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seedu.taskman.commons.exceptions.IllegalValueException;
import seedu.taskman.commons.util.CollectionUtil;

import java.util.Collection;

public class Schedule {

    private final ObservableList<TimeSlot> timeSlots;

    public Schedule() {
        timeSlots = FXCollections.observableArrayList();
    }

    public Schedule(long startEpochSecond, long endEpochSecond) throws IllegalValueException {
        this();
        timeSlots.add(new TimeSlot(startEpochSecond, endEpochSecond));
    }

    public Schedule(String singleTimeSlot) throws IllegalValueException {
        this();
        timeSlots.add(new TimeSlot(singleTimeSlot));
    }

    // TODO: add "no overlap exception"
    public Schedule(Collection<TimeSlot> timeSlots) throws IllegalValueException {
        this();
        CollectionUtil.assertNoNullElements(timeSlots);
        // TODO: unique check is insufficient, check for no overlaps
        if (!CollectionUtil.elementsAreUnique(timeSlots)) {
            throw new IllegalValueException("Overlaps Exist!");
        }
        this.timeSlots.addAll(timeSlots);
    }

    public ObservableList<TimeSlot> getTimeSlots() {
        return timeSlots;
    }


     //todo: implement this
    @Override
    public String toString() {
        if (timeSlots.size() > 0) {
            return timeSlots.get(0).toString();
        } else {
            return "";
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Schedule schedule = (Schedule) o;
        return this.timeSlots.equals(schedule.timeSlots);
    }

    @Override
    public int hashCode() {
        return timeSlots.hashCode();
    }

}
