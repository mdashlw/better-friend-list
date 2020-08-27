package ru.mdashlw.enelix.updater;

public final class ModInfo {

  private final String name;
  private final String author;

  public ModInfo(final String name, final String author) {
    this.name = name;
    this.author = author;
  }

  public String getName() {
    return this.name;
  }

  public String getAuthor() {
    return this.author;
  }

  @Override
  public String toString() {
    return "ModInfo{" +
        "name='" + this.name + '\'' +
        ", author='" + this.author + '\'' +
        '}';
  }
}
