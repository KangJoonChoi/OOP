/** Class that prints the Collatz sequence starting from a given number.
 *  @KangJoonChoi YOUR NAME HERE
 */
public class Collatz {

    /** Buggy implementation of nextNumber! */
    public static int nextNumber(int n) {
        if (n%2 == 0) {/** if the remainder divided by 2 is 0, it is even*/
            return n/2; /** returning half of the number*/
        } else{/** odd numbers as it is not even therefore return 3*n +1 */
            return 3*n+1;
        }
    }

    public static void main(String[] args) {
        int n = 5;
        System.out.print(n + " ");
        while (n != 1) {
            n = nextNumber(n);
            System.out.print(n + " ");
        }
        System.out.println();
    }
}

