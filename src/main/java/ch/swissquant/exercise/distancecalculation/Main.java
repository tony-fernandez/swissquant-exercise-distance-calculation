package ch.swissquant.exercise.distancecalculation;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.PriorityQueue;

public class Main {
	
	public static void main(String args[]) throws URISyntaxException {
		Point[] points = extractPoints();
		processClosests(points);
		processFurthest(points);
	}

	private static void processClosests(Point[] points) {
		long startTime = System.currentTimeMillis();

		Point[] closestsPoints = closests(points, new Point(100, 300), 10);
		
		for (Point point : closestsPoints) {
			System.out.println(point);
		}

		long endTime = System.currentTimeMillis();

		System.out.println(endTime - startTime);
	}

	private static void processFurthest(Point[] points) {
		long startTime = System.currentTimeMillis();

		Point[] closestsPoints = furthest(points, new Point(100, 300), 10);
		
		for (Point point : closestsPoints) {
			System.out.println(point);
		}

		long endTime = System.currentTimeMillis();

		System.out.println(endTime - startTime);
	}

	
	private static Point[] extractPoints() throws URISyntaxException {
		int totalNumberOfRecords = 10000000;
		Point[] allPoints = new Point[totalNumberOfRecords];

		try (FileChannel fc = FileChannel.open(Paths.get(Main.class.getResource("points").toURI()))) {
			MappedByteBuffer mbb = fc.map(MapMode.READ_ONLY, 0, totalNumberOfRecords * 4l);

			int[] points = new int[2];
			int pointCounter = 0;

			for (int i = 0; i < totalNumberOfRecords; i++) {

				points[pointCounter] = mbb.getInt();

				if (pointCounter == 1) {
					Point point = new Point(points[0], points[1]);
					allPoints[i] = point;
					pointCounter = 0;
					points = new int[2];
				}

				pointCounter++;
			}
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}

		return allPoints;
	}

	private static Point[] closests(Point[] points, Point origin, int k) {
		return points(points, origin, k, (p1, p2) -> Double.compare(distance(p1, origin), distance(p2, origin)));
	}

	private static Point[] furthest(Point[] points, Point origin, int k) {
		Comparator<Point> comparator = (p1, p2) -> Double.compare(distance(p1, origin), distance(p2, origin));
		return points(points, origin, k, comparator.reversed());
	}

	private static Point[] points(Point[] points, Point origin, int k, Comparator<Point> comparator) {
		PriorityQueue<Point> queue = new PriorityQueue<Point>(k, comparator);

		for (Point point : points) {
			if (point != null) {
				queue.add(point);
			}
		}

		Point[] res = new Point[k];
		for (int i = 0; i < k; i++) {
			res[i] = queue.poll();
		}
		return res;
	}

	private static double distance(Point point1, Point point2) {
		return Point2D.distance(point1.getX(), point1.getY(), point2.getX(), point2.getY());
	}

}
