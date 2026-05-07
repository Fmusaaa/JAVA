public class SuhuDemo {
public static void main(String[] args) {
Suhu suhuku=new Suhu();
suhuku.cToF(25);suhuku.cToK(25);suhuku.cToRa(25);
System.out.println("C ke F : "+suhuku.cTF(25));
System.out.println("C ke K : "+suhuku.cTK(25));
System.out.println("C ke Ra : "+suhuku.cTRa(25));
Suhu suhumu=new Suhu(20);
suhumu.cToF(suhumu.c);suhumu.cToRa(suhumu.c);
System.out.println("C ke F : "+suhumu.cTF(suhumu.c));
System.out.println("C ke K : "+suhumu.cTK(suhumu.c));
System.out.println("C ke Ra : "+suhumu.cTRa(suhumu.c));
Suhu suhunya=new Suhu();
suhunya.inputC();
suhunya.cToK(suhunya.c);
suhunya.cToF(suhunya.c);suhunya.cToRa(suhunya.c);
}
}