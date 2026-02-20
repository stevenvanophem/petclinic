package be.envano.petclinic.specialty.rest;

import be.envano.petclinic.specialty.SpecialtyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;

@RestControllerAdvice(basePackageClasses = SpecialtyRestController.class)
class SpecialtyRestExceptionHandler {

    @ExceptionHandler({
        IllegalArgumentException.class
    })
    ProblemDetail handleValidation(Exception exception) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.getMessage());
        detail.setType(URI.create("urn:petclinic:specialty:validation"));
        detail.setTitle("Validation failed");
        return detail;
    }

    @ExceptionHandler
    ProblemDetail handleNotFound(SpecialtyException.NotFound exception) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());
        detail.setType(URI.create("urn:petclinic:specialty:not-found"));
        detail.setTitle("Specialty not found");
        return detail;
    }

    @ExceptionHandler
    ProblemDetail handleVersionConflict(SpecialtyException.VersionConflict exception) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, exception.getMessage());
        detail.setType(URI.create("urn:petclinic:specialty:version-conflict"));
        detail.setTitle("Version conflict");
        return detail;
    }

    @ExceptionHandler
    ProblemDetail handleDuplicateName(SpecialtyException.DuplicateName exception) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, exception.getMessage());
        detail.setType(URI.create("urn:petclinic:specialty:duplicate-name"));
        detail.setTitle("Duplicate specialty name");
        return detail;
    }

}
