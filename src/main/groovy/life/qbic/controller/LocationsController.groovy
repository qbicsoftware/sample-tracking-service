package life.qbic.controller

import io.micronaut.context.annotation.Requires
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import life.qbic.datamodel.services.Contact
import life.qbic.datamodel.services.Location
import life.qbic.micronaututils.auth.Authentication
import life.qbic.service.ILocationService

import javax.annotation.security.RolesAllowed
import javax.inject.Inject

@Requires(beans = Authentication.class)
@Secured(SecurityRule.IS_AUTHENTICATED)
@Controller("/locations")
class LocationsController {

  private final ILocationService locService

  @Inject
  LocationsController(ILocationService locService) {
    this.locService = locService
  }

  /**
   * Endpoint for retrieving contact information for a user given an email address.
   *
   * @param email
   * @return an HTTPResponse with the associated contact
   * @deprecated As of 1.1.0 this method is marked as deprecated. Please avoid using it.
   */
  @Get(uri = "/contacts/{email}", produces = MediaType.APPLICATION_JSON)
  @RolesAllowed(["READER", "WRITER"])
  @Operation(summary = "Provides the contact information linked to an e-mail",
          description = "Provides detailed contact information that is linked to an e-mail",
          tags = "Contact")
  @ApiResponse(responseCode = "200", description = "Current contact associated with the email address",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = Contact.class)))
  @ApiResponse(responseCode = "400", description = "The provided e-mail address is invalid")
  @ApiResponse(responseCode = "401", description = "Unauthorized access")
  @ApiResponse(responseCode = "404", description = "Contact for the provided e-mail address not found")
  @ApiResponse(responseCode = "500", description = "Retrieval of contact information failed for an unknown reason")
  //@Deprecated(since=1.1.0, forRemoval=false) // works for Java11
  @Deprecated
  HttpResponse<Contact> contacts(@PathVariable('email') String email) {
    if (!RegExValidator.isValidMail(email)) {
      HttpResponse<Contact> res = HttpResponse.status(HttpStatus.BAD_REQUEST, "${email} is not a valid email address!")
      return res
    }
    try {
      Contact contact = locService.searchPersonByEmail(email)
      if (contact != null) {
        HttpResponse<Contact> res = HttpResponse.ok(contact)
        return res
      } else {
        String reason = "Email address ${email} was not found in the system!"
        HttpResponse<Contact> res = HttpResponse.status(HttpStatus.NOT_FOUND, reason);
        return res
      }
    }
    catch (Exception e) {
      return HttpResponse.status(HttpStatus.INTERNAL_SERVER_ERROR, e.message)
    }
    }

  @Get(uri = "/{user_id}", produces = MediaType.APPLICATION_JSON)
  @RolesAllowed(["READER"])
  @Operation(summary = "Provides the locations information linked to a user identifier",
          description = "Provides detailed locations information that is linked to a user",
          tags = "Location")
  @ApiResponse(responseCode = "200", description = "Location information associated with the user identifier is provided",
          content = @Content(
                  mediaType = "application/json",
                  array = @ArraySchema(schema = @Schema(implementation = Location.class))))
  @ApiResponse(responseCode = "400", description = "Bad Request. The provided user identification is invalid.")
  @ApiResponse(responseCode = "401", description = "Unauthorized access")
  @ApiResponse(responseCode = "404", description = "location information for the provided user identifier not found")
  @ApiResponse(responseCode = "500", description = "Retrieval of location information for the provided user failed for an unknown reason")
  HttpResponse<List<Location>> locations(@PathVariable('user_id') String userId) {
    HttpResponse<List<Location>> response
    List<Location> searchResult
    try {
      searchResult = locService.getLocationsForPerson(userId)
      if (searchResult != null) {
        response = HttpResponse.ok(searchResult)
      } else {
        response = HttpResponse.status(HttpStatus.NOT_FOUND, "Location information for user ${userId} was not found in the system!")
      }
    } catch (IllegalArgumentException ignored) {
      response = HttpResponse.status(HttpStatus.BAD_REQUEST, ignored.getMessage())
    } catch (Exception ignored) {
      response = HttpResponse.status(HttpStatus.INTERNAL_SERVER_ERROR, ignored.getMessage())
    }
    return response
  }

  @Get(uri = "/", produces = MediaType.APPLICATION_JSON)
  @Operation(summary = "Provides all available locations",
          description = "Provides all available locations",
          tags = "Location")
  @ApiResponse(responseCode = "200", description = "All available locations",
          content = @Content(
                  mediaType = "application/json",
                  array = @ArraySchema(schema = @Schema(implementation = Location.class))))
  @ApiResponse(responseCode = "401", description = "Unauthorized access")
  @ApiResponse(responseCode = "500", description = "Listing of available locations failed for an unknown reason")
  @RolesAllowed(["READER", "WRITER"])
  HttpResponse<List<Location>> listLocations() {
    try {
      List<Location> res = locService.listLocations()
      return HttpResponse.ok(res)
    }
    catch (Exception e) {
      HttpResponse.status(HttpStatus.INTERNAL_SERVER_ERROR, e)
    }
  }
}
