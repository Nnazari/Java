public class Primes {
    public static void main(String[] args) {
        System.out.println("Простые числа:");
        for (int i=2;i<=100;i++){
            if (isPrime(i)) {
                System.out.println(i);
            }
        }
    }
    public static boolean isPrime(int n){
    //Функция распознавания простых чисел
            if(n < 2)
                return false;
            for (int i=2; i<=Math.sqrt(n); i++){
                if(n % i == 0)
                    return false;
            }
            return true;
    }

}