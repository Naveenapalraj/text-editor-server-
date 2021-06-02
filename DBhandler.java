import java.sql.*;
import org.json.simple.*;
import java.util.*;  
import org.postgresql.util.PGobject;

public class DBhandler {
    Connection con = null;
    PreparedStatement stmt = null;
    ResultSet rs = null;

    void dbConnection(){
        try{
            Class.forName("org.postgresql.Driver");
            con = DriverManager.getConnection("jdbc:postgresql://localhost:5432/accountdetails","postgres","postgres");
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    void closeConnection(){
        try {
            if(rs!=null) rs.close();
        } catch (SQLException se) {
            se.printStackTrace();
        }
        try {
            if(stmt!=null) stmt.close();
        } catch (SQLException se) {
            se.printStackTrace();
        }
        try {
            if(con!=null) con.close();
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }

    //Method to check if email already exist

    boolean checkEmailExist(String email){
        boolean isEmailExists = false;
        dbConnection();
        try{
            stmt = con.prepareStatement("SELECT * FROM users ORDER BY email");
            rs = stmt.executeQuery();
            while(rs.next()){
                String checkEmail = rs.getString("email");
                if(checkEmail.equals(email)){
                    isEmailExists  = true;
                }
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return isEmailExists;
    }

    // Method to create new account

    boolean createAccount(String username,String email,String password){
        boolean isEmailExist = checkEmailExist(email);
        boolean isnewUser = false;
        if(isEmailExist){
            System.out.println("email already exists");
        }
        else{
            if(email !="" && email !=null && username !="" && username !=null && password !="" && password !=null){
                try{
                    stmt = con.prepareStatement("INSERT INTO users(name,email,password) VALUES(?,?,?)");
                    stmt.setString(1,username);
                    stmt.setString(2,email);
                    stmt.setString(3,password);
                    stmt.executeUpdate();
                    isnewUser = true;
                }
                catch(Exception e){
                    e.printStackTrace();
                }
                finally{
                    closeConnection();
                }
            }
            else{
                System.out.println("Please check the value");
            }
        }
        return isnewUser;
    }

    //Method to check if email and password provided correctly to login

    boolean checkLoginDetails(String email,String password){
        boolean isValidUser = false;
        dbConnection();
        try{
            stmt = con.prepareStatement("SELECT * FROM users ORDER BY email");
            rs = stmt.executeQuery();
            while(rs.next()){
                String checkEmail = rs.getString("email");
                String checkpassword = rs.getString("password");
                if(checkEmail.equals(email) && checkpassword.equals(password)){
                    isValidUser = true;
                }
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return isValidUser;
    }

    //Method to login user and updating the status to true 

    boolean loginUser(String email,String password){
        boolean checkUserDetails = checkLoginDetails(email,password);
        boolean userExists = false;
        if(checkUserDetails){
            try{
                userExists =true;
                stmt = con.prepareStatement("UPDATE users SET userStatus = ? WHERE email = ?");
                stmt.setBoolean(1,true);
                stmt.setString(2,email);
                stmt.executeUpdate();
            }
            catch(Exception e){
                e.printStackTrace();
            }
            finally{
                closeConnection();
            }
        }
        return userExists;
    }

    //Method to logout user

    void logOutUser(String email,boolean userStatus){
        dbConnection();
        try{
            stmt = con.prepareStatement("UPDATE users SET userStatus = ? WHERE email = ?");
            stmt.setBoolean(1,userStatus);
            stmt.setString(2,email);
            stmt.executeUpdate();
        }
        catch(Exception e){
            e.printStackTrace();
        }
        finally{
            closeConnection();
        }
    }

    //Method to get the current user

    JSONObject getCurrentUser(String email){
        dbConnection();
        JSONObject userDetails = new JSONObject();
        JSONArray detailsInarray = new JSONArray();
        boolean checkEmail = checkEmailExist(email);
        if(checkEmail){
            try{
                stmt = con.prepareStatement("SELECT * FROM users WHERE email =?");
                stmt.setString(1,email);
                rs = stmt.executeQuery();
                while(rs.next()){
                    JSONObject userData = new JSONObject();
                    userData.put("id",rs.getInt(1));
                    userData.put("name",rs.getString(2));
                    userData.put("email",rs.getString(3));
                    userData.put("userStatus",rs.getBoolean(5));
                    detailsInarray.add(userData);
                }
                userDetails.put("user",detailsInarray);
            }
            catch(Exception e){
                e.printStackTrace();
            }
            finally{
                closeConnection();
            }
        }
        return userDetails;
    }

    //Method to store editor data

    void storeEditorData(int userid,String name,Object bodyContent){
        dbConnection();
        try{
            PGobject editorData = new PGobject();
            editorData.setType("json");
            editorData.setValue(bodyContent.toString());

            stmt = con.prepareStatement("INSERT INTO editorData(name,bodyContent,userid)VALUES(?,?,?)");
            stmt.setString(1,name);
            stmt.setObject(2,editorData);
            stmt.setInt(3,userid);

            stmt.executeUpdate();
        }
        catch(Exception e){
            e.printStackTrace();
        }
        finally{
            closeConnection();
        }
        
    }

    //Method to get the last added data

    JSONObject getLastStoredData(){
        dbConnection();
        JSONObject editorDetails = new JSONObject();
        JSONArray dataInArray = new JSONArray();
        try{
            stmt = con.prepareStatement("SELECT * FROM editorData ORDER BY id DESC LIMIT 1");
            rs = stmt.executeQuery();
                while(rs.next()){
                    JSONObject editorData = new JSONObject();
                    editorData.put("id",rs.getInt(1));
                    editorData.put("name",rs.getString(2));
                    editorData.put("bodyContent",rs.getObject(3));
                    dataInArray.add(editorData);
                }
                editorDetails.put("document",dataInArray);
            }
            catch(Exception e){
                e.printStackTrace();
            }
            finally{
                closeConnection();
            }
            return editorDetails;
        }

    //Method to get the Editor data 

    JSONObject getEditorData(int id){
        dbConnection();
        JSONObject editorDetails = new JSONObject();
        JSONArray dataInArray = new JSONArray();
        try{
            stmt = con.prepareStatement("SELECT * FROM editorData WHERE userid =? ORDER BY id asc");
            stmt.setInt(1,id);
            rs = stmt.executeQuery();
            while(rs.next()){
                JSONObject editorData = new JSONObject();
                editorData.put("id",rs.getInt(1));
                editorData.put("name",rs.getString(2));
                editorData.put("bodyContent",rs.getObject(3));
                editorData.put("userId",rs.getInt(4));
                dataInArray.add(editorData);
            }
            editorDetails.put("document",dataInArray);
        }
        catch(Exception e){
            e.printStackTrace();
        }
        finally{
            closeConnection();
        }
        return editorDetails;
    }

    //Method to delete editor Data

    void deleteEditorData(int id){
        dbConnection();
        try{
            stmt = con.prepareStatement("DELETE FROM editorData WHERE id = ?");
            stmt.setInt(1,id);
            stmt.executeUpdate();
        }
        catch(Exception e){
            e.printStackTrace();
        }
        finally{
            closeConnection();
        }
    }

    //Method to update editor data

    void updateEditorData(int userId,String name,Object bodyContent){
        dbConnection();
        try{
            PGobject editorData = new PGobject();
            editorData.setType("json");
            editorData.setValue(bodyContent.toString());
            stmt = con.prepareStatement("UPDATE editorData SET name = ?,bodyContent = ? WHERE id = ?");
            stmt.setString(1,name);
            stmt.setObject(2,editorData);
            stmt.setInt(3,userId);
            stmt.executeUpdate();
        }
        catch(Exception e){
            e.printStackTrace();
        }
        finally{
            closeConnection();
        }
    }

}
