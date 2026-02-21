package be.envano.petclinic.pet.rest;

import be.envano.petclinic.pet.PetException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;

@RestControllerAdvice(basePackageClasses = PetRestController.class)
class PetRestExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    ProblemDetail handleValidation(IllegalArgumentException exception) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.getMessage());
        detail.setTitle("Validation failed");
        detail.setType(URI.create("urn:petclinic:pet:validation"));
        return detail;
    }

    @ExceptionHandler(PetException.NotFound.class)
    ProblemDetail handleNotFound(PetException.NotFound exception) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());
        detail.setTitle("Pet not found");
        detail.setType(URI.create("urn:petclinic:pet:not-found"));
        return detail;
    }

    @ExceptionHandler(PetException.VersionConflict.class)
    ProblemDetail handleVersionConflict(PetException.VersionConflict exception) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, exception.getMessage());
        detail.setTitle("Version conflict");
        detail.setType(URI.create("urn:petclinic:pet:version-conflict"));
        return detail;
    }

    @ExceptionHandler(PetException.DuplicateName.class)
    ProblemDetail handleDuplicateName(PetException.DuplicateName exception) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, exception.getMessage());
        detail.setTitle("Duplicate pet name");
        detail.setType(URI.create("urn:petclinic:pet:duplicate-name"));
        return detail;
    }

}
