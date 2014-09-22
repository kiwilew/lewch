package org.lewch.polypartition;

/**
 * 直接方程工具類
 * 
 * @author Lewch
 * 
 */
public class StraightLineUtil {

	/**
	 * 计算两点的直线方程的参数a,b,c
	 * 
	 * @param p1
	 * @param p2
	 * @return
	 */
	public static Param calParam(Point2D p1, Point2D p2) {
		double a, b, c;
		double x1 = p1.getX(), y1 = p1.getY(), x2 = p2.getX(), y2 = p2.getY();
		a = y2 - y1;
		b = x1 - x2;
		c = (x2 - x1) * y1 - (y2 - y1) * x1;
		if (b < 0) {
			a *= -1;
			b *= -1;
			c *= -1;
		} else if (b == 0 && a < 0) {
			a *= -1;
			c *= -1;
		}
		return new Param(a, b, c);
	}

	/**
	 * 计算两条直线的交点
	 * 
	 * @param pm1
	 * @param pm2
	 * @return
	 */
	public static Point2D getIntersectPoint(Param pm1, Param pm2) {
		return getIntersectPoint(pm1.a, pm1.b, pm1.c, pm2.a, pm2.b, pm2.c);
	}

	public static Point2D getIntersectPoint(double a1, double b1, double c1,
			double a2, double b2, double c2) {
		Point2D p = null;
		double m = a1 * b2 - a2 * b1;
		if (m == 0) {
			return null;
		}
		double x = (c2 * b1 - c1 * b2) / m;
		double y = (c1 * a2 - c2 * a1) / m;
		p = new Point2D(x, y);
		return p;
	}

	public static void main(String[] args) {
		Point2D p1 = new Point2D(-1, -1);
		Point2D p2 = new Point2D(1, 1);

		Point2D p3 = new Point2D(0, 0);
		Point2D p4 = new Point2D(0, 1);

		Param pm1 = calParam(p1, p2);
		Param pm2 = calParam(p3, p4);
		Point2D rp = getIntersectPoint(pm1, pm2);
		System.out.println("他们的交点为: (" + rp.getX() + "," + rp.getY() + ")");
	}

}

/**
 * 平面二元方向参数对象
 * 
 * @author Lewch
 * 
 */
class Param {
	public double a;
	public double b;
	public double c;

	public Param(double a, double b, double c) {
		this.a = a;
		this.b = b;
		this.c = c;
	}
}

/**
 * 平面点坐标
 * 
 * @author Lewch
 * 
 */
class Point2D {
	private double x;
	private double y;

	public Point2D() {
	}

	public Point2D(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	@Override
	public String toString() {
		// return "(x:" + this.x + ",y:" + this.y + ")";
		return this.x + " " + this.y;
	}

}
