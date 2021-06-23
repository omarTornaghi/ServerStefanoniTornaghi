import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class RisorsaServerImpl extends UnicastRemoteObject implements RisorsaServer {
    private final Deposito deposito = new Deposito();
    //Coda thread-safe
    Queue<OperazioneDeposito> depositiNonEffettuati = new ConcurrentLinkedQueue<>();
    Queue<RisorsaClient> prelieviNonEffettuati = new ConcurrentLinkedQueue<>();

    protected RisorsaServerImpl() throws RemoteException {
    }

    public synchronized void aggiungiRisorsa(Risorsa r, RisorsaClient c) throws RemoteException {
        //Posso aggiungere?
        boolean aggiunta = deposito.inserisciRisorsa(r);
        if(aggiunta){
            //Notifico il client
            c.notificaAggiunta(r);
        }
        else {
            //Il deposito è pieno
            depositiNonEffettuati.add(new OperazioneDeposito(r, c));
            System.out.println("Depositi in coda: " + depositiNonEffettuati.size());
        }
        //c'è un prelievo in attesa?
        if(!prelieviNonEffettuati.isEmpty()){
            //Si, quindi lo eseguo
            RisorsaClient clientPrelievo = prelieviNonEffettuati.poll();
            prelevaRisorsa(clientPrelievo);
        }
    }

    public synchronized void prelevaRisorsa(RisorsaClient c) throws RemoteException{
        Risorsa r = deposito.prelievoRisorsa();
        if(r != null){
            //Notifico il client
            c.notificaPrelievo(r);
        }
        else{
            //Il deposito è vuoto
            prelieviNonEffettuati.add(c);
            System.out.println("Prelievi in coda: " + prelieviNonEffettuati.size());
        }
        //Posso fare un inserimento?
        if(!depositiNonEffettuati.isEmpty()){
            OperazioneDeposito od = depositiNonEffettuati.poll();
            aggiungiRisorsa(od.getRisorsa(), od.getClient());
        }
    }

    public static void main(String[] args) throws RemoteException {
        //Creazione registry
        RisorsaServer resServ = new RisorsaServerImpl();
        Registry reg = LocateRegistry.createRegistry(1099);
        reg.rebind("DEPOSITO", resServ);
    }
}
