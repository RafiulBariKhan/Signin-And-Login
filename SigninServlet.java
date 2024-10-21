package loginServlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SigninServlet extends HttpServlet{
    private Connection conn;
    private PreparedStatement ps;
    private Statement st;
    /**
     *
     * @throws ServletException
     */
    @Override
    public void init() throws ServletException{
//        We cannot add another exception after ServletException because the above is the prototype of init() method.
        try{
            conn = DriverManager.getConnection("jdbc:oracle:thin:@//localhost:1521/xe", "user2", "abcd");
            System.out.println("Connected successfully to the databse");
            ps = conn.prepareStatement("insert into users values(?,?,?)");
            st = conn.createStatement();
        }catch(SQLException sq){
            ServletException obj = new ServletException(sq.getMessage());
            throw obj;
        }
    }
    
    /**
     *
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
        resp.setContentType("text/html");
        PrintWriter pw = resp.getWriter();
        pw.println("<html>");
        pw.println("<head><title>Login Response</title></head>");
        pw.println("<body>");
        
        String userid = req.getParameter("userid");
        String username = req.getParameter("username");
        String pwd = req.getParameter("password");
        
        try{
            ResultSet rs = st.executeQuery("select userid from users");
            while(rs.next()){
                String uid = rs.getString(1);
                boolean checkRecord = uid.equals(userid);
                if(checkRecord){
                    pw.println("User Id already exists!");
                    pw.println("<p>Please try again!</p><p><a href='signin.html'>Try again</a></p>");
                    pw.println("<br>Already have an account ?");
                    pw.println("<p><a href='index.html'>Log In</a></p>");
                    pw.println("</body>");
                    pw.println("</html>");
                    pw.close();
                    System.exit(0);
                }
            }
            ps.setString(1, userid);
            ps.setString(2, username);
            ps.setString(3, pwd);
            int ans = ps.executeUpdate();
            
            pw.println("<h3>Sign in Successfull!</h3><h2>Welcome Mr."+username+"</h2>");
            pw.println("<h3>Please <a href='index.html'>click here</a> to go to login page.</h3>");
            
        }catch(SQLException sq){
            System.out.println("Some problem in dopost" + sq);
            pw.println("<h2>Sorry!</h2>");
            pw.println("<p>Server is facing some issues. Try later. Actually there is some error in our code and server"
                    + " issue is just anything written in println.");
        }
        
        pw.println("</body>");
        pw.println("</html>");
        pw.close();
    }
    
     @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
    
    }
    
    /**
     *
     */
    @Override
    public void destroy(){
        try{
            conn.close();
        }catch(SQLException sq){
            System.out.println("Some problem in closing conn " + sq);
        }
    }
}
