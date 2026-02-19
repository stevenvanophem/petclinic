package be.envano.petclinic.specialty.support;

import be.envano.petclinic.specialty.SpecialtyEvent;
import org.springframework.context.event.EventListener;

import java.util.ArrayList;
import java.util.List;

public class SpecialtyEventConsumer {

    private final List<SpecialtyEvent> events = new ArrayList<>();

    @EventListener
    public void onEvent(SpecialtyEvent event) {
        this.events.add(event);
    }

    public List<SpecialtyEvent> events() {
        return events;
    }

}
