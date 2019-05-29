package model;


import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Type;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.net.URI;
import java.util.Objects;

@Entity
public class Module {

  @Id
  private String code;

  private String name;
  private URI uri;

  private String level;
  private String scqfLevel;
  private String fheqLevel;
  @Type(type="text")
  private String description;
  private int credits;
  private URI link;

  public Module(String name, String code, URI uri) {
    this.name = name;
    this.code = code;
    this.uri = uri;
  }

  public Module() {
  }

  public void setLevel(String level) {
    this.level = level;
  }

  public void setScqfLevel(String scqfLevel) {
    this.scqfLevel = scqfLevel;
  }

  public void setFheqLevel(String fheqLevel) {
    this.fheqLevel = fheqLevel;
  }

  public void setDescription(String description) {

    this.description = StringUtils.substringBeforeLast(description, " Browse qualifications in related");

  }

  public void setCredits(int credits) {
    this.credits = credits;
  }


  public void setLink(URI link) {
    this.link = link;
  }

  public URI getUri() {
    return uri;
  }

  public boolean validate() {

    if (code.length() > 7 || code.length() < 2) {
      return false;
    }

    if (name.length() < 6) {
      return false;
    }

    if (credits < 10 || credits > 60) {
      return false;
    }

    return true;
  }

  @Override
  public String toString() {
    return "Module{" +
            "name='" + name + '\'' +
            ", code='" + code + '\'' +
            ", uri=" + uri +
            ", level='" + level + '\'' +
            ", scqfLevel='" + scqfLevel + '\'' +
            ", fheqLevel='" + fheqLevel + '\'' +
            ", description='" + description + '\'' +
            ", credits=" + credits +
            ", link=" + link +
            '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Module module = (Module) o;
    return Objects.equals(code, module.code);
  }

  @Override
  public int hashCode() {

    return Objects.hash(code);
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public void setUri(URI uri) {
    this.uri = uri;
  }

  public String getLevel() {
    return level;
  }

  public String getScqfLevel() {
    return scqfLevel;
  }

  public String getFheqLevel() {
    return fheqLevel;
  }

  public String getDescription() {
    return description;
  }

  public int getCredits() {
    return credits;
  }

  public URI getLink() {
    return link;
  }
}
