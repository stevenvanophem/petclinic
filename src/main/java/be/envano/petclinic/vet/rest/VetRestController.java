package be.envano.petclinic.vet.rest;

import be.envano.petclinic.vet.Vet;
import be.envano.petclinic.vet.VetCommand;
import be.envano.petclinic.vet.VetService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
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
@RequestMapping("/vets")
class VetRestController {

    private final VetService service;

    VetRestController(VetService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    VetRestModel.Response post(@RequestBody VetRestModel.PostRequest request) {
        Objects.requireNonNull(request);

        VetCommand.Hire command = VetCommandFactory.create(request);
        Vet vet = service.hire(command);
        return VetResponseFactory.create(vet);
    }

    @PutMapping("/{id}/name")
    VetRestModel.Response rename(@PathVariable long id, @RequestBody VetRestModel.RenameRequest request) {
        Objects.requireNonNull(request);

        VetCommand.Rename command = VetCommandFactory.create(request, id);
        Vet vet = service.rename(command);
        return VetResponseFactory.create(vet);
    }

    @PutMapping("/{id}/specialties")
    VetRestModel.Response specialize(@PathVariable long id, @RequestBody VetRestModel.SpecializeRequest request) {
        Objects.requireNonNull(request);

        VetCommand.Specialize command = VetCommandFactory.create(request, id);
        Vet vet = service.specialize(command);
        return VetResponseFactory.create(vet);
    }

    @DeleteMapping("/{id}/specialties/{specialtyId}")
    VetRestModel.Response deSpecialize(
        @PathVariable long id,
        @PathVariable long specialtyId,
        @RequestBody VetRestModel.DeSpecializeRequest request
    ) {
        Objects.requireNonNull(request);

        VetCommand.DeSpecialize command = VetCommandFactory.create(request, id, specialtyId);
        Vet vet = service.deSpecialize(command);
        return VetResponseFactory.create(vet);
    }

    @DeleteMapping("/{id}")
    VetRestModel.FireResponse fire(@PathVariable long id, @RequestBody VetRestModel.FireRequest request) {
        Objects.requireNonNull(request);

        VetCommand.Fire command = VetCommandFactory.create(request, id);
        service.fire(command);
        return VetResponseFactory.createFireResponse();
    }

    @GetMapping
    List<VetRestModel.Response> findAll() {
        return service.findAll().stream()
            .map(VetResponseFactory::create)
            .toList();
    }

}
