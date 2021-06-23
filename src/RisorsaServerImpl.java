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
            //Altrimenti
            System.out.println("Non posso aggiungere quindi mi metto in coda");
            depositiNonEffettuati.add(new OperazioneDeposito(r, c));
        }
        //c'è un prelievo in attesa?
        if(!prelieviNonEffettuati.isEmpty()){
            System.out.println("Posso fare anche un prelievo");
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
            //Altrimenti
            System.out.println("Non posso prelevare quindi mi metto in coda");
            prelieviNonEffettuati.add(c);
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
