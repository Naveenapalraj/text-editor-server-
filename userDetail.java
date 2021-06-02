import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import org.json.simple.*;
import org.json.simple.parser.JSONParser;

public class userDetail extends HttpServlet{
    public void doGet(HttpServletRequest request,HttpServletResponse response) throws ServletException,IOException{
        String email =  request.getParameter("email");
        DBhandler db = new DBhandler();
        JSONObject currentUser = db.getCurrentUser(email);
        response.setContentType("application/json");
        PrintWriter writer = response.getWriter();
        writer.println(currentUser);
    }

    public void doPut(HttpServletRequest request,HttpServletResponse response) throws ServletException,IOException{
        BufferedReader reader = request.getReader();
        String line = reader.readLine();
        try{
            JSONParser parser = new JSONParser();
            JSONObject userDetails = (JSONObject) parser.parse(line);
            Object getUser = userDetails.get("user");
            JSONObject userData = (JSONObject) getUser;
            Object getEmail = userData.get("email");
            Object getStatus = userData.get("userStatus");
            String email = getEmail.toString();
            boolean userStatus = Boolean.parseBoolean(getStatus.toString());
            DBhandler db = new DBhandler();
            db.logOutUser(email,userStatus); 
            Cookie removecookie = new Cookie("currentUser", "");
            removecookie.setMaxAge(0);
            removecookie.setPath("/");
            response.addCookie(removecookie);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}