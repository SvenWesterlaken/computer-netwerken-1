package nl.avans.smtpstatemachine;

import nl.avans.SmtpContext;

public class WaitForRcptToState implements SmtpStateInf {
    SmtpContext context;

    public WaitForRcptToState(SmtpContext context) {
        this.context=context;
    }

    @Override
    public void Handle(String data) {
        if(data.toUpperCase().startsWith("RCPT TO: ")) {
            context.AddRecipient(data.substring(9));
            context.SendData("250 Ok");
            context.SetNewState(new WaitForRcptToOrDataState(context));
            return;
        }
        if(data.toUpperCase().startsWith("QUIT")) {
            context.SendData("221 Bye");
            context.DisconnectSocket();
            return;
        }
        context.SendData("503 Error...");
        //Handle "RCPT TO: <user@domain.nl>" Command & TRANSITION TO NEXT STATE
        //Handle "QUIT" Command
        //Generate "503 Error on invalid input"
    }
}
