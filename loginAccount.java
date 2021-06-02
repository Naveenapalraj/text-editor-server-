import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import org.json.simple.*;

public class loginAccount extends HttpServlet{
    public void doGet(HttpServletRequest request,HttpServletResponse response) throws ServletException,IOException{
        String email =  request.getParameter("email"); 
        String password = request.getParameter("password"); 
        DBhandler db = new DBhandler();
        boolean userExists = db.loginUser(email,password);
        if(userExists){
            Cookie addUserToCookie = new Cookie("currentUser",email);
            addUserToCookie.setMaxAge(24 * 3600);
            response.addCookie(addUserToCookie);
            response.sendRedirect("http://localhost:8080/textEditorApp/home/#/homePage");
        }
        else{
            response.sendRedirect("http://localhost:8080/textEditorApp/home/#/signup-page");
        }
    }

    public void doPost(HttpServletRequest request,HttpServletResponse response) throws ServletException,IOException{
        String username =  request.getParameter("name"); 
        String email =  request.getParameter("email"); 
        String password = request.getParameter("password"); 
        try{
            DBhandler db = new DBhandler();
            boolean isNewUser = db.createAccount(username,email,password); 
            if(isNewUser){
                response.sendRedirect("http://localhost:8080/textEditorApp/home/#/signin-page");
            }
            else{
                response.sendRedirect("http://localhost:8080/textEditorApp/home/#/signup-page");
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}