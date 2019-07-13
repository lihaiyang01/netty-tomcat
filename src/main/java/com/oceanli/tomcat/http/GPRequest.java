package com.oceanli.tomcat.http;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

public class GPRequest {

    HttpRequest req;
    private String url;

    private String method;

    ChannelHandlerContext ctx;

    public GPRequest(ChannelHandlerContext ctx, HttpRequest req) {
        this.ctx = ctx;
        this.req = req;
    }

    public String getUrl() {
        return req.uri();

    }

    public String getMethod() {
        return req.method().name();
    }

    public Map<String, List<String>> getParameters() {
        QueryStringDecoder decoder = new QueryStringDecoder(req.uri());
        return decoder.parameters();
    }
    public String getParameter(String name) {
        Map<String, List<String>> parameters = getParameters();
        List<String> param = parameters.get(name);
        if (null == param) {
            return null;
        } else {
            return param.get(0);
        }
    }
}
