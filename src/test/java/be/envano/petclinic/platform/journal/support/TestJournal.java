package be.envano.petclinic.platform.journal.support;

import be.envano.petclinic.platform.journal.Journal;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TestJournal implements Journal {

	private final List<Object> events = new ArrayList<>();

	@Override
	public void appendEvent(Object event) {
		Objects.requireNonNull(event);
		this.events.add(event);
	}

	public List<Object> events() {
		return events;
	}

}
