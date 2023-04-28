package es.udc.pcv.backend.rest.dtos;

public class PageableDto {
  private int page=0;
  private int size=10;
  private String sortValue;
  private String sortOrder;

  public PageableDto() {
  }

  public PageableDto(int page, int size, String sortValue, String sortOrder) {
    this.page = page;
    this.size = size;
    this.sortValue = sortValue;
    this.sortOrder = sortOrder;
  }

  public int getPage() {
    return page;
  }

  public void setPage(int page) {
    this.page = page;
  }

  public int getSize() {
    return size;
  }

  public void setSize(int size) {
    this.size = size;
  }

  public String getSortValue() {
    return sortValue;
  }

  public void setSortValue(String sortValue) {
    this.sortValue = sortValue;
  }

  public String getSortOrder() {
    return sortOrder;
  }

  public void setSortOrder(String sortOrder) {
    this.sortOrder = sortOrder;
  }
}
