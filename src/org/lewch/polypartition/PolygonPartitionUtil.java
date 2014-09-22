package org.lewch.polypartition;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 跨180度经线空间平面按跨180度经线分割为多个不跨180度经线空间平面
 * 
 * 在PostgreSQL数据库空间平台函数中，跨180度经线小平面被解析为大平面，此方法按180度经线将平面切分为多个小的且以解决问题
 * 
 * 方法返回空间数据规范的MutiPoly
 * 
 * @author Lewch
 * 
 */
public class PolygonPartitionUtil {

	public static void main(String[] args) {
		String str = "175 3,-177 2,-172 -5,171 -9,174 -2,175 3";
		String str2 = "175 3,-180 2,-172 -5,171 -9,174 -2,175 3";
		String str3 = "175 3,171 -9,174 -2,175 3";
		String str4 = "-175 3,-177 2,-172 -5,171 -9,174 -2,-175 3";
		String str5 = "-175 80, -170 75, 180 74,180 70,180 60,-178 55,170 40,-170 35,177 20,-168 10,"
				+ "165 0,150 40,-175 80";

		String str6 = "-170 20," + "-175 16," + "-178 18," + "180 17,"
				+ "-180 14," + "180 10," + "-178 8," + "180 4," + "176 3,"
				+ "-180 0," + "177 -4," + "173 -8," + "172 -12," + "-179 -15,"
				+ "177 -18," + "-176 -23," + "-166 -7," + "-170 20";

		// System.out.println(Double.parseDouble("3"));
		// System.out.println(Double.parseDouble("2"));
		// System.out.println(Double.parseDouble("3.2"));
		// System.out.println(Double.parseDouble("3"));
		// System.out.println(Double.parseDouble("3"));

		// String left =
		// "175.0 3.0,-177.0 2.0,-172.0 -5.0,171.0 -9.0,174.0 -2.0";
		String left = "175 3,-177 2,-172 -5,171 -9,174 -2";
		// String str4="";
		// System.out.println(partitionPolygonByInverseMeridian(str));
		// System.out.println(partitionPolygonByInverseMeridian(str2));
		// System.out.println(partitionPolygonByInverseMeridian(str3));
		// System.out.println(partitionPolygonByInverseMeridian(str4));
		// System.out.println(partitionPolygonByInverseMeridian(str5));
		// System.out.println(partitionPolygonByInverseMeridian(str6));
		System.out.println(buildIntersectSqlQueryCondition("quadrangle", str6));
		// System.out.println(buildIntersectSqlQueryCondition("quadrangle",left));
	}

	public static String buildIntersectSqlQueryCondition(String targetColumn,
			String longLatStr) {
		List<String> polys = getPolygonSql(longLatStr);
		if (polys == null || polys.size() == 0) {
			return null;
		}
		StringBuilder sb = new StringBuilder();
		for (String poly : polys) {
			sb.append(" or ");
			sb.append(" ST_Intersects( ");
			sb.append(targetColumn);
			sb.append(" , ");
			sb.append(poly);
			sb.append(" )='t' ");
		}

		return sb.toString();
	}

	private static List<String> getPolygonSql(String longLatStr) {
		List<String> res = new ArrayList<String>();

		List<List<Point2D>> list = partitionPolygonByInverseMeridian(longLatStr);
		if (list == null || list.size() == 0) {
			return null;
		}

		for (List<Point2D> poly : list) {
			String polyStr = poly.toString().trim();
			polyStr = polyStr.substring(1, polyStr.length() - 1);

			polyStr = " ST_PolyFromText('POLYGON((" + polyStr + "))',4326) ";

			res.add(polyStr);
		}

		return res;
	}

	/**
	 * 按逆子午线(180度经线)划分多边形
	 * 
	 * @param longLatStr
	 * @return
	 */
	private static List<List<Point2D>> partitionPolygonByInverseMeridian(
			String longLatStr) {
		// 整体多边形各点集合
		List<Point2D> points = longLatStrToPointList(longLatStr);
		// 经分割后各小块区域顶点集
		List<List<Point2D>> partitionArealist = new ArrayList<List<Point2D>>();

		if (points == null || points.size() == 0) {
			return null;
		}

		// 整体顶点第一点与最后一个点是否重合，理论上要求重合

		Point2D pStart = points.get(0);
		Point2D pEnd = points.get(points.size() - 1);
		if (pStart.getX() == pEnd.getX()// 第一点与最后一点重合
				&& pStart.getY() == pEnd.getY()) {

		} else {// 第一点与最后一点不重合
			// 构造封闭图形,即使第一点与最后一点重合
			points.add(pStart);
		}

		// 整体从第一个顶点所引出到最后一点（与第一个顶点重合的点）区域顶点集
		List<Point2D> baseArea = new ArrayList<Point2D>();
		// 新构建区域顶点集
		List<Point2D> newArea = new ArrayList<Point2D>();
		boolean addPointToNewArea = false;
		for (int i = 0, len = points.size(); i < len; i++) {
			// 整体顶点集的第一个点
			if (i == 0) {
				baseArea.add(points.get(i));
				continue;
			}

			// 当前点
			Point2D curPoint = points.get(i);
			// 上一点
			Point2D prePoint = points.get(i - 1);
			// 点在180度经线上
			if (curPoint.getX() == 180 || curPoint.getX() == -180) {
				// 基础区域添加当前点根据基础区域点所在位置决定，用同一区域内的180的值表示
				if (pStart.getX() >= 0) {
					baseArea.add(new Point2D(180, curPoint.getY()));
				} else {
					baseArea.add(new Point2D(-180, curPoint.getY()));
				}

				// 新建区域已构建时，当前点在180度经线上且新建区域与与当前在180度经线上的点不能构建有效多边形则取消新建区域
				// 若能构建有效多边形则完成新建多边形构建并重新初始化
				if (addPointToNewArea) {// 新区域已构建(至少有一点)
					Point2D preNewAreaP = newArea.get(newArea.size() - 1);
					if (newArea.size() == 1) {// 不能构成有效多边形
						newArea.clear();
						// 以下两行可删除
						newArea = null;
						newArea = new ArrayList<Point2D>();

						//
						addPointToNewArea = false;
					} else {
						if (preNewAreaP.getX() >= 0) {
							newArea.add(new Point2D(180, curPoint.getY()));
						} else {
							newArea.add(new Point2D(-180, curPoint.getY()));
						}
						// 完成新建区域的构建
						newArea.add(newArea.get(0));
						// divisionArealist.add(newArea);
						addPartitionArealistVal(partitionArealist, newArea);

						newArea.clear();
						// 以下两行可删除
						newArea = null;
						newArea = new ArrayList<Point2D>();

						//
						addPointToNewArea = false;
					}
				} else {
					addPointToNewArea = true;
					// 该点需要根据下一点修正
					newArea.add(new Point2D(180, curPoint.getY()));
				}

			} else if (curPoint.getX() * prePoint.getX() < 0) {// 点与上一点跨180度经线
				Point2D intersectP = getInterPointOfConnectLineWithLong180(
						curPoint, prePoint);
				if (pStart.getX() >= 0) {
					baseArea.add(new Point2D(180, intersectP.getY()));
				} else {
					baseArea.add(new Point2D(-180, intersectP.getY()));
				}

				// 新建区域已构建时，当前点在180度经线上且新建区域与与当前在180度经线上的点不能构建有效多边形则取消新建区域
				// 若能构建有效多边形则完成新建多边形构建并重新初始化
				if (addPointToNewArea) {// 新区域已构建
					Point2D preNewAreaP = newArea.get(newArea.size() - 1);
					if (newArea.size() == 1) {// 不能构成有效多边形
						newArea.clear();
						// 以下两行可删除
						newArea = null;
						newArea = new ArrayList<Point2D>();

						//
						addPointToNewArea = false;
					} else {

						if (preNewAreaP.getX() >= 0) {
							newArea.add(new Point2D(180, intersectP.getY()));
						} else {
							newArea.add(new Point2D(-180, intersectP.getY()));
						}
						// 完成新建区域的构建
						newArea.add(newArea.get(0));
						// divisionArealist.add(newArea);
						addPartitionArealistVal(partitionArealist, newArea);

						newArea.clear();
						// 以下两行可删除
						newArea = null;
						newArea = new ArrayList<Point2D>();

						//
						addPointToNewArea = false;

						baseArea.add(curPoint);
					}
				} else {
					addPointToNewArea = true;

					if (curPoint.getX() >= 0) {
						newArea.add(new Point2D(180, intersectP.getY()));
					} else {
						newArea.add(new Point2D(-180, intersectP.getY()));
					}
					newArea.add(curPoint);
				}

			} else {

				if (addPointToNewArea) {// 新区域已构建
					Point2D preNewAreaP = newArea.get(newArea.size() - 1);
					if (preNewAreaP.getX() == 180 || preNewAreaP.getX() == -180) {
						if (curPoint.getX() >= 0) {
							newArea.get(newArea.size() - 1).setX(180);
						} else {
							newArea.get(newArea.size() - 1).setX(-180);
						}
					}
					newArea.add(curPoint);
				} else {
					baseArea.add(curPoint);
				}
			}
		}

		partitionArealist.add(baseArea);

		return partitionArealist;

	}

	private static List<List<Point2D>> addPartitionArealistVal(
			List<List<Point2D>> partitionArealist, List<Point2D> newArea) {
		List<Point2D> area = new ArrayList<Point2D>();
		if (newArea != null && newArea.size() != 0) {
			for (int i = 0, len = newArea.size(); i < len; i++) {
				area.add(newArea.get(i));
			}
		}
		partitionArealist.add(area);
		return partitionArealist;
	}

	/**
	 * 获取两参数连接线与180度经线交点
	 * 
	 * @param p1
	 * @param p2
	 * @return
	 */
	private static Point2D getInterPointOfConnectLineWithLong180(Point2D p1,
			Point2D p2) {
		// 将180度经线两侧的两点对应映射到0度经线两侧的两点
		p1 = GeoPointUtil.pointMapLong0Point(p1);
		p2 = GeoPointUtil.pointMapLong0Point(p2);

		// 0度经线上的两点
		Point2D p3 = new Point2D(0, 0);
		Point2D p4 = new Point2D(0, 1);

		Param pm1 = StraightLineUtil.calParam(p1, p2);
		Param pm2 = StraightLineUtil.calParam(p3, p4);
		// 两直线交点（位于0度经线上）
		Point2D rp = StraightLineUtil.getIntersectPoint(pm1, pm2);

		// 0度经线上的交点对应到180度经线上并返回
		rp = GeoPointUtil.pointOnLong0ToPointOnLong180(rp);

		// System.out.println("交点为: (" + rp.getX() + "," + rp.getY() + ")");

		return rp;
	}

	private static List<Point2D> longLatStrToPointList(String longLatStr) {
		if (longLatStr == null || longLatStr.trim().length() == 0) {
			return null;
		}
		List<Point2D> list = new ArrayList<Point2D>();
		String[] points = longLatStr.split(",");
		if (points != null && points.length > 0) {
			for (String longLat : points) {
				try {
					String[] ll = longLat.trim().split(" ");
					// System.out.println(ll);
					String longVal = ll[0].trim();
					if (longVal.indexOf(".") == -1) {
						longVal = longVal + ".0";
					}
					String latVal = ll[1].trim();
					if (latVal.indexOf(".") == -1) {
						latVal = latVal + ".0";
					}

					Point2D p = new Point2D(Double.parseDouble(longVal),
							Double.parseDouble(latVal));
					list.add(p);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		return list;
	}
}
