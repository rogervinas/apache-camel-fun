package com.fun.camel.helpers;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import java.io.IOException;

public class FTPHelper {

    private final FTPClient client = new FTPClient();

    private final String host;
    private final int port;
    private final String user;
    private final String pass;

    public FTPHelper(String host, int port, String user, String pass) {
        this.host = host;
        this.port = port;
        this.user = user;
        this.pass = pass;
    }

    public void deleteFiles(String pathname) throws IOException {
        try {
            client.connect(host, port);
            client.login(user, pass);
            client.enterLocalPassiveMode();
            client.changeWorkingDirectory(pathname);
            for (FTPFile file : client.listFiles()) {
                if(!client.deleteFile(file.getName())) {
                    throw new IOException("Cannot delete remote file " + file.getName());
                }
            }
        } catch (IOException e) {
            throw e;
        } finally {
            client.disconnect();
        }
    }

    public FTPFile[] listFiles(String pathname) throws IOException {
        try {
            client.connect(host, port);
            client.login(user, pass);
            client.enterLocalPassiveMode();
            return client.listFiles(pathname);
        } catch (IOException e) {
            throw e;
        } finally {
            client.disconnect();
        }
    }
}
