public class OperazioneDeposito {
    private Risorsa risorsa;
    private RisorsaClient client;

    public OperazioneDeposito(Risorsa risorsa, RisorsaClient client) {
        this.risorsa = risorsa;
        this.client = client;
    }

    public Risorsa getRisorsa() {
        return risorsa;
    }

    public void setRisorsa(Risorsa risorsa) {
        this.risorsa = risorsa;
    }

    public RisorsaClient getClient() {
        return client;
    }

    public void setClient(RisorsaClient client) {
        this.client = client;
    }
}
