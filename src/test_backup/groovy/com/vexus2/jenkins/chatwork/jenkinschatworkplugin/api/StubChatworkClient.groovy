package com.vexus2.jenkins.chatwork.jenkinschatworkplugin.api

class StubChatworkClient extends ChatworkClient {
    StubChatworkClient(String apiKey, String proxySv, String proxyPort) {
        super(apiKey, proxySv, proxyPort)
    }

    String path
    String response

    @Override
    protected String get(String path) {
        if (this.path == path) {
            response
        } else {
            throw new Exception("Unknown path: expected ${this.path}, but actual ${path}")
        }
    }

    @Override
    protected void post(String path, Map<String, String> params) throws IOException {
        if (this.path != path) {
            throw new Exception("Unknown path: expected ${this.path}, but actual ${path}")
        }
    }
}
