package com.oceanli.tomcat;

import com.oceanli.tomcat.http.GPRequest;
import com.oceanli.tomcat.http.GPResponse;
import com.oceanli.tomcat.http.GPServlet;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class GPTomcat {

    private ServerSocket server;
    private int port = 8080;
    Properties xmlWeb = new Properties();
    public Map<String, GPServlet> servletMap = new HashMap<>();

    public GPTomcat() {

    }

    public void init() {
        try {
            //读取web.properties中的配置的servletClass和url键值对，并粗如servletMap中
            String webInfo = this.getClass().getResource("/").getPath();
            FileInputStream fis = new FileInputStream(webInfo + "web.properties");
            xmlWeb.load(fis);
            for (Object key : xmlWeb.keySet()) {
                String k = key.toString();
                if (k.endsWith(".url")) {
                    String url = xmlWeb.getProperty(k);
                    String servletName = k.replaceAll(".url$", "");
                    String className = xmlWeb.getProperty(servletName + ".className");
                    servletMap.put(url, (GPServlet)Class.forName(className).newInstance());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void start() {

        init();

        //BOSS线程
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        //工作线程
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            //netty服务
            ServerBootstrap server = new ServerBootstrap();
            //链式编程
            server.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        //客户端初始化处理
                        @Override
                        protected void initChannel(SocketChannel client) throws Exception {
                            //无锁化串行编程
                            //编码器
                            client.pipeline().addLast(new HttpResponseEncoder());
                            //解码器
                            client.pipeline().addLast(new HttpRequestDecoder());
                            //业务逻辑处理
                            client.pipeline().addLast(new GPTomcatHandler());

                        }
                    })
                    //针对主线程的配置 分配线程最大数量128
                    .option(ChannelOption.SO_BACKLOG, 128)
                    //针对子线程的配置 保持长链接
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            //启动服务器
            ChannelFuture f = server.bind(port).sync();
            System.out.println("GPTomcat start, port:" + port);
            f.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class GPTomcatHandler extends ChannelInboundHandlerAdapter {

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            super.exceptionCaught(ctx, cause);
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            if (msg instanceof HttpRequest) {
                HttpRequest req = (HttpRequest) msg;
                GPRequest request = new GPRequest(ctx, req);
                GPResponse response = new GPResponse(ctx, req);
                String url = request.getUrl();
                if (servletMap.containsKey(url)) {
                    servletMap.get(url).service(request, response);
                } else {
                    response.write("404 -Not Found>>>>>");
                }
            }
        }
    }

    public static void main(String[] args) {
        new GPTomcat().start();
    }

}
