package models.cli.qb;

import models.cli.Cli;
import tools.HttpHelper;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by SpereShelde on 2018/7/2.
 */
public class QBittorrent extends Cli{

    private String username, passwd;
    private String webUI;
    private String unpd;
    private String sid;
    private String site;
    private ArrayList<String> urls = new ArrayList<>();
    private double upload, download;
    private int apiVersion = 0;

    public void setUrls(ArrayList<String> urls) {
        this.urls = urls;
    }

    public QBittorrent(String webUI, String unpd, String site, ArrayList<String> urls, double download, double upload) {
        this.unpd = unpd;
        if (webUI.lastIndexOf("/") == webUI.length() - 1) {
            this.webUI = webUI.substring(0, webUI.length() - 1);
        } else {
            this.webUI = webUI;
        }
        this.site = site;
        this.urls = urls;
        this.upload = upload;
        this.download = download;
        String[] usernamePasswd = unpd.split("-");
        this.username = usernamePasswd[0];
        this.passwd = usernamePasswd[1];
        try {
            this.sid = HttpHelper.loginToQB(webUI + "/login", "BoxHelper", username, passwd, "127.0.0.1");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addTorrents(){

        if (this.apiVersion == 0){
            try {
                apiVersion = Integer.parseInt(HttpHelper.doGetToQB(webUI + "/version/api", "BoxHelper", "127.0.0.1"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (apiVersion >= 7){
            Map<String, String> contents = new HashMap();
            StringBuilder links = new StringBuilder();
            urls.forEach(url -> links.append(url + "\n"));
            contents.put("urls", links.toString());
            contents.put("dlLimit", new BigDecimal(download * 1024 * 1024 + "").toPlainString());
            contents.put("upLimit", new BigDecimal(upload * 1024 * 1024 + "").toPlainString());
            contents.put("category", "BoxHelper");
            try {
                Boolean success =  HttpHelper.doPostToQB(webUI + "/command/download", "BoxHelper", sid, "127.0.0.1", contents);
                if (success) System.out.println("QB: successfully add torrents in " + site + ".");
                else System.out.println("QB: cannot add torrents above, please check your session ID.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("QB: version is not supported.");
        }

    }


}
