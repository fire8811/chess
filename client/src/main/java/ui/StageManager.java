package ui;

enum ClientStage {
    PRELOGIN, POSTLOGIN, IN_GAME;
}

public class StageManager {
    private ClientStage stage;

    public StageManager(){
        stage = ClientStage.PRELOGIN;
    }

    public ClientStage getStage() {
        return stage;
    }

    public void setStage(ClientStage newStage){
        this.stage = newStage;
    }
}
