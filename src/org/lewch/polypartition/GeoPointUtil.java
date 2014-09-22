package org.lewch.polypartition;

public class GeoPointUtil {

	/**
	 * 180度经线两侧的两点对应映射到0度经线两侧的两点
	 * 
	 * @param point
	 * @return
	 */
	public static Point2D pointMapLong0Point(Point2D point) {
		Point2D newPoint = new Point2D();
		double newX = 0.0;
		if (point.getX() > 0) {
			newX = 180.0 - point.getX();
		} else {
			newX = -180.0 - point.getX();
		}
		newPoint.setX(newX);
		newPoint.setY(point.getY());

		return newPoint;
	}

	/**
	 * 0度经线上的交点对应到180度经线上
	 * 
	 * @param point
	 * @return
	 */
	public static Point2D pointOnLong0ToPointOnLong180(Point2D point) {
		Point2D newPoint = new Point2D();
		if (point.getX() == 0) {
			newPoint.setX(180);
			newPoint.setY(point.getY());
			return newPoint;
		} else {
			return point;
		}

	}

	// public static void main(String[] args) {
	// Point2D p1 = new Point2D(170, 1);
	// Point2D p2 = new Point2D(165, 1);
	// Point2D p3 = new Point2D(-170, 1);
	// Point2D p4 = new Point2D(-155, 1);
	// Point2D p5 = new Point2D(180, 1);
	// Point2D p6 = new Point2D(-180, 1);
	// Point2D p7 = new Point2D(0, 1);
	// System.out.println(pointMapLong0Point(p1));
	// System.out.println(pointMapLong0Point(p2));
	// System.out.println(pointMapLong0Point(p3));
	// System.out.println(pointMapLong0Point(p4));
	// System.out.println(pointMapLong0Point(p5));
	// System.out.println(pointMapLong0Point(p6));
	// System.out.println(pointMapLong0Point(p7));
	// }

}
