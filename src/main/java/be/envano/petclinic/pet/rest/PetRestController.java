package be.envano.petclinic.pet.rest;

import be.envano.petclinic.pet.Pet;
import be.envano.petclinic.pet.PetCommand;
import be.envano.petclinic.pet.PetService;
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
@RequestMapping("/pets")
class PetRestController {

    private final PetService service;

    PetRestController(PetService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    PetRestModel.Response post(@RequestBody PetRestModel.PostRequest request) {
        Objects.requireNonNull(request);

        PetCommand.Register command = PetCommandFactory.create(request);
        Pet pet = service.register(command);
        return PetResponseFactory.create(pet);
    }

    @PutMapping("/{id}/name")
    PetRestModel.Response rename(@PathVariable long id, @RequestBody PetRestModel.RenameRequest request) {
        Objects.requireNonNull(request);

        PetCommand.Rename command = PetCommandFactory.create(request, id);
        Pet pet = service.rename(command);
        return PetResponseFactory.create(pet);
    }

    @GetMapping
    List<PetRestModel.Response> findAll() {
        return service.findAll().stream()
            .map(PetResponseFactory::create)
            .toList();
    }

}
