public class CalculTQR {

    public static int calculTQR(double M) {
        int tqr;
        if (M < 100) {
            tqr = 0;
        } else if (100 <= M && M <= 1000) {
            tqr = 20;
        } else if (1000 < M && M <= 5000) {
            tqr = 30;
        } else if (5000 < M && M <= 10000) {
            tqr = 50;
        } else if (10000 < M && M <= 50000) {
            tqr = 100;
        } else {
            int n = (int) (M / 50000);
            tqr = 50 * (n + 2);
        }
        return tqr;
    }

    public static void main(String[] args) {
        double valeurM = 7500.0; // Remplacez cette valeur par celle que vous souhaitez
        int resultattqr = calculTQR(valeurM);
        System.out.println("La Valeur du TQR pour " + valeurM + " est : " + resultattqr);
    }
}
