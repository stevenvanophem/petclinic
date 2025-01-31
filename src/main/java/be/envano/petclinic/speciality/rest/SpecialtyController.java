package be.envano.petclinic.speciality.rest;

import be.envano.petclinic.speciality.Specialty;
import be.envano.petclinic.speciality.SpecialtyCatalog;
import be.envano.petclinic.speciality.SpecialtyCommand;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
