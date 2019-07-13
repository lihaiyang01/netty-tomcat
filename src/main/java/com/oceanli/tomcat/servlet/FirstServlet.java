package com.oceanli.tomcat.servlet;

import com.oceanli.tomcat.http.GPRequest;
import com.oceanli.tomcat.http.GPResponse;
import com.oceanli.tomcat.http.GPServlet;

public class FirstServlet extends GPServlet {
    @Override
    public void doGet(GPRequest request, GPResponse response) {
        doPost(request,response);
    }

    @Override
    public void doPost(GPRequest request, GPResponse response) {
        response.write("this is a first servlet");
    }
}
