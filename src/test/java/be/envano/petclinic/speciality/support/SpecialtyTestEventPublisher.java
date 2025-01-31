package be.envano.petclinic.speciality.support;

import be.envano.petclinic.speciality.SpecialtyEvent;
import be.envano.petclinic.speciality.SpecialtyEventPublisher;

import java.util.ArrayList;
import java.util.List;

public class SpecialtyTestEventPublisher implements SpecialtyEventPublisher {

    private final List<SpecialtyEvent> events = new ArrayList<>();

    @Override
    public void publish(SpecialtyEvent event) {
        this.events.add(event);
    }

    public List<SpecialtyEvent> events() {
        return events;
    }

}
