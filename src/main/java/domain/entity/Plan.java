package domain.entity;

public class Plan {
  private Long id;
  private String name;
  private String description;
  private int cpus;
  private int ram;
  private int disk;

  public Plan() {
  }

  public Plan(Long id, String name, String description, int cpus, int ram, int disk) {
    this.id = id;
    this.name = name;
    this.description = description;
    this.cpus = cpus;
    this.ram = ram;
    this.disk = disk;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public int getCpus() {
    return cpus;
  }

  public void setCpus(int cpus) {
    this.cpus = cpus;
  }

  public int getRam() {
    return ram;
  }

  public void setRam(int ram) {
    this.ram = ram;
  }

  public int getDisk() {
    return disk;
  }

  public void setDisk(int disk) {
    this.disk = disk;
  }
}
