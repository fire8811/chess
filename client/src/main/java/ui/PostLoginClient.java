package ui;

import serverfacade.ServerFacade;

public class PostLoginClient implements Client {
    private final ServerFacade server;
    private final String url;
    private final StageManager stageManager;

    public PostLoginClient(String url, ServerFacade server, StageManager stageManager) {
        this.server = server;
        this.url = url;
        this.stageManager = stageManager;
    }

    @Override
    public String eval(String input) {
        return "";
    }
}
