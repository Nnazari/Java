import java.awt.geom.Rectangle2D;

public class Tricorn extends FractalGenerator{
    public static final int MAX_ITERATIONS_COUNT = 2000;
    @Override
    public void getInitialRange(Rectangle2D.Double range) {
        range.x = -2;
        range.y = -2;
        range.width = 4;
        range.height = 4;
    }



    @Override
    public int numIterations(double x, double y) {
        int count = 0;
        double re = 0;
        double im = 0;
        double z_n2 = 0;

        while (count < MAX_ITERATIONS_COUNT && z_n2 < 4) {
            count++;

            double nextRe = re*re - im*im + x;
            double nextIm = -2 * re * im + y;

            z_n2 = nextRe*nextRe + nextIm*nextIm;

            re = nextRe;
            im = nextIm;
        }

        return count < MAX_ITERATIONS_COUNT ? count : -1;
    }
    public static String getString() {
        return "Tricorn";
    }
}
