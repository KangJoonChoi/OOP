public class number {
    public static void main(String[] args){
        String s= "35";
        //Object q = (Object) s;
        Integer c = (Integer)((Number) ((Object) s));
        System.out.println(c);
    }
}
