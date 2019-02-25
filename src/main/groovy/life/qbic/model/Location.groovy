package life.qbic.model

import com.fasterxml.jackson.annotation.JsonProperty
import io.micronaut.context.annotation.Property
import io.micronaut.context.annotation.Requires
import java.text.DateFormat
import java.text.SimpleDateFormat
import javax.inject.Singleton

class Location  {

  private String name
  private String responsiblePerson
  private String responsibleEmail
  private Address address
  private Status status
  private Date arrivalDate
  private Date forwardDate

  /**
   * Get name
   * @return name
   **/
  @JsonProperty("name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Location name(String name) {
    this.name = name;
    return this;
  }

  /**
   * Get responsiblePerson
   * @return responsiblePerson
   **/
  @JsonProperty("responsible_person")
  public String getResponsiblePerson() {
    return responsiblePerson;
  }

  public void setResponsiblePerson(String responsiblePerson) {
    this.responsiblePerson = responsiblePerson;
  }

  public Location responsiblePerson(String responsiblePerson) {
    this.responsiblePerson = responsiblePerson;
    return this;
  }
  
  /**
   * Get responsibleEmail
   * @return responsibleEmail
   **/
  @JsonProperty("responsible_person_email")
  public String getResponsibleEmail() {
    return responsibleEmail;
  }

  public void setResponsibleEmail(String responsibleEmail) {
    this.responsibleEmail = responsibleEmail;
  }

  public Location responsibleEmail(String responsibleEmail) {
    this.responsibleEmail = responsibleEmail;
    return this;
  }
  
  /**
   * Get address
   * @return address
   **/
  @JsonProperty("address")
  public Address getAddress() {
    return address;
  }

  public void setAddress(Address address) {
    this.address = address;
  }

  public Location address(Address address) {
    this.address = address;
    return this;
  }

  /**
   * Get sample status
   * @return status
   **/
  @JsonProperty("sample_status")
  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  public Location status(Status status) {
    this.status = status;
    return this;
  }

  /**
   * Get arrival_date
   * @return arrivalDate
   **/
  @JsonProperty("arrival_date")
  public Date getArrivalDate() {
    return arrivalDate;
  }

  public void setArrivalDate(Date arrivalDate) {
    this.arrivalDate = arrivalDate;
  }

  public Location arrivalDate(Date arrivalDate) {
    this.arrivalDate = arrivalDate;
    return this;
  }

  /**
   * Get forward_date
   * @return forwardDate
   **/
  @JsonProperty("forward_date")
  public Date getforwardDate() {
    return forwardDate;
  }

  public void setforwardDate(Date forwardDate) {
    this.forwardDate = forwardDate;
  }

  public Location forwardDate(Date forwardDate) {
    this.forwardDate = forwardDate;
    return this;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Location {\n");

    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    responsible person: ").append(toIndentedString(responsiblePerson)).append("\n");
    sb.append("    address: ").append(toIndentedString(address)).append("\n");
    sb.append("    sample status: ").append(toIndentedString(status)).append("\n");
    sb.append("    arrival date: ").append(toIndentedString(parseDate(arrivalDate))).append("\n");
    sb.append("    forward date: ").append(toIndentedString(parseDate(forwardDate))).append("\n");
    sb.append("}");
    return sb.toString();
  }

  public String parseDate(Date date) {
    return date;
//    DateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
//    return df.format(date);
  }
  
  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private static String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

