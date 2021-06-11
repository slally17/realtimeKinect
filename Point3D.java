public class Point3D implements Compare3D<Point3D> {

	private long xcoord;
	private long ycoord;
	private long zcoord;
  private long count;

	public Point3D() {
		xcoord = 0;
		ycoord = 0;
		zcoord = 0;
    count = 0;
	}
	public Point3D(long x, long y, long z) {
		xcoord = x;
		ycoord = y;
		zcoord = z;
    count = 0;
	}
  public Point3D(long x, long y, long z, long count) {
    xcoord = x;
    ycoord = y;
    zcoord = z;
    this.count = count;
  }
	public long getX() {
		return xcoord;
	}
	public long getY() {
		return ycoord;
	}
	public long getZ() {
		return zcoord;
	}
  public long getCount()  {
    return count;
  }

	public Direction3D directionFrom(long X, long Y, long Z) {
		if(xcoord > X && ycoord >= Y && zcoord >= Z) {
			return Direction3D.NEF;
		}
		else if(xcoord == X && ycoord == Y && zcoord == Z) {
			return Direction3D.NEF;
		}
		else if(xcoord <= X && ycoord > Y && zcoord >= Z) {
			return Direction3D.NWF;
		}
		else if(xcoord < X && ycoord <= Y && zcoord >= Z) {
			return Direction3D.SWF;
		}
		else if(xcoord >= X && ycoord < Y && zcoord >= Z) {
			return Direction3D.SEF;
		}
		else if(xcoord > X && ycoord >= Y && zcoord < Z) {
			return Direction3D.NEB;
		}
		else if(xcoord <= X && ycoord > Y && zcoord < Z) {
			return Direction3D.NWB;
		}
		else if(xcoord < X && ycoord <= Y && zcoord < Z) {
			return Direction3D.SWB;
		}
		else if(xcoord >= X && ycoord < Y && zcoord < Z) {
			return Direction3D.SEB;
		}
		else {
			return Direction3D.NOQUADRANT;
		}
	}

	public Direction3D inQuadrant(float xLo, float xHi, float yLo, float yHi, float zLo, float zHi) {
		if(xcoord > (xHi-xLo)/2 + xLo && ycoord >= (yHi-yLo)/2 + yLo && zcoord >= (zHi-zLo)/2 + zLo) {
			return Direction3D.NEF;
		}
		else if(xcoord == (xHi-xLo)/2 + xLo && ycoord == (yHi-yLo)/2 + yLo && zcoord == (zHi-zLo)/2 + zLo) {
			return Direction3D.NEF;
		}
		else if(xcoord <= (xHi-xLo)/2 + xLo && ycoord > (yHi-yLo)/2 + yLo && zcoord >= (zHi-zLo)/2 + zLo) {
			return Direction3D.NWF;
		}
		else if(xcoord < (xHi-xLo)/2 + xLo && ycoord <= (yHi-yLo)/2 + yLo && zcoord >= (zHi-zLo)/2 + zLo) {
			return Direction3D.SWF;
		}
		else if(xcoord >= (xHi-xLo)/2 + xLo && ycoord < (yHi-yLo)/2 + yLo && zcoord >= (zHi-zLo)/2 + zLo) {
			return Direction3D.SEF;
		}
		else if(xcoord > (xHi-xLo)/2 + xLo && ycoord >= (yHi-yLo)/2 + yLo && zcoord < (zHi-zLo)/2 + zLo) {
			return Direction3D.NEB;
		}
		else if(xcoord <= (xHi-xLo)/2 + xLo && ycoord > (yHi-yLo)/2 + yLo && zcoord < (zHi-zLo)/2 + zLo) {
			return Direction3D.NWB;
		}
		else if(xcoord < (xHi-xLo)/2 + xLo && ycoord <= (yHi-yLo)/2 + yLo && zcoord < (zHi-zLo)/2 + zLo) {
			return Direction3D.SWB;
		}
		else if(xcoord >= (xHi-xLo)/2 + xLo && ycoord < (yHi-yLo)/2 + yLo && zcoord < (zHi-zLo)/2 + zLo) {
			return Direction3D.SEB;
		}
		else {
			return Direction3D.NOQUADRANT;
		}
	}

	public boolean inBox(float xLo, float xHi, float yLo, float yHi, float zLo, float zHi) {
		if(xcoord <= xHi && xcoord >= xLo && ycoord <= yHi && ycoord >= yLo && zcoord <= zHi && zcoord >= zLo) {
			return true;
		}
		else {
			return false;
		}
	}

	public String toString() {
      return "(" + xcoord + ", " + ycoord + ", " + zcoord + ")";
	}

	public boolean equals(Object o) {
		if(o == null) {
			return false;
		}

		if(o.getClass().equals(this.getClass())) {
			Point3D other = (Point3D) o;
			if(xcoord == other.xcoord && ycoord == other.ycoord && zcoord == other.zcoord) {
				return true;
			}
		}
		return false;
	}
}
