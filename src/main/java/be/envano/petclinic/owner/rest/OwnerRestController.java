package be.envano.petclinic.owner.rest;

import be.envano.petclinic.owner.Owner;
import be.envano.petclinic.owner.OwnerCommand;
import be.envano.petclinic.owner.OwnerService;
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
@RequestMapping("/owners")
class OwnerRestController {

    private final OwnerService service;

    OwnerRestController(OwnerService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    OwnerRestModel.Response post(@RequestBody OwnerRestModel.PostRequest request) {
        Objects.requireNonNull(request);

        OwnerCommand.Register command = OwnerCommandFactory.create(request);
        Owner owner = service.register(command);
        return OwnerResponseFactory.create(owner);
    }

    @PutMapping("/{id}/name")
    OwnerRestModel.Response rename(@PathVariable long id, @RequestBody OwnerRestModel.RenameRequest request) {
        Objects.requireNonNull(request);

        OwnerCommand.Rename command = OwnerCommandFactory.create(request, id);
        Owner owner = service.rename(command);
        return OwnerResponseFactory.create(owner);
    }

    @PutMapping("/{id}/contact-details")
    OwnerRestModel.Response changeContactDetails(
        @PathVariable long id,
        @RequestBody OwnerRestModel.ChangeContactDetailsRequest request
    ) {
        Objects.requireNonNull(request);

        OwnerCommand.ChangeContactDetails command = OwnerCommandFactory.create(request, id);
        Owner owner = service.changeContactDetails(command);
        return OwnerResponseFactory.create(owner);
    }

    @GetMapping
    List<OwnerRestModel.Response> findAll() {
        return service.findAll().stream()
            .map(OwnerResponseFactory::create)
            .toList();
    }

}
