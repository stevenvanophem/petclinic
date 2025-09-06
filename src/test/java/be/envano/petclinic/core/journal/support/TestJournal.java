package be.envano.petclinic.core.journal.support;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import be.envano.petclinic.core.journal.Journal;

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
