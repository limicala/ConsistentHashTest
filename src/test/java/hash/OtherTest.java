package hash;

public class OtherTest {

    public static void main(String[] args) {
        func1();
    }


    static void func1(){
        int i = 0, j = 0;
        outer:
        for(i = 0; i < 10; i++){

            for (j = 0; j < 10; j++){

                if(j == 2) break ;
                if(i == 3) break outer;
            }
        }

        System.out.println("i = "+i+",j = "+j);
    }
}
