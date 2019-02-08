package life.qbic

import io.micronaut.context.annotation.Parameter
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Produces
import life.qbic.model.Address
import life.qbic.model.Contact

import javax.inject.Inject

@Controller("/locations")
class LocationsController {

    //@Inject
    //private final Connection userdbConnection

    private final Address address

    @Inject LocationsController(Address address) {
        this.address = address
    }

    @Get("/contacts/{email}")
    @Produces(MediaType.APPLICATION_JSON)
    Contact contacts(@Parameter('email') String email){
        return new Contact(fullName: "Sven Fillinger", email: email, address: address)
    }

}
