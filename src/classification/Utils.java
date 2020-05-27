package classification;

import organizestream.Instance;

public class Utils {

    //euclidian distance
    public static double distance(Instance i1, Instance i2) {
        if (i1.getAttributes().size() != i2.getAttributes().size()) {
            return Double.MAX_VALUE;
        }

        double distance = 0;

        for (int i = 0; i < i1.getAttributes().size(); i++) {
            distance += Math.pow(i1.getAttributes().get(i) - i2.getAttributes().get(i), 2);
        }

        return Math.sqrt(distance);
    }
}
