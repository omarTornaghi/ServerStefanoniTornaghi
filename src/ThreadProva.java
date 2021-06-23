import java.util.Random;

public class ThreadProva extends Thread{
    int id;
    Deposito d;

    public  ThreadProva(Deposito dep, int id){
        d = dep;
        this.id = id;
    }
    @Override
    public void run() {
        for(int i = 0; i < 10; i++) {
            Random random = new Random();
            if(random.nextBoolean()){
                Risorsa r = new Risorsa("" + id + i, new Data(), "230678");
                System.out.println("Creata risorsa: " + r);
                d.inserisciRisorsa(r);
            }
            else{
                Risorsa r = d.prelievoRisorsa();
                System.out.println("Risorsa presa: " + r);
            }
        }
    }
}
