package es.udc.pcv.backend.rest.dtos;

import java.util.List;

public class OdsWithCollaborationAreaDto {
  private List<OdsSummaryDTO> odsSummary;
  private List<CollaborationAreaDTO> areaList;

  public OdsWithCollaborationAreaDto() {
  }

  public List<OdsSummaryDTO> getOdsSummary() {
    return odsSummary;
  }

  public void setOdsSummary(List<OdsSummaryDTO> odsSummary) {
    this.odsSummary = odsSummary;
  }

  public List<CollaborationAreaDTO> getAreaList() {
    return areaList;
  }

  public void setAreaList(List<CollaborationAreaDTO> areaList) {
    this.areaList = areaList;
  }
}
