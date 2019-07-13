package com.oceanli.tomcat.http;

public abstract class GPServlet {

    public void service(GPRequest request, GPResponse response) {
        if ("GET".equalsIgnoreCase(request.getMethod())) {
            doGet(request,response);
        } else {
            doPost(request,response);
        }
    }

    public abstract void doGet(GPRequest request, GPResponse response);

    public abstract void doPost(GPRequest request, GPResponse response);
}
