public class Matematika{
    int hasil;float hasil1;
    void pertambahan(int a,int b){
        hasil = a + b;System.out.println("pertambahan : "+a+" + "+b+" = "+hasil);
    }

    void pengurangan(int a,int b){
        hasil = a - b;System.out.println("pengurangan : "+a+" - "+b+" = "+hasil);
    }

    void perkalian(int a,int b){
        hasil = a * b;System.out.println("perkalian : "+a+" * "+b+" = "+hasil);
    }

    void pembagian(int a,int b){
        hasil1 = (float)a / (float)b;System.out.println("pembagian : "+a+" / "+b+" = "+hasil1);
    }
    void pertambahan(float a,float b){ //overloading,parameter pecahan
        hasil1 = a + b;System.out.println("pertambahan : "+a+" + "+b+" = "+hasil1);
    }
    void pengurangan(float a,float b){
        hasil1 = a - b;System.out.println("pengurangan : "+a+" - "+b+" = "+hasil1);
    }
    void perkalian(float a,float b){
        hasil1 = a * b;System.out.println("perkalian : "+a+" * "+b+" = "+hasil1);
    }
    void pembagian(float a,float b){
        hasil1 = a / b;System.out.println("pembagian : "+a+" / "+b+" = "+hasil1);
    }
}