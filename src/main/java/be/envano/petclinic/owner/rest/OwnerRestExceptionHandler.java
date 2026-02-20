package be.envano.petclinic.owner.rest;

import be.envano.petclinic.owner.OwnerException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;

@RestControllerAdvice(basePackageClasses = OwnerRestController.class)
class OwnerRestExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    ProblemDetail handleValidation(IllegalArgumentException exception) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.getMessage());
        detail.setTitle("Validation failed");
        detail.setType(URI.create("urn:petclinic:owner:validation"));
        return detail;
    }

    @ExceptionHandler(OwnerException.NotFound.class)
    ProblemDetail handleNotFound(OwnerException.NotFound exception) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());
        detail.setTitle("Owner not found");
        detail.setType(URI.create("urn:petclinic:owner:not-found"));
        return detail;
    }

    @ExceptionHandler(OwnerException.VersionConflict.class)
    ProblemDetail handleVersionConflict(OwnerException.VersionConflict exception) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, exception.getMessage());
        detail.setTitle("Version conflict");
        detail.setType(URI.create("urn:petclinic:owner:version-conflict"));
        return detail;
    }

    @ExceptionHandler(OwnerException.DuplicateName.class)
    ProblemDetail handleDuplicateName(OwnerException.DuplicateName exception) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, exception.getMessage());
        detail.setTitle("Duplicate owner name");
        detail.setType(URI.create("urn:petclinic:owner:duplicate-name"));
        return detail;
    }

}
