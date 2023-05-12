package es.udc.pcv.backend.model.to;

import org.springframework.core.io.Resource;

public class ResourceWithType {
  private Resource resource;
  private String extension;

  public ResourceWithType(Resource resource, String extension) {
    this.resource = resource;
    this.extension = extension;
  }

  public Resource getResource() {
    return resource;
  }

  public void setResource(Resource resource) {
    this.resource = resource;
  }

  public String getExtension() {
    return extension;
  }

  public void setExtension(String extension) {
    this.extension = extension;
  }
}
