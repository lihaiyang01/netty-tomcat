package com.oceanli.tomcat.http;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;

import java.io.OutputStream;

public class GPResponse {


    ChannelHandlerContext ctx;

    HttpRequest req;

    public GPResponse(ChannelHandlerContext ctx, HttpRequest req) {
        this.req = req;
        this.ctx = ctx;
    }

    public void write(String s) {

        try {
            if (s == null || "".equals(s)) {
                return;
            }
            FullHttpResponse response = new DefaultFullHttpResponse(
                    //设置HTTP版本为1.1
                    HttpVersion.HTTP_1_1,
                    //设置响应状态码
                    HttpResponseStatus.OK,
                    //输出的值编码为UTF-8
                    Unpooled.wrappedBuffer(s.getBytes("UTF-8")));
            response.headers().set("Content-Type", "text/html;");
            ctx.write(response);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ctx.flush();
            ctx.close();
        }
    }
}
