public class Deposito {
    private final int CAPACITA_MASSIMA = 100;
    private final Risorsa[] risorse = new Risorsa[CAPACITA_MASSIMA];
    private int numElementi = 0;

    public Deposito() {
    }

    public synchronized boolean inserisciRisorsa(Risorsa r) {
        if (numElementi >= CAPACITA_MASSIMA) {
            System.out.println("Array pieno, risorsa non inserita: " + r);
            return false;
        }
        risorse[numElementi++] = r;
        System.out.println("Aggiunta risorsa in DEPOSITO: " + r);
        System.out.println("numElementi: " + numElementi);
        return true;

    }

    public synchronized Risorsa prelievoRisorsa() {
        if (numElementi == 0) {
            System.out.println("Array vuoto, prelievo non effettuabile");
            return null;
        }
        Risorsa r = risorse[--numElementi];
        System.out.println("Prelievo risorsa in DEPOSITO: " + r);
        return r;
    }

}
