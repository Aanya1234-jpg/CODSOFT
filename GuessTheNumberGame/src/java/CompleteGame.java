import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(name = "GameServlet", urlPatterns = {"/game"})
public class CompleteGame extends HttpServlet {

    private final Random random = new Random();
    private static final int CLOSE_THRESHOLD = 10;
    private static final int MAX_ATTEMPTS = 10;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        HttpSession session = request.getSession();
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        
        if ("start".equals(action)) {
            int secretNumber = random.nextInt(100) + 1;
            
            session.setAttribute("secretNumber", secretNumber);
            session.setAttribute("attemptsLeft", MAX_ATTEMPTS);
            
            System.out.println("New game started. Secret number is: " + secretNumber);

            out.print("{\"message\": \"New game started!\", \"attemptsLeft\": " + MAX_ATTEMPTS + "}");
            
        } else if ("guess".equals(action)) {
            Integer secretNumber = (Integer) session.getAttribute("secretNumber");
            Integer attemptsLeft = (Integer) session.getAttribute("attemptsLeft");
            
            if (secretNumber == null) {
                out.print("{\"error\": \"Game not started. Please start a new game.\"}");
                out.flush();
                return;
            }
            
            int userGuess = Integer.parseInt(request.getParameter("guess"));
            attemptsLeft--;
            session.setAttribute("attemptsLeft", attemptsLeft);
            
            String feedback;
            String feedbackType;

            int difference = Math.abs(userGuess - secretNumber);

            if (userGuess == secretNumber) {
                feedbackType = "correct";
                int attemptsTaken = MAX_ATTEMPTS - attemptsLeft;

                if (attemptsTaken <= 2) {
                    feedback = "Brilliant! You guessed it in only " + attemptsTaken + " attempt(s). A true mind-reader!";
                } else {
                    feedback = "Correct! The number was " + secretNumber;
                }
                session.removeAttribute("secretNumber");
                
            } else if (attemptsLeft <= 0) {
                feedbackType = "lose";
                feedback = "Sorry, you lost! The number was " + secretNumber + ".";
                session.removeAttribute("secretNumber");
            } else if (difference <= CLOSE_THRESHOLD) {
                feedbackType = "close";
                feedback = userGuess < secretNumber ? "Low, but you're close!" : "High, but you're close!";
            } else {
                feedbackType = "wrong";
                feedback = userGuess < secretNumber ? "Too low!" : "Too high!";
            }
            
            String jsonResponse = String.format(
                "{\"feedback\": \"%s\", \"attemptsLeft\": %d, \"feedbackType\": \"%s\"}",
                feedback, attemptsLeft, feedbackType
            );
            out.print(jsonResponse);
        }
        
        out.flush();
    }
}