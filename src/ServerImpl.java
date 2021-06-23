import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ServerImpl extends UnicastRemoteObject implements ResourceServer {
    private static Deposito deposito = new Deposito();
    //Coda thread-safe
    Queue<OperazioneDeposito> depositiNonEffettuati = new ConcurrentLinkedQueue<OperazioneDeposito>();
    Queue<ResourceClient> prelieviNonEffettuati = new ConcurrentLinkedQueue<ResourceClient>();

    protected ServerImpl() throws RemoteException {
    }

    public synchronized void aggiungiRisorsa(Risorsa r, ResourceClient c) throws RemoteException {
        //Posso aggiungere?
        boolean aggiunta = deposito.inserisciRisorsa(r);
        if(aggiunta){
            System.out.println("Risorsa aggiunta: " + r);
            //Notifico il client
            c.notificaAggiunta(r);
            //c'è un prelievo in attesa?
            if(!prelieviNonEffettuati.isEmpty()){
                System.out.println("Posso fare anche un prelievo");
                //Si, quindi lo eseguo
                ResourceClient clientPrelievo = prelieviNonEffettuati.poll();
                prelevaRisorsa(clientPrelievo);
            }
        }
        else {
            //Altrimenti
            System.out.println("Non posso aggiungere quindi mi metto in coda");
            depositiNonEffettuati.add(new OperazioneDeposito(r, c));
        }
    }

    public synchronized void prelevaRisorsa(ResourceClient c) throws RemoteException{
        Risorsa r = deposito.prelievoRisorsa();
        if(r != null){
            System.out.println("Prelievo effettuato: " + r);
            //Notifico il client
            c.notificaPrelievo(r);
            //Posso fare un inserimento?
            if(!depositiNonEffettuati.isEmpty()){
                OperazioneDeposito od = depositiNonEffettuati.poll();
                aggiungiRisorsa(od.getRisorsa(), od.getClient());
            }
        }
        else{
            //Altrimenti
            System.out.println("Non posso prelevare quindi mi metto in coda");
            prelieviNonEffettuati.add(c);
        }
    }

    public static void main(String[] args) throws RemoteException {
        //Creazione registry
        ResourceServer resServ = (ResourceServer) new ServerImpl();
        Registry reg = LocateRegistry.createRegistry(1099);
        reg.rebind("DEPOSITO", resServ);
    }
}
