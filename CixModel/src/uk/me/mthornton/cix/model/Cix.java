package uk.me.mthornton.cix.model;

import uk.me.mthornton.utility.EventPublisher;

import java.util.Map;

/*
Initially load from database. Subsequently, the database can listen for changes and write them to store.
The gui can likewise listen for changes
 */

public class Cix {
    private EventPublisher<ChangeEvent> eventPublisher = new EventPublisher<>();
    private Map<String, Forum> forums;

    public EventPublisher<ChangeEvent> getEventPublisher() {
        return eventPublisher;
    }

    /** Map of subscribed forums */
    public Map<String, Forum> getForums() {
        return forums;
    }

    /** compare with any existing value and generate either an add or update event */
    public void addForum(Forum forum) {
        Forum existing = forums.putIfAbsent(forum.getName(), forum);
        if (existing == null) {
            eventPublisher.publish(ChangeEvent.add(forum));
        } else {
            ChangeEvent event = existing.updateFrom(forum);
            if (event != null) {
                eventPublisher.publish(event);
            }
        }
    }

    public void removeForum(Forum forum) {
        if (forums.remove(forum.getName(), forum)) {
            eventPublisher.publish(ChangeEvent.remove(forum));
        }
    }
}
