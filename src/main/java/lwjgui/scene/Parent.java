package lwjgui.scene;

import lwjgui.collections.ObservableList;

public abstract class Parent extends Node { 
   
   /**
   *
   * @return unmodifiable list of children.
   */
  public ObservableList<Node> getChildrenUnmodifyable() {
      return new ObservableList<Node>(super.getChildren());
  }
}
