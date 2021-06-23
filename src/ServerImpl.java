public class ServerImpl {
    private static Deposito deposito = new Deposito();

    public static void main(String[] args) {
        //Creazione registry
        for(int i = 0; i< 5; i++){
            ThreadProva th = new ThreadProva(deposito);
            th.run();
        }
    }
}
