package be.envano.petclinic.specialty.rest;

import be.envano.petclinic.specialty.Specialty;
import be.envano.petclinic.specialty.SpecialtyCommand;
import be.envano.petclinic.specialty.SpecialtyService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping(value = "/specialties")
class SpecialtyRestController {

    private final SpecialtyService catalog;

    SpecialtyRestController(SpecialtyService catalog) {
        this.catalog = catalog;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    SpecialtyRestModel.Response post(@RequestBody SpecialtyRestModel.PostRequest request) {
        Objects.requireNonNull(request);

        SpecialtyCommand.Register command = SpecialtyCommandFactory.create(request);
        Specialty specialty = catalog.register(command);
        return SpecialtyResponseFactory.create(specialty);
    }

    @PutMapping(value = "/{id}/name")
    SpecialtyRestModel.Response put(
        @PathVariable long id,
        @RequestBody SpecialtyRestModel.RenameRequest request
    ) {
        Objects.requireNonNull(request);

        SpecialtyCommand.Rename command = SpecialtyCommandFactory.create(request, id);
        Specialty specialty = catalog.rename(command);
        return SpecialtyResponseFactory.create(specialty);
    }

    @GetMapping
    List<SpecialtyRestModel.Response> findAll() {
        return catalog.findAll().stream()
            .map(SpecialtyResponseFactory::create)
            .toList();
    }

}
