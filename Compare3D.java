public interface Compare3D<T> {

   // Returns the x-coordinate field of the user data object.
   public long getX();

   // Returns the y-coordinate field of the user data object.
   public long getY();

	 // Returns the z-coordinate field of the user data object
	 public long getZ();

   // Returns indicator of the direction to the user data object from the
   // location (X, Y) specified by the parameters.
   // The indicators are defined in the enumeration Direction, and are used
   // as follows:
   //
   //    NE:  locations are the same, or vector from (X, Y) to user data object
   //         has direction in [0, 90) degrees
   //    NW:  vector from (X, Y) to user data object has direction in [90, 180)
   //    SW:  vector from (X, Y) to user data object has direction in [180, 270)
   //    SE:  vector from (X, Y) to user data object has direction in [270, 360)
   //
   public Direction3D directionFrom(long X, long Y, long Z);

   // Returns indicator of which quadrant of the box specified by the
   // parameters that user data object lies in.
   // The indicators are defined in the enumeration Direction, and are used
   // as follows, relative to the center of the rectangle:
   //
   //    NEF:  user data object lies in NEF quadrant, including non-negative
   //         x-axis, but not the positive y-axis
   //    NWF:  user data object lies in the NWF quadrant, including the positive
   //         y-axis, but not the negative x-axis
   //    SWF:  user data object lies in the SWF quadrant, including the negative
   //         x-axis, but not the negative y-axis
   //    SEF:  user data object lies in the SEF quadrant, including the negative
   //         y-axis, but not the positive x-axis
	 //    NEB:  user data object lies in NEF quadrant, including non-negative
   //         x-axis, but not the positive y-axis
   //    NWB:  user data object lies in the NWF quadrant, including the positive
   //         y-axis, but not the negative x-axis
   //    SWB:  user data object lies in the SWF quadrant, including the negative
   //         x-axis, but not the negative y-axis
   //    SEB:  user data object lies in the SEF quadrant, including the negative
   //         y-axis, but not the positive x-axis
   //    NOQUADRANT:  user data object lies outside the specified rectangle
   //
   public Direction3D inQuadrant(float xLo, float xHi, float yLo, float yHi, float zLo, float zHi);

   // Returns true iff the user data object lies within or on the boundaries
   // of the box specified by the parameters.
   public boolean   inBox(float xLo, float xHi, float yLo, float yHi, float zLo, float zHi);
}
