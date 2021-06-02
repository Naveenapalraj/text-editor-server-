import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import org.json.simple.*;
import org.json.simple.parser.JSONParser;

public class editorData extends HttpServlet{
    public void doPost(HttpServletRequest request,HttpServletResponse response) throws ServletException,IOException{
        BufferedReader reader = request.getReader();
        String line = reader.readLine();
        try{
            JSONParser parser = new JSONParser();
            JSONObject editorDetails = (JSONObject) parser.parse(line);
            Object getEditorData = editorDetails.get("document");
            JSONObject editorValues = (JSONObject) getEditorData;
            Object getUserId = editorValues.get("user");
            Object getEditorName = editorValues.get("name");
            Object getEditorContent = editorValues.get("bodyContent");
            int userId = Integer.parseInt((String) getUserId);
            String editorName = getEditorName.toString();

            JSONArray bodyContent = (JSONArray) getEditorContent;
            DBhandler db = new DBhandler();
            db.storeEditorData(userId,editorName,bodyContent); 
            JSONObject textEditorData = db.getLastStoredData();
            PrintWriter writer = response.getWriter();
            writer.println(textEditorData);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public void doGet(HttpServletRequest request,HttpServletResponse response) throws ServletException,IOException{
        int currentUserId = Integer.parseInt(request.getParameter("id")); 
        DBhandler db = new DBhandler();
        JSONObject editorData = db.getEditorData(currentUserId);;
        response.setContentType("application/json");
        PrintWriter writer = response.getWriter();
        writer.println(editorData);
    }

    public void doDelete(HttpServletRequest request,HttpServletResponse response) throws ServletException,IOException{
        StringBuffer url = request.getRequestURL();
        int getId = Integer.parseInt(url.substring(url.lastIndexOf("/")+1));
        JSONObject documentDetails = new JSONObject();
        JSONObject editorData = new JSONObject();
        editorData.put("id",getId);
        editorData.put("url",url.toString());
        documentDetails.put("document",editorData);
        PrintWriter writer = response.getWriter();
        writer.println(documentDetails);
        DBhandler db = new DBhandler();
        db.deleteEditorData(getId);
    }

    public void doPut(HttpServletRequest request,HttpServletResponse response) throws ServletException,IOException{
        StringBuffer url = request.getRequestURL();
        int getId = Integer.parseInt(url.substring(url.lastIndexOf("/")+1));
        BufferedReader reader = request.getReader();
        String line = reader.readLine();
        try{
            JSONParser parser = new JSONParser();
            JSONObject editorData = (JSONObject) parser.parse(line);
            Object getDocumentData = editorData.get("document");
            JSONObject documentData = (JSONObject) getDocumentData;
            Object getEditorName = documentData.get("name");
            Object getEditorContent = documentData.get("bodyContent");
            String editorName = getEditorName.toString();
            JSONArray editorBodyContent = (JSONArray) getEditorContent;
            DBhandler db = new DBhandler();
            db.updateEditorData(getId,editorName,editorBodyContent);
        }
        catch(Exception e){
            e.printStackTrace();
        }
        
    }

}
