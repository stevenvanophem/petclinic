package be.envano.petclinic.specialty.internal.rest;

import be.envano.petclinic.specialty.Specialty;
import be.envano.petclinic.specialty.SpecialtyCatalog;
import be.envano.petclinic.specialty.SpecialtyCommand;
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
class SpecialtyController {

    private final SpecialtyCatalog catalog;

    SpecialtyController(SpecialtyCatalog catalog) {
        this.catalog = catalog;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    RestModel.Response post(@RequestBody RestModel.PostRequest request) {
        Objects.requireNonNull(request);

        SpecialtyCommand.Register command = CommandFactory.create(request);
        Specialty specialty = catalog.register(command);
        return ResponseFactory.create(specialty);
    }

    @PutMapping(value = "/{id}/name")
    RestModel.Response put(
        @PathVariable long id,
        @RequestBody RestModel.RenameRequest request
    ) {
        Objects.requireNonNull(request);

        SpecialtyCommand.Rename command = CommandFactory.create(request, id);
        Specialty specialty = catalog.rename(command);
        return ResponseFactory.create(specialty);
    }

    @GetMapping
    List<RestModel.Response> findAll() {
        return catalog.findAll().stream()
            .map(ResponseFactory::create)
            .toList();
    }

}
