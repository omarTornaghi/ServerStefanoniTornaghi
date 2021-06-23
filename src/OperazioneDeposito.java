public class OperazioneDeposito {
    private Risorsa risorsa;
    private ResourceClient client;

    public OperazioneDeposito(Risorsa risorsa, ResourceClient client) {
        this.risorsa = risorsa;
        this.client = client;
    }

    public Risorsa getRisorsa() {
        return risorsa;
    }

    public void setRisorsa(Risorsa risorsa) {
        this.risorsa = risorsa;
    }

    public ResourceClient getClient() {
        return client;
    }

    public void setClient(ResourceClient client) {
        this.client = client;
    }
}
