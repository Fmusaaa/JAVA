import java.util.Scanner;
public class Suhu {
    int c;float hasil;
    Scanner in=new Scanner(System.in);
    public Suhu(){}
    public Suhu(int c){//Konstruktor
        this.c=c;
    }
    void inputC(){
        System.out.print("C : ");
        c=in.nextInt();//INPUT INTEGER
    }
    void cTok(int c){
        hasil=c + 273.15f;
        System.out.println(c+"  C ="+hasil+"  K");
    }
    float cTK(int c){
        return c + 273.15f;
    }

    void cToK(int c){
        cTok(c);
    }
    float cTRa(int c){

        return (c + 273.15f) * 1.8f;
    }
    void cToRa(int c){
        hasil = cTRa(c);
        System.out.println(c+"  C ="+hasil+"  Ra");
    }
    void cToF(int c){
        hasil=c * 1.8f+ 32;
        System.out.println(c+"  C ="+hasil+"  F");
    }
    float cTF(int c){
        return c * 1.8f + 32;
    }
}