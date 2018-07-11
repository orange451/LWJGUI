package lwjgui.scene;

import lwjgui.collections.ObservableList;

public abstract class Parent extends Node {
    /**
    *
    * @return unmodifiable list of children.
    */
   @Override
   public ObservableList<Node> getChildren() {
       return new ObservableList<Node>(super.getChildren());
   }
}
