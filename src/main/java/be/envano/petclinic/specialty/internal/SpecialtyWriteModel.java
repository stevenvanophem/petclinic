package be.envano.petclinic.specialty.internal;

import be.envano.petclinic.specialty.Specialty;
import be.envano.petclinic.specialty.SpecialtyCommand;
import be.envano.petclinic.specialty.SpecialtyEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SpecialtyWriteModel {

    private final List<SpecialtyEvent> events = new ArrayList<>();

    private final Specialty.Id id;
    private Specialty.Name name;
    private final int version;

    public static SpecialtyWriteModel load(SpecialtyCommand.Load command) {
        return new SpecialtyWriteModel(command);
    }

    public static SpecialtyWriteModel register(Specialty.Id id, SpecialtyCommand.Register command) {
        return new SpecialtyWriteModel(id, command);
    }

    private SpecialtyWriteModel(SpecialtyCommand.Load command) {
        Objects.requireNonNull(command);
        this.id = command.id();
        this.name = command.name();
        this.version = command.version();
    }

    private SpecialtyWriteModel(Specialty.Id id, SpecialtyCommand.Register command) {
        Objects.requireNonNull(id);
        Objects.requireNonNull(command);
        this.id = id;
        this.name = command.name();
        this.version = 0;
        this.events.add(new SpecialtyEvent.Registered(this.toSnapshot()));
    }

    public void rename(SpecialtyCommand.Rename command) {
        Objects.requireNonNull(command);

        Specialty.Name originalName = this.name;

        if (this.version != command.version())
            throw new IllegalStateException("specialty versions do not match");

        this.name = command.name();
        this.events.add(new SpecialtyEvent.Renamed(this.toSnapshot(), originalName));
    }

    public Specialty toSnapshot() {
        return new Specialty(this.id, this.name, this.version);
    }

    public Specialty.Id id() {
        return id;
    }

    public Specialty.Name name() {
        return name;
    }

    public int version() {
        return version;
    }

    public List<SpecialtyEvent> events() {
        return List.copyOf(events);
    }

}
