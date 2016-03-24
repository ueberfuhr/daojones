package de.ars.daojones.internal.drivers.notes.datahandler;

public class CharacterArrayDataHandler extends AbstractArrayDataHandler<Character> {

  @Override
  public Class<? extends Character[]> getKeyType() {
    return Character[].class;
  }

}
