package loginServlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LoginServlet extends HttpServlet{
    private Connection conn;
    private PreparedStatement ps;

    /**
     *
     * @throws ServletException
     */
    @Override
    public void init() throws ServletException{
        ServletConfig cfg = getServletConfig(); // super.getServletConfig() is also true.
        String connUrl = cfg.getInitParameter("ConnectionURL");
        String connId = cfg.getInitParameter("ConnectionUserid");
        String connPassword = cfg.getInitParameter("ConnectionPassword");
//        We cannot add another exception after ServletException because the above is the prototype of init() method.
        try{
            conn = DriverManager.getConnection(connUrl, connId, connPassword);
            System.out.println("Connected successfully to the databse");
            ps = conn.prepareStatement("Select username from users where userid = ? and password = ?");
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
        String pwd = req.getParameter("password");
        try{
            ps.setString(1, userid);
            ps.setString(2, pwd);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                String username = rs.getString(1);
                pw.println("<h2>Welcome " + username + "</h2>");
                pw.println("<p>Login successfull. Enjoy Surfing!</p>");
            }else{
                pw.println("<h2>Sorry!</h2>");
                pw.println("<p>Login rejected. Invald userid/password!</p>");
                pw.println("<p>Try again <a href='index.html'>Login again</a></p>");
            }
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