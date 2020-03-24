package life.qbic.controller

import io.micronaut.context.annotation.Requires
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.context.annotation.Parameter
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.PathVariable
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import life.qbic.datamodel.services.Sample
import life.qbic.micronaututils.auth.Authentication

import javax.annotation.security.RolesAllowed
import javax.inject.Inject
import life.qbic.datamodel.services.Contact
import life.qbic.datamodel.services.Location
import life.qbic.service.ILocationService

@Requires(beans = Authentication.class)
@Secured(SecurityRule.IS_AUTHENTICATED)
@Controller("/locations")
class LocationsController {

  private final ILocationService locService

  @Inject
  LocationsController(ILocationService locService) {
    this.locService = locService
  }

  @Get(uri = "/contacts/{email}", produces = MediaType.APPLICATION_JSON)
  @RolesAllowed(["READER", "WRITER"])
  @Operation(summary = "Provides the contact information linked to an e-mail",
          description = "Provides detailed contact information that is linked to an e-mail",
          tags = "Contact")
  @ApiResponse(responseCode = "200", description = "Current contact associated with the email address",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = Contact.class)))
  @ApiResponse(responseCode = "400", description = "Invalid e-mail address")
  @ApiResponse(responseCode = "401", description = "Unauthorized access")
  @ApiResponse(responseCode = "404", description = "Contact not found")
  HttpResponse<Contact> contacts(@PathVariable('email') String email){
    if(!RegExValidator.isValidMail(email)) {
      HttpResponse<Contact> res = HttpResponse.badRequest("Not a valid email address!")
      return res
    } else {
      Contact contact = locService.searchPersonByEmail(email);
      if(contact!=null) {
        HttpResponse<Contact> res = HttpResponse.ok(contact)
        return res
      }
      else {
        HttpResponse<Contact> res = HttpResponse.notFound("Email address was not found in the system!")
        return res
      }
    }
  }

  @Get(uri = "/{contact_email}", produces = MediaType.APPLICATION_JSON)
  @Operation(summary = "Provides the locations information linked to an e-mail",
          description = "Provides detailed locations information that is linked to an e-mail",
          tags = "Contact")
  @ApiResponse(responseCode = "200", description = "Current locations associated with the email address",
          content = @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = Location.class)))
  @ApiResponse(responseCode = "400", description = "Invalid e-mail address")
  @ApiResponse(responseCode = "401", description = "Unauthorized access")
  @ApiResponse(responseCode = "404", description = "Location not found")
  @RolesAllowed(["READER", "WRITER"])
  HttpResponse<List<Location>> locations(@PathVariable('contact_email') String contact_email){
    if(!RegExValidator.isValidMail(contact_email)) {
      HttpResponse<Contact> res = HttpResponse.badRequest("Not a valid email address!")
      return res
    } else {
      List<Location> locations = locService.getLocationsForEmail(contact_email);
      return HttpResponse.ok(locations)
    }
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
  @RolesAllowed(["READER", "WRITER"])
  HttpResponse<List<Location>> listLocations() {
    List<Location> res = locService.listLocations()
    return HttpResponse.ok(res)
  }
}
