
public class Palindrome {
    public static void main(String[] args) {
        for (int i = 0; i < args.length; i++) {
            String s = args[i];
            if (isPalindrome(s)) {
                System.out.println(s+" is Palindrom");
            }else{
                System.out.println(s+"is not Palindrom");
            }
        }
    }
    public static String reverseString(String s){
        String revs = "";//Создаем доп переменную для реверсирования
        for (int i=0; i<s.length();i++){
            revs += s.charAt(s.length() - i-1);
        }
        return revs;
    }
    public static boolean isPalindrome(String s){
        if (s.equalsIgnoreCase(reverseString(s))) {
            return true;
        }
        return false;
    }

}