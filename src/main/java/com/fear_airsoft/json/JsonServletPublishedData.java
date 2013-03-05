package com.fear_airsoft.json;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class JsonServletPublishedData extends HttpServlet {
  private static final long serialVersionUID = 1L;
  private static final Logger logger = Logger.getLogger(JsonServletPublishedData.class.getName());
  static final String publishedDataUrl="https://www.googledrive.com/host/0B4Nj2G61OMg-OGgtdXdGei1zR2M";
  private JsonClient jsonClient = new JsonClient();

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException{
    resp.setHeader("Access-Control-Allow-Origin", "*");
    resp.setContentType("application/json");
    resp.setCharacterEncoding("UTF-8");
    String service=req.getPathInfo();
    String result=jsonClient.executeGet(publishedDataUrl+service);
    PrintWriter out = resp.getWriter();
    out.write(result);
    out.close();
  }
 
  @Override
  protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{ 
    resp.setHeader("Access-Control-Allow-Origin", "*");
    resp.setHeader("Access-Control-Allow-Methods", "GET");
  }
}