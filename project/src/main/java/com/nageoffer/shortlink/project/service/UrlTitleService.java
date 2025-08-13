package com.nageoffer.shortlink.project.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ProtocolException;

/**
 * Url标题接口层
 * @author 20784
 */
public interface UrlTitleService {

    String getTitleByUrl(String url) throws IOException;
}
