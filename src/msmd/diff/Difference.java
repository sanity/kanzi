package msmd.diff;

/**
 *  Description of the Class
 *
 * @author     ian
 * @created    September 25, 2002
 */
public interface Difference {
  /**
   *  Description of the Method
   *
   * @param  m  Description of the Parameter
   * @return    Description of the Return Value
   */
  public abstract Difference remap(Remapper m);


  public abstract void markup();


  /**  Applies this difference to a document */
  public abstract void apply();
}

