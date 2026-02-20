package be.envano.petclinic.vet.rest;

import be.envano.petclinic.vet.VetException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;

@RestControllerAdvice(basePackageClasses = VetRestController.class)
class VetRestExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    ProblemDetail handleValidation(IllegalArgumentException exception) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.getMessage());
        detail.setTitle("Validation failed");
        detail.setType(URI.create("urn:petclinic:vet:validation"));
        return detail;
    }

    @ExceptionHandler(VetException.NotFound.class)
    ProblemDetail handleNotFound(VetException.NotFound exception) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());
        detail.setTitle("Vet not found");
        detail.setType(URI.create("urn:petclinic:vet:not-found"));
        return detail;
    }

    @ExceptionHandler(VetException.VersionConflict.class)
    ProblemDetail handleVersionConflict(VetException.VersionConflict exception) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, exception.getMessage());
        detail.setTitle("Version conflict");
        detail.setType(URI.create("urn:petclinic:vet:version-conflict"));
        return detail;
    }

    @ExceptionHandler(VetException.DuplicateName.class)
    ProblemDetail handleDuplicateName(VetException.DuplicateName exception) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, exception.getMessage());
        detail.setTitle("Duplicate vet name");
        detail.setType(URI.create("urn:petclinic:vet:duplicate-name"));
        return detail;
    }

}
